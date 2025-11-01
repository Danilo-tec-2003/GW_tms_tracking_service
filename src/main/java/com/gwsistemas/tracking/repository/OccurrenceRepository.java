package com.gwsistemas.tracking.repository;

import com.gwsistemas.tracking.model.Occurrence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OccurrenceRepository extends JpaRepository<Occurrence, Long> {

}
