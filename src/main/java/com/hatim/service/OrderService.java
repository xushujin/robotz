package com.hatim.service;

/**
 * 绑定订单
 * Created by Hatim on 2017/4/24.
 */
public interface OrderService {

    /**
     * 订单绑定
     *
     * @param userId  用户ID
     * @param orderNo 订单号
     * @return
     */
    String orderBinding(String userId, String orderNo);

    /**
     * 订单解绑
     *
     * @param userId  用户ID
     * @param orderNo 订单号
     * @return
     */
    String orderUnBinding(String userId, String orderNo);
}
