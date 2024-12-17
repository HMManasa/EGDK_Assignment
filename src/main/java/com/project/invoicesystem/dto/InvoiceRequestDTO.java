package com.project.invoicesystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRequestDTO {

    private double amount;
    @JsonProperty("due_date")
    private LocalDate dueDate;
}
