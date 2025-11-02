package com.gwsistemas.tracking.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa uma encomenda.
 * Contém código de rastreamento, informações do cliente e lista de ocorrências.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "trackingCode")
@ToString(exclude = "occurrences")
@Entity
@Table(name = "tb_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String trackingCode;

    private String customerName;

    private String deliveryAddress;

    //Relação 1:N — Uma encomenda pode ter várias ocorrências
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Occurrence> occurrences = new ArrayList<>();

}
