package com.unischeduler.web.rest;

import static com.unischeduler.domain.TimetableVersionAsserts.*;
import static com.unischeduler.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unischeduler.IntegrationTest;
import com.unischeduler.domain.Timetable;
import com.unischeduler.domain.TimetableVersion;
import com.unischeduler.repository.TimetableVersionRepository;
import com.unischeduler.service.TimetableVersionService;
import com.unischeduler.service.dto.TimetableVersionDTO;
import com.unischeduler.service.mapper.TimetableVersionMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TimetableVersionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TimetableVersionResourceIT {

    private static final Integer DEFAULT_VERSION_NUMBER = 1;
    private static final Integer UPDATED_VERSION_NUMBER = 2;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_TOTAL_HARD_CONFLICTS = 0;
    private static final Integer UPDATED_TOTAL_HARD_CONFLICTS = 1;

    private static final Integer DEFAULT_TOTAL_SOFT_PENALTY = 0;
    private static final Integer UPDATED_TOTAL_SOFT_PENALTY = 1;

    private static final Double DEFAULT_AVERAGE_STUDENT_GAP = 0D;
    private static final Double UPDATED_AVERAGE_STUDENT_GAP = 1D;

    private static final Double DEFAULT_ROOM_UTILIZATION = 0D;
    private static final Double UPDATED_ROOM_UTILIZATION = 1D;

    private static final String ENTITY_API_URL = "/api/timetable-versions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TimetableVersionRepository timetableVersionRepository;

    @Mock
    private TimetableVersionRepository timetableVersionRepositoryMock;

    @Autowired
    private TimetableVersionMapper timetableVersionMapper;

    @Mock
    private TimetableVersionService timetableVersionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTimetableVersionMockMvc;

    private TimetableVersion timetableVersion;

    private TimetableVersion insertedTimetableVersion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimetableVersion createEntity(EntityManager em) {
        TimetableVersion timetableVersion = new TimetableVersion()
            .versionNumber(DEFAULT_VERSION_NUMBER)
            .createdAt(DEFAULT_CREATED_AT)
            .totalHardConflicts(DEFAULT_TOTAL_HARD_CONFLICTS)
            .totalSoftPenalty(DEFAULT_TOTAL_SOFT_PENALTY)
            .averageStudentGap(DEFAULT_AVERAGE_STUDENT_GAP)
            .roomUtilization(DEFAULT_ROOM_UTILIZATION);
        // Add required entity
        Timetable timetable;
        if (TestUtil.findAll(em, Timetable.class).isEmpty()) {
            timetable = TimetableResourceIT.createEntity();
            em.persist(timetable);
            em.flush();
        } else {
            timetable = TestUtil.findAll(em, Timetable.class).get(0);
        }
        timetableVersion.setTimetable(timetable);
        return timetableVersion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimetableVersion createUpdatedEntity(EntityManager em) {
        TimetableVersion updatedTimetableVersion = new TimetableVersion()
            .versionNumber(UPDATED_VERSION_NUMBER)
            .createdAt(UPDATED_CREATED_AT)
            .totalHardConflicts(UPDATED_TOTAL_HARD_CONFLICTS)
            .totalSoftPenalty(UPDATED_TOTAL_SOFT_PENALTY)
            .averageStudentGap(UPDATED_AVERAGE_STUDENT_GAP)
            .roomUtilization(UPDATED_ROOM_UTILIZATION);
        // Add required entity
        Timetable timetable;
        if (TestUtil.findAll(em, Timetable.class).isEmpty()) {
            timetable = TimetableResourceIT.createUpdatedEntity();
            em.persist(timetable);
            em.flush();
        } else {
            timetable = TestUtil.findAll(em, Timetable.class).get(0);
        }
        updatedTimetableVersion.setTimetable(timetable);
        return updatedTimetableVersion;
    }

    @BeforeEach
    void initTest() {
        timetableVersion = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTimetableVersion != null) {
            timetableVersionRepository.delete(insertedTimetableVersion);
            insertedTimetableVersion = null;
        }
    }

    @Test
    @Transactional
    void createTimetableVersion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TimetableVersion
        TimetableVersionDTO timetableVersionDTO = timetableVersionMapper.toDto(timetableVersion);
        var returnedTimetableVersionDTO = om.readValue(
            restTimetableVersionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(timetableVersionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TimetableVersionDTO.class
        );

        // Validate the TimetableVersion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTimetableVersion = timetableVersionMapper.toEntity(returnedTimetableVersionDTO);
        assertTimetableVersionUpdatableFieldsEquals(returnedTimetableVersion, getPersistedTimetableVersion(returnedTimetableVersion));

        insertedTimetableVersion = returnedTimetableVersion;
    }

    @Test
    @Transactional
    void createTimetableVersionWithExistingId() throws Exception {
        // Create the TimetableVersion with an existing ID
        timetableVersion.setId(1L);
        TimetableVersionDTO timetableVersionDTO = timetableVersionMapper.toDto(timetableVersion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimetableVersionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(timetableVersionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TimetableVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkVersionNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        timetableVersion.setVersionNumber(null);

        // Create the TimetableVersion, which fails.
        TimetableVersionDTO timetableVersionDTO = timetableVersionMapper.toDto(timetableVersion);

        restTimetableVersionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(timetableVersionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        timetableVersion.setCreatedAt(null);

        // Create the TimetableVersion, which fails.
        TimetableVersionDTO timetableVersionDTO = timetableVersionMapper.toDto(timetableVersion);

        restTimetableVersionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(timetableVersionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTimetableVersions() throws Exception {
        // Initialize the database
        insertedTimetableVersion = timetableVersionRepository.saveAndFlush(timetableVersion);

        // Get all the timetableVersionList
        restTimetableVersionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timetableVersion.getId().intValue())))
            .andExpect(jsonPath("$.[*].versionNumber").value(hasItem(DEFAULT_VERSION_NUMBER)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].totalHardConflicts").value(hasItem(DEFAULT_TOTAL_HARD_CONFLICTS)))
            .andExpect(jsonPath("$.[*].totalSoftPenalty").value(hasItem(DEFAULT_TOTAL_SOFT_PENALTY)))
            .andExpect(jsonPath("$.[*].averageStudentGap").value(hasItem(DEFAULT_AVERAGE_STUDENT_GAP)))
            .andExpect(jsonPath("$.[*].roomUtilization").value(hasItem(DEFAULT_ROOM_UTILIZATION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTimetableVersionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(timetableVersionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTimetableVersionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(timetableVersionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTimetableVersionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(timetableVersionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTimetableVersionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(timetableVersionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTimetableVersion() throws Exception {
        // Initialize the database
        insertedTimetableVersion = timetableVersionRepository.saveAndFlush(timetableVersion);

        // Get the timetableVersion
        restTimetableVersionMockMvc
            .perform(get(ENTITY_API_URL_ID, timetableVersion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(timetableVersion.getId().intValue()))
            .andExpect(jsonPath("$.versionNumber").value(DEFAULT_VERSION_NUMBER))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.totalHardConflicts").value(DEFAULT_TOTAL_HARD_CONFLICTS))
            .andExpect(jsonPath("$.totalSoftPenalty").value(DEFAULT_TOTAL_SOFT_PENALTY))
            .andExpect(jsonPath("$.averageStudentGap").value(DEFAULT_AVERAGE_STUDENT_GAP))
            .andExpect(jsonPath("$.roomUtilization").value(DEFAULT_ROOM_UTILIZATION));
    }

    @Test
    @Transactional
    void getNonExistingTimetableVersion() throws Exception {
        // Get the timetableVersion
        restTimetableVersionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTimetableVersion() throws Exception {
        // Initialize the database
        insertedTimetableVersion = timetableVersionRepository.saveAndFlush(timetableVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the timetableVersion
        TimetableVersion updatedTimetableVersion = timetableVersionRepository.findById(timetableVersion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTimetableVersion are not directly saved in db
        em.detach(updatedTimetableVersion);
        updatedTimetableVersion
            .versionNumber(UPDATED_VERSION_NUMBER)
            .createdAt(UPDATED_CREATED_AT)
            .totalHardConflicts(UPDATED_TOTAL_HARD_CONFLICTS)
            .totalSoftPenalty(UPDATED_TOTAL_SOFT_PENALTY)
            .averageStudentGap(UPDATED_AVERAGE_STUDENT_GAP)
            .roomUtilization(UPDATED_ROOM_UTILIZATION);
        TimetableVersionDTO timetableVersionDTO = timetableVersionMapper.toDto(updatedTimetableVersion);

        restTimetableVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timetableVersionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(timetableVersionDTO))
            )
            .andExpect(status().isOk());

        // Validate the TimetableVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTimetableVersionToMatchAllProperties(updatedTimetableVersion);
    }

    @Test
    @Transactional
    void putNonExistingTimetableVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetableVersion.setId(longCount.incrementAndGet());

        // Create the TimetableVersion
        TimetableVersionDTO timetableVersionDTO = timetableVersionMapper.toDto(timetableVersion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimetableVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timetableVersionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(timetableVersionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimetableVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTimetableVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetableVersion.setId(longCount.incrementAndGet());

        // Create the TimetableVersion
        TimetableVersionDTO timetableVersionDTO = timetableVersionMapper.toDto(timetableVersion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimetableVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(timetableVersionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimetableVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTimetableVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetableVersion.setId(longCount.incrementAndGet());

        // Create the TimetableVersion
        TimetableVersionDTO timetableVersionDTO = timetableVersionMapper.toDto(timetableVersion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimetableVersionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(timetableVersionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimetableVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTimetableVersionWithPatch() throws Exception {
        // Initialize the database
        insertedTimetableVersion = timetableVersionRepository.saveAndFlush(timetableVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the timetableVersion using partial update
        TimetableVersion partialUpdatedTimetableVersion = new TimetableVersion();
        partialUpdatedTimetableVersion.setId(timetableVersion.getId());

        partialUpdatedTimetableVersion.createdAt(UPDATED_CREATED_AT).averageStudentGap(UPDATED_AVERAGE_STUDENT_GAP);

        restTimetableVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimetableVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTimetableVersion))
            )
            .andExpect(status().isOk());

        // Validate the TimetableVersion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTimetableVersionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTimetableVersion, timetableVersion),
            getPersistedTimetableVersion(timetableVersion)
        );
    }

    @Test
    @Transactional
    void fullUpdateTimetableVersionWithPatch() throws Exception {
        // Initialize the database
        insertedTimetableVersion = timetableVersionRepository.saveAndFlush(timetableVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the timetableVersion using partial update
        TimetableVersion partialUpdatedTimetableVersion = new TimetableVersion();
        partialUpdatedTimetableVersion.setId(timetableVersion.getId());

        partialUpdatedTimetableVersion
            .versionNumber(UPDATED_VERSION_NUMBER)
            .createdAt(UPDATED_CREATED_AT)
            .totalHardConflicts(UPDATED_TOTAL_HARD_CONFLICTS)
            .totalSoftPenalty(UPDATED_TOTAL_SOFT_PENALTY)
            .averageStudentGap(UPDATED_AVERAGE_STUDENT_GAP)
            .roomUtilization(UPDATED_ROOM_UTILIZATION);

        restTimetableVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimetableVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTimetableVersion))
            )
            .andExpect(status().isOk());

        // Validate the TimetableVersion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTimetableVersionUpdatableFieldsEquals(
            partialUpdatedTimetableVersion,
            getPersistedTimetableVersion(partialUpdatedTimetableVersion)
        );
    }

    @Test
    @Transactional
    void patchNonExistingTimetableVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetableVersion.setId(longCount.incrementAndGet());

        // Create the TimetableVersion
        TimetableVersionDTO timetableVersionDTO = timetableVersionMapper.toDto(timetableVersion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimetableVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, timetableVersionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(timetableVersionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimetableVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTimetableVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetableVersion.setId(longCount.incrementAndGet());

        // Create the TimetableVersion
        TimetableVersionDTO timetableVersionDTO = timetableVersionMapper.toDto(timetableVersion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimetableVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(timetableVersionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimetableVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTimetableVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetableVersion.setId(longCount.incrementAndGet());

        // Create the TimetableVersion
        TimetableVersionDTO timetableVersionDTO = timetableVersionMapper.toDto(timetableVersion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimetableVersionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(timetableVersionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimetableVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTimetableVersion() throws Exception {
        // Initialize the database
        insertedTimetableVersion = timetableVersionRepository.saveAndFlush(timetableVersion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the timetableVersion
        restTimetableVersionMockMvc
            .perform(delete(ENTITY_API_URL_ID, timetableVersion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return timetableVersionRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected TimetableVersion getPersistedTimetableVersion(TimetableVersion timetableVersion) {
        return timetableVersionRepository.findById(timetableVersion.getId()).orElseThrow();
    }

    protected void assertPersistedTimetableVersionToMatchAllProperties(TimetableVersion expectedTimetableVersion) {
        assertTimetableVersionAllPropertiesEquals(expectedTimetableVersion, getPersistedTimetableVersion(expectedTimetableVersion));
    }

    protected void assertPersistedTimetableVersionToMatchUpdatableProperties(TimetableVersion expectedTimetableVersion) {
        assertTimetableVersionAllUpdatablePropertiesEquals(
            expectedTimetableVersion,
            getPersistedTimetableVersion(expectedTimetableVersion)
        );
    }
}
