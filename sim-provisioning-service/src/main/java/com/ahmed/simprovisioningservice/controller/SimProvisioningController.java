package com.ahmed.simprovisioningservice.controller;

import com.ahmed.simprovisioningservice.domain.SimCard;
import com.ahmed.simprovisioningservice.service.SimProvisioningService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sims")
@RequiredArgsConstructor
public class SimProvisioningController {

    private final SimProvisioningService simProvisioningService;

    @PostMapping("/activate")
    public ResponseEntity<SimCard> activateSim(@RequestBody ActivationRequest request) {
        SimCard activatedSim = simProvisioningService.activateSim(
                request.getIccid(),
                request.getMsisdn(),
                request.getImsi()
        );
        return ResponseEntity.ok(activatedSim);
    }
}

@Data
class ActivationRequest {
    private String iccid;
    private String msisdn;
    private String imsi;
}