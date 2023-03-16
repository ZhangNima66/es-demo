package com.zhangnima.hotel.service;

import java.io.IOException;

import com.baomidou.mybatisplus.extension.service.IService;

import com.zhangnima.hotel.pojo.Hotel;
import com.zhangnima.hotel.pojo.requset.PageRequest;
import com.zhangnima.hotel.pojo.result.PageResult;

/**
 * @author ZhangNima
 */
public interface IHotelService extends IService<Hotel> {

    /**
     * 搜索
     *
     * @param request 请求信息
     * @return 搜索结果
     * @throws IOException 异常
     */
    PageResult search(PageRequest request) throws IOException;
}
