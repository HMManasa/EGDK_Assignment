package com.project.invoicesystem.service;

import com.project.invoicesystem.constants.InvoiceStatusConstants;
import com.project.invoicesystem.dto.InvoiceRequestDTO;
import com.project.invoicesystem.dto.InvoiceResponseDTO;
import com.project.invoicesystem.entity.Invoice;
import com.project.invoicesystem.mapper.InvoiceMapper;
import com.project.invoicesystem.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceServiceTest {

    @InjectMocks
    private InvoiceService invoiceService;

    @Mock
    private InvoiceRepository invoiceRepository;

    private final InvoiceMapper invoiceMapper = InvoiceMapper.INSTANCE;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateInvoice() {
        InvoiceRequestDTO requestDTO = new InvoiceRequestDTO();
        requestDTO.setAmount(100.0);
        requestDTO.setDueDate(LocalDate.now().plusDays(10));

        Invoice invoice = new Invoice();
        invoice.setAmount(100.0);
        invoice.setDueDate(LocalDate.now().plusDays(10));
        invoice.setStatus(InvoiceStatusConstants.PENDING);

        Invoice savedInvoice = new Invoice();
        savedInvoice.setId(1L);
        savedInvoice.setAmount(100.0);
        savedInvoice.setDueDate(LocalDate.now().plusDays(10));
        savedInvoice.setStatus(InvoiceStatusConstants.PENDING);

        when(invoiceRepository.save(any(Invoice.class))).thenReturn(savedInvoice);

        InvoiceResponseDTO responseDTO = invoiceService.createInvoice(requestDTO);

        assertNotNull(responseDTO);
        assertEquals(InvoiceStatusConstants.PENDING, responseDTO.getStatus());
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void testGetInvoices() {
        Invoice invoice1 = new Invoice();
        invoice1.setId(1L);
        invoice1.setAmount(100.0);
        invoice1.setPaidAmount(50.0);
        invoice1.setStatus(InvoiceStatusConstants.PENDING);
        invoice1.setDueDate(LocalDate.now().plusDays(10));

        Invoice invoice2 = new Invoice();
        invoice2.setId(2L);
        invoice2.setAmount(200.0);
        invoice2.setPaidAmount(200.0);
        invoice2.setStatus(InvoiceStatusConstants.PAID);
        invoice2.setDueDate(LocalDate.now().plusDays(5));

        when(invoiceRepository.findAll()).thenReturn(Arrays.asList(invoice1, invoice2));

        List<InvoiceResponseDTO> invoices = invoiceService.getInvoices();

        assertNotNull(invoices);
        assertEquals(2, invoices.size());
        verify(invoiceRepository, times(1)).findAll();
    }

    @Test
    void testPayInvoice() {
        Long invoiceId = 1L;
        double paymentAmount = 50.0;

        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setAmount(100.0);
        invoice.setPaidAmount(0.0);
        invoice.setStatus(InvoiceStatusConstants.PENDING);
        invoice.setDueDate(LocalDate.now().plusDays(10));

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        InvoiceResponseDTO responseDTO = invoiceService.payInvoice(invoiceId, paymentAmount);

        assertNotNull(responseDTO);
        assertEquals(50.0, invoice.getPaidAmount());
        assertEquals(InvoiceStatusConstants.PENDING, invoice.getStatus());
        verify(invoiceRepository, times(1)).save(invoice);
    }

    @Test
    void testPayInvoiceFullyPaid() {
        Long invoiceId = 1L;
        double paymentAmount = 100.0;

        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setAmount(100.0);
        invoice.setPaidAmount(0.0);
        invoice.setStatus(InvoiceStatusConstants.PENDING);
        invoice.setDueDate(LocalDate.now().plusDays(10));

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        InvoiceResponseDTO responseDTO = invoiceService.payInvoice(invoiceId, paymentAmount);

        assertNotNull(responseDTO);
        assertEquals(100.0, invoice.getPaidAmount());
        assertEquals(InvoiceStatusConstants.PAID, invoice.getStatus());
        verify(invoiceRepository, times(1)).save(invoice);
    }

    @Test
    void testProcessOverdueInvoices() {
        Invoice overdueInvoice = new Invoice();
        overdueInvoice.setId(1L);
        overdueInvoice.setAmount(100.0);
        overdueInvoice.setPaidAmount(0.0);
        overdueInvoice.setStatus(InvoiceStatusConstants.PENDING);
        overdueInvoice.setDueDate(LocalDate.now().minusDays(15));

        when(invoiceRepository.findAll()).thenReturn(List.of(overdueInvoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(overdueInvoice);

        invoiceService.processOverdueInvoices(10.0, 10);

        verify(invoiceRepository, times(2)).save(any(Invoice.class)); // Once for the original invoice, once for the new invoice
    }

    @Test
    void testPayInvoiceThrowsExceptionForNonPendingInvoice() {
        Long invoiceId = 1L;
        double paymentAmount = 50.0;

        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setAmount(100.0);
        invoice.setPaidAmount(0.0);
        invoice.setStatus(InvoiceStatusConstants.PAID);
        invoice.setDueDate(LocalDate.now().plusDays(10));

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invoiceService.payInvoice(invoiceId, paymentAmount));

        assertEquals("Invalid or non-pending invoice", exception.getMessage());
    }
}
