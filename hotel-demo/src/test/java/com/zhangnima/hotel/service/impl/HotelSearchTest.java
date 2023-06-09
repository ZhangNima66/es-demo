package com.zhangnima.hotel.service.impl;


import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.zhangnima.hotel.pojo.HotelDoc;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.DistanceUnit;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.NamedValue;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class HotelSearchTest {
    @Autowired
    private ElasticsearchClient client;

    @Test
    void matchAll() throws IOException {
        SearchResponse<HotelDoc> response = client.search(qr ->
            qr.index("hotel")
                .query(q ->
                    q.matchAll(ma -> ma)
                ), HotelDoc.class
        );
        log.info(response.toString());
    }

    @Test
    void match() throws IOException {
        SearchResponse<HotelDoc> response = client.search(
            qr -> qr.index("hotel").query(
                q -> q.match(
                    mq -> mq.field("all").query("外滩如家")
                )
            ), HotelDoc.class
        );

        log.info(response.toString());
    }

    @Test
    void multiMatch() throws IOException {
        SearchResponse<HotelDoc> response = client.search(
            qr -> qr.index("hotel").query(
                q -> q.multiMatch(
                    qm -> qm.fields("brand", "name", "business").query("外滩如家")
                )
            ), HotelDoc.class
        );

        log.info(response.toString());
    }

    @Test
    void term() throws IOException {
        SearchResponse<HotelDoc> response = client.search(
            qr -> qr.index("hotel").query(
                q -> q.term(
                    t -> t.field("brand").value("如家")
                )
            ), HotelDoc.class
        );

        log.info(response.toString());
    }

    @Test
    void range() throws IOException {
        SearchResponse<HotelDoc> response = client.search(
            qr -> qr.index("hotel").query(
                q -> q.range(
                    t -> t.field("price").gte(JsonData.of("100")).lte(JsonData.of("300"))
                )
            ), HotelDoc.class
        );

        log.info(response.toString());
    }

    @Test
    void geoDistance() throws IOException {
        SearchResponse<HotelDoc> response = client.search(
            qr -> qr.index("hotel").query(
                q -> q.geoDistance(
                    t -> t.field("location").location(gl -> gl.latlon(l -> l.lat(31.21).lon(121.5))).distance("15km")
                )
            ), HotelDoc.class
        );

        log.info(response.toString());
    }

    @Test
    void functionScore() throws IOException {
        SearchResponse<HotelDoc> response = client.search(
            qr -> qr.index("hotel").query(
                q -> q.functionScore(
                    fs -> fs.query(
                        fsq -> fsq.match(m -> m.field("all").query("外滩"))
                    ).functions(
                        f -> f.filter(
                            fq -> fq.term(t -> t.field("brand").value("如家"))
                        ).weight(10D)
                    ).boostMode(FunctionBoostMode.Multiply)
                )
            ), HotelDoc.class
        );

        log.info(response.toString());
    }

    @Test
    void bool() throws IOException {
        SearchResponse<HotelDoc> response = client.search(
            qr -> qr.index("hotel").query(
                q -> q.bool(
                    b -> b.must(
                        mq -> mq.match(m -> m.field("name").query("如家"))
                    ).mustNot(
                        mnq -> mnq.range(mnqr -> mnqr.field("price").gte(JsonData.of("500")))
                    ).filter(
                        fq -> fq.geoDistance(fqgd -> fqgd.field("location").location(gl -> gl.latlon(l -> l.lat(31.21).lon(121.5))).distance("15km"))
                    )
                )
            ), HotelDoc.class
        );

        log.info(response.toString());
    }

    @Test
    void sort() throws IOException {
        SearchResponse<HotelDoc> response = client.search(
            qr -> qr.index("hotel").query(
                q -> q.match(
                    mq -> mq.field("all").query("外滩如家")
                )
            ).sort(
                so -> so.field(FieldSort.of(fs -> fs.field("score").order(SortOrder.Desc)))
            ).sort(
                so -> so.field(FieldSort.of(fs -> fs.field("price").order(SortOrder.Asc)))
            ), HotelDoc.class
        );

        log.info(response.toString());
    }

    @Test
    void sortGeoDistance() throws IOException {
        SearchResponse<HotelDoc> response = client.search(
            qr -> qr.index("hotel").query(
                q -> q.matchAll(ma -> ma)
            ).sort(
                so -> so.geoDistance(sog -> sog.field("location").location(sogl ->
                    sogl.latlon(ll -> ll.lat(31.0034).lon(121.61222))).order(SortOrder.Asc).unit(DistanceUnit.Kilometers))
            ), HotelDoc.class
        );

        log.info(response.toString());
    }

    @Test
    void searchAfter() throws IOException {
        SearchResponse<HotelDoc> response = client.search(
            qr -> qr.index("hotel").query(
                q -> q.matchAll(ma -> ma)
            ).sort(
                so -> so.field(FieldSort.of(fs -> fs.field("score").order(SortOrder.Desc)))
            ).sort(
                so -> so.field(FieldSort.of(fs -> fs.field("price").order(SortOrder.Asc)))
            ).searchAfter(
                "49",
                "562"
            ), HotelDoc.class
        );

        log.info(response.toString());
    }


    @Test
    void limit() throws IOException {
        SearchResponse<HotelDoc> response = client.search(
            qr -> qr.index("hotel").query(
                q -> q.matchAll(ma -> ma)
            ).sort(
                so -> so.field(FieldSort.of(fs -> fs.field("score").order(SortOrder.Desc)))
            ).sort(
                so -> so.field(FieldSort.of(fs -> fs.field("price").order(SortOrder.Asc)))
            ).from(0).size(20), HotelDoc.class
        );

        log.info(response.toString());
        log.warn(response.hits().hits().size() + "");
    }

    @Test
    void highlight() throws IOException {
        SearchResponse<HotelDoc> response = client.search(
            qr -> qr.index("hotel").query(
                q -> q.match(m -> m.field("all").query("如家"))
            ).sort(
                so -> so.field(FieldSort.of(fs -> fs.field("score").order(SortOrder.Desc)))
            ).sort(
                so -> so.field(FieldSort.of(fs -> fs.field("price").order(SortOrder.Asc)))
            ).highlight(
                hl -> hl.fields("name", hf -> hf.requireFieldMatch(false).preTags("<em>").postTags("</em>"))
            )
            , HotelDoc.class
        );

        log.info(response.toString());
    }

    @Test
    void aggs() throws IOException {
        SearchResponse<HotelDoc> response = client.search(
            qr -> qr.index("hotel").query(q -> q.matchAll(ma -> ma))
                .size(0)
                .aggregations("brandAggs",
                    ag -> ag.terms(
                            agt -> agt.field("brand")
                                .order(Collections.singletonList(new NamedValue<>("brandStat.avg", SortOrder.Desc)))
                                .size(10))
                        .aggregations("brandStat", agag -> agag.stats(ags -> ags.field("score")))
                ), HotelDoc.class
        );
        log.info(response.toString());
    }

    @Test
    void suggestion() throws IOException {
        SearchResponse<HotelDoc> response = client.search(
            qr -> qr.index("hotel")
                .suggest(
                    sg -> sg.text("sh")
                        .suggesters("suggestions", fsg -> fsg.completion(sc -> sc.field("suggestion").skipDuplicates(true).size(10)))
                ), HotelDoc.class
        );
        log.info(response.toString());
    }
}
