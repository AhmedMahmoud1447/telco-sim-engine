package com.ahmed.policyquotaservice.event;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotaExhaustedEvent {
    private String msisdn;
    private LocalDateTime timestamp;
}