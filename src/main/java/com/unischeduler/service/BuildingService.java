package com.unischeduler.service;

import com.unischeduler.domain.Building;
import com.unischeduler.repository.BuildingRepository;
import com.unischeduler.service.dto.BuildingDTO;
import com.unischeduler.service.mapper.BuildingMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.unischeduler.domain.Building}.
 */
@Service
@Transactional
public class BuildingService {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;

    private final BuildingMapper buildingMapper;

    public BuildingService(BuildingRepository buildingRepository, BuildingMapper buildingMapper) {
        this.buildingRepository = buildingRepository;
        this.buildingMapper = buildingMapper;
    }

    /**
     * Save a building.
     *
     * @param buildingDTO the entity to save.
     * @return the persisted entity.
     */
    public BuildingDTO save(BuildingDTO buildingDTO) {
        LOG.debug("Request to save Building : {}", buildingDTO);
        Building building = buildingMapper.toEntity(buildingDTO);
        building = buildingRepository.save(building);
        return buildingMapper.toDto(building);
    }

    /**
     * Update a building.
     *
     * @param buildingDTO the entity to save.
     * @return the persisted entity.
     */
    public BuildingDTO update(BuildingDTO buildingDTO) {
        LOG.debug("Request to update Building : {}", buildingDTO);
        Building building = buildingMapper.toEntity(buildingDTO);
        building = buildingRepository.save(building);
        return buildingMapper.toDto(building);
    }

    /**
     * Partially update a building.
     *
     * @param buildingDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BuildingDTO> partialUpdate(BuildingDTO buildingDTO) {
        LOG.debug("Request to partially update Building : {}", buildingDTO);

        return buildingRepository
            .findById(buildingDTO.getId())
            .map(existingBuilding -> {
                buildingMapper.partialUpdate(existingBuilding, buildingDTO);

                return existingBuilding;
            })
            .map(buildingRepository::save)
            .map(buildingMapper::toDto);
    }

    /**
     * Get all the buildings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BuildingDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Buildings");
        return buildingRepository.findAll(pageable).map(buildingMapper::toDto);
    }

    /**
     * Get one building by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BuildingDTO> findOne(Long id) {
        LOG.debug("Request to get Building : {}", id);
        return buildingRepository.findById(id).map(buildingMapper::toDto);
    }

    /**
     * Delete the building by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Building : {}", id);
        buildingRepository.deleteById(id);
    }
}
