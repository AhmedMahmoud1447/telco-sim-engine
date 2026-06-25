package com.ahmed.telemetryservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageEvent {
    private String msisdn;
    private Long bytesUsed;
    private String usageType;
}