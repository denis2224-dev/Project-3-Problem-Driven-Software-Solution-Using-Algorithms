package com.unischeduler.repository;

import com.unischeduler.domain.StudentGroup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StudentGroup entity.
 */
@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    default Optional<StudentGroup> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StudentGroup> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StudentGroup> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select studentGroup from StudentGroup studentGroup left join fetch studentGroup.department",
        countQuery = "select count(studentGroup) from StudentGroup studentGroup"
    )
    Page<StudentGroup> findAllWithToOneRelationships(Pageable pageable);

    @Query("select studentGroup from StudentGroup studentGroup left join fetch studentGroup.department")
    List<StudentGroup> findAllWithToOneRelationships();

    @Query("select studentGroup from StudentGroup studentGroup left join fetch studentGroup.department where studentGroup.id =:id")
    Optional<StudentGroup> findOneWithToOneRelationships(@Param("id") Long id);
}
