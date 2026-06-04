package com.unischeduler.repository;

import com.unischeduler.domain.CourseEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CourseEvent entity.
 */
@Repository
public interface CourseEventRepository extends JpaRepository<CourseEvent, Long> {
    default Optional<CourseEvent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CourseEvent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CourseEvent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select courseEvent from CourseEvent courseEvent left join fetch courseEvent.course left join fetch courseEvent.professor left join fetch courseEvent.studentGroup",
        countQuery = "select count(courseEvent) from CourseEvent courseEvent"
    )
    Page<CourseEvent> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select courseEvent from CourseEvent courseEvent left join fetch courseEvent.course left join fetch courseEvent.professor left join fetch courseEvent.studentGroup"
    )
    List<CourseEvent> findAllWithToOneRelationships();

    @Query(
        "select courseEvent from CourseEvent courseEvent left join fetch courseEvent.course left join fetch courseEvent.professor left join fetch courseEvent.studentGroup where courseEvent.id =:id"
    )
    Optional<CourseEvent> findOneWithToOneRelationships(@Param("id") Long id);
}
