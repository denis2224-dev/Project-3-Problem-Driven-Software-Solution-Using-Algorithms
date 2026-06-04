package com.unischeduler.repository;

import com.unischeduler.domain.ProfessorPreference;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfessorPreference entity.
 */
@Repository
public interface ProfessorPreferenceRepository extends JpaRepository<ProfessorPreference, Long> {
    default Optional<ProfessorPreference> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ProfessorPreference> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ProfessorPreference> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select professorPreference from ProfessorPreference professorPreference left join fetch professorPreference.professor left join fetch professorPreference.timeslot",
        countQuery = "select count(professorPreference) from ProfessorPreference professorPreference"
    )
    Page<ProfessorPreference> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select professorPreference from ProfessorPreference professorPreference left join fetch professorPreference.professor left join fetch professorPreference.timeslot"
    )
    List<ProfessorPreference> findAllWithToOneRelationships();

    @Query(
        "select professorPreference from ProfessorPreference professorPreference left join fetch professorPreference.professor left join fetch professorPreference.timeslot where professorPreference.id =:id"
    )
    Optional<ProfessorPreference> findOneWithToOneRelationships(@Param("id") Long id);
}
