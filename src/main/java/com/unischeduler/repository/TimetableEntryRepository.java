package com.unischeduler.repository;

import com.unischeduler.domain.TimetableEntry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TimetableEntry entity.
 */
@Repository
public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {
    default Optional<TimetableEntry> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TimetableEntry> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TimetableEntry> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select timetableEntry from TimetableEntry timetableEntry left join fetch timetableEntry.timetableVersion left join fetch timetableEntry.room left join fetch timetableEntry.timeslot",
        countQuery = "select count(timetableEntry) from TimetableEntry timetableEntry"
    )
    Page<TimetableEntry> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select timetableEntry from TimetableEntry timetableEntry left join fetch timetableEntry.timetableVersion left join fetch timetableEntry.room left join fetch timetableEntry.timeslot"
    )
    List<TimetableEntry> findAllWithToOneRelationships();

    @Query(
        "select timetableEntry from TimetableEntry timetableEntry left join fetch timetableEntry.timetableVersion left join fetch timetableEntry.room left join fetch timetableEntry.timeslot where timetableEntry.id =:id"
    )
    Optional<TimetableEntry> findOneWithToOneRelationships(@Param("id") Long id);
}
