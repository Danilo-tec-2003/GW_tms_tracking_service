package com.gwsistemas.tracking.mapper;

import com.gwsistemas.tracking.dto.input.OccurrenceCreateDTO;
import com.gwsistemas.tracking.dto.output.OccurrenceDTO;
import com.gwsistemas.tracking.model.Occurrence;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OccurrenceMapper {

    //Entity -> DTO (output)
    OccurrenceDTO toDTO(Occurrence occurrence);

    //DTO -> Entity (input)
    Occurrence toEntity(OccurrenceCreateDTO dto);
}
