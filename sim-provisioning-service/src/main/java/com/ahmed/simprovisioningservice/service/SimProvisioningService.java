package com.ahmed.simprovisioningservice.service;

import com.ahmed.simprovisioningservice.domain.SimCard;
import com.ahmed.simprovisioningservice.domain.SimStatus;
import com.ahmed.simprovisioningservice.repository.SimCardRepository;
import com.ahmed.simprovisioningservice.event.SimActivatedEvent;
import com.ahmed.simprovisioningservice.event.SimEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimProvisioningService {

    private final SimCardRepository simCardRepository;
    private final StringRedisTemplate redisTemplate;
    private final SimEventProducer simEventProducer;

    private static final String LOCK_PREFIX = "lock:msisdn:";

    @Transactional
    public SimCard activateSim(String iccid, String msisdn, String imsi) {
        String lockKey = LOCK_PREFIX + msisdn;

        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", Duration.ofSeconds(10));

        if (Boolean.FALSE.equals(isLocked)) {
            throw new IllegalStateException("This number is currently being processed. Please try again later.");
        }

        try {
            log.info("Successfully locked the number: {}", msisdn);

            if (simCardRepository.existsByMsisdn(msisdn)) {
                throw new IllegalArgumentException("The MSISDN " + msisdn + " is already associated with another SIM card.");
            }

            SimCard simCard = simCardRepository.findByIccid(iccid)
                    .orElseGet(() -> SimCard.builder().iccid(iccid).build());

            simCard.setMsisdn(msisdn);
            simCard.setImsi(imsi);
            simCard.setStatus(SimStatus.ACTIVE);
            simCard.setActivatedAt(LocalDateTime.now());

            SimCard savedSim = simCardRepository.save(simCard);

            SimActivatedEvent event = SimActivatedEvent.builder()
                    .iccid(savedSim.getIccid())
                    .msisdn(savedSim.getMsisdn())
                    .imsi(savedSim.getImsi())
                    .activationTimestamp(savedSim.getActivatedAt())
                    .build();

            simEventProducer.sendSimActivatedEvent(event);

            return savedSim;

        } finally {
            redisTemplate.delete(lockKey);
            log.info("Successfully unlocked the number: {}", msisdn);
        }
    }
}