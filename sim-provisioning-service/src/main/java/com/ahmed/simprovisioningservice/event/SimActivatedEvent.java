package com.ahmed.simprovisioningservice.event;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimActivatedEvent {
    private String iccid;
    private String msisdn;
    private String imsi;
    private LocalDateTime activationTimestamp;
}