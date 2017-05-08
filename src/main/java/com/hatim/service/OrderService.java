package com.hatim.service;

/**
 * 订单绑定
 * Created by Hatim on 2017/4/24.
 */
public interface OrderService {

    /**
     * 订单绑定
     *
     * @param account  用户账号
     * @param orderNo 订单号
     * @return
     */
    boolean orderBinding(String account, String orderNo);

    /**
     * 订单解绑
     *
     * @param account  用户账号
     * @param orderNo 订单号
     * @return
     */
    boolean orderUnBinding(String account, String orderNo);
}
