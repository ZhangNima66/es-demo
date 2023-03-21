package com.zhangmia.hotel.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.zhangmia.hotel.pojo.Hotel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface HotelMapper extends BaseMapper<Hotel> {
}
