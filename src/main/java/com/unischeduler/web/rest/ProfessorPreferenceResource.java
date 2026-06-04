package com.unischeduler.web.rest;

import com.unischeduler.repository.ProfessorPreferenceRepository;
import com.unischeduler.service.ProfessorPreferenceService;
import com.unischeduler.service.dto.ProfessorPreferenceDTO;
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
 * REST controller for managing {@link com.unischeduler.domain.ProfessorPreference}.
 */
@RestController
@RequestMapping("/api/professor-preferences")
public class ProfessorPreferenceResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProfessorPreferenceResource.class);

    private static final String ENTITY_NAME = "professorPreference";

    @Value("${jhipster.clientApp.name:uniScheduler}")
    private String applicationName;

    private final ProfessorPreferenceService professorPreferenceService;

    private final ProfessorPreferenceRepository professorPreferenceRepository;

    public ProfessorPreferenceResource(
        ProfessorPreferenceService professorPreferenceService,
        ProfessorPreferenceRepository professorPreferenceRepository
    ) {
        this.professorPreferenceService = professorPreferenceService;
        this.professorPreferenceRepository = professorPreferenceRepository;
    }

    /**
     * {@code POST  /professor-preferences} : Create a new professorPreference.
     *
     * @param professorPreferenceDTO the professorPreferenceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new professorPreferenceDTO, or with status {@code 400 (Bad Request)} if the professorPreference has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProfessorPreferenceDTO> createProfessorPreference(
        @Valid @RequestBody ProfessorPreferenceDTO professorPreferenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save ProfessorPreference : {}", professorPreferenceDTO);
        if (professorPreferenceDTO.getId() != null) {
            throw new BadRequestAlertException("A new professorPreference cannot already have an ID", ENTITY_NAME, "idexists");
        }
        professorPreferenceDTO = professorPreferenceService.save(professorPreferenceDTO);
        return ResponseEntity.created(new URI("/api/professor-preferences/" + professorPreferenceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, professorPreferenceDTO.getId().toString()))
            .body(professorPreferenceDTO);
    }

    /**
     * {@code PUT  /professor-preferences/:id} : Updates an existing professorPreference.
     *
     * @param id the id of the professorPreferenceDTO to save.
     * @param professorPreferenceDTO the professorPreferenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated professorPreferenceDTO,
     * or with status {@code 400 (Bad Request)} if the professorPreferenceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the professorPreferenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProfessorPreferenceDTO> updateProfessorPreference(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProfessorPreferenceDTO professorPreferenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProfessorPreference : {}, {}", id, professorPreferenceDTO);
        if (professorPreferenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, professorPreferenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!professorPreferenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        professorPreferenceDTO = professorPreferenceService.update(professorPreferenceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, professorPreferenceDTO.getId().toString()))
            .body(professorPreferenceDTO);
    }

    /**
     * {@code PATCH  /professor-preferences/:id} : Partial updates given fields of an existing professorPreference, field will ignore if it is null
     *
     * @param id the id of the professorPreferenceDTO to save.
     * @param professorPreferenceDTO the professorPreferenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated professorPreferenceDTO,
     * or with status {@code 400 (Bad Request)} if the professorPreferenceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the professorPreferenceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the professorPreferenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProfessorPreferenceDTO> partialUpdateProfessorPreference(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProfessorPreferenceDTO professorPreferenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProfessorPreference partially : {}, {}", id, professorPreferenceDTO);
        if (professorPreferenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, professorPreferenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!professorPreferenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProfessorPreferenceDTO> result = professorPreferenceService.partialUpdate(professorPreferenceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, professorPreferenceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /professor-preferences} : get all the Professor Preferences.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Professor Preferences in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProfessorPreferenceDTO>> getAllProfessorPreferences(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of ProfessorPreferences");
        Page<ProfessorPreferenceDTO> page;
        if (eagerload) {
            page = professorPreferenceService.findAllWithEagerRelationships(pageable);
        } else {
            page = professorPreferenceService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /professor-preferences/:id} : get the "id" professorPreference.
     *
     * @param id the id of the professorPreferenceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the professorPreferenceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfessorPreferenceDTO> getProfessorPreference(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProfessorPreference : {}", id);
        Optional<ProfessorPreferenceDTO> professorPreferenceDTO = professorPreferenceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(professorPreferenceDTO);
    }

    /**
     * {@code DELETE  /professor-preferences/:id} : delete the "id" professorPreference.
     *
     * @param id the id of the professorPreferenceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessorPreference(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProfessorPreference : {}", id);
        professorPreferenceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
