package com.ahmed.policyquotaservice.consumer;

import com.ahmed.policyquotaservice.dto.UsageEvent;
import com.ahmed.policyquotaservice.service.PolicyQuotaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelemetryEventConsumer {

    private final PolicyQuotaService policyQuotaService;
    private static final String TELEMETRY_TOPIC = "network-usage-topic";

    @KafkaListener(topics = TELEMETRY_TOPIC, groupId = "policy-quota-group")
    public void consumeTelemetryData(UsageEvent event) {
        log.info("Policy Quota Service received usage event from Kafka for MSISDN: {}", event.getMsisdn());

        policyQuotaService.consumeQuota(event.getMsisdn(), event.getBytesUsed());
    }
}