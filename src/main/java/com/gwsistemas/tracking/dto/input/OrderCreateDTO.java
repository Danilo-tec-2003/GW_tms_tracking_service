package com.gwsistemas.tracking.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OrderCreateDTO {

    @NotBlank(message = "Codigo de rastreamento nao pode estr em branco.")
    @Size(min = 10, max = 30, message = "Codigo de rastreamento deve ter entre 10 e 30 caracteres")
    private String trackingCode;

    @NotBlank(message = "Nome do Cliente nao pode estar em branco.")
    private String customerName;

    @NotBlank(message = "Endereco de entrega nao pode estar em branco.")
    private String deliveryAddress;

}
