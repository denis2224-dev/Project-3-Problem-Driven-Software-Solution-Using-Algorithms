package com.unischeduler.repository;

import com.unischeduler.domain.TimetableVersion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TimetableVersion entity.
 */
@Repository
public interface TimetableVersionRepository extends JpaRepository<TimetableVersion, Long> {
    Optional<TimetableVersion> findFirstBySolverJobIdOrderByCreatedAtDesc(Long solverJobId);

    default Optional<TimetableVersion> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TimetableVersion> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TimetableVersion> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select timetableVersion from TimetableVersion timetableVersion left join fetch timetableVersion.timetable",
        countQuery = "select count(timetableVersion) from TimetableVersion timetableVersion"
    )
    Page<TimetableVersion> findAllWithToOneRelationships(Pageable pageable);

    @Query("select timetableVersion from TimetableVersion timetableVersion left join fetch timetableVersion.timetable")
    List<TimetableVersion> findAllWithToOneRelationships();

    @Query(
        "select timetableVersion from TimetableVersion timetableVersion left join fetch timetableVersion.timetable where timetableVersion.id =:id"
    )
    Optional<TimetableVersion> findOneWithToOneRelationships(@Param("id") Long id);
}
