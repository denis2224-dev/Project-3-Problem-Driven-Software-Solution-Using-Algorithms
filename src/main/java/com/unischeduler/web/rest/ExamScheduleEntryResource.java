package com.unischeduler.web.rest;

import com.unischeduler.repository.ExamScheduleEntryRepository;
import com.unischeduler.service.ExamScheduleEntryService;
import com.unischeduler.service.dto.ExamScheduleEntryDTO;
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
 * REST controller for managing {@link com.unischeduler.domain.ExamScheduleEntry}.
 */
@RestController
@RequestMapping("/api/exam-schedule-entries")
public class ExamScheduleEntryResource {

    private static final Logger LOG = LoggerFactory.getLogger(ExamScheduleEntryResource.class);

    private static final String ENTITY_NAME = "examScheduleEntry";

    @Value("${jhipster.clientApp.name:uniScheduler}")
    private String applicationName;

    private final ExamScheduleEntryService examScheduleEntryService;

    private final ExamScheduleEntryRepository examScheduleEntryRepository;

    public ExamScheduleEntryResource(
        ExamScheduleEntryService examScheduleEntryService,
        ExamScheduleEntryRepository examScheduleEntryRepository
    ) {
        this.examScheduleEntryService = examScheduleEntryService;
        this.examScheduleEntryRepository = examScheduleEntryRepository;
    }

    /**
     * {@code POST  /exam-schedule-entries} : Create a new examScheduleEntry.
     *
     * @param examScheduleEntryDTO the examScheduleEntryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new examScheduleEntryDTO, or with status {@code 400 (Bad Request)} if the examScheduleEntry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ExamScheduleEntryDTO> createExamScheduleEntry(@Valid @RequestBody ExamScheduleEntryDTO examScheduleEntryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ExamScheduleEntry : {}", examScheduleEntryDTO);
        if (examScheduleEntryDTO.getId() != null) {
            throw new BadRequestAlertException("A new examScheduleEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        examScheduleEntryDTO = examScheduleEntryService.save(examScheduleEntryDTO);
        return ResponseEntity.created(new URI("/api/exam-schedule-entries/" + examScheduleEntryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, examScheduleEntryDTO.getId().toString()))
            .body(examScheduleEntryDTO);
    }

    /**
     * {@code PUT  /exam-schedule-entries/:id} : Updates an existing examScheduleEntry.
     *
     * @param id the id of the examScheduleEntryDTO to save.
     * @param examScheduleEntryDTO the examScheduleEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated examScheduleEntryDTO,
     * or with status {@code 400 (Bad Request)} if the examScheduleEntryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the examScheduleEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExamScheduleEntryDTO> updateExamScheduleEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ExamScheduleEntryDTO examScheduleEntryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ExamScheduleEntry : {}, {}", id, examScheduleEntryDTO);
        if (examScheduleEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, examScheduleEntryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examScheduleEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        examScheduleEntryDTO = examScheduleEntryService.update(examScheduleEntryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, examScheduleEntryDTO.getId().toString()))
            .body(examScheduleEntryDTO);
    }

    /**
     * {@code PATCH  /exam-schedule-entries/:id} : Partial updates given fields of an existing examScheduleEntry, field will ignore if it is null
     *
     * @param id the id of the examScheduleEntryDTO to save.
     * @param examScheduleEntryDTO the examScheduleEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated examScheduleEntryDTO,
     * or with status {@code 400 (Bad Request)} if the examScheduleEntryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the examScheduleEntryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the examScheduleEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ExamScheduleEntryDTO> partialUpdateExamScheduleEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ExamScheduleEntryDTO examScheduleEntryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ExamScheduleEntry partially : {}, {}", id, examScheduleEntryDTO);
        if (examScheduleEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, examScheduleEntryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examScheduleEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ExamScheduleEntryDTO> result = examScheduleEntryService.partialUpdate(examScheduleEntryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, examScheduleEntryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /exam-schedule-entries} : get all the Exam Schedule Entries.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Exam Schedule Entries in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ExamScheduleEntryDTO>> getAllExamScheduleEntries(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of ExamScheduleEntries");
        Page<ExamScheduleEntryDTO> page;
        if (eagerload) {
            page = examScheduleEntryService.findAllWithEagerRelationships(pageable);
        } else {
            page = examScheduleEntryService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /exam-schedule-entries/:id} : get the "id" examScheduleEntry.
     *
     * @param id the id of the examScheduleEntryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the examScheduleEntryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExamScheduleEntryDTO> getExamScheduleEntry(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ExamScheduleEntry : {}", id);
        Optional<ExamScheduleEntryDTO> examScheduleEntryDTO = examScheduleEntryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(examScheduleEntryDTO);
    }

    /**
     * {@code DELETE  /exam-schedule-entries/:id} : delete the "id" examScheduleEntry.
     *
     * @param id the id of the examScheduleEntryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamScheduleEntry(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ExamScheduleEntry : {}", id);
        examScheduleEntryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
