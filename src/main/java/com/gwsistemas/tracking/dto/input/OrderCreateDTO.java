package com.gwsistemas.tracking.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO usado para criar uma nova encomenda.
 * Contém código de rastreamento, nome do cliente e endereço de entrega.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateDTO {

    @NotBlank(message = "O código de rastreamento não pode estar em branco")
    @Size(min = 10, max = 30, message = "O código de rastreamento deve ter entre 10 e 30 caracteres")
    private String trackingCode;

    @NotBlank(message = "O nome do cliente não pode estar em branco")
    private String customerName;

    @NotBlank(message = "O endereço de entrega não pode estar em branco")
    private String deliveryAddress;

}
