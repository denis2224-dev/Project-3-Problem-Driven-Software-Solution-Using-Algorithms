package com.unischeduler.web.rest;

import com.unischeduler.repository.BuildingRepository;
import com.unischeduler.service.BuildingService;
import com.unischeduler.service.dto.BuildingDTO;
import com.unischeduler.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.unischeduler.domain.Building}.
 */
@RestController
@RequestMapping("/api/buildings")
public class BuildingResource {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingResource.class);

    private static final String ENTITY_NAME = "building";

    @Value("${jhipster.clientApp.name:uniScheduler}")
    private String applicationName;

    private final BuildingService buildingService;

    private final BuildingRepository buildingRepository;

    public BuildingResource(BuildingService buildingService, BuildingRepository buildingRepository) {
        this.buildingService = buildingService;
        this.buildingRepository = buildingRepository;
    }

    /**
     * {@code POST  /buildings} : Create a new building.
     *
     * @param buildingDTO the buildingDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new buildingDTO, or with status {@code 400 (Bad Request)} if the building has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BuildingDTO> createBuilding(@Valid @RequestBody BuildingDTO buildingDTO) throws URISyntaxException {
        LOG.debug("REST request to save Building : {}", buildingDTO);
        if (buildingDTO.getId() != null) {
            throw new BadRequestAlertException("A new building cannot already have an ID", ENTITY_NAME, "idexists");
        }
        buildingDTO = buildingService.save(buildingDTO);
        return ResponseEntity.created(new URI("/api/buildings/" + buildingDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, buildingDTO.getId().toString()))
            .body(buildingDTO);
    }

    /**
     * {@code PUT  /buildings/:id} : Updates an existing building.
     *
     * @param id the id of the buildingDTO to save.
     * @param buildingDTO the buildingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated buildingDTO,
     * or with status {@code 400 (Bad Request)} if the buildingDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the buildingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BuildingDTO> updateBuilding(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BuildingDTO buildingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Building : {}, {}", id, buildingDTO);
        if (buildingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, buildingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!buildingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        buildingDTO = buildingService.update(buildingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, buildingDTO.getId().toString()))
            .body(buildingDTO);
    }

    /**
     * {@code PATCH  /buildings/:id} : Partial updates given fields of an existing building, field will ignore if it is null
     *
     * @param id the id of the buildingDTO to save.
     * @param buildingDTO the buildingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated buildingDTO,
     * or with status {@code 400 (Bad Request)} if the buildingDTO is not valid,
     * or with status {@code 404 (Not Found)} if the buildingDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the buildingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BuildingDTO> partialUpdateBuilding(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BuildingDTO buildingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Building partially : {}, {}", id, buildingDTO);
        if (buildingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, buildingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!buildingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BuildingDTO> result = buildingService.partialUpdate(buildingDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, buildingDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /buildings} : get all the Buildings.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Buildings in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BuildingDTO>> getAllBuildings(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Buildings");
        Page<BuildingDTO> page = buildingService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /buildings/:id} : get the "id" building.
     *
     * @param id the id of the buildingDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the buildingDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BuildingDTO> getBuilding(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Building : {}", id);
        Optional<BuildingDTO> buildingDTO = buildingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(buildingDTO);
    }

    /**
     * {@code DELETE  /buildings/:id} : delete the "id" building.
     *
     * @param id the id of the buildingDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Building : {}", id);
        buildingService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
