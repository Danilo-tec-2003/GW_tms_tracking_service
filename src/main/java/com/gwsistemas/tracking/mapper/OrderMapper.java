package com.gwsistemas.tracking.mapper;

import com.gwsistemas.tracking.dto.input.OrderCreateDTO;
import com.gwsistemas.tracking.dto.output.OrderDetailsDTO;
import com.gwsistemas.tracking.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {OccurrenceMapper.class})
public interface OrderMapper {

    //DTO -> Entity (input)
    Order toEntity(OrderCreateDTO dto);

    //Entity -> DTO (output)
    OrderDetailsDTO toDetailsDTO(Order order);
}
