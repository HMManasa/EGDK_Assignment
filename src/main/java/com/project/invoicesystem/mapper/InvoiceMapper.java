package com.project.invoicesystem.mapper;

import com.project.invoicesystem.dto.InvoiceRequestDTO;
import com.project.invoicesystem.dto.InvoiceResponseDTO;
import com.project.invoicesystem.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InvoiceMapper {
    InvoiceMapper INSTANCE = Mappers.getMapper(InvoiceMapper.class);
    Invoice toEntity(InvoiceRequestDTO dto);
    InvoiceResponseDTO toDto(Invoice entity);

}
