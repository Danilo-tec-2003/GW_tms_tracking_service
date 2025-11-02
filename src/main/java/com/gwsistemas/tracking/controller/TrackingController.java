package com.gwsistemas.tracking.controller;

import com.gwsistemas.tracking.dto.input.OccurrenceCreateDTO;
import com.gwsistemas.tracking.dto.input.OrderCreateDTO;
import com.gwsistemas.tracking.dto.output.OccurrenceDTO;
import com.gwsistemas.tracking.dto.output.OrderDetailsDTO;
import com.gwsistemas.tracking.service.TrackingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    /**
     * Endpoint para Consultar Status Atual e Timeline.
     * Mapeado para: GET /api/orders/{trackingCode}
     */
    @GetMapping("/{trackingCode}")
    public ResponseEntity<OrderDetailsDTO> getTrackingDetails(
            @PathVariable String trackingCode) {

        OrderDetailsDTO dto = trackingService.getTrackingDetails(trackingCode);
        return ResponseEntity.ok(dto);
    }

    /**
     * Endpoint para Registrar nova Ocorrência.
     * Mapeado para: POST /api/orders/{trackingCode}/events
     */
    @PostMapping("/{trackingCode}/events")
    public ResponseEntity<OccurrenceDTO> registerNewOccurrence(
            @PathVariable String trackingCode,
            @Valid @RequestBody OccurrenceCreateDTO dto) {

        OccurrenceDTO newOccurrence = trackingService.registerNewOccurrence(trackingCode, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOccurrence);
    }

    /**
     * Endpoint para Cadastrar uma nova Encomenda.
     * Mapeado para: POST /api/orders
     */
    @PostMapping
    public ResponseEntity<OrderDetailsDTO> createNewOrder(
            @Valid @RequestBody OrderCreateDTO dto) {

        OrderDetailsDTO newOrder = trackingService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }
}
