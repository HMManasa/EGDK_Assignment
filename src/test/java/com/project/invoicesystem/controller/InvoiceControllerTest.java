package com.project.invoicesystem.controller;

import com.project.invoicesystem.constants.InvoiceStatusConstants;
import com.project.invoicesystem.dto.InvoiceRequestDTO;
import com.project.invoicesystem.dto.InvoiceResponseDTO;
import com.project.invoicesystem.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class InvoiceControllerTest {

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateInvoice() {
        InvoiceRequestDTO requestDTO = new InvoiceRequestDTO();
        requestDTO.setAmount(100.0);
        requestDTO.setDueDate(LocalDate.of(2023, 12, 31));

        InvoiceResponseDTO responseDTO = new InvoiceResponseDTO(1L, 100.0, 0.0, LocalDate.of(2023, 12, 31), InvoiceStatusConstants.PENDING);

        when(invoiceService.createInvoice(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<InvoiceResponseDTO> response = invoiceController.createInvoice(requestDTO);

        assertNotNull(response);
        assertNotNull(response.getBody(), "Response body is null");
        assertEquals(201, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getId());
        assertEquals(InvoiceStatusConstants.PENDING, response.getBody().getStatus());
    }

    @Test
    void testGetInvoices() {
        InvoiceResponseDTO responseDTO = new InvoiceResponseDTO(1L, 100.0, 0.0, LocalDate.of(2023, 12, 31), InvoiceStatusConstants.PENDING);

        when(invoiceService.getInvoices()).thenReturn(List.of(responseDTO));

        ResponseEntity<List<InvoiceResponseDTO>> response = invoiceController.getInvoices();

        assertNotNull(response);
        assertNotNull(response.getBody(), "Response body is null");
        assertEquals(200, response.getStatusCode().value());
        assertFalse(response.getBody().isEmpty(), "Response body is empty");
    }

    @Test
    void testPayInvoice() {
        InvoiceResponseDTO responseDTO = new InvoiceResponseDTO(1L, 100.0, 100.0, LocalDate.of(2023, 12, 31), InvoiceStatusConstants.PAID);

        when(invoiceService.payInvoice(1L, 100.0)).thenReturn(responseDTO);

        ResponseEntity<InvoiceResponseDTO> response = invoiceController.payInvoice(1L, Map.of("amount", 100.0));

        assertNotNull(response);
        assertNotNull(response.getBody(), "Response body is null");
        assertEquals(200, response.getStatusCode().value());
        assertEquals(InvoiceStatusConstants.PAID, response.getBody().getStatus());
        assertEquals(100.0, response.getBody().getPaidAmount());
    }

    @Test
    void testProcessOverdue() {
        Map<String, Object> request = Map.of("late_fee", 50.0, "overdue_days", 30);
        ResponseEntity<Void> response = invoiceController.processOverdue(request);
        assert response.getStatusCode() == HttpStatus.NO_CONTENT;
    }
}
