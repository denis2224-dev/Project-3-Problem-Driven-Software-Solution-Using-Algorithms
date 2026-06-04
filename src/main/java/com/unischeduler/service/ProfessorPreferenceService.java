package com.unischeduler.service;

import com.unischeduler.domain.ProfessorPreference;
import com.unischeduler.repository.ProfessorPreferenceRepository;
import com.unischeduler.service.dto.ProfessorPreferenceDTO;
import com.unischeduler.service.mapper.ProfessorPreferenceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.unischeduler.domain.ProfessorPreference}.
 */
@Service
@Transactional
public class ProfessorPreferenceService {

    private static final Logger LOG = LoggerFactory.getLogger(ProfessorPreferenceService.class);

    private final ProfessorPreferenceRepository professorPreferenceRepository;

    private final ProfessorPreferenceMapper professorPreferenceMapper;

    public ProfessorPreferenceService(
        ProfessorPreferenceRepository professorPreferenceRepository,
        ProfessorPreferenceMapper professorPreferenceMapper
    ) {
        this.professorPreferenceRepository = professorPreferenceRepository;
        this.professorPreferenceMapper = professorPreferenceMapper;
    }

    /**
     * Save a professorPreference.
     *
     * @param professorPreferenceDTO the entity to save.
     * @return the persisted entity.
     */
    public ProfessorPreferenceDTO save(ProfessorPreferenceDTO professorPreferenceDTO) {
        LOG.debug("Request to save ProfessorPreference : {}", professorPreferenceDTO);
        ProfessorPreference professorPreference = professorPreferenceMapper.toEntity(professorPreferenceDTO);
        professorPreference = professorPreferenceRepository.save(professorPreference);
        return professorPreferenceMapper.toDto(professorPreference);
    }

    /**
     * Update a professorPreference.
     *
     * @param professorPreferenceDTO the entity to save.
     * @return the persisted entity.
     */
    public ProfessorPreferenceDTO update(ProfessorPreferenceDTO professorPreferenceDTO) {
        LOG.debug("Request to update ProfessorPreference : {}", professorPreferenceDTO);
        ProfessorPreference professorPreference = professorPreferenceMapper.toEntity(professorPreferenceDTO);
        professorPreference = professorPreferenceRepository.save(professorPreference);
        return professorPreferenceMapper.toDto(professorPreference);
    }

    /**
     * Partially update a professorPreference.
     *
     * @param professorPreferenceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProfessorPreferenceDTO> partialUpdate(ProfessorPreferenceDTO professorPreferenceDTO) {
        LOG.debug("Request to partially update ProfessorPreference : {}", professorPreferenceDTO);

        return professorPreferenceRepository
            .findById(professorPreferenceDTO.getId())
            .map(existingProfessorPreference -> {
                professorPreferenceMapper.partialUpdate(existingProfessorPreference, professorPreferenceDTO);

                return existingProfessorPreference;
            })
            .map(professorPreferenceRepository::save)
            .map(professorPreferenceMapper::toDto);
    }

    /**
     * Get all the professorPreferences.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProfessorPreferenceDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ProfessorPreferences");
        return professorPreferenceRepository.findAll(pageable).map(professorPreferenceMapper::toDto);
    }

    /**
     * Get all the professorPreferences with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ProfessorPreferenceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return professorPreferenceRepository.findAllWithEagerRelationships(pageable).map(professorPreferenceMapper::toDto);
    }

    /**
     * Get one professorPreference by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProfessorPreferenceDTO> findOne(Long id) {
        LOG.debug("Request to get ProfessorPreference : {}", id);
        return professorPreferenceRepository.findOneWithEagerRelationships(id).map(professorPreferenceMapper::toDto);
    }

    /**
     * Delete the professorPreference by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ProfessorPreference : {}", id);
        professorPreferenceRepository.deleteById(id);
    }
}
