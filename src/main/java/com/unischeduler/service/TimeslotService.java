package com.unischeduler.service;

import com.unischeduler.domain.Timeslot;
import com.unischeduler.repository.TimeslotRepository;
import com.unischeduler.service.dto.TimeslotDTO;
import com.unischeduler.service.mapper.TimeslotMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.unischeduler.domain.Timeslot}.
 */
@Service
@Transactional
public class TimeslotService {

    private static final Logger LOG = LoggerFactory.getLogger(TimeslotService.class);

    private final TimeslotRepository timeslotRepository;

    private final TimeslotMapper timeslotMapper;

    public TimeslotService(TimeslotRepository timeslotRepository, TimeslotMapper timeslotMapper) {
        this.timeslotRepository = timeslotRepository;
        this.timeslotMapper = timeslotMapper;
    }

    /**
     * Save a timeslot.
     *
     * @param timeslotDTO the entity to save.
     * @return the persisted entity.
     */
    public TimeslotDTO save(TimeslotDTO timeslotDTO) {
        LOG.debug("Request to save Timeslot : {}", timeslotDTO);
        Timeslot timeslot = timeslotMapper.toEntity(timeslotDTO);
        timeslot = timeslotRepository.save(timeslot);
        return timeslotMapper.toDto(timeslot);
    }

    /**
     * Update a timeslot.
     *
     * @param timeslotDTO the entity to save.
     * @return the persisted entity.
     */
    public TimeslotDTO update(TimeslotDTO timeslotDTO) {
        LOG.debug("Request to update Timeslot : {}", timeslotDTO);
        Timeslot timeslot = timeslotMapper.toEntity(timeslotDTO);
        timeslot = timeslotRepository.save(timeslot);
        return timeslotMapper.toDto(timeslot);
    }

    /**
     * Partially update a timeslot.
     *
     * @param timeslotDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TimeslotDTO> partialUpdate(TimeslotDTO timeslotDTO) {
        LOG.debug("Request to partially update Timeslot : {}", timeslotDTO);

        return timeslotRepository
            .findById(timeslotDTO.getId())
            .map(existingTimeslot -> {
                timeslotMapper.partialUpdate(existingTimeslot, timeslotDTO);

                return existingTimeslot;
            })
            .map(timeslotRepository::save)
            .map(timeslotMapper::toDto);
    }

    /**
     * Get all the timeslots.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TimeslotDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Timeslots");
        return timeslotRepository.findAll(pageable).map(timeslotMapper::toDto);
    }

    /**
     * Get one timeslot by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TimeslotDTO> findOne(Long id) {
        LOG.debug("Request to get Timeslot : {}", id);
        return timeslotRepository.findById(id).map(timeslotMapper::toDto);
    }

    /**
     * Delete the timeslot by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Timeslot : {}", id);
        timeslotRepository.deleteById(id);
    }
}
