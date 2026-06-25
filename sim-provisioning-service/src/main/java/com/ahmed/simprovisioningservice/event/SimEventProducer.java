package com.ahmed.simprovisioningservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimEventProducer {

    private static final String TOPIC = "sim-activation-topic";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendSimActivatedEvent(SimActivatedEvent event) {
        log.info("Sending SIM activation event for MSISDN: {}", event.getMsisdn());

        kafkaTemplate.send(TOPIC, event.getMsisdn(), event);
    }
}