package com.project.invoicesystem.entity;


import com.project.invoicesystem.constants.InvoiceStatusConstants;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private double amount;
    private double paidAmount;
    private LocalDate dueDate;
    private String status;

    public Invoice(double amount, LocalDate dueDate) {
        this.amount = amount;
        this.paidAmount = 0;
        this.dueDate = dueDate;
        this.status = InvoiceStatusConstants.PENDING;
    }
}
