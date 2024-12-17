package com.project.invoicesystem.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponseDTO {

    private Long id;
    private double amount;
    private double paidAmount;
    private LocalDate dueDate;
    private String status;
}
