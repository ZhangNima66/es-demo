package com.zhangnima.hotel.pojo.result;

import java.util.List;

import com.zhangnima.hotel.pojo.HotelDoc;

import lombok.Data;

/**
 * @author ZhangNima
 */
@Data
public class PageResult {
    private Long total;
    private List<HotelDoc> hotels;
}
