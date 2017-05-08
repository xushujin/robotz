package com.hatim.domain;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Hatim on 2017/5/6.
 */
public interface MemberRepository extends MongoRepository<Member, ObjectId> {
    /**
     * 用户账号查询用户信息
     * @param account
     * @return
     */
    Member findByAccount(String account);
}
