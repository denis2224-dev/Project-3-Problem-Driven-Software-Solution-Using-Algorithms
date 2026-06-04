package com.unischeduler.repository;

import com.unischeduler.domain.SolverJob;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SolverJob entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SolverJobRepository extends JpaRepository<SolverJob, Long> {}
