package com.unischeduler.service;

import com.unischeduler.domain.TimetableVersion;
import com.unischeduler.repository.TimetableVersionRepository;
import com.unischeduler.service.dto.TimetableVersionDTO;
import com.unischeduler.service.mapper.TimetableVersionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.unischeduler.domain.TimetableVersion}.
 */
@Service
@Transactional
public class TimetableVersionService {

    private static final Logger LOG = LoggerFactory.getLogger(TimetableVersionService.class);

    private final TimetableVersionRepository timetableVersionRepository;

    private final TimetableVersionMapper timetableVersionMapper;

    public TimetableVersionService(TimetableVersionRepository timetableVersionRepository, TimetableVersionMapper timetableVersionMapper) {
        this.timetableVersionRepository = timetableVersionRepository;
        this.timetableVersionMapper = timetableVersionMapper;
    }

    /**
     * Save a timetableVersion.
     *
     * @param timetableVersionDTO the entity to save.
     * @return the persisted entity.
     */
    public TimetableVersionDTO save(TimetableVersionDTO timetableVersionDTO) {
        LOG.debug("Request to save TimetableVersion : {}", timetableVersionDTO);
        TimetableVersion timetableVersion = timetableVersionMapper.toEntity(timetableVersionDTO);
        timetableVersion = timetableVersionRepository.save(timetableVersion);
        return timetableVersionMapper.toDto(timetableVersion);
    }

    /**
     * Update a timetableVersion.
     *
     * @param timetableVersionDTO the entity to save.
     * @return the persisted entity.
     */
    public TimetableVersionDTO update(TimetableVersionDTO timetableVersionDTO) {
        LOG.debug("Request to update TimetableVersion : {}", timetableVersionDTO);
        TimetableVersion timetableVersion = timetableVersionMapper.toEntity(timetableVersionDTO);
        timetableVersion = timetableVersionRepository.save(timetableVersion);
        return timetableVersionMapper.toDto(timetableVersion);
    }

    /**
     * Partially update a timetableVersion.
     *
     * @param timetableVersionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TimetableVersionDTO> partialUpdate(TimetableVersionDTO timetableVersionDTO) {
        LOG.debug("Request to partially update TimetableVersion : {}", timetableVersionDTO);

        return timetableVersionRepository
            .findById(timetableVersionDTO.getId())
            .map(existingTimetableVersion -> {
                timetableVersionMapper.partialUpdate(existingTimetableVersion, timetableVersionDTO);

                return existingTimetableVersion;
            })
            .map(timetableVersionRepository::save)
            .map(timetableVersionMapper::toDto);
    }

    /**
     * Get all the timetableVersions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TimetableVersionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TimetableVersions");
        return timetableVersionRepository.findAll(pageable).map(timetableVersionMapper::toDto);
    }

    /**
     * Get all the timetableVersions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TimetableVersionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return timetableVersionRepository.findAllWithEagerRelationships(pageable).map(timetableVersionMapper::toDto);
    }

    /**
     * Get one timetableVersion by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TimetableVersionDTO> findOne(Long id) {
        LOG.debug("Request to get TimetableVersion : {}", id);
        return timetableVersionRepository.findOneWithEagerRelationships(id).map(timetableVersionMapper::toDto);
    }

    /**
     * Delete the timetableVersion by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TimetableVersion : {}", id);
        timetableVersionRepository.deleteById(id);
    }
}
