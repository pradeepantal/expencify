package com.expensify.expensify.repo;

import com.expensify.expensify.model.Info;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AccountingInfo extends MongoRepository<Info, String> {
}
