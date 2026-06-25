package com.ahmed.policyquotaservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuotaEventProducer {

    private static final String TOPIC = "quota-exhausted-topic";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendQuotaExhaustedEvent(QuotaExhaustedEvent event) {
        log.info("Sending quota exhausted event to Kafka for MSISDN: {}", event.getMsisdn());

        kafkaTemplate.send(TOPIC, event.getMsisdn(), event);
    }
}