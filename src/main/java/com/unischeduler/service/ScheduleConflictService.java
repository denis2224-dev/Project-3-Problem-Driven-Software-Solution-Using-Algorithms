package com.unischeduler.service;

import com.unischeduler.domain.ScheduleConflict;
import com.unischeduler.repository.ScheduleConflictRepository;
import com.unischeduler.service.dto.ScheduleConflictDTO;
import com.unischeduler.service.mapper.ScheduleConflictMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.unischeduler.domain.ScheduleConflict}.
 */
@Service
@Transactional
public class ScheduleConflictService {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleConflictService.class);

    private final ScheduleConflictRepository scheduleConflictRepository;

    private final ScheduleConflictMapper scheduleConflictMapper;

    public ScheduleConflictService(ScheduleConflictRepository scheduleConflictRepository, ScheduleConflictMapper scheduleConflictMapper) {
        this.scheduleConflictRepository = scheduleConflictRepository;
        this.scheduleConflictMapper = scheduleConflictMapper;
    }

    /**
     * Save a scheduleConflict.
     *
     * @param scheduleConflictDTO the entity to save.
     * @return the persisted entity.
     */
    public ScheduleConflictDTO save(ScheduleConflictDTO scheduleConflictDTO) {
        LOG.debug("Request to save ScheduleConflict : {}", scheduleConflictDTO);
        ScheduleConflict scheduleConflict = scheduleConflictMapper.toEntity(scheduleConflictDTO);
        scheduleConflict = scheduleConflictRepository.save(scheduleConflict);
        return scheduleConflictMapper.toDto(scheduleConflict);
    }

    /**
     * Update a scheduleConflict.
     *
     * @param scheduleConflictDTO the entity to save.
     * @return the persisted entity.
     */
    public ScheduleConflictDTO update(ScheduleConflictDTO scheduleConflictDTO) {
        LOG.debug("Request to update ScheduleConflict : {}", scheduleConflictDTO);
        ScheduleConflict scheduleConflict = scheduleConflictMapper.toEntity(scheduleConflictDTO);
        scheduleConflict = scheduleConflictRepository.save(scheduleConflict);
        return scheduleConflictMapper.toDto(scheduleConflict);
    }

    /**
     * Partially update a scheduleConflict.
     *
     * @param scheduleConflictDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ScheduleConflictDTO> partialUpdate(ScheduleConflictDTO scheduleConflictDTO) {
        LOG.debug("Request to partially update ScheduleConflict : {}", scheduleConflictDTO);

        return scheduleConflictRepository
            .findById(scheduleConflictDTO.getId())
            .map(existingScheduleConflict -> {
                scheduleConflictMapper.partialUpdate(existingScheduleConflict, scheduleConflictDTO);

                return existingScheduleConflict;
            })
            .map(scheduleConflictRepository::save)
            .map(scheduleConflictMapper::toDto);
    }

    /**
     * Get all the scheduleConflicts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ScheduleConflictDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ScheduleConflicts");
        return scheduleConflictRepository.findAll(pageable).map(scheduleConflictMapper::toDto);
    }

    /**
     * Get all the scheduleConflicts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ScheduleConflictDTO> findAllWithEagerRelationships(Pageable pageable) {
        return scheduleConflictRepository.findAllWithEagerRelationships(pageable).map(scheduleConflictMapper::toDto);
    }

    /**
     * Get one scheduleConflict by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ScheduleConflictDTO> findOne(Long id) {
        LOG.debug("Request to get ScheduleConflict : {}", id);
        return scheduleConflictRepository.findOneWithEagerRelationships(id).map(scheduleConflictMapper::toDto);
    }

    /**
     * Delete the scheduleConflict by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ScheduleConflict : {}", id);
        scheduleConflictRepository.deleteById(id);
    }
}
