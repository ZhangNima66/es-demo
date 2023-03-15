package com.zhangnima.hotel.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhangnima.hotel.pojo.Hotel;

@Mapper
public interface HotelMapper extends BaseMapper<Hotel> {
}
