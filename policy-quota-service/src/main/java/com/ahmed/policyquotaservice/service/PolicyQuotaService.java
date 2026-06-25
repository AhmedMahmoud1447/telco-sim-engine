package com.ahmed.policyquotaservice.service;

import com.ahmed.policyquotaservice.domain.SimQuota;
import com.ahmed.policyquotaservice.event.QuotaExhaustedEvent;
import com.ahmed.policyquotaservice.event.QuotaEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyQuotaService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final QuotaEventProducer quotaEventProducer;
    private static final String QUOTA_PREFIX = "quota:sim:";

    public void createOrUpdateQuota(String msisdn, Long totalBytes) {
        String key = QUOTA_PREFIX + msisdn;
        SimQuota quota = SimQuota.builder()
                .msisdn(msisdn)
                .totalQuotaBytes(totalBytes)
                .remainingBytes(totalBytes)
                .build();

        redisTemplate.opsForValue().set(key, quota);
        log.info("New quota initialized in Redis for MSISDN: [{}], Total: [{} bytes]", msisdn, totalBytes);
    }

    public void consumeQuota(String msisdn, Long bytesUsed) {
        String key = QUOTA_PREFIX + msisdn;
        SimQuota quota = (SimQuota) redisTemplate.opsForValue().get(key);

        if (quota == null) {
            log.warn("No active quota found in Redis for MSISDN: {}. Creating an initial 1GB quota.", msisdn);
            createOrUpdateQuota(msisdn, 1024 * 1024 * 1024L);
            quota = (SimQuota) redisTemplate.opsForValue().get(key);
        }

        long newRemaining = quota.getRemainingBytes() - bytesUsed;
        quota.setRemainingBytes(Math.max(0, newRemaining));

        redisTemplate.opsForValue().set(key, quota);
        log.info("Quota updated in Redis for MSISDN: [{}], Remaining: [{} bytes]", msisdn, quota.getRemainingBytes());

        if (quota.getRemainingBytes() <= 0) {
            log.warn("ALERT: Quota completely exhausted for MSISDN: {}!", msisdn);

            QuotaExhaustedEvent exhaustedEvent = QuotaExhaustedEvent.builder()
                    .msisdn(msisdn)
                    .timestamp(LocalDateTime.now())
                    .build();

            quotaEventProducer.sendQuotaExhaustedEvent(exhaustedEvent);
        }
    }
}