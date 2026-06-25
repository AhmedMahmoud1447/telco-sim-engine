package com.ahmed.policyquotaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageEvent {
    private String msisdn;
    private Long bytesUsed;
    private String usageType;
}