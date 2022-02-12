package com.example.dao;

import com.example.docs.StockStore;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayloadStoreRepo extends MongoRepository<StockStore, ObjectId> {

    List<StockStore> findAllByOrderByReceivedAtDesc();
}