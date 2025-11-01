package com.gwsistemas.tracking.dto;

import com.gwsistemas.tracking.enums.TrackingStatus;
import jakarta.validation.constraints.NotNull;

public class OccurrenceCreateDTO {

    @NotNull(message = "Status do pedido nao pode estar em branco")
    private TrackingStatus status;

}
