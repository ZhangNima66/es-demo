package com.zhangnima.hotel.pojo.requset;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author ZhangNima
 */
@Data
public class PageRequest {
    private String key;
    @NotNull
    private Integer page;
    @NotNull
    private Integer size;
    private String sortBy;
    private String city;
    private String brand;
    private String starName;
    private Integer minPrice;
    private Integer maxPrice;
    private String location;
}
