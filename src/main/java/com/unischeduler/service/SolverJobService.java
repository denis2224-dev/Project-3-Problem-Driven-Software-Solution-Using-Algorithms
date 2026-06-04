package com.unischeduler.service;

import com.unischeduler.domain.SolverJob;
import com.unischeduler.repository.SolverJobRepository;
import com.unischeduler.service.dto.SolverJobDTO;
import com.unischeduler.service.mapper.SolverJobMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.unischeduler.domain.SolverJob}.
 */
@Service
@Transactional
public class SolverJobService {

    private static final Logger LOG = LoggerFactory.getLogger(SolverJobService.class);

    private final SolverJobRepository solverJobRepository;

    private final SolverJobMapper solverJobMapper;

    public SolverJobService(SolverJobRepository solverJobRepository, SolverJobMapper solverJobMapper) {
        this.solverJobRepository = solverJobRepository;
        this.solverJobMapper = solverJobMapper;
    }

    /**
     * Save a solverJob.
     *
     * @param solverJobDTO the entity to save.
     * @return the persisted entity.
     */
    public SolverJobDTO save(SolverJobDTO solverJobDTO) {
        LOG.debug("Request to save SolverJob : {}", solverJobDTO);
        SolverJob solverJob = solverJobMapper.toEntity(solverJobDTO);
        solverJob = solverJobRepository.save(solverJob);
        return solverJobMapper.toDto(solverJob);
    }

    /**
     * Update a solverJob.
     *
     * @param solverJobDTO the entity to save.
     * @return the persisted entity.
     */
    public SolverJobDTO update(SolverJobDTO solverJobDTO) {
        LOG.debug("Request to update SolverJob : {}", solverJobDTO);
        SolverJob solverJob = solverJobMapper.toEntity(solverJobDTO);
        solverJob = solverJobRepository.save(solverJob);
        return solverJobMapper.toDto(solverJob);
    }

    /**
     * Partially update a solverJob.
     *
     * @param solverJobDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SolverJobDTO> partialUpdate(SolverJobDTO solverJobDTO) {
        LOG.debug("Request to partially update SolverJob : {}", solverJobDTO);

        return solverJobRepository
            .findById(solverJobDTO.getId())
            .map(existingSolverJob -> {
                solverJobMapper.partialUpdate(existingSolverJob, solverJobDTO);

                return existingSolverJob;
            })
            .map(solverJobRepository::save)
            .map(solverJobMapper::toDto);
    }

    /**
     * Get all the solverJobs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SolverJobDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SolverJobs");
        return solverJobRepository.findAll(pageable).map(solverJobMapper::toDto);
    }

    /**
     * Get one solverJob by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SolverJobDTO> findOne(Long id) {
        LOG.debug("Request to get SolverJob : {}", id);
        return solverJobRepository.findById(id).map(solverJobMapper::toDto);
    }

    /**
     * Delete the solverJob by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SolverJob : {}", id);
        solverJobRepository.deleteById(id);
    }
}
