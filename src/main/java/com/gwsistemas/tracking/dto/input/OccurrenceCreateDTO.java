package com.gwsistemas.tracking.dto.input;

import com.gwsistemas.tracking.enums.TrackingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OccurrenceCreateDTO {

    @NotNull(message = "Status do pedido nao pode estar em branco")
    private TrackingStatus status;

}
