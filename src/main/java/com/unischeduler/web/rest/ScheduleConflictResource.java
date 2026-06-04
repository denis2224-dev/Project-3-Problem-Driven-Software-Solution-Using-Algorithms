package com.unischeduler.web.rest;

import com.unischeduler.repository.ScheduleConflictRepository;
import com.unischeduler.service.ScheduleConflictService;
import com.unischeduler.service.dto.ScheduleConflictDTO;
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
 * REST controller for managing {@link com.unischeduler.domain.ScheduleConflict}.
 */
@RestController
@RequestMapping("/api/schedule-conflicts")
public class ScheduleConflictResource {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleConflictResource.class);

    private static final String ENTITY_NAME = "scheduleConflict";

    @Value("${jhipster.clientApp.name:uniScheduler}")
    private String applicationName;

    private final ScheduleConflictService scheduleConflictService;

    private final ScheduleConflictRepository scheduleConflictRepository;

    public ScheduleConflictResource(
        ScheduleConflictService scheduleConflictService,
        ScheduleConflictRepository scheduleConflictRepository
    ) {
        this.scheduleConflictService = scheduleConflictService;
        this.scheduleConflictRepository = scheduleConflictRepository;
    }

    /**
     * {@code POST  /schedule-conflicts} : Create a new scheduleConflict.
     *
     * @param scheduleConflictDTO the scheduleConflictDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scheduleConflictDTO, or with status {@code 400 (Bad Request)} if the scheduleConflict has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ScheduleConflictDTO> createScheduleConflict(@Valid @RequestBody ScheduleConflictDTO scheduleConflictDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ScheduleConflict : {}", scheduleConflictDTO);
        if (scheduleConflictDTO.getId() != null) {
            throw new BadRequestAlertException("A new scheduleConflict cannot already have an ID", ENTITY_NAME, "idexists");
        }
        scheduleConflictDTO = scheduleConflictService.save(scheduleConflictDTO);
        return ResponseEntity.created(new URI("/api/schedule-conflicts/" + scheduleConflictDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, scheduleConflictDTO.getId().toString()))
            .body(scheduleConflictDTO);
    }

    /**
     * {@code PUT  /schedule-conflicts/:id} : Updates an existing scheduleConflict.
     *
     * @param id the id of the scheduleConflictDTO to save.
     * @param scheduleConflictDTO the scheduleConflictDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduleConflictDTO,
     * or with status {@code 400 (Bad Request)} if the scheduleConflictDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scheduleConflictDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleConflictDTO> updateScheduleConflict(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ScheduleConflictDTO scheduleConflictDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ScheduleConflict : {}, {}", id, scheduleConflictDTO);
        if (scheduleConflictDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduleConflictDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scheduleConflictRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        scheduleConflictDTO = scheduleConflictService.update(scheduleConflictDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scheduleConflictDTO.getId().toString()))
            .body(scheduleConflictDTO);
    }

    /**
     * {@code PATCH  /schedule-conflicts/:id} : Partial updates given fields of an existing scheduleConflict, field will ignore if it is null
     *
     * @param id the id of the scheduleConflictDTO to save.
     * @param scheduleConflictDTO the scheduleConflictDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduleConflictDTO,
     * or with status {@code 400 (Bad Request)} if the scheduleConflictDTO is not valid,
     * or with status {@code 404 (Not Found)} if the scheduleConflictDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the scheduleConflictDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ScheduleConflictDTO> partialUpdateScheduleConflict(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ScheduleConflictDTO scheduleConflictDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ScheduleConflict partially : {}, {}", id, scheduleConflictDTO);
        if (scheduleConflictDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduleConflictDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scheduleConflictRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ScheduleConflictDTO> result = scheduleConflictService.partialUpdate(scheduleConflictDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scheduleConflictDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /schedule-conflicts} : get all the Schedule Conflicts.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Schedule Conflicts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ScheduleConflictDTO>> getAllScheduleConflicts(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of ScheduleConflicts");
        Page<ScheduleConflictDTO> page;
        if (eagerload) {
            page = scheduleConflictService.findAllWithEagerRelationships(pageable);
        } else {
            page = scheduleConflictService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /schedule-conflicts/:id} : get the "id" scheduleConflict.
     *
     * @param id the id of the scheduleConflictDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scheduleConflictDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleConflictDTO> getScheduleConflict(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ScheduleConflict : {}", id);
        Optional<ScheduleConflictDTO> scheduleConflictDTO = scheduleConflictService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scheduleConflictDTO);
    }

    /**
     * {@code DELETE  /schedule-conflicts/:id} : delete the "id" scheduleConflict.
     *
     * @param id the id of the scheduleConflictDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleConflict(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ScheduleConflict : {}", id);
        scheduleConflictService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
