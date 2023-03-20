package com.zhangnima.hotel.rest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhangnima.hotel.pojo.requset.PageRequest;
import com.zhangnima.hotel.pojo.result.PageResult;
import com.zhangnima.hotel.service.IHotelService;

/**
 * @author ZhangNima
 */
@RestController
@RequestMapping("/hotel")
public class HotelController {

    @Autowired
    private IHotelService hotelService;

    @PostMapping("/list")
    public PageResult query(@RequestBody @Valid PageRequest request) throws IOException {
        return hotelService.search(request);
    }

    @PostMapping("/filters")
    public Map<String, List<String>> filters() throws IOException {
        return hotelService.filters();
    }

    @GetMapping("/suggestion")
    public List<String> suggestion(String key) throws IOException {
        return hotelService.suggestion(key);
    }

}
