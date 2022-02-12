package com.example.streaming;

import com.example.dao.MessageLogRepo;
import com.example.dao.PayloadStoreRepo;
import com.example.docs.MessageLog;
import com.example.docs.StockStore;
import com.example.event.StockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;

@Slf4j
@Lazy
@Component
@RequiredArgsConstructor
public class EventHandlerAdapter {

    private static final Random RAN = new Random();
    private final PayloadStoreRepo payloadStoreRepo;
    private final MessageLogRepo messageLogRepo;
    private final StreamBridge bridge;

    private static float newPrice(float initPrice) {
        return initPrice * (RAN.nextFloat() * 5F);
    }

    @Bean
    @Transactional
    public Consumer<Message<StockEvent>> priceCalculator() {
        return event -> {
            var messageId = event.getHeaders().getId();
            if (Objects.nonNull(messageId) && !messageLogRepo.existsById(messageId)) {
                var payload = event.getPayload();
                log.info("PRICE CALCULATOR :: STOCK ==> {} NEW PRICE ==> {}", payload.getStock(), payload.getPrice());
                float newPrice = newPrice(RAN.nextInt() * 100.9F);
                payload.setPrice(newPrice);
                bridge.send(BindingConstants.stockMarketOut, payload);
                // Marked message is processed
                messageLogRepo.save(
                        MessageLog.builder()
                                .id(messageId)
                                .receivedAt(LocalDateTime.now())
                                .build()
                );
            }
        };
    }

    @Bean
    @Transactional
    public Consumer<Message<StockEvent>> stockMarket() {
        return event -> {
            var messageId = event.getHeaders().getId();
            if (Objects.nonNull(messageId) && !messageLogRepo.existsById(messageId)) {
                var payload = event.getPayload();
                log.info("STOCK MARKET :: STOCK ==> {} NEW PRICE ==> {}", payload.getStock(), payload.getPrice());
                payloadStoreRepo.save(
                        StockStore.builder()
                                .stock(payload.getStock())
                                .price(payload.getPrice())
                                .receivedAt(LocalDateTime.now())
                                .build()
                );
                // Marked message is processed
                messageLogRepo.save(
                        MessageLog.builder()
                                .id(messageId)
                                .receivedAt(LocalDateTime.now())
                                .build()
                );
            }
        };
    }
}