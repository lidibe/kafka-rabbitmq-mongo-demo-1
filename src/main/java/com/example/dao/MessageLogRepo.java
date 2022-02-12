package com.example.dao;

import com.example.docs.MessageLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageLogRepo extends MongoRepository<MessageLog, UUID> {
}