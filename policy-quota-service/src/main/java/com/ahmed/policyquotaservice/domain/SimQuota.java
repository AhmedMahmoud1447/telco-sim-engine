package com.ahmed.policyquotaservice.domain;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimQuota implements Serializable {
    private static final long serialVersionUID = 1L;

    private String msisdn;
    private Long totalQuotaBytes;
    private Long remainingBytes;
}