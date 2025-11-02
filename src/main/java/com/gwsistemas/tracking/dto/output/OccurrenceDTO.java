package com.gwsistemas.tracking.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gwsistemas.tracking.enums.TrackingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO usado para retornar informações de uma ocorrência de encomenda.
 * Contém o status e a data/hora da ocorrência.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OccurrenceDTO {

    private TrackingStatus status;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime occurrenceTimestamp;

}
