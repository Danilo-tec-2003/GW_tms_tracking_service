package com.gwsistemas.tracking.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO usado para retornar os detalhes de uma encomenda.
 * Contém informações do pedido e a lista de ocorrências associadas.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsDTO {

    private String trackingCode;

    private String customerName;

    private String deliveryAddress;

    private List<OccurrenceDTO> occurrences;

}
