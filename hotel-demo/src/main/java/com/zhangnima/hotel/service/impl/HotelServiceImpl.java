package com.zhangnima.hotel.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangnima.hotel.mapper.HotelMapper;
import com.zhangnima.hotel.pojo.Hotel;
import com.zhangnima.hotel.pojo.HotelDoc;
import com.zhangnima.hotel.pojo.requset.PageRequest;
import com.zhangnima.hotel.pojo.result.PageResult;
import com.zhangnima.hotel.service.IHotelService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.DistanceUnit;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.json.JsonData;

/**
 * @author ZhangNima
 */
@Service
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    private final String HOTEL_ES_INDEX = "hotel";
    @Autowired
    private ElasticsearchClient client;


    @Override
    public PageResult search(PageRequest request) throws IOException {
        SearchRequest.Builder builder = new SearchRequest.Builder();
        builder.index(HOTEL_ES_INDEX);
        builder.query(
            q -> q.functionScore(
                qf -> qf.query(
                    qfq -> qfq.bool(b -> {
                        String key = request.getKey();
                        if (StringUtils.isBlank(key)) {
                            b.must(mq -> mq.matchAll(ma -> ma));
                        } else {
                            b.must(mq -> mq.match(m -> m.field("all").query(key)));
                        }
                        String city = request.getCity();
                        if (StringUtils.isNotBlank(city)) {
                            b.filter(ft -> ft.term(ftt -> ftt.field("city").value(city)));
                        }
                        String brand = request.getBrand();
                        if (StringUtils.isNotBlank(brand)) {
                            b.filter(ft -> ft.term(ftt -> ftt.field("brand").value(brand)));
                        }
                        String starName = request.getStarName();
                        if (StringUtils.isNotBlank(starName)) {
                            b.filter(ft -> ft.term(ftt -> ftt.field("star_name").value(starName)));
                        }
                        Integer minPrice = request.getMinPrice();
                        Integer maxPrice = request.getMaxPrice();
                        if (minPrice != null && maxPrice != null) {
                            b.filter(ft -> ft.range(ftr -> ftr.field("price").gte(JsonData.of(minPrice)).lte(JsonData.of(maxPrice))));
                        }
                        return b;
                    })
                ).functions(
                    f -> f.filter(fq -> fq.term(fqt -> fqt.field("isAd").value(true))).weight(10D)
                ).boostMode(FunctionBoostMode.Sum)
            )
        );
        String sortBy = request.getSortBy();
        if (StringUtils.isNotBlank(sortBy) && !"default".equals(sortBy)) {
            builder.sort(s -> s.field(sf -> sf.field(sortBy).order(SortOrder.Desc)));
        }
        String location = request.getLocation();
        if (StringUtils.isNotBlank(location)) {
            builder.sort(s -> s.geoDistance(sg -> sg.field("location").location(sgl -> sgl.text(location)).order(SortOrder.Asc).unit(DistanceUnit.Kilometers)));
        }
        int page = request.getPage();
        int size = request.getSize();
        builder.from((page - 1) * size);
        builder.size(size);
        SearchResponse<HotelDoc> response = client.search(builder.build(), HotelDoc.class);

        return handleResponse(response);
    }

    private PageResult handleResponse(SearchResponse<HotelDoc> response) {
        PageResult result = new PageResult();
        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        List<HotelDoc> hotels = response.hits().hits().stream().map(hit -> {
            HotelDoc hotel = hit.source();
            String sort = CollectionUtils.isNotEmpty(hit.sort()) ? hit.sort().get(0) : null;
            if (hotel != null) {
                hotel.setDistance(sort);
            }
            return hotel;
        }).collect(Collectors.toList());

        result.setTotal(total);
        result.setHotels(hotels);
        return result;
    }

    @Override
    public Map<String, List<String>> filters() throws IOException {
        SearchRequest.Builder builder = new SearchRequest.Builder();
        builder.index("hotel");
        builder.size(0);
        builder.aggregations("brandAgg", ag -> ag.terms(agt -> agt.field("brand").size(100)));
        builder.aggregations("cityAgg", ag -> ag.terms(agt -> agt.field("city").size(100)));
        builder.aggregations("starAgg", ag -> ag.terms(agt -> agt.field("star_name").size(100)));

        Map<String, List<String>> result = new HashMap<>();
        SearchResponse<HotelDoc> response = client.search(builder.build(), HotelDoc.class);
        List<String> brandAgg = response.aggregations().get("brandAgg").sterms().buckets().array().stream().map(b -> b.key().stringValue()).collect(Collectors.toList());
        result.put("brand", brandAgg);
        List<String> cityAgg = response.aggregations().get("cityAgg").sterms().buckets().array().stream().map(b -> b.key().stringValue()).collect(Collectors.toList());
        result.put("city", cityAgg);
        List<String> starAgg = response.aggregations().get("starAgg").sterms().buckets().array().stream().map(b -> b.key().stringValue()).collect(Collectors.toList());
        result.put("starName", starAgg);

        return result;
    }

    @Override
    public List<String> suggestion(String key) throws IOException {
        SearchRequest.Builder builder = new SearchRequest.Builder();
        builder.index("hotel");
        builder.suggest(sg ->
            sg.text(key)
                .suggesters("key_suggestion", sgf -> sgf.completion(
                    sc -> sc.field("suggestion")
                        .skipDuplicates(true)
                        .size(10)
                ))
        );

        SearchResponse<HotelDoc> response = client.search(builder.build(), HotelDoc.class);
        List<CompletionSuggestOption<HotelDoc>> options = response.suggest().get("key_suggestion").get(0).completion().options();

        return options.stream().map(CompletionSuggestOption::text).collect(Collectors.toList());
    }

}
