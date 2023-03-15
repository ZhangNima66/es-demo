package com.zhangnima.hotel.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.zhangnima.hotel.pojo.Hotel;

@SpringBootTest
class HotelServiceImplTest {

    @Autowired
    private HotelServiceImpl hotelService;

    @Test
    void getOne() {
        Hotel hotel = hotelService.getById(38812);
        System.out.println(hotel);
    }
}