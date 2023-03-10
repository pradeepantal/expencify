package com.expensify.expensify.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = " Information from accounting system ")
public class Info {

    private String vendorName;
    private int amountPaid;
    private String invoices;

}