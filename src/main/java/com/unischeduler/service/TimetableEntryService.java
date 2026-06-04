package com.unischeduler.service;

import com.unischeduler.domain.TimetableEntry;
import com.unischeduler.repository.TimetableEntryRepository;
import com.unischeduler.service.dto.TimetableEntryDTO;
import com.unischeduler.service.mapper.TimetableEntryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.unischeduler.domain.TimetableEntry}.
 */
@Service
@Transactional
public class TimetableEntryService {

    private static final Logger LOG = LoggerFactory.getLogger(TimetableEntryService.class);

    private final TimetableEntryRepository timetableEntryRepository;

    private final TimetableEntryMapper timetableEntryMapper;

    public TimetableEntryService(TimetableEntryRepository timetableEntryRepository, TimetableEntryMapper timetableEntryMapper) {
        this.timetableEntryRepository = timetableEntryRepository;
        this.timetableEntryMapper = timetableEntryMapper;
    }

    /**
     * Save a timetableEntry.
     *
     * @param timetableEntryDTO the entity to save.
     * @return the persisted entity.
     */
    public TimetableEntryDTO save(TimetableEntryDTO timetableEntryDTO) {
        LOG.debug("Request to save TimetableEntry : {}", timetableEntryDTO);
        TimetableEntry timetableEntry = timetableEntryMapper.toEntity(timetableEntryDTO);
        timetableEntry = timetableEntryRepository.save(timetableEntry);
        return timetableEntryMapper.toDto(timetableEntry);
    }

    /**
     * Update a timetableEntry.
     *
     * @param timetableEntryDTO the entity to save.
     * @return the persisted entity.
     */
    public TimetableEntryDTO update(TimetableEntryDTO timetableEntryDTO) {
        LOG.debug("Request to update TimetableEntry : {}", timetableEntryDTO);
        TimetableEntry timetableEntry = timetableEntryMapper.toEntity(timetableEntryDTO);
        timetableEntry = timetableEntryRepository.save(timetableEntry);
        return timetableEntryMapper.toDto(timetableEntry);
    }

    /**
     * Partially update a timetableEntry.
     *
     * @param timetableEntryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TimetableEntryDTO> partialUpdate(TimetableEntryDTO timetableEntryDTO) {
        LOG.debug("Request to partially update TimetableEntry : {}", timetableEntryDTO);

        return timetableEntryRepository
            .findById(timetableEntryDTO.getId())
            .map(existingTimetableEntry -> {
                timetableEntryMapper.partialUpdate(existingTimetableEntry, timetableEntryDTO);

                return existingTimetableEntry;
            })
            .map(timetableEntryRepository::save)
            .map(timetableEntryMapper::toDto);
    }

    /**
     * Get all the timetableEntries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TimetableEntryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TimetableEntries");
        return timetableEntryRepository.findAll(pageable).map(timetableEntryMapper::toDto);
    }

    /**
     * Get all the timetableEntries with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TimetableEntryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return timetableEntryRepository.findAllWithEagerRelationships(pageable).map(timetableEntryMapper::toDto);
    }

    /**
     * Get one timetableEntry by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TimetableEntryDTO> findOne(Long id) {
        LOG.debug("Request to get TimetableEntry : {}", id);
        return timetableEntryRepository.findOneWithEagerRelationships(id).map(timetableEntryMapper::toDto);
    }

    /**
     * Delete the timetableEntry by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TimetableEntry : {}", id);
        timetableEntryRepository.deleteById(id);
    }
}
