package com.zhangnima.hotel.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zhangnima.hotel.mapper.HotelMapper;
import com.zhangnima.hotel.pojo.Hotel;
import com.zhangnima.hotel.service.IHotelService;

@Service
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {
}
