package com.expensify.expensify.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfoRequest {

    private String policyName;
    private String ownerEmail;



}
