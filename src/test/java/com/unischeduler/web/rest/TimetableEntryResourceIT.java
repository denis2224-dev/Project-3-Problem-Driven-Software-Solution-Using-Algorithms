package com.unischeduler.web.rest;

import static com.unischeduler.domain.TimetableEntryAsserts.*;
import static com.unischeduler.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unischeduler.IntegrationTest;
import com.unischeduler.domain.CourseEvent;
import com.unischeduler.domain.Room;
import com.unischeduler.domain.Timeslot;
import com.unischeduler.domain.TimetableEntry;
import com.unischeduler.domain.TimetableVersion;
import com.unischeduler.security.AuthoritiesConstants;
import com.unischeduler.repository.TimetableEntryRepository;
import com.unischeduler.service.TimetableEntryService;
import com.unischeduler.service.dto.TimetableEntryDTO;
import com.unischeduler.service.mapper.TimetableEntryMapper;
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
 * Integration tests for the {@link TimetableEntryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class TimetableEntryResourceIT {

    private static final String ENTITY_API_URL = "/api/timetable-entries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TimetableEntryRepository timetableEntryRepository;

    @Mock
    private TimetableEntryRepository timetableEntryRepositoryMock;

    @Autowired
    private TimetableEntryMapper timetableEntryMapper;

    @Mock
    private TimetableEntryService timetableEntryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTimetableEntryMockMvc;

    private TimetableEntry timetableEntry;

    private TimetableEntry insertedTimetableEntry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimetableEntry createEntity(EntityManager em) {
        TimetableEntry timetableEntry = new TimetableEntry();
        // Add required entity
        TimetableVersion timetableVersion;
        if (TestUtil.findAll(em, TimetableVersion.class).isEmpty()) {
            timetableVersion = TimetableVersionResourceIT.createEntity(em);
            em.persist(timetableVersion);
            em.flush();
        } else {
            timetableVersion = TestUtil.findAll(em, TimetableVersion.class).get(0);
        }
        timetableEntry.setTimetableVersion(timetableVersion);
        // Add required entity
        CourseEvent courseEvent;
        if (TestUtil.findAll(em, CourseEvent.class).isEmpty()) {
            courseEvent = CourseEventResourceIT.createEntity(em);
            em.persist(courseEvent);
            em.flush();
        } else {
            courseEvent = TestUtil.findAll(em, CourseEvent.class).get(0);
        }
        timetableEntry.setCourseEvent(courseEvent);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createEntity(em);
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        timetableEntry.setRoom(room);
        // Add required entity
        Timeslot timeslot;
        if (TestUtil.findAll(em, Timeslot.class).isEmpty()) {
            timeslot = TimeslotResourceIT.createEntity();
            em.persist(timeslot);
            em.flush();
        } else {
            timeslot = TestUtil.findAll(em, Timeslot.class).get(0);
        }
        timetableEntry.setTimeslot(timeslot);
        return timetableEntry;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimetableEntry createUpdatedEntity(EntityManager em) {
        TimetableEntry updatedTimetableEntry = new TimetableEntry();
        // Add required entity
        TimetableVersion timetableVersion;
        if (TestUtil.findAll(em, TimetableVersion.class).isEmpty()) {
            timetableVersion = TimetableVersionResourceIT.createUpdatedEntity(em);
            em.persist(timetableVersion);
            em.flush();
        } else {
            timetableVersion = TestUtil.findAll(em, TimetableVersion.class).get(0);
        }
        updatedTimetableEntry.setTimetableVersion(timetableVersion);
        // Add required entity
        CourseEvent courseEvent;
        if (TestUtil.findAll(em, CourseEvent.class).isEmpty()) {
            courseEvent = CourseEventResourceIT.createUpdatedEntity(em);
            em.persist(courseEvent);
            em.flush();
        } else {
            courseEvent = TestUtil.findAll(em, CourseEvent.class).get(0);
        }
        updatedTimetableEntry.setCourseEvent(courseEvent);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createUpdatedEntity(em);
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        updatedTimetableEntry.setRoom(room);
        // Add required entity
        Timeslot timeslot;
        if (TestUtil.findAll(em, Timeslot.class).isEmpty()) {
            timeslot = TimeslotResourceIT.createUpdatedEntity();
            em.persist(timeslot);
            em.flush();
        } else {
            timeslot = TestUtil.findAll(em, Timeslot.class).get(0);
        }
        updatedTimetableEntry.setTimeslot(timeslot);
        return updatedTimetableEntry;
    }

    @BeforeEach
    void initTest() {
        timetableEntry = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTimetableEntry != null) {
            timetableEntryRepository.delete(insertedTimetableEntry);
            insertedTimetableEntry = null;
        }
    }

    @Test
    @Transactional
    void createTimetableEntry() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TimetableEntry
        TimetableEntryDTO timetableEntryDTO = timetableEntryMapper.toDto(timetableEntry);
        var returnedTimetableEntryDTO = om.readValue(
            restTimetableEntryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(timetableEntryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TimetableEntryDTO.class
        );

        // Validate the TimetableEntry in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTimetableEntry = timetableEntryMapper.toEntity(returnedTimetableEntryDTO);
        assertTimetableEntryUpdatableFieldsEquals(returnedTimetableEntry, getPersistedTimetableEntry(returnedTimetableEntry));

        insertedTimetableEntry = returnedTimetableEntry;
    }

    @Test
    @Transactional
    void createTimetableEntryWithExistingId() throws Exception {
        // Create the TimetableEntry with an existing ID
        timetableEntry.setId(1L);
        TimetableEntryDTO timetableEntryDTO = timetableEntryMapper.toDto(timetableEntry);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimetableEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(timetableEntryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TimetableEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTimetableEntries() throws Exception {
        // Initialize the database
        insertedTimetableEntry = timetableEntryRepository.saveAndFlush(timetableEntry);

        // Get all the timetableEntryList
        restTimetableEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timetableEntry.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTimetableEntriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(timetableEntryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTimetableEntryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(timetableEntryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTimetableEntriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(timetableEntryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTimetableEntryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(timetableEntryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTimetableEntry() throws Exception {
        // Initialize the database
        insertedTimetableEntry = timetableEntryRepository.saveAndFlush(timetableEntry);

        // Get the timetableEntry
        restTimetableEntryMockMvc
            .perform(get(ENTITY_API_URL_ID, timetableEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(timetableEntry.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingTimetableEntry() throws Exception {
        // Get the timetableEntry
        restTimetableEntryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTimetableEntry() throws Exception {
        // Initialize the database
        insertedTimetableEntry = timetableEntryRepository.saveAndFlush(timetableEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the timetableEntry
        TimetableEntry updatedTimetableEntry = timetableEntryRepository.findById(timetableEntry.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTimetableEntry are not directly saved in db
        em.detach(updatedTimetableEntry);
        TimetableEntryDTO timetableEntryDTO = timetableEntryMapper.toDto(updatedTimetableEntry);

        restTimetableEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timetableEntryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(timetableEntryDTO))
            )
            .andExpect(status().isOk());

        // Validate the TimetableEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTimetableEntryToMatchAllProperties(updatedTimetableEntry);
    }

    @Test
    @Transactional
    void putNonExistingTimetableEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetableEntry.setId(longCount.incrementAndGet());

        // Create the TimetableEntry
        TimetableEntryDTO timetableEntryDTO = timetableEntryMapper.toDto(timetableEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimetableEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timetableEntryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(timetableEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimetableEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTimetableEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetableEntry.setId(longCount.incrementAndGet());

        // Create the TimetableEntry
        TimetableEntryDTO timetableEntryDTO = timetableEntryMapper.toDto(timetableEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimetableEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(timetableEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimetableEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTimetableEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetableEntry.setId(longCount.incrementAndGet());

        // Create the TimetableEntry
        TimetableEntryDTO timetableEntryDTO = timetableEntryMapper.toDto(timetableEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimetableEntryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(timetableEntryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimetableEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTimetableEntryWithPatch() throws Exception {
        // Initialize the database
        insertedTimetableEntry = timetableEntryRepository.saveAndFlush(timetableEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the timetableEntry using partial update
        TimetableEntry partialUpdatedTimetableEntry = new TimetableEntry();
        partialUpdatedTimetableEntry.setId(timetableEntry.getId());

        restTimetableEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimetableEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTimetableEntry))
            )
            .andExpect(status().isOk());

        // Validate the TimetableEntry in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTimetableEntryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTimetableEntry, timetableEntry),
            getPersistedTimetableEntry(timetableEntry)
        );
    }

    @Test
    @Transactional
    void fullUpdateTimetableEntryWithPatch() throws Exception {
        // Initialize the database
        insertedTimetableEntry = timetableEntryRepository.saveAndFlush(timetableEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the timetableEntry using partial update
        TimetableEntry partialUpdatedTimetableEntry = new TimetableEntry();
        partialUpdatedTimetableEntry.setId(timetableEntry.getId());

        restTimetableEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimetableEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTimetableEntry))
            )
            .andExpect(status().isOk());

        // Validate the TimetableEntry in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTimetableEntryUpdatableFieldsEquals(partialUpdatedTimetableEntry, getPersistedTimetableEntry(partialUpdatedTimetableEntry));
    }

    @Test
    @Transactional
    void patchNonExistingTimetableEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetableEntry.setId(longCount.incrementAndGet());

        // Create the TimetableEntry
        TimetableEntryDTO timetableEntryDTO = timetableEntryMapper.toDto(timetableEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimetableEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, timetableEntryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(timetableEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimetableEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTimetableEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetableEntry.setId(longCount.incrementAndGet());

        // Create the TimetableEntry
        TimetableEntryDTO timetableEntryDTO = timetableEntryMapper.toDto(timetableEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimetableEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(timetableEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimetableEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTimetableEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetableEntry.setId(longCount.incrementAndGet());

        // Create the TimetableEntry
        TimetableEntryDTO timetableEntryDTO = timetableEntryMapper.toDto(timetableEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimetableEntryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(timetableEntryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimetableEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTimetableEntry() throws Exception {
        // Initialize the database
        insertedTimetableEntry = timetableEntryRepository.saveAndFlush(timetableEntry);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the timetableEntry
        restTimetableEntryMockMvc
            .perform(delete(ENTITY_API_URL_ID, timetableEntry.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return timetableEntryRepository.count();
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

    protected TimetableEntry getPersistedTimetableEntry(TimetableEntry timetableEntry) {
        return timetableEntryRepository.findById(timetableEntry.getId()).orElseThrow();
    }

    protected void assertPersistedTimetableEntryToMatchAllProperties(TimetableEntry expectedTimetableEntry) {
        assertTimetableEntryAllPropertiesEquals(expectedTimetableEntry, getPersistedTimetableEntry(expectedTimetableEntry));
    }

    protected void assertPersistedTimetableEntryToMatchUpdatableProperties(TimetableEntry expectedTimetableEntry) {
        assertTimetableEntryAllUpdatablePropertiesEquals(expectedTimetableEntry, getPersistedTimetableEntry(expectedTimetableEntry));
    }
}
