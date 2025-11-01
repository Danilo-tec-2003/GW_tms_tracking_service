package com.gwsistemas.tracking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gwsistemas.tracking.enums.TrackingStatus;
import java.time.LocalDateTime;

public class OccurrenceDTO {

    private TrackingStatus status;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime occurrenceTimestamp;

}
