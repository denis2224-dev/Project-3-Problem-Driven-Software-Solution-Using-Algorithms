package com.unischeduler.web.rest;

import static com.unischeduler.domain.ScheduleConflictAsserts.*;
import static com.unischeduler.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unischeduler.IntegrationTest;
import com.unischeduler.domain.ScheduleConflict;
import com.unischeduler.domain.TimetableVersion;
import com.unischeduler.domain.enumeration.ConflictSeverity;
import com.unischeduler.domain.enumeration.ConflictType;
import com.unischeduler.security.AuthoritiesConstants;
import com.unischeduler.repository.ScheduleConflictRepository;
import com.unischeduler.service.ScheduleConflictService;
import com.unischeduler.service.dto.ScheduleConflictDTO;
import com.unischeduler.service.mapper.ScheduleConflictMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ScheduleConflictResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class ScheduleConflictResourceIT {

    private static final ConflictType DEFAULT_CONFLICT_TYPE = ConflictType.PROFESSOR_CLASH;
    private static final ConflictType UPDATED_CONFLICT_TYPE = ConflictType.GROUP_CLASH;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ConflictSeverity DEFAULT_SEVERITY = ConflictSeverity.HARD;
    private static final ConflictSeverity UPDATED_SEVERITY = ConflictSeverity.SOFT;

    private static final String ENTITY_API_URL = "/api/schedule-conflicts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ScheduleConflictRepository scheduleConflictRepository;

    @Mock
    private ScheduleConflictRepository scheduleConflictRepositoryMock;

    @Autowired
    private ScheduleConflictMapper scheduleConflictMapper;

    @Mock
    private ScheduleConflictService scheduleConflictServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScheduleConflictMockMvc;

    private ScheduleConflict scheduleConflict;

    private ScheduleConflict insertedScheduleConflict;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduleConflict createEntity(EntityManager em) {
        ScheduleConflict scheduleConflict = new ScheduleConflict()
            .conflictType(DEFAULT_CONFLICT_TYPE)
            .description(DEFAULT_DESCRIPTION)
            .severity(DEFAULT_SEVERITY);
        // Add required entity
        TimetableVersion timetableVersion;
        if (TestUtil.findAll(em, TimetableVersion.class).isEmpty()) {
            timetableVersion = TimetableVersionResourceIT.createEntity(em);
            em.persist(timetableVersion);
            em.flush();
        } else {
            timetableVersion = TestUtil.findAll(em, TimetableVersion.class).get(0);
        }
        scheduleConflict.setTimetableVersion(timetableVersion);
        return scheduleConflict;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduleConflict createUpdatedEntity(EntityManager em) {
        ScheduleConflict updatedScheduleConflict = new ScheduleConflict()
            .conflictType(UPDATED_CONFLICT_TYPE)
            .description(UPDATED_DESCRIPTION)
            .severity(UPDATED_SEVERITY);
        // Add required entity
        TimetableVersion timetableVersion;
        if (TestUtil.findAll(em, TimetableVersion.class).isEmpty()) {
            timetableVersion = TimetableVersionResourceIT.createUpdatedEntity(em);
            em.persist(timetableVersion);
            em.flush();
        } else {
            timetableVersion = TestUtil.findAll(em, TimetableVersion.class).get(0);
        }
        updatedScheduleConflict.setTimetableVersion(timetableVersion);
        return updatedScheduleConflict;
    }

    @BeforeEach
    void initTest() {
        scheduleConflict = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedScheduleConflict != null) {
            scheduleConflictRepository.delete(insertedScheduleConflict);
            insertedScheduleConflict = null;
        }
    }

    @Test
    @Transactional
    void createScheduleConflict() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ScheduleConflict
        ScheduleConflictDTO scheduleConflictDTO = scheduleConflictMapper.toDto(scheduleConflict);
        var returnedScheduleConflictDTO = om.readValue(
            restScheduleConflictMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleConflictDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ScheduleConflictDTO.class
        );

        // Validate the ScheduleConflict in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedScheduleConflict = scheduleConflictMapper.toEntity(returnedScheduleConflictDTO);
        assertScheduleConflictUpdatableFieldsEquals(returnedScheduleConflict, getPersistedScheduleConflict(returnedScheduleConflict));

        insertedScheduleConflict = returnedScheduleConflict;
    }

    @Test
    @Transactional
    void createScheduleConflictWithExistingId() throws Exception {
        // Create the ScheduleConflict with an existing ID
        scheduleConflict.setId(1L);
        ScheduleConflictDTO scheduleConflictDTO = scheduleConflictMapper.toDto(scheduleConflict);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScheduleConflictMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleConflictDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ScheduleConflict in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkConflictTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduleConflict.setConflictType(null);

        // Create the ScheduleConflict, which fails.
        ScheduleConflictDTO scheduleConflictDTO = scheduleConflictMapper.toDto(scheduleConflict);

        restScheduleConflictMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleConflictDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduleConflict.setDescription(null);

        // Create the ScheduleConflict, which fails.
        ScheduleConflictDTO scheduleConflictDTO = scheduleConflictMapper.toDto(scheduleConflict);

        restScheduleConflictMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleConflictDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSeverityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduleConflict.setSeverity(null);

        // Create the ScheduleConflict, which fails.
        ScheduleConflictDTO scheduleConflictDTO = scheduleConflictMapper.toDto(scheduleConflict);

        restScheduleConflictMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleConflictDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllScheduleConflicts() throws Exception {
        // Initialize the database
        insertedScheduleConflict = scheduleConflictRepository.saveAndFlush(scheduleConflict);

        // Get all the scheduleConflictList
        restScheduleConflictMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheduleConflict.getId().intValue())))
            .andExpect(jsonPath("$.[*].conflictType").value(hasItem(DEFAULT_CONFLICT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].severity").value(hasItem(DEFAULT_SEVERITY.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllScheduleConflictsWithEagerRelationshipsIsEnabled() throws Exception {
        when(scheduleConflictServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restScheduleConflictMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(scheduleConflictServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllScheduleConflictsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(scheduleConflictServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restScheduleConflictMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(scheduleConflictRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getScheduleConflict() throws Exception {
        // Initialize the database
        insertedScheduleConflict = scheduleConflictRepository.saveAndFlush(scheduleConflict);

        // Get the scheduleConflict
        restScheduleConflictMockMvc
            .perform(get(ENTITY_API_URL_ID, scheduleConflict.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(scheduleConflict.getId().intValue()))
            .andExpect(jsonPath("$.conflictType").value(DEFAULT_CONFLICT_TYPE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.severity").value(DEFAULT_SEVERITY.toString()));
    }

    @Test
    @Transactional
    void getNonExistingScheduleConflict() throws Exception {
        // Get the scheduleConflict
        restScheduleConflictMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingScheduleConflict() throws Exception {
        // Initialize the database
        insertedScheduleConflict = scheduleConflictRepository.saveAndFlush(scheduleConflict);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduleConflict
        ScheduleConflict updatedScheduleConflict = scheduleConflictRepository.findById(scheduleConflict.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedScheduleConflict are not directly saved in db
        em.detach(updatedScheduleConflict);
        updatedScheduleConflict.conflictType(UPDATED_CONFLICT_TYPE).description(UPDATED_DESCRIPTION).severity(UPDATED_SEVERITY);
        ScheduleConflictDTO scheduleConflictDTO = scheduleConflictMapper.toDto(updatedScheduleConflict);

        restScheduleConflictMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleConflictDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleConflictDTO))
            )
            .andExpect(status().isOk());

        // Validate the ScheduleConflict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedScheduleConflictToMatchAllProperties(updatedScheduleConflict);
    }

    @Test
    @Transactional
    void putNonExistingScheduleConflict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleConflict.setId(longCount.incrementAndGet());

        // Create the ScheduleConflict
        ScheduleConflictDTO scheduleConflictDTO = scheduleConflictMapper.toDto(scheduleConflict);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleConflictMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleConflictDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleConflictDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleConflict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchScheduleConflict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleConflict.setId(longCount.incrementAndGet());

        // Create the ScheduleConflict
        ScheduleConflictDTO scheduleConflictDTO = scheduleConflictMapper.toDto(scheduleConflict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleConflictMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleConflictDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleConflict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamScheduleConflict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleConflict.setId(longCount.incrementAndGet());

        // Create the ScheduleConflict
        ScheduleConflictDTO scheduleConflictDTO = scheduleConflictMapper.toDto(scheduleConflict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleConflictMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleConflictDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScheduleConflict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateScheduleConflictWithPatch() throws Exception {
        // Initialize the database
        insertedScheduleConflict = scheduleConflictRepository.saveAndFlush(scheduleConflict);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduleConflict using partial update
        ScheduleConflict partialUpdatedScheduleConflict = new ScheduleConflict();
        partialUpdatedScheduleConflict.setId(scheduleConflict.getId());

        partialUpdatedScheduleConflict.severity(UPDATED_SEVERITY);

        restScheduleConflictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScheduleConflict.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScheduleConflict))
            )
            .andExpect(status().isOk());

        // Validate the ScheduleConflict in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScheduleConflictUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedScheduleConflict, scheduleConflict),
            getPersistedScheduleConflict(scheduleConflict)
        );
    }

    @Test
    @Transactional
    void fullUpdateScheduleConflictWithPatch() throws Exception {
        // Initialize the database
        insertedScheduleConflict = scheduleConflictRepository.saveAndFlush(scheduleConflict);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduleConflict using partial update
        ScheduleConflict partialUpdatedScheduleConflict = new ScheduleConflict();
        partialUpdatedScheduleConflict.setId(scheduleConflict.getId());

        partialUpdatedScheduleConflict.conflictType(UPDATED_CONFLICT_TYPE).description(UPDATED_DESCRIPTION).severity(UPDATED_SEVERITY);

        restScheduleConflictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScheduleConflict.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScheduleConflict))
            )
            .andExpect(status().isOk());

        // Validate the ScheduleConflict in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScheduleConflictUpdatableFieldsEquals(
            partialUpdatedScheduleConflict,
            getPersistedScheduleConflict(partialUpdatedScheduleConflict)
        );
    }

    @Test
    @Transactional
    void patchNonExistingScheduleConflict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleConflict.setId(longCount.incrementAndGet());

        // Create the ScheduleConflict
        ScheduleConflictDTO scheduleConflictDTO = scheduleConflictMapper.toDto(scheduleConflict);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleConflictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scheduleConflictDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduleConflictDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleConflict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchScheduleConflict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleConflict.setId(longCount.incrementAndGet());

        // Create the ScheduleConflict
        ScheduleConflictDTO scheduleConflictDTO = scheduleConflictMapper.toDto(scheduleConflict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleConflictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduleConflictDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleConflict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamScheduleConflict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleConflict.setId(longCount.incrementAndGet());

        // Create the ScheduleConflict
        ScheduleConflictDTO scheduleConflictDTO = scheduleConflictMapper.toDto(scheduleConflict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleConflictMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(scheduleConflictDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScheduleConflict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteScheduleConflict() throws Exception {
        // Initialize the database
        insertedScheduleConflict = scheduleConflictRepository.saveAndFlush(scheduleConflict);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the scheduleConflict
        restScheduleConflictMockMvc
            .perform(delete(ENTITY_API_URL_ID, scheduleConflict.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return scheduleConflictRepository.count();
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

    protected ScheduleConflict getPersistedScheduleConflict(ScheduleConflict scheduleConflict) {
        return scheduleConflictRepository.findById(scheduleConflict.getId()).orElseThrow();
    }

    protected void assertPersistedScheduleConflictToMatchAllProperties(ScheduleConflict expectedScheduleConflict) {
        assertScheduleConflictAllPropertiesEquals(expectedScheduleConflict, getPersistedScheduleConflict(expectedScheduleConflict));
    }

    protected void assertPersistedScheduleConflictToMatchUpdatableProperties(ScheduleConflict expectedScheduleConflict) {
        assertScheduleConflictAllUpdatablePropertiesEquals(
            expectedScheduleConflict,
            getPersistedScheduleConflict(expectedScheduleConflict)
        );
    }
}
