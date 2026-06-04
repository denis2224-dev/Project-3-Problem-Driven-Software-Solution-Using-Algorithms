package com.unischeduler.repository;

import com.unischeduler.domain.ScheduleConflict;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ScheduleConflict entity.
 */
@Repository
public interface ScheduleConflictRepository extends JpaRepository<ScheduleConflict, Long> {
    default Optional<ScheduleConflict> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ScheduleConflict> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ScheduleConflict> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select scheduleConflict from ScheduleConflict scheduleConflict left join fetch scheduleConflict.timetableVersion",
        countQuery = "select count(scheduleConflict) from ScheduleConflict scheduleConflict"
    )
    Page<ScheduleConflict> findAllWithToOneRelationships(Pageable pageable);

    @Query("select scheduleConflict from ScheduleConflict scheduleConflict left join fetch scheduleConflict.timetableVersion")
    List<ScheduleConflict> findAllWithToOneRelationships();

    @Query(
        "select scheduleConflict from ScheduleConflict scheduleConflict left join fetch scheduleConflict.timetableVersion where scheduleConflict.id =:id"
    )
    Optional<ScheduleConflict> findOneWithToOneRelationships(@Param("id") Long id);
}
