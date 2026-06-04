package com.unischeduler.service;

import com.unischeduler.domain.StudentGroup;
import com.unischeduler.repository.StudentGroupRepository;
import com.unischeduler.service.dto.StudentGroupDTO;
import com.unischeduler.service.mapper.StudentGroupMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.unischeduler.domain.StudentGroup}.
 */
@Service
@Transactional
public class StudentGroupService {

    private static final Logger LOG = LoggerFactory.getLogger(StudentGroupService.class);

    private final StudentGroupRepository studentGroupRepository;

    private final StudentGroupMapper studentGroupMapper;

    public StudentGroupService(StudentGroupRepository studentGroupRepository, StudentGroupMapper studentGroupMapper) {
        this.studentGroupRepository = studentGroupRepository;
        this.studentGroupMapper = studentGroupMapper;
    }

    /**
     * Save a studentGroup.
     *
     * @param studentGroupDTO the entity to save.
     * @return the persisted entity.
     */
    public StudentGroupDTO save(StudentGroupDTO studentGroupDTO) {
        LOG.debug("Request to save StudentGroup : {}", studentGroupDTO);
        StudentGroup studentGroup = studentGroupMapper.toEntity(studentGroupDTO);
        studentGroup = studentGroupRepository.save(studentGroup);
        return studentGroupMapper.toDto(studentGroup);
    }

    /**
     * Update a studentGroup.
     *
     * @param studentGroupDTO the entity to save.
     * @return the persisted entity.
     */
    public StudentGroupDTO update(StudentGroupDTO studentGroupDTO) {
        LOG.debug("Request to update StudentGroup : {}", studentGroupDTO);
        StudentGroup studentGroup = studentGroupMapper.toEntity(studentGroupDTO);
        studentGroup = studentGroupRepository.save(studentGroup);
        return studentGroupMapper.toDto(studentGroup);
    }

    /**
     * Partially update a studentGroup.
     *
     * @param studentGroupDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StudentGroupDTO> partialUpdate(StudentGroupDTO studentGroupDTO) {
        LOG.debug("Request to partially update StudentGroup : {}", studentGroupDTO);

        return studentGroupRepository
            .findById(studentGroupDTO.getId())
            .map(existingStudentGroup -> {
                studentGroupMapper.partialUpdate(existingStudentGroup, studentGroupDTO);

                return existingStudentGroup;
            })
            .map(studentGroupRepository::save)
            .map(studentGroupMapper::toDto);
    }

    /**
     * Get all the studentGroups.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StudentGroupDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all StudentGroups");
        return studentGroupRepository.findAll(pageable).map(studentGroupMapper::toDto);
    }

    /**
     * Get all the studentGroups with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<StudentGroupDTO> findAllWithEagerRelationships(Pageable pageable) {
        return studentGroupRepository.findAllWithEagerRelationships(pageable).map(studentGroupMapper::toDto);
    }

    /**
     * Get one studentGroup by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StudentGroupDTO> findOne(Long id) {
        LOG.debug("Request to get StudentGroup : {}", id);
        return studentGroupRepository.findOneWithEagerRelationships(id).map(studentGroupMapper::toDto);
    }

    /**
     * Delete the studentGroup by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete StudentGroup : {}", id);
        studentGroupRepository.deleteById(id);
    }
}
