package com.ahmed.simprovisioningservice.repository;

import com.ahmed.simprovisioningservice.domain.SimCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SimCardRepository extends JpaRepository<SimCard, Long> {
    Optional<SimCard> findByIccid(String iccid);
    Optional<SimCard> findByMsisdn(String msisdn);
    boolean existsByMsisdn(String msisdn);
}