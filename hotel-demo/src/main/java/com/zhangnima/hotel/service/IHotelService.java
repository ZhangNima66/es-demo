package com.zhangnima.hotel.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    /**
     * 获取过滤条件
     *
     * @return 过滤条件map
     */
    Map<String, List<String>> filters() throws IOException;

    /**
     * 搜索自动补全
     *
     * @param key 搜索内容
     * @return 补全列表
     */
    List<String> suggestion(String key) throws IOException;
}
