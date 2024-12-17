package com.project.invoicesystem.service;

import com.project.invoicesystem.constants.InvoiceStatusConstants;
import com.project.invoicesystem.dto.InvoiceRequestDTO;
import com.project.invoicesystem.dto.InvoiceResponseDTO;
import com.project.invoicesystem.entity.Invoice;
import com.project.invoicesystem.mapper.InvoiceMapper;
import com.project.invoicesystem.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    private final InvoiceMapper invoiceMapper = InvoiceMapper.INSTANCE;

    /**
     * Creates a new invoice based on the provided request data.
     *
     * @param requestDTO the data for the new invoice.
     * @return the created invoice details.
     */
    public InvoiceResponseDTO createInvoice(InvoiceRequestDTO requestDTO) {
        Invoice invoice = invoiceMapper.toEntity(requestDTO);
        invoice.setStatus(InvoiceStatusConstants.PENDING);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toDto(savedInvoice);
    }

    /**
     * Retrieves a list of all invoices in the system.
     *
     * @return a list of invoice response DTOs.
     */
    public List<InvoiceResponseDTO> getInvoices() {
        return invoiceRepository.findAll().stream()
                .map(invoiceMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Pays an invoice by updating its paid amount and status if fully paid.
     *
     * @param id the ID of the invoice to be paid.
     * @param amount the amount to pay towards the invoice.
     * @return the updated invoice details.
     */
    public InvoiceResponseDTO payInvoice(Long id, double amount) {
        Invoice invoice = findInvoiceById(id);
        validatePendingInvoice(invoice);
        updatePaidAmount(invoice, amount);
        return invoiceMapper.toDto(invoiceRepository.save(invoice));
    }

    /**
     * Processes overdue invoices and applies a late fee if applicable.
     *
     * @param lateFee the fee to apply to overdue invoices.
     * @param overdueDays the number of days after which an invoice is considered overdue.
     */
    public void processOverdueInvoices(double lateFee, int overdueDays) {
        List<Invoice> overdueInvoices = findOverdueInvoices(overdueDays);
        overdueInvoices.forEach(invoice -> processOverdueInvoice(invoice, lateFee));
    }

    // --- Refactored Methods ---
    /**
     * Finds an invoice by its ID.
     *
     * @param id the ID of the invoice to find.
     * @return the found invoice.
     * @throws IllegalArgumentException if the invoice is not found.
     */
    private Invoice findInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
    }

    /**
     * Validates that the invoice is in a pending state.
     *
     * @param invoice the invoice to validate.
     * @throws IllegalArgumentException if the invoice is not pending.
     */
    private void validatePendingInvoice(Invoice invoice) {
        if (!InvoiceStatusConstants.PENDING.equals(invoice.getStatus())) {
            throw new IllegalArgumentException("Invalid or non-pending invoice");
        }
    }

    /**
     * Updates the paid amount of an invoice and changes its status to "PAID" if fully paid.
     *
     * @param invoice the invoice to update.
     * @param amount the amount to pay towards the invoice.
     */
    private void updatePaidAmount(Invoice invoice, double amount) {
        invoice.setPaidAmount(invoice.getPaidAmount() + amount);
        if (invoice.getPaidAmount() >= invoice.getAmount()) {
            invoice.setStatus(InvoiceStatusConstants.PAID);
        }
    }

    /**
     * Finds invoices that are overdue based on the specified overdue days.
     *
     * @param overdueDays the number of days after which an invoice is considered overdue.
     * @return a list of overdue invoices.
     */
    private List<Invoice> findOverdueInvoices(int overdueDays) {
        LocalDate currentDate = LocalDate.now();
        return invoiceRepository.findAll().stream()
                .filter(invoice -> InvoiceStatusConstants.PENDING.equals(invoice.getStatus())
                        && invoice.getDueDate().plusDays(overdueDays).isBefore(currentDate))
                .collect(Collectors.toList());
    }

    /**
     * Processes an individual overdue invoice by applying the appropriate late fee and updating its status.
     *
     * @param invoice the overdue invoice to process.
     * @param lateFee the late fee to apply.
     */
    private void processOverdueInvoice(Invoice invoice, double lateFee) {
        if (invoice.getPaidAmount() > 0) {
            markAsPaidAndCreateNewInvoice(invoice, lateFee);
        } else {
            markAsVoidAndCreateNewInvoice(invoice, lateFee);
        }
        invoiceRepository.save(invoice);
    }

    /**
     * Marks an invoice as "PAID" and creates a new invoice with the remaining amount and late fee.
     *
     * @param invoice the invoice to mark as paid.
     * @param lateFee the late fee to apply to the new invoice.
     */
    private void markAsPaidAndCreateNewInvoice(Invoice invoice, double lateFee) {
        invoice.setStatus(InvoiceStatusConstants.PAID);
        double remainingAmount = invoice.getAmount() - invoice.getPaidAmount() + lateFee;
        Invoice newInvoice = new Invoice(remainingAmount, LocalDate.now().plusDays(30));
        invoiceRepository.save(newInvoice);
    }

    /**
     * Marks an invoice as "VOID" and creates a new invoice with the updated amount and late fee.
     *
     * @param invoice the invoice to mark as void.
     * @param lateFee the late fee to apply to the new invoice.
     */
    private void markAsVoidAndCreateNewInvoice(Invoice invoice, double lateFee) {
        invoice.setStatus(InvoiceStatusConstants.VOID);
        double newAmount = invoice.getAmount() + lateFee;
        Invoice newInvoice = new Invoice(newAmount, LocalDate.now().plusDays(30));
        invoiceRepository.save(newInvoice);
    }
}
