package com.gwsistemas.tracking.dto.input;

import com.gwsistemas.tracking.enums.TrackingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO usado para criar uma nova ocorrência de uma encomenda.
 * Contém apenas o status que será registrado.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OccurrenceCreateDTO {
    @NotNull(message = "O status do pedido não pode ser nulo")
    private TrackingStatus status;

}
