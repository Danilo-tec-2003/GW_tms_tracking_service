package com.gwsistemas.tracking.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsDTO {

    private String trackingCode;

    private String customerName;

    private String deliveryAddress;

    private List<OccurrenceDTO> occurrences;

}
