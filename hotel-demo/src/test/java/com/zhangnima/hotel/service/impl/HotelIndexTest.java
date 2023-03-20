package com.zhangnima.hotel.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.GeoPointProperty;
import co.elastic.clients.elasticsearch._types.mapping.IntegerNumberProperty;
import co.elastic.clients.elasticsearch._types.mapping.KeywordProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TextProperty;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping.Builder;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;
import co.elastic.clients.elasticsearch.indices.PutMappingResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.log4j.Log4j2;

@SpringBootTest
@Log4j2
public class HotelIndexTest {

    private ElasticsearchClient client;

    @Test
    void testInit() {
        System.out.println(client);
    }

    @Test
    void createIndex() throws IOException {
        Map<String, Property> propertyMap = new HashMap<>();
        propertyMap.put("id", new Property(new KeywordProperty.Builder().build()));
        propertyMap.put("name", new Property(new TextProperty.Builder().analyzer("ik_max_word").copyTo("all").build()));
        propertyMap.put("address", new Property(new KeywordProperty.Builder().index(false).build()));
        propertyMap.put("price", new Property(new IntegerNumberProperty.Builder().build()));
        propertyMap.put("score", new Property(new IntegerNumberProperty.Builder().build()));
        propertyMap.put("brand", new Property(new KeywordProperty.Builder().copyTo("all").build()));
        propertyMap.put("city", new Property(new KeywordProperty.Builder().build()));
        propertyMap.put("star_name", new Property(new KeywordProperty.Builder().build()));
        propertyMap.put("business", new Property(new KeywordProperty.Builder().copyTo("all").build()));
        propertyMap.put("location", new Property(new GeoPointProperty.Builder().build()));
        propertyMap.put("pic", new Property(new KeywordProperty.Builder().index(false).build()));
        propertyMap.put("all", new Property(new TextProperty.Builder().analyzer("ik_max_word").copyTo("all").build()));

        TypeMapping typeMapping = new TypeMapping.Builder().properties(propertyMap).build();
        CreateIndexResponse response = client.indices().create(
            new CreateIndexRequest.Builder()
                .index("hotel")
                .mappings(typeMapping)
                .build()
        );
        log.info(response);
    }

    @Test
    void getIndex() throws IOException {
        GetIndexResponse response = client.indices().get(
            new GetIndexRequest.Builder()
                .index("hotel")
                .build()
        );
        log.info(response);
    }

    @Test
    void existIndex() throws IOException {

        BooleanResponse response = client.indices().exists(
            new ExistsRequest.Builder()
                .index("hotel")
                .build()
        );
        log.info(response.value());
    }

    @Test
    void updateIndexMapping() throws IOException {
        PutMappingResponse response = client.indices().putMapping(
            new PutMappingRequest.Builder()
                .index("hotel")
                .properties("test", new Property(new IntegerNumberProperty.Builder().build()))
                .build()
        );
        log.info(response);
    }

    @Test
    void deleteIndex() throws IOException {
        DeleteIndexResponse response = client.indices().delete(
            new DeleteIndexRequest.Builder()
                .index("hotel")
                .build()
        );
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
