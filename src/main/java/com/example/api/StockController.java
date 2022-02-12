package com.example.api;

import com.example.dao.PayloadStoreRepo;
import com.example.docs.StockStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final PayloadStoreRepo payloadStoreRepo;

    @GetMapping
    public List<StockStore> stocks() {
        return payloadStoreRepo.findAllByOrderByReceivedAtDesc();
    }
}
