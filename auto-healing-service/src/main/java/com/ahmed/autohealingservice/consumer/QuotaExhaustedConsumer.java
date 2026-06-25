package com.ahmed.autohealingservice.consumer;

import com.ahmed.autohealingservice.dto.QuotaExhaustedEvent;
import com.ahmed.autohealingservice.service.AutoHealingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuotaExhaustedConsumer {

    private final AutoHealingService autoHealingService;
    private static final String EXHAUSTED_TOPIC = "quota-exhausted-topic";

    @KafkaListener(topics = EXHAUSTED_TOPIC, groupId = "auto-healing-group")
    public void consumeQuotaExhaustedEvent(QuotaExhaustedEvent event) {
        log.warn("Auto-Healing Engine detected quota exhaustion for MSISDN: {} at {}",
                event.getMsisdn(), event.getTimestamp());

        autoHealingService.executeAutoHealingAction(event.getMsisdn());
    }
}