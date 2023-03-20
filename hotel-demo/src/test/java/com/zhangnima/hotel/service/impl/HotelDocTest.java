package com.zhangnima.hotel.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.zhangnima.hotel.pojo.Hotel;
import com.zhangnima.hotel.pojo.HotelDoc;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.log4j.Log4j2;

@SpringBootTest
@Log4j2
public class HotelDocTest {
    private ElasticsearchClient client;

    @Autowired
    private HotelServiceImpl hotelService;

    @Test
    void insertDoc() throws IOException {
        Hotel hotel = hotelService.getById(47066);
        IndexResponse response = client.index(idx ->
            idx.index("hotel")
                .id(hotel.getId().toString())
                .document(new HotelDoc(hotel))
        );
        log.info(response);
    }

    @Test
    void getDoc() throws IOException {
        GetResponse<HotelDoc> response = client.get(request ->
            request.index("hotel")
                .id("47066"), HotelDoc.class
        );
        log.info(response);
        log.info(response.source());
    }

    @Test
    void deleteDoc() throws IOException {
        DeleteResponse response = client.delete(request ->
            request.index("hotel")
                .id("47066")
        );
        log.info(response);
    }

    @Test
    void updateDoc() throws IOException {
        HotelDoc hotelDoc = new HotelDoc();
        hotelDoc.setName("上海浦东东站华美达酒店");

        UpdateResponse<HotelDoc> response = client.update(request ->
            request.index("hotel")
                .id("47066")
                .doc(hotelDoc), HotelDoc.class
        );
        log.info(response);
    }

    @Test
    void bulk() throws IOException {
        List<Hotel> hotels = hotelService.list();

        BulkRequest.Builder builder = new BulkRequest.Builder();
        hotels.forEach(hotel -> builder.operations(
            op -> op.index(index ->
                index.index("hotel")
                    .id(hotel.getId().toString())
                    .document(new HotelDoc(hotel))
            )
        ));

        BulkResponse response = client.bulk(builder.build());
        log.info(response);
    }

    @BeforeEach
    void setUp() {
        RestClient restClient = RestClient.builder(
            new HttpHost("192.168.50.2", 9200)
        ).build();

        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        this.client = new ElasticsearchClient(transport);
    }

}
