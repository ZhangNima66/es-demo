package com.zhangnima.hotel.service.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
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
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

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


}
