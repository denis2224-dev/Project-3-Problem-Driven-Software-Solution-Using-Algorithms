package com.unischeduler.web.rest;

import static com.unischeduler.domain.TimeslotAsserts.*;
import static com.unischeduler.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unischeduler.IntegrationTest;
import com.unischeduler.domain.Timeslot;
import com.unischeduler.domain.enumeration.AcademicDayOfWeek;
import com.unischeduler.repository.TimeslotRepository;
import com.unischeduler.service.dto.TimeslotDTO;
import com.unischeduler.service.mapper.TimeslotMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TimeslotResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TimeslotResourceIT {

    private static final AcademicDayOfWeek DEFAULT_DAY_OF_WEEK = AcademicDayOfWeek.MONDAY;
    private static final AcademicDayOfWeek UPDATED_DAY_OF_WEEK = AcademicDayOfWeek.TUESDAY;

    private static final String DEFAULT_START_TIME = "AAAAA";
    private static final String UPDATED_START_TIME = "BBBBB";

    private static final String DEFAULT_END_TIME = "AAAAA";
    private static final String UPDATED_END_TIME = "BBBBB";

    private static final String ENTITY_API_URL = "/api/timeslots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TimeslotRepository timeslotRepository;

    @Autowired
    private TimeslotMapper timeslotMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTimeslotMockMvc;

    private Timeslot timeslot;

    private Timeslot insertedTimeslot;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Timeslot createEntity() {
        return new Timeslot().dayOfWeek(DEFAULT_DAY_OF_WEEK).startTime(DEFAULT_START_TIME).endTime(DEFAULT_END_TIME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Timeslot createUpdatedEntity() {
        return new Timeslot().dayOfWeek(UPDATED_DAY_OF_WEEK).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);
    }

    @BeforeEach
    void initTest() {
        timeslot = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTimeslot != null) {
            timeslotRepository.delete(insertedTimeslot);
            insertedTimeslot = null;
        }
    }

    @Test
    @Transactional
    void createTimeslot() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Timeslot
        TimeslotDTO timeslotDTO = timeslotMapper.toDto(timeslot);
        var returnedTimeslotDTO = om.readValue(
            restTimeslotMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(timeslotDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TimeslotDTO.class
        );

        // Validate the Timeslot in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTimeslot = timeslotMapper.toEntity(returnedTimeslotDTO);
        assertTimeslotUpdatableFieldsEquals(returnedTimeslot, getPersistedTimeslot(returnedTimeslot));

        insertedTimeslot = returnedTimeslot;
    }

    @Test
    @Transactional
    void createTimeslotWithExistingId() throws Exception {
        // Create the Timeslot with an existing ID
        timeslot.setId(1L);
        TimeslotDTO timeslotDTO = timeslotMapper.toDto(timeslot);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimeslotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(timeslotDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Timeslot in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDayOfWeekIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        timeslot.setDayOfWeek(null);

        // Create the Timeslot, which fails.
        TimeslotDTO timeslotDTO = timeslotMapper.toDto(timeslot);

        restTimeslotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(timeslotDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        timeslot.setStartTime(null);

        // Create the Timeslot, which fails.
        TimeslotDTO timeslotDTO = timeslotMapper.toDto(timeslot);

        restTimeslotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(timeslotDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        timeslot.setEndTime(null);

        // Create the Timeslot, which fails.
        TimeslotDTO timeslotDTO = timeslotMapper.toDto(timeslot);

        restTimeslotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(timeslotDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTimeslots() throws Exception {
        // Initialize the database
        insertedTimeslot = timeslotRepository.saveAndFlush(timeslot);

        // Get all the timeslotList
        restTimeslotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeslot.getId().intValue())))
            .andExpect(jsonPath("$.[*].dayOfWeek").value(hasItem(DEFAULT_DAY_OF_WEEK.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME)))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME)));
    }

    @Test
    @Transactional
    void getTimeslot() throws Exception {
        // Initialize the database
        insertedTimeslot = timeslotRepository.saveAndFlush(timeslot);

        // Get the timeslot
        restTimeslotMockMvc
            .perform(get(ENTITY_API_URL_ID, timeslot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(timeslot.getId().intValue()))
            .andExpect(jsonPath("$.dayOfWeek").value(DEFAULT_DAY_OF_WEEK.toString()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME));
    }

    @Test
    @Transactional
    void getNonExistingTimeslot() throws Exception {
        // Get the timeslot
        restTimeslotMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTimeslot() throws Exception {
        // Initialize the database
        insertedTimeslot = timeslotRepository.saveAndFlush(timeslot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the timeslot
        Timeslot updatedTimeslot = timeslotRepository.findById(timeslot.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTimeslot are not directly saved in db
        em.detach(updatedTimeslot);
        updatedTimeslot.dayOfWeek(UPDATED_DAY_OF_WEEK).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);
        TimeslotDTO timeslotDTO = timeslotMapper.toDto(updatedTimeslot);

        restTimeslotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timeslotDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(timeslotDTO))
            )
            .andExpect(status().isOk());

        // Validate the Timeslot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTimeslotToMatchAllProperties(updatedTimeslot);
    }

    @Test
    @Transactional
    void putNonExistingTimeslot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timeslot.setId(longCount.incrementAndGet());

        // Create the Timeslot
        TimeslotDTO timeslotDTO = timeslotMapper.toDto(timeslot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeslotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timeslotDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(timeslotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Timeslot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTimeslot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timeslot.setId(longCount.incrementAndGet());

        // Create the Timeslot
        TimeslotDTO timeslotDTO = timeslotMapper.toDto(timeslot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeslotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(timeslotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Timeslot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTimeslot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timeslot.setId(longCount.incrementAndGet());

        // Create the Timeslot
        TimeslotDTO timeslotDTO = timeslotMapper.toDto(timeslot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeslotMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(timeslotDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Timeslot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTimeslotWithPatch() throws Exception {
        // Initialize the database
        insertedTimeslot = timeslotRepository.saveAndFlush(timeslot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the timeslot using partial update
        Timeslot partialUpdatedTimeslot = new Timeslot();
        partialUpdatedTimeslot.setId(timeslot.getId());

        partialUpdatedTimeslot.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);

        restTimeslotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimeslot.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTimeslot))
            )
            .andExpect(status().isOk());

        // Validate the Timeslot in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTimeslotUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTimeslot, timeslot), getPersistedTimeslot(timeslot));
    }

    @Test
    @Transactional
    void fullUpdateTimeslotWithPatch() throws Exception {
        // Initialize the database
        insertedTimeslot = timeslotRepository.saveAndFlush(timeslot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the timeslot using partial update
        Timeslot partialUpdatedTimeslot = new Timeslot();
        partialUpdatedTimeslot.setId(timeslot.getId());

        partialUpdatedTimeslot.dayOfWeek(UPDATED_DAY_OF_WEEK).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);

        restTimeslotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimeslot.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTimeslot))
            )
            .andExpect(status().isOk());

        // Validate the Timeslot in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTimeslotUpdatableFieldsEquals(partialUpdatedTimeslot, getPersistedTimeslot(partialUpdatedTimeslot));
    }

    @Test
    @Transactional
    void patchNonExistingTimeslot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timeslot.setId(longCount.incrementAndGet());

        // Create the Timeslot
        TimeslotDTO timeslotDTO = timeslotMapper.toDto(timeslot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeslotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, timeslotDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(timeslotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Timeslot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTimeslot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timeslot.setId(longCount.incrementAndGet());

        // Create the Timeslot
        TimeslotDTO timeslotDTO = timeslotMapper.toDto(timeslot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeslotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(timeslotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Timeslot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTimeslot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timeslot.setId(longCount.incrementAndGet());

        // Create the Timeslot
        TimeslotDTO timeslotDTO = timeslotMapper.toDto(timeslot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeslotMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(timeslotDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Timeslot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTimeslot() throws Exception {
        // Initialize the database
        insertedTimeslot = timeslotRepository.saveAndFlush(timeslot);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the timeslot
        restTimeslotMockMvc
            .perform(delete(ENTITY_API_URL_ID, timeslot.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return timeslotRepository.count();
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

    protected Timeslot getPersistedTimeslot(Timeslot timeslot) {
        return timeslotRepository.findById(timeslot.getId()).orElseThrow();
    }

    protected void assertPersistedTimeslotToMatchAllProperties(Timeslot expectedTimeslot) {
        assertTimeslotAllPropertiesEquals(expectedTimeslot, getPersistedTimeslot(expectedTimeslot));
    }

    protected void assertPersistedTimeslotToMatchUpdatableProperties(Timeslot expectedTimeslot) {
        assertTimeslotAllUpdatablePropertiesEquals(expectedTimeslot, getPersistedTimeslot(expectedTimeslot));
    }
}
