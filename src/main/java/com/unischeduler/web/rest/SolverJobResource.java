package com.unischeduler.web.rest;

import com.unischeduler.repository.SolverJobRepository;
import com.unischeduler.security.AuthoritiesConstants;
import com.unischeduler.service.SolverJobOrchestrationService;
import com.unischeduler.service.SolverJobService;
import com.unischeduler.service.dto.SolverJobResultDTO;
import com.unischeduler.service.dto.SolverJobStatisticsDTO;
import com.unischeduler.service.dto.SolverJobDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.unischeduler.domain.SolverJob}.
 */
@RestController
@RequestMapping("/api/solver-jobs")
public class SolverJobResource {

    private static final Logger LOG = LoggerFactory.getLogger(SolverJobResource.class);

    private static final String ENTITY_NAME = "solverJob";

    @Value("${jhipster.clientApp.name:uniScheduler}")
    private String applicationName;

    private final SolverJobService solverJobService;

    private final SolverJobOrchestrationService solverJobOrchestrationService;

    private final SolverJobRepository solverJobRepository;

    public SolverJobResource(
        SolverJobService solverJobService,
        SolverJobOrchestrationService solverJobOrchestrationService,
        SolverJobRepository solverJobRepository
    ) {
        this.solverJobService = solverJobService;
        this.solverJobOrchestrationService = solverJobOrchestrationService;
        this.solverJobRepository = solverJobRepository;
    }

    /**
     * {@code POST  /solver-jobs/generate-timetable} : Create and start an asynchronous timetable solver job.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and the created solver job.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/generate-timetable")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SolverJobDTO> generateTimetable() throws URISyntaxException {
        LOG.debug("REST request to generate timetable asynchronously");
        SolverJobDTO solverJobDTO = solverJobOrchestrationService.generateTimetable();
        return ResponseEntity.created(new URI("/api/solver-jobs/" + solverJobDTO.getId())).body(solverJobDTO);
    }

    /**
     * {@code POST  /solver-jobs} : Create a new solverJob.
     *
     * @param solverJobDTO the solverJobDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new solverJobDTO, or with status {@code 400 (Bad Request)} if the solverJob has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SolverJobDTO> createSolverJob(@Valid @RequestBody SolverJobDTO solverJobDTO) throws URISyntaxException {
        LOG.debug("REST request to save SolverJob : {}", solverJobDTO);
        if (solverJobDTO.getId() != null) {
            throw new BadRequestAlertException("A new solverJob cannot already have an ID", ENTITY_NAME, "idexists");
        }
        solverJobDTO = solverJobService.save(solverJobDTO);
        return ResponseEntity.created(new URI("/api/solver-jobs/" + solverJobDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, solverJobDTO.getId().toString()))
            .body(solverJobDTO);
    }

    /**
     * {@code PUT  /solver-jobs/:id} : Updates an existing solverJob.
     *
     * @param id the id of the solverJobDTO to save.
     * @param solverJobDTO the solverJobDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solverJobDTO,
     * or with status {@code 400 (Bad Request)} if the solverJobDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the solverJobDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SolverJobDTO> updateSolverJob(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SolverJobDTO solverJobDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SolverJob : {}, {}", id, solverJobDTO);
        if (solverJobDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, solverJobDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!solverJobRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        solverJobDTO = solverJobService.update(solverJobDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, solverJobDTO.getId().toString()))
            .body(solverJobDTO);
    }

    /**
     * {@code PATCH  /solver-jobs/:id} : Partial updates given fields of an existing solverJob, field will ignore if it is null
     *
     * @param id the id of the solverJobDTO to save.
     * @param solverJobDTO the solverJobDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solverJobDTO,
     * or with status {@code 400 (Bad Request)} if the solverJobDTO is not valid,
     * or with status {@code 404 (Not Found)} if the solverJobDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the solverJobDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SolverJobDTO> partialUpdateSolverJob(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SolverJobDTO solverJobDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SolverJob partially : {}, {}", id, solverJobDTO);
        if (solverJobDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, solverJobDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!solverJobRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SolverJobDTO> result = solverJobService.partialUpdate(solverJobDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, solverJobDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /solver-jobs} : get all the Solver Jobs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Solver Jobs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SolverJobDTO>> getAllSolverJobs(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of SolverJobs");
        Page<SolverJobDTO> page = solverJobService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /solver-jobs/:id} : get the "id" solverJob.
     *
     * @param id the id of the solverJobDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the solverJobDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SolverJobDTO> getSolverJob(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SolverJob : {}", id);
        Optional<SolverJobDTO> solverJobDTO = solverJobService.findOne(id);
        return ResponseUtil.wrapOrNotFound(solverJobDTO);
    }

    /**
     * {@code GET  /solver-jobs/:id/result} : get the generated timetable result for a solver job.
     *
     * @param id the id of the solver job.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the result payload.
     */
    @GetMapping("/{id}/result")
    public ResponseEntity<SolverJobResultDTO> getSolverJobResult(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SolverJob result : {}", id);
        return ResponseUtil.wrapOrNotFound(solverJobOrchestrationService.getResult(id));
    }

    /**
     * {@code GET  /solver-jobs/:id/statistics} : get runtime statistics for a solver job.
     *
     * @param id the id of the solver job.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the statistics payload.
     */
    @GetMapping("/{id}/statistics")
    public ResponseEntity<SolverJobStatisticsDTO> getSolverJobStatistics(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SolverJob statistics : {}", id);
        return ResponseUtil.wrapOrNotFound(solverJobOrchestrationService.getStatistics(id));
    }

    /**
     * {@code POST  /solver-jobs/:id/cancel} : request cancellation for a solver job.
     *
     * @param id the id of the solver job.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the cancelled job.
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SolverJobDTO> cancelSolverJob(@PathVariable("id") Long id) {
        LOG.debug("REST request to cancel SolverJob : {}", id);
        return ResponseUtil.wrapOrNotFound(solverJobOrchestrationService.cancel(id));
    }

    /**
     * {@code DELETE  /solver-jobs/:id} : delete the "id" solverJob.
     *
     * @param id the id of the solverJobDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSolverJob(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SolverJob : {}", id);
        solverJobService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
