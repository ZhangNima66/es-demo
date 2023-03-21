package com.zhangnima.hotel.mq;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhangnima.hotel.constants.HotelMqConstants;
import com.zhangnima.hotel.service.IHotelService;

/**
 * @author ZhangNima
 */
@Component
public class HotelListener {

    @Autowired
    private IHotelService hotelService;

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(HotelMqConstants.INSERT_QUEUE_NAME),
        exchange = @Exchange(value = HotelMqConstants.EXCHANGE_NAME),
        key = HotelMqConstants.INSERT_KEY
    ))
    public void insert(Long hotelId) throws IOException {
        hotelService.insertDoc(hotelId);
    }

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(HotelMqConstants.DELETE_QUEUE_NAME),
        exchange = @Exchange(value = HotelMqConstants.EXCHANGE_NAME),
        key = HotelMqConstants.DELETE_KEY
    ))
    public void delete(Long hotelId) throws IOException {
        hotelService.deleteDoc(hotelId);
    }


}
