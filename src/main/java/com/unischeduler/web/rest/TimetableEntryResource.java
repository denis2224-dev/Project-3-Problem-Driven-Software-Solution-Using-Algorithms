package com.unischeduler.web.rest;

import com.unischeduler.repository.TimetableEntryRepository;
import com.unischeduler.service.TimetableEntryService;
import com.unischeduler.service.dto.TimetableEntryDTO;
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
 * REST controller for managing {@link com.unischeduler.domain.TimetableEntry}.
 */
@RestController
@RequestMapping("/api/timetable-entries")
public class TimetableEntryResource {

    private static final Logger LOG = LoggerFactory.getLogger(TimetableEntryResource.class);

    private static final String ENTITY_NAME = "timetableEntry";

    @Value("${jhipster.clientApp.name:uniScheduler}")
    private String applicationName;

    private final TimetableEntryService timetableEntryService;

    private final TimetableEntryRepository timetableEntryRepository;

    public TimetableEntryResource(TimetableEntryService timetableEntryService, TimetableEntryRepository timetableEntryRepository) {
        this.timetableEntryService = timetableEntryService;
        this.timetableEntryRepository = timetableEntryRepository;
    }

    /**
     * {@code POST  /timetable-entries} : Create a new timetableEntry.
     *
     * @param timetableEntryDTO the timetableEntryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timetableEntryDTO, or with status {@code 400 (Bad Request)} if the timetableEntry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TimetableEntryDTO> createTimetableEntry(@Valid @RequestBody TimetableEntryDTO timetableEntryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TimetableEntry : {}", timetableEntryDTO);
        if (timetableEntryDTO.getId() != null) {
            throw new BadRequestAlertException("A new timetableEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        timetableEntryDTO = timetableEntryService.save(timetableEntryDTO);
        return ResponseEntity.created(new URI("/api/timetable-entries/" + timetableEntryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, timetableEntryDTO.getId().toString()))
            .body(timetableEntryDTO);
    }

    /**
     * {@code PUT  /timetable-entries/:id} : Updates an existing timetableEntry.
     *
     * @param id the id of the timetableEntryDTO to save.
     * @param timetableEntryDTO the timetableEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timetableEntryDTO,
     * or with status {@code 400 (Bad Request)} if the timetableEntryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timetableEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TimetableEntryDTO> updateTimetableEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TimetableEntryDTO timetableEntryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TimetableEntry : {}, {}", id, timetableEntryDTO);
        if (timetableEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timetableEntryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timetableEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        timetableEntryDTO = timetableEntryService.update(timetableEntryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timetableEntryDTO.getId().toString()))
            .body(timetableEntryDTO);
    }

    /**
     * {@code PATCH  /timetable-entries/:id} : Partial updates given fields of an existing timetableEntry, field will ignore if it is null
     *
     * @param id the id of the timetableEntryDTO to save.
     * @param timetableEntryDTO the timetableEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timetableEntryDTO,
     * or with status {@code 400 (Bad Request)} if the timetableEntryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the timetableEntryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the timetableEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TimetableEntryDTO> partialUpdateTimetableEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TimetableEntryDTO timetableEntryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TimetableEntry partially : {}, {}", id, timetableEntryDTO);
        if (timetableEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timetableEntryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timetableEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TimetableEntryDTO> result = timetableEntryService.partialUpdate(timetableEntryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timetableEntryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /timetable-entries} : get all the Timetable Entries.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Timetable Entries in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TimetableEntryDTO>> getAllTimetableEntries(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of TimetableEntries");
        Page<TimetableEntryDTO> page;
        if (eagerload) {
            page = timetableEntryService.findAllWithEagerRelationships(pageable);
        } else {
            page = timetableEntryService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /timetable-entries/:id} : get the "id" timetableEntry.
     *
     * @param id the id of the timetableEntryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timetableEntryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TimetableEntryDTO> getTimetableEntry(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TimetableEntry : {}", id);
        Optional<TimetableEntryDTO> timetableEntryDTO = timetableEntryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(timetableEntryDTO);
    }

    /**
     * {@code DELETE  /timetable-entries/:id} : delete the "id" timetableEntry.
     *
     * @param id the id of the timetableEntryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimetableEntry(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TimetableEntry : {}", id);
        timetableEntryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
