package com.hatim.domain;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Hatim on 2017/5/6.
 */
public interface LogRepository extends MongoRepository<Log, ObjectId> {
}
