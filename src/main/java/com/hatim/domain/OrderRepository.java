package com.hatim.domain;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Hatim on 2017/5/6.
 */
public interface OrderRepository extends MongoRepository<Order, ObjectId> {

    /**
     * 按条件查询
     * @param account
     * @param orderNo
     * @return
     */
    Order findByAccountAndOrderNo(String account, String orderNo);
}
