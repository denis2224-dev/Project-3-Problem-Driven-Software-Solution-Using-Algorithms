package com.unischeduler.web.rest;

import com.unischeduler.repository.StudentGroupRepository;
import com.unischeduler.service.StudentGroupService;
import com.unischeduler.service.dto.StudentGroupDTO;
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
 * REST controller for managing {@link com.unischeduler.domain.StudentGroup}.
 */
@RestController
@RequestMapping("/api/student-groups")
public class StudentGroupResource {

    private static final Logger LOG = LoggerFactory.getLogger(StudentGroupResource.class);

    private static final String ENTITY_NAME = "studentGroup";

    @Value("${jhipster.clientApp.name:uniScheduler}")
    private String applicationName;

    private final StudentGroupService studentGroupService;

    private final StudentGroupRepository studentGroupRepository;

    public StudentGroupResource(StudentGroupService studentGroupService, StudentGroupRepository studentGroupRepository) {
        this.studentGroupService = studentGroupService;
        this.studentGroupRepository = studentGroupRepository;
    }

    /**
     * {@code POST  /student-groups} : Create a new studentGroup.
     *
     * @param studentGroupDTO the studentGroupDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new studentGroupDTO, or with status {@code 400 (Bad Request)} if the studentGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StudentGroupDTO> createStudentGroup(@Valid @RequestBody StudentGroupDTO studentGroupDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save StudentGroup : {}", studentGroupDTO);
        if (studentGroupDTO.getId() != null) {
            throw new BadRequestAlertException("A new studentGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        studentGroupDTO = studentGroupService.save(studentGroupDTO);
        return ResponseEntity.created(new URI("/api/student-groups/" + studentGroupDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, studentGroupDTO.getId().toString()))
            .body(studentGroupDTO);
    }

    /**
     * {@code PUT  /student-groups/:id} : Updates an existing studentGroup.
     *
     * @param id the id of the studentGroupDTO to save.
     * @param studentGroupDTO the studentGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentGroupDTO,
     * or with status {@code 400 (Bad Request)} if the studentGroupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the studentGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentGroupDTO> updateStudentGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StudentGroupDTO studentGroupDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StudentGroup : {}, {}", id, studentGroupDTO);
        if (studentGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentGroupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        studentGroupDTO = studentGroupService.update(studentGroupDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studentGroupDTO.getId().toString()))
            .body(studentGroupDTO);
    }

    /**
     * {@code PATCH  /student-groups/:id} : Partial updates given fields of an existing studentGroup, field will ignore if it is null
     *
     * @param id the id of the studentGroupDTO to save.
     * @param studentGroupDTO the studentGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentGroupDTO,
     * or with status {@code 400 (Bad Request)} if the studentGroupDTO is not valid,
     * or with status {@code 404 (Not Found)} if the studentGroupDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the studentGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StudentGroupDTO> partialUpdateStudentGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StudentGroupDTO studentGroupDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StudentGroup partially : {}, {}", id, studentGroupDTO);
        if (studentGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentGroupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StudentGroupDTO> result = studentGroupService.partialUpdate(studentGroupDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studentGroupDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /student-groups} : get all the Student Groups.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Student Groups in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StudentGroupDTO>> getAllStudentGroups(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of StudentGroups");
        Page<StudentGroupDTO> page;
        if (eagerload) {
            page = studentGroupService.findAllWithEagerRelationships(pageable);
        } else {
            page = studentGroupService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /student-groups/:id} : get the "id" studentGroup.
     *
     * @param id the id of the studentGroupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the studentGroupDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentGroupDTO> getStudentGroup(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StudentGroup : {}", id);
        Optional<StudentGroupDTO> studentGroupDTO = studentGroupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(studentGroupDTO);
    }

    /**
     * {@code DELETE  /student-groups/:id} : delete the "id" studentGroup.
     *
     * @param id the id of the studentGroupDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentGroup(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StudentGroup : {}", id);
        studentGroupService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
