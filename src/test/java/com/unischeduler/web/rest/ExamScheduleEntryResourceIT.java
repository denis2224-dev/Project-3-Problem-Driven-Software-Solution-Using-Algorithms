package com.unischeduler.web.rest;

import static com.unischeduler.domain.ExamScheduleEntryAsserts.*;
import static com.unischeduler.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unischeduler.IntegrationTest;
import com.unischeduler.domain.Exam;
import com.unischeduler.domain.ExamScheduleEntry;
import com.unischeduler.domain.Room;
import com.unischeduler.domain.Timeslot;
import com.unischeduler.domain.TimetableVersion;
import com.unischeduler.repository.ExamScheduleEntryRepository;
import com.unischeduler.service.ExamScheduleEntryService;
import com.unischeduler.service.dto.ExamScheduleEntryDTO;
import com.unischeduler.service.mapper.ExamScheduleEntryMapper;
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
 * Integration tests for the {@link ExamScheduleEntryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ExamScheduleEntryResourceIT {

    private static final String ENTITY_API_URL = "/api/exam-schedule-entries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExamScheduleEntryRepository examScheduleEntryRepository;

    @Mock
    private ExamScheduleEntryRepository examScheduleEntryRepositoryMock;

    @Autowired
    private ExamScheduleEntryMapper examScheduleEntryMapper;

    @Mock
    private ExamScheduleEntryService examScheduleEntryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExamScheduleEntryMockMvc;

    private ExamScheduleEntry examScheduleEntry;

    private ExamScheduleEntry insertedExamScheduleEntry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExamScheduleEntry createEntity(EntityManager em) {
        ExamScheduleEntry examScheduleEntry = new ExamScheduleEntry();
        // Add required entity
        Exam exam;
        if (TestUtil.findAll(em, Exam.class).isEmpty()) {
            exam = ExamResourceIT.createEntity(em);
            em.persist(exam);
            em.flush();
        } else {
            exam = TestUtil.findAll(em, Exam.class).get(0);
        }
        examScheduleEntry.setExam(exam);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createEntity(em);
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        examScheduleEntry.setRoom(room);
        // Add required entity
        Timeslot timeslot;
        if (TestUtil.findAll(em, Timeslot.class).isEmpty()) {
            timeslot = TimeslotResourceIT.createEntity();
            em.persist(timeslot);
            em.flush();
        } else {
            timeslot = TestUtil.findAll(em, Timeslot.class).get(0);
        }
        examScheduleEntry.setTimeslot(timeslot);
        // Add required entity
        TimetableVersion timetableVersion;
        if (TestUtil.findAll(em, TimetableVersion.class).isEmpty()) {
            timetableVersion = TimetableVersionResourceIT.createEntity(em);
            em.persist(timetableVersion);
            em.flush();
        } else {
            timetableVersion = TestUtil.findAll(em, TimetableVersion.class).get(0);
        }
        examScheduleEntry.setTimetableVersion(timetableVersion);
        return examScheduleEntry;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExamScheduleEntry createUpdatedEntity(EntityManager em) {
        ExamScheduleEntry updatedExamScheduleEntry = new ExamScheduleEntry();
        // Add required entity
        Exam exam;
        if (TestUtil.findAll(em, Exam.class).isEmpty()) {
            exam = ExamResourceIT.createUpdatedEntity(em);
            em.persist(exam);
            em.flush();
        } else {
            exam = TestUtil.findAll(em, Exam.class).get(0);
        }
        updatedExamScheduleEntry.setExam(exam);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createUpdatedEntity(em);
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        updatedExamScheduleEntry.setRoom(room);
        // Add required entity
        Timeslot timeslot;
        if (TestUtil.findAll(em, Timeslot.class).isEmpty()) {
            timeslot = TimeslotResourceIT.createUpdatedEntity();
            em.persist(timeslot);
            em.flush();
        } else {
            timeslot = TestUtil.findAll(em, Timeslot.class).get(0);
        }
        updatedExamScheduleEntry.setTimeslot(timeslot);
        // Add required entity
        TimetableVersion timetableVersion;
        if (TestUtil.findAll(em, TimetableVersion.class).isEmpty()) {
            timetableVersion = TimetableVersionResourceIT.createUpdatedEntity(em);
            em.persist(timetableVersion);
            em.flush();
        } else {
            timetableVersion = TestUtil.findAll(em, TimetableVersion.class).get(0);
        }
        updatedExamScheduleEntry.setTimetableVersion(timetableVersion);
        return updatedExamScheduleEntry;
    }

    @BeforeEach
    void initTest() {
        examScheduleEntry = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedExamScheduleEntry != null) {
            examScheduleEntryRepository.delete(insertedExamScheduleEntry);
            insertedExamScheduleEntry = null;
        }
    }

    @Test
    @Transactional
    void createExamScheduleEntry() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ExamScheduleEntry
        ExamScheduleEntryDTO examScheduleEntryDTO = examScheduleEntryMapper.toDto(examScheduleEntry);
        var returnedExamScheduleEntryDTO = om.readValue(
            restExamScheduleEntryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examScheduleEntryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ExamScheduleEntryDTO.class
        );

        // Validate the ExamScheduleEntry in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedExamScheduleEntry = examScheduleEntryMapper.toEntity(returnedExamScheduleEntryDTO);
        assertExamScheduleEntryUpdatableFieldsEquals(returnedExamScheduleEntry, getPersistedExamScheduleEntry(returnedExamScheduleEntry));

        insertedExamScheduleEntry = returnedExamScheduleEntry;
    }

    @Test
    @Transactional
    void createExamScheduleEntryWithExistingId() throws Exception {
        // Create the ExamScheduleEntry with an existing ID
        examScheduleEntry.setId(1L);
        ExamScheduleEntryDTO examScheduleEntryDTO = examScheduleEntryMapper.toDto(examScheduleEntry);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExamScheduleEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examScheduleEntryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ExamScheduleEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllExamScheduleEntries() throws Exception {
        // Initialize the database
        insertedExamScheduleEntry = examScheduleEntryRepository.saveAndFlush(examScheduleEntry);

        // Get all the examScheduleEntryList
        restExamScheduleEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examScheduleEntry.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExamScheduleEntriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(examScheduleEntryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExamScheduleEntryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(examScheduleEntryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExamScheduleEntriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(examScheduleEntryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExamScheduleEntryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(examScheduleEntryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getExamScheduleEntry() throws Exception {
        // Initialize the database
        insertedExamScheduleEntry = examScheduleEntryRepository.saveAndFlush(examScheduleEntry);

        // Get the examScheduleEntry
        restExamScheduleEntryMockMvc
            .perform(get(ENTITY_API_URL_ID, examScheduleEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(examScheduleEntry.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingExamScheduleEntry() throws Exception {
        // Get the examScheduleEntry
        restExamScheduleEntryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExamScheduleEntry() throws Exception {
        // Initialize the database
        insertedExamScheduleEntry = examScheduleEntryRepository.saveAndFlush(examScheduleEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examScheduleEntry
        ExamScheduleEntry updatedExamScheduleEntry = examScheduleEntryRepository.findById(examScheduleEntry.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExamScheduleEntry are not directly saved in db
        em.detach(updatedExamScheduleEntry);
        ExamScheduleEntryDTO examScheduleEntryDTO = examScheduleEntryMapper.toDto(updatedExamScheduleEntry);

        restExamScheduleEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, examScheduleEntryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examScheduleEntryDTO))
            )
            .andExpect(status().isOk());

        // Validate the ExamScheduleEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExamScheduleEntryToMatchAllProperties(updatedExamScheduleEntry);
    }

    @Test
    @Transactional
    void putNonExistingExamScheduleEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examScheduleEntry.setId(longCount.incrementAndGet());

        // Create the ExamScheduleEntry
        ExamScheduleEntryDTO examScheduleEntryDTO = examScheduleEntryMapper.toDto(examScheduleEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamScheduleEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, examScheduleEntryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examScheduleEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamScheduleEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExamScheduleEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examScheduleEntry.setId(longCount.incrementAndGet());

        // Create the ExamScheduleEntry
        ExamScheduleEntryDTO examScheduleEntryDTO = examScheduleEntryMapper.toDto(examScheduleEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamScheduleEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examScheduleEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamScheduleEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExamScheduleEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examScheduleEntry.setId(longCount.incrementAndGet());

        // Create the ExamScheduleEntry
        ExamScheduleEntryDTO examScheduleEntryDTO = examScheduleEntryMapper.toDto(examScheduleEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamScheduleEntryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examScheduleEntryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExamScheduleEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExamScheduleEntryWithPatch() throws Exception {
        // Initialize the database
        insertedExamScheduleEntry = examScheduleEntryRepository.saveAndFlush(examScheduleEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examScheduleEntry using partial update
        ExamScheduleEntry partialUpdatedExamScheduleEntry = new ExamScheduleEntry();
        partialUpdatedExamScheduleEntry.setId(examScheduleEntry.getId());

        restExamScheduleEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamScheduleEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExamScheduleEntry))
            )
            .andExpect(status().isOk());

        // Validate the ExamScheduleEntry in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExamScheduleEntryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedExamScheduleEntry, examScheduleEntry),
            getPersistedExamScheduleEntry(examScheduleEntry)
        );
    }

    @Test
    @Transactional
    void fullUpdateExamScheduleEntryWithPatch() throws Exception {
        // Initialize the database
        insertedExamScheduleEntry = examScheduleEntryRepository.saveAndFlush(examScheduleEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examScheduleEntry using partial update
        ExamScheduleEntry partialUpdatedExamScheduleEntry = new ExamScheduleEntry();
        partialUpdatedExamScheduleEntry.setId(examScheduleEntry.getId());

        restExamScheduleEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamScheduleEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExamScheduleEntry))
            )
            .andExpect(status().isOk());

        // Validate the ExamScheduleEntry in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExamScheduleEntryUpdatableFieldsEquals(
            partialUpdatedExamScheduleEntry,
            getPersistedExamScheduleEntry(partialUpdatedExamScheduleEntry)
        );
    }

    @Test
    @Transactional
    void patchNonExistingExamScheduleEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examScheduleEntry.setId(longCount.incrementAndGet());

        // Create the ExamScheduleEntry
        ExamScheduleEntryDTO examScheduleEntryDTO = examScheduleEntryMapper.toDto(examScheduleEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamScheduleEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, examScheduleEntryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(examScheduleEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamScheduleEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExamScheduleEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examScheduleEntry.setId(longCount.incrementAndGet());

        // Create the ExamScheduleEntry
        ExamScheduleEntryDTO examScheduleEntryDTO = examScheduleEntryMapper.toDto(examScheduleEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamScheduleEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(examScheduleEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamScheduleEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExamScheduleEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examScheduleEntry.setId(longCount.incrementAndGet());

        // Create the ExamScheduleEntry
        ExamScheduleEntryDTO examScheduleEntryDTO = examScheduleEntryMapper.toDto(examScheduleEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamScheduleEntryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(examScheduleEntryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExamScheduleEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExamScheduleEntry() throws Exception {
        // Initialize the database
        insertedExamScheduleEntry = examScheduleEntryRepository.saveAndFlush(examScheduleEntry);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the examScheduleEntry
        restExamScheduleEntryMockMvc
            .perform(delete(ENTITY_API_URL_ID, examScheduleEntry.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return examScheduleEntryRepository.count();
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

    protected ExamScheduleEntry getPersistedExamScheduleEntry(ExamScheduleEntry examScheduleEntry) {
        return examScheduleEntryRepository.findById(examScheduleEntry.getId()).orElseThrow();
    }

    protected void assertPersistedExamScheduleEntryToMatchAllProperties(ExamScheduleEntry expectedExamScheduleEntry) {
        assertExamScheduleEntryAllPropertiesEquals(expectedExamScheduleEntry, getPersistedExamScheduleEntry(expectedExamScheduleEntry));
    }

    protected void assertPersistedExamScheduleEntryToMatchUpdatableProperties(ExamScheduleEntry expectedExamScheduleEntry) {
        assertExamScheduleEntryAllUpdatablePropertiesEquals(
            expectedExamScheduleEntry,
            getPersistedExamScheduleEntry(expectedExamScheduleEntry)
        );
    }
}
