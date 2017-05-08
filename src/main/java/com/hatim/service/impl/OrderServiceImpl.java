package com.hatim.service.impl;

import com.hatim.domain.Order;
import com.hatim.domain.OrderRepository;
import com.hatim.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Hatim on 2017/5/6.
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    /**
     * 订单绑定
     *
     * @param account 用户账号
     * @param orderNo 订单号
     * @return
     */
    @Override
    public boolean orderBinding(String account, String orderNo) {
        Order order = new Order();
        order.setAccount(account);
        order.setOrderNo(orderNo);
        order.setCreateDate(new Date());
        order = orderRepository.save(order);
        if (order.getId() == null) {
            return false;
        }
        return true;
    }

    /**
     * 订单解绑
     *
     * @param account 用户账号
     * @param orderNo 订单号
     * @return
     */
    @Override
    public boolean orderUnBinding(String account, String orderNo) {
        Order order = orderRepository.findByAccountAndOrderNo(account, orderNo);
        if(order != null && order.getId() != null){
            orderRepository.delete(order);
        }
        return true;
    }
}
