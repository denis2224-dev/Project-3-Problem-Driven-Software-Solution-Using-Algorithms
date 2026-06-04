package com.unischeduler.web.rest;

import com.unischeduler.repository.TimeslotRepository;
import com.unischeduler.service.TimeslotService;
import com.unischeduler.service.dto.TimeslotDTO;
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
 * REST controller for managing {@link com.unischeduler.domain.Timeslot}.
 */
@RestController
@RequestMapping("/api/timeslots")
public class TimeslotResource {

    private static final Logger LOG = LoggerFactory.getLogger(TimeslotResource.class);

    private static final String ENTITY_NAME = "timeslot";

    @Value("${jhipster.clientApp.name:uniScheduler}")
    private String applicationName;

    private final TimeslotService timeslotService;

    private final TimeslotRepository timeslotRepository;

    public TimeslotResource(TimeslotService timeslotService, TimeslotRepository timeslotRepository) {
        this.timeslotService = timeslotService;
        this.timeslotRepository = timeslotRepository;
    }

    /**
     * {@code POST  /timeslots} : Create a new timeslot.
     *
     * @param timeslotDTO the timeslotDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timeslotDTO, or with status {@code 400 (Bad Request)} if the timeslot has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TimeslotDTO> createTimeslot(@Valid @RequestBody TimeslotDTO timeslotDTO) throws URISyntaxException {
        LOG.debug("REST request to save Timeslot : {}", timeslotDTO);
        if (timeslotDTO.getId() != null) {
            throw new BadRequestAlertException("A new timeslot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        timeslotDTO = timeslotService.save(timeslotDTO);
        return ResponseEntity.created(new URI("/api/timeslots/" + timeslotDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, timeslotDTO.getId().toString()))
            .body(timeslotDTO);
    }

    /**
     * {@code PUT  /timeslots/:id} : Updates an existing timeslot.
     *
     * @param id the id of the timeslotDTO to save.
     * @param timeslotDTO the timeslotDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timeslotDTO,
     * or with status {@code 400 (Bad Request)} if the timeslotDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timeslotDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TimeslotDTO> updateTimeslot(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TimeslotDTO timeslotDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Timeslot : {}, {}", id, timeslotDTO);
        if (timeslotDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timeslotDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timeslotRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        timeslotDTO = timeslotService.update(timeslotDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timeslotDTO.getId().toString()))
            .body(timeslotDTO);
    }

    /**
     * {@code PATCH  /timeslots/:id} : Partial updates given fields of an existing timeslot, field will ignore if it is null
     *
     * @param id the id of the timeslotDTO to save.
     * @param timeslotDTO the timeslotDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timeslotDTO,
     * or with status {@code 400 (Bad Request)} if the timeslotDTO is not valid,
     * or with status {@code 404 (Not Found)} if the timeslotDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the timeslotDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TimeslotDTO> partialUpdateTimeslot(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TimeslotDTO timeslotDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Timeslot partially : {}, {}", id, timeslotDTO);
        if (timeslotDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timeslotDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timeslotRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TimeslotDTO> result = timeslotService.partialUpdate(timeslotDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timeslotDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /timeslots} : get all the Timeslots.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Timeslots in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TimeslotDTO>> getAllTimeslots(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Timeslots");
        Page<TimeslotDTO> page = timeslotService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /timeslots/:id} : get the "id" timeslot.
     *
     * @param id the id of the timeslotDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timeslotDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TimeslotDTO> getTimeslot(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Timeslot : {}", id);
        Optional<TimeslotDTO> timeslotDTO = timeslotService.findOne(id);
        return ResponseUtil.wrapOrNotFound(timeslotDTO);
    }

    /**
     * {@code DELETE  /timeslots/:id} : delete the "id" timeslot.
     *
     * @param id the id of the timeslotDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeslot(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Timeslot : {}", id);
        timeslotService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
