package com.unischeduler.web.rest;

import com.unischeduler.repository.CourseEventRepository;
import com.unischeduler.service.CourseEventService;
import com.unischeduler.service.dto.CourseEventDTO;
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
 * REST controller for managing {@link com.unischeduler.domain.CourseEvent}.
 */
@RestController
@RequestMapping("/api/course-events")
public class CourseEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(CourseEventResource.class);

    private static final String ENTITY_NAME = "courseEvent";

    @Value("${jhipster.clientApp.name:uniScheduler}")
    private String applicationName;

    private final CourseEventService courseEventService;

    private final CourseEventRepository courseEventRepository;

    public CourseEventResource(CourseEventService courseEventService, CourseEventRepository courseEventRepository) {
        this.courseEventService = courseEventService;
        this.courseEventRepository = courseEventRepository;
    }

    /**
     * {@code POST  /course-events} : Create a new courseEvent.
     *
     * @param courseEventDTO the courseEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courseEventDTO, or with status {@code 400 (Bad Request)} if the courseEvent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CourseEventDTO> createCourseEvent(@Valid @RequestBody CourseEventDTO courseEventDTO) throws URISyntaxException {
        LOG.debug("REST request to save CourseEvent : {}", courseEventDTO);
        if (courseEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new courseEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        courseEventDTO = courseEventService.save(courseEventDTO);
        return ResponseEntity.created(new URI("/api/course-events/" + courseEventDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, courseEventDTO.getId().toString()))
            .body(courseEventDTO);
    }

    /**
     * {@code PUT  /course-events/:id} : Updates an existing courseEvent.
     *
     * @param id the id of the courseEventDTO to save.
     * @param courseEventDTO the courseEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseEventDTO,
     * or with status {@code 400 (Bad Request)} if the courseEventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the courseEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseEventDTO> updateCourseEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CourseEventDTO courseEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CourseEvent : {}, {}", id, courseEventDTO);
        if (courseEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        courseEventDTO = courseEventService.update(courseEventDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseEventDTO.getId().toString()))
            .body(courseEventDTO);
    }

    /**
     * {@code PATCH  /course-events/:id} : Partial updates given fields of an existing courseEvent, field will ignore if it is null
     *
     * @param id the id of the courseEventDTO to save.
     * @param courseEventDTO the courseEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseEventDTO,
     * or with status {@code 400 (Bad Request)} if the courseEventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the courseEventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the courseEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CourseEventDTO> partialUpdateCourseEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CourseEventDTO courseEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CourseEvent partially : {}, {}", id, courseEventDTO);
        if (courseEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CourseEventDTO> result = courseEventService.partialUpdate(courseEventDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseEventDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /course-events} : get all the Course Events.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Course Events in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CourseEventDTO>> getAllCourseEvents(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of CourseEvents");
        Page<CourseEventDTO> page;
        if (eagerload) {
            page = courseEventService.findAllWithEagerRelationships(pageable);
        } else {
            page = courseEventService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /course-events/:id} : get the "id" courseEvent.
     *
     * @param id the id of the courseEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courseEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseEventDTO> getCourseEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CourseEvent : {}", id);
        Optional<CourseEventDTO> courseEventDTO = courseEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(courseEventDTO);
    }

    /**
     * {@code DELETE  /course-events/:id} : delete the "id" courseEvent.
     *
     * @param id the id of the courseEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CourseEvent : {}", id);
        courseEventService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
