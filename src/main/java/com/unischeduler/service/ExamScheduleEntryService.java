package com.unischeduler.service;

import com.unischeduler.domain.ExamScheduleEntry;
import com.unischeduler.repository.ExamScheduleEntryRepository;
import com.unischeduler.service.dto.ExamScheduleEntryDTO;
import com.unischeduler.service.mapper.ExamScheduleEntryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.unischeduler.domain.ExamScheduleEntry}.
 */
@Service
@Transactional
public class ExamScheduleEntryService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamScheduleEntryService.class);

    private final ExamScheduleEntryRepository examScheduleEntryRepository;

    private final ExamScheduleEntryMapper examScheduleEntryMapper;

    public ExamScheduleEntryService(
        ExamScheduleEntryRepository examScheduleEntryRepository,
        ExamScheduleEntryMapper examScheduleEntryMapper
    ) {
        this.examScheduleEntryRepository = examScheduleEntryRepository;
        this.examScheduleEntryMapper = examScheduleEntryMapper;
    }

    /**
     * Save a examScheduleEntry.
     *
     * @param examScheduleEntryDTO the entity to save.
     * @return the persisted entity.
     */
    public ExamScheduleEntryDTO save(ExamScheduleEntryDTO examScheduleEntryDTO) {
        LOG.debug("Request to save ExamScheduleEntry : {}", examScheduleEntryDTO);
        ExamScheduleEntry examScheduleEntry = examScheduleEntryMapper.toEntity(examScheduleEntryDTO);
        examScheduleEntry = examScheduleEntryRepository.save(examScheduleEntry);
        return examScheduleEntryMapper.toDto(examScheduleEntry);
    }

    /**
     * Update a examScheduleEntry.
     *
     * @param examScheduleEntryDTO the entity to save.
     * @return the persisted entity.
     */
    public ExamScheduleEntryDTO update(ExamScheduleEntryDTO examScheduleEntryDTO) {
        LOG.debug("Request to update ExamScheduleEntry : {}", examScheduleEntryDTO);
        ExamScheduleEntry examScheduleEntry = examScheduleEntryMapper.toEntity(examScheduleEntryDTO);
        examScheduleEntry = examScheduleEntryRepository.save(examScheduleEntry);
        return examScheduleEntryMapper.toDto(examScheduleEntry);
    }

    /**
     * Partially update a examScheduleEntry.
     *
     * @param examScheduleEntryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ExamScheduleEntryDTO> partialUpdate(ExamScheduleEntryDTO examScheduleEntryDTO) {
        LOG.debug("Request to partially update ExamScheduleEntry : {}", examScheduleEntryDTO);

        return examScheduleEntryRepository
            .findById(examScheduleEntryDTO.getId())
            .map(existingExamScheduleEntry -> {
                examScheduleEntryMapper.partialUpdate(existingExamScheduleEntry, examScheduleEntryDTO);

                return existingExamScheduleEntry;
            })
            .map(examScheduleEntryRepository::save)
            .map(examScheduleEntryMapper::toDto);
    }

    /**
     * Get all the examScheduleEntries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ExamScheduleEntryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ExamScheduleEntries");
        return examScheduleEntryRepository.findAll(pageable).map(examScheduleEntryMapper::toDto);
    }

    /**
     * Get all the examScheduleEntries with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ExamScheduleEntryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return examScheduleEntryRepository.findAllWithEagerRelationships(pageable).map(examScheduleEntryMapper::toDto);
    }

    /**
     * Get one examScheduleEntry by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ExamScheduleEntryDTO> findOne(Long id) {
        LOG.debug("Request to get ExamScheduleEntry : {}", id);
        return examScheduleEntryRepository.findOneWithEagerRelationships(id).map(examScheduleEntryMapper::toDto);
    }

    /**
     * Delete the examScheduleEntry by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ExamScheduleEntry : {}", id);
        examScheduleEntryRepository.deleteById(id);
    }
}
