package com.zhangmia.hotel.service.impl;

import com.zhangmia.hotel.mapper.HotelMapper;
import com.zhangmia.hotel.pojo.Hotel;
import com.zhangmia.hotel.service.IHotelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

@Service
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {
}
