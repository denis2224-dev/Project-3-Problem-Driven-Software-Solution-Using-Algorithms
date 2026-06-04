package com.unischeduler.repository;

import com.unischeduler.domain.ExamScheduleEntry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ExamScheduleEntry entity.
 */
@Repository
public interface ExamScheduleEntryRepository extends JpaRepository<ExamScheduleEntry, Long> {
    default Optional<ExamScheduleEntry> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ExamScheduleEntry> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ExamScheduleEntry> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select examScheduleEntry from ExamScheduleEntry examScheduleEntry left join fetch examScheduleEntry.exam left join fetch examScheduleEntry.room left join fetch examScheduleEntry.timeslot left join fetch examScheduleEntry.timetableVersion",
        countQuery = "select count(examScheduleEntry) from ExamScheduleEntry examScheduleEntry"
    )
    Page<ExamScheduleEntry> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select examScheduleEntry from ExamScheduleEntry examScheduleEntry left join fetch examScheduleEntry.exam left join fetch examScheduleEntry.room left join fetch examScheduleEntry.timeslot left join fetch examScheduleEntry.timetableVersion"
    )
    List<ExamScheduleEntry> findAllWithToOneRelationships();

    @Query(
        "select examScheduleEntry from ExamScheduleEntry examScheduleEntry left join fetch examScheduleEntry.exam left join fetch examScheduleEntry.room left join fetch examScheduleEntry.timeslot left join fetch examScheduleEntry.timetableVersion where examScheduleEntry.id =:id"
    )
    Optional<ExamScheduleEntry> findOneWithToOneRelationships(@Param("id") Long id);
}
