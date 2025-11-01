package com.gwsistemas.tracking.model;

import com.gwsistemas.tracking.enums.TrackingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "order")
@Entity
@Table(name = "tb_occurrence")
public class Occurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrackingStatus status;

    @Column(nullable = false)
    private LocalDateTime occurrenceTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

}
