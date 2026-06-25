package com.ahmed.telemetryservice.consumer;

import com.ahmed.telemetryservice.dto.UsageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsageEventConsumer {

    private static final String USAGE_TOPIC = "network-usage-topic";

    @KafkaListener(topics = USAGE_TOPIC, groupId = "telemetry-group")
    public void consumeUsageEvent(UsageEvent event) {
        log.info("Received network usage event for MSISDN: [{}], Type: [{}], Amount: [{} bytes]",
                event.getMsisdn(), event.getUsageType(), event.getBytesUsed());

        if (event.getMsisdn() == null || event.getBytesUsed() <= 0) {
            log.warn("Skipping invalid usage event data.");
            return;
        }

        processTelemetryData(event);
    }

    private void processTelemetryData(UsageEvent event) {
        log.info("Processing telemetry records for MSISDN: {}", event.getMsisdn());
    }
}