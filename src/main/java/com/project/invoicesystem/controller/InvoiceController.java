package com.project.invoicesystem.controller;

import com.project.invoicesystem.dto.InvoiceRequestDTO;
import com.project.invoicesystem.dto.InvoiceResponseDTO;
import com.project.invoicesystem.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    /**
     * Creates a new invoice in the system.
     *
     * @param invoiceRequest the data required to create a new invoice.
     * @return a ResponseEntity containing the created invoice details with HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<InvoiceResponseDTO> createInvoice(@RequestBody InvoiceRequestDTO invoiceRequest) {
        InvoiceResponseDTO response = invoiceService.createInvoice(invoiceRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves all invoices in the system.
     *
     * @return a ResponseEntity containing a list of all invoices with HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<InvoiceResponseDTO>> getInvoices() {
        List<InvoiceResponseDTO> response = invoiceService.getInvoices();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Pays an invoice by updating its status with the specified payment amount.
     *
     * @param id the ID of the invoice to be paid.
     * @param paymentRequest a map containing the payment amount.
     * @return a ResponseEntity containing the updated invoice details with HTTP status 200 (OK).
     */
    @PostMapping("/{id}/payments")
    public ResponseEntity<InvoiceResponseDTO> payInvoice(@PathVariable Long id, @RequestBody Map<String, Double> paymentRequest) {
        double amount = paymentRequest.get("amount");
        InvoiceResponseDTO response = invoiceService.payInvoice(id, amount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Processes overdue invoices and applies late fees based on the overdue days.
     *
     * @param request a map containing the late fee and the number of overdue days.
     * @return a ResponseEntity with HTTP status 204 (No Content) after processing the overdue invoices.
     */
    @PostMapping("/process-overdue")
    public ResponseEntity<Void> processOverdue(@RequestBody Map<String, Object> request) {
        double lateFee = (double) request.get("late_fee");
        int overdueDays = (int) request.get("overdue_days");
        invoiceService.processOverdueInvoices(lateFee, overdueDays);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
