package com.unischeduler.web.rest;

import com.unischeduler.repository.TimetableEntryRepository;
import com.unischeduler.repository.TimetableVersionRepository;
import com.unischeduler.service.TimetableVersionService;
import com.unischeduler.service.dto.SolverJobResultEntryDTO;
import com.unischeduler.service.dto.TimetableVersionDTO;
import com.unischeduler.service.mapper.TimetableEntryResultMapper;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.unischeduler.domain.TimetableVersion}.
 */
@RestController
@RequestMapping("/api/timetable-versions")
public class TimetableVersionResource {

    private static final Logger LOG = LoggerFactory.getLogger(TimetableVersionResource.class);

    private static final String ENTITY_NAME = "timetableVersion";

    @Value("${jhipster.clientApp.name:uniScheduler}")
    private String applicationName;

    private final TimetableVersionService timetableVersionService;

    private final TimetableVersionRepository timetableVersionRepository;

    private final TimetableEntryRepository timetableEntryRepository;

    private final TimetableEntryResultMapper timetableEntryResultMapper;

    public TimetableVersionResource(
        TimetableVersionService timetableVersionService,
        TimetableVersionRepository timetableVersionRepository,
        TimetableEntryRepository timetableEntryRepository,
        TimetableEntryResultMapper timetableEntryResultMapper
    ) {
        this.timetableVersionService = timetableVersionService;
        this.timetableVersionRepository = timetableVersionRepository;
        this.timetableEntryRepository = timetableEntryRepository;
        this.timetableEntryResultMapper = timetableEntryResultMapper;
    }

    /**
     * {@code POST  /timetable-versions} : Create a new timetableVersion.
     *
     * @param timetableVersionDTO the timetableVersionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timetableVersionDTO, or with status {@code 400 (Bad Request)} if the timetableVersion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TimetableVersionDTO> createTimetableVersion(@Valid @RequestBody TimetableVersionDTO timetableVersionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TimetableVersion : {}", timetableVersionDTO);
        if (timetableVersionDTO.getId() != null) {
            throw new BadRequestAlertException("A new timetableVersion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        timetableVersionDTO = timetableVersionService.save(timetableVersionDTO);
        return ResponseEntity.created(new URI("/api/timetable-versions/" + timetableVersionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, timetableVersionDTO.getId().toString()))
            .body(timetableVersionDTO);
    }

    /**
     * {@code PUT  /timetable-versions/:id} : Updates an existing timetableVersion.
     *
     * @param id the id of the timetableVersionDTO to save.
     * @param timetableVersionDTO the timetableVersionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timetableVersionDTO,
     * or with status {@code 400 (Bad Request)} if the timetableVersionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timetableVersionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TimetableVersionDTO> updateTimetableVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TimetableVersionDTO timetableVersionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TimetableVersion : {}, {}", id, timetableVersionDTO);
        if (timetableVersionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timetableVersionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timetableVersionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        timetableVersionDTO = timetableVersionService.update(timetableVersionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timetableVersionDTO.getId().toString()))
            .body(timetableVersionDTO);
    }

    /**
     * {@code PATCH  /timetable-versions/:id} : Partial updates given fields of an existing timetableVersion, field will ignore if it is null
     *
     * @param id the id of the timetableVersionDTO to save.
     * @param timetableVersionDTO the timetableVersionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timetableVersionDTO,
     * or with status {@code 400 (Bad Request)} if the timetableVersionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the timetableVersionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the timetableVersionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TimetableVersionDTO> partialUpdateTimetableVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TimetableVersionDTO timetableVersionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TimetableVersion partially : {}, {}", id, timetableVersionDTO);
        if (timetableVersionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timetableVersionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timetableVersionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TimetableVersionDTO> result = timetableVersionService.partialUpdate(timetableVersionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timetableVersionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /timetable-versions} : get all the Timetable Versions.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Timetable Versions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TimetableVersionDTO>> getAllTimetableVersions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of TimetableVersions");
        Page<TimetableVersionDTO> page;
        if (eagerload) {
            page = timetableVersionService.findAllWithEagerRelationships(pageable);
        } else {
            page = timetableVersionService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /timetable-versions/:id} : get the "id" timetableVersion.
     *
     * @param id the id of the timetableVersionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timetableVersionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TimetableVersionDTO> getTimetableVersion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TimetableVersion : {}", id);
        Optional<TimetableVersionDTO> timetableVersionDTO = timetableVersionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(timetableVersionDTO);
    }

    /**
     * {@code GET  /timetable-versions/:id/entries} : get flattened timetable entries for weekly grid rendering.
     *
     * @param id the id of the timetable version.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the ordered entries.
     */
    @GetMapping("/{id}/entries")
    public ResponseEntity<List<SolverJobResultEntryDTO>> getTimetableVersionEntries(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TimetableVersion entries : {}", id);
        if (!timetableVersionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        List<SolverJobResultEntryDTO> entries = timetableEntryRepository
            .findAllForVersionWithDetails(id)
            .stream()
            .map(timetableEntryResultMapper::toResultEntry)
            .toList();
        return ResponseEntity.ok(entries);
    }

    /**
     * {@code GET  /timetable-versions/:id/export/csv} : export a timetable version as CSV.
     *
     * @param id the id of the timetable version.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and CSV content.
     */
    @GetMapping(value = "/{id}/export/csv", produces = "text/csv")
    public ResponseEntity<String> exportTimetableVersionCsv(@PathVariable("id") Long id) {
        LOG.debug("REST request to export TimetableVersion CSV : {}", id);
        if (!timetableVersionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        List<SolverJobResultEntryDTO> entries = timetableEntryRepository
            .findAllForVersionWithDetails(id)
            .stream()
            .map(timetableEntryResultMapper::toResultEntry)
            .toList();
        String csv = toCsv(entries);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("text/csv"))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"timetable-version-" + id + ".csv\"")
            .body(csv);
    }

    /**
     * {@code DELETE  /timetable-versions/:id} : delete the "id" timetableVersion.
     *
     * @param id the id of the timetableVersionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimetableVersion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TimetableVersion : {}", id);
        timetableVersionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    private String toCsv(List<SolverJobResultEntryDTO> entries) {
        StringBuilder csv = new StringBuilder(
            "Day,Start Time,End Time,Course Code,Course Name,Event Type,Professor,Student Group,Room,Building\n"
        );
        for (SolverJobResultEntryDTO entry : entries) {
            csv.append(
                csvRow(
                    entry.getDayOfWeek(),
                    entry.getStartTime(),
                    entry.getEndTime(),
                    entry.getCourseCode(),
                    entry.getCourseName(),
                    entry.getEventType(),
                    entry.getProfessorName(),
                    entry.getStudentGroupName(),
                    entry.getRoomCode(),
                    entry.getBuildingCode()
                )
            ).append('\n');
        }
        return csv.toString();
    }

    private String csvRow(String... values) {
        return String.join(",", java.util.Arrays.stream(values).map(this::csvValue).toList());
    }

    private String csvValue(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
