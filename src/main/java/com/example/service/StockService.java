package com.example.service;

import com.example.event.StockEvent;
import com.example.streaming.BindingConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2(topic = "[Stock - Service]")
@Service
@RequiredArgsConstructor
class StockService {

    private static final List<String> stocks = Arrays.asList("ZOOM", "ORCL", "TSLA");
    private static final Random RAN = new Random();
    private final StreamBridge bridge;
    private Map<String, StockEvent> lastTrade;

    private static String randomStock() {
        return stocks.get(RAN.nextInt(stocks.size()));
    }

    @PostConstruct
    public void init() {
        this.lastTrade = stocks.stream()
                .map(stock -> StockEvent.builder().stock(stock).build())
                .collect(Collectors.toMap(StockEvent::getStock, Function.identity()));
    }

    @Scheduled(fixedRate = 1000L)
    @Transactional
    void marketMovement() {
        var event = this.lastTrade.get(randomStock());
        log.info("STREAM TO PRICE CALCULATOR :: STOCK ==> {} NEW PRICE ==> {}", event.getStock(), event.getPrice());
        bridge.send(BindingConstants.priceCalculatorOut, event);
    }
}