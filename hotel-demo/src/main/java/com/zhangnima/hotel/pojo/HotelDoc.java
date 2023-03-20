package com.zhangnima.hotel.pojo;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import co.elastic.clients.json.JsonpDeserializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HotelDoc {
    private Long id;
    private String name;
    private String address;
    private Integer price;
    private Integer score;
    private String brand;
    private String city;
    @JsonProperty("star_name")
    private String starName;
    private String business;
    private String location;
    private String pic;
    private Object distance;
    private List<String> suggestion;

    public HotelDoc(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.address = hotel.getAddress();
        this.price = hotel.getPrice();
        this.score = hotel.getScore();
        this.brand = hotel.getBrand();
        this.city = hotel.getCity();
        this.starName = hotel.getStarName();
        this.business = hotel.getBusiness();
        this.location = hotel.getLatitude() + ", " + hotel.getLongitude();
        this.pic = hotel.getPic();

        String regex = "[\",，、/\\\\]";
        String[] split = this.business.split(regex);
        List<String> suggestion = new ArrayList<>();
        suggestion.add(this.brand);
        Collections.addAll(suggestion, split);
        this.suggestion = suggestion;
    }

}
