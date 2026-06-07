package com.unischeduler.web.rest;

import static com.unischeduler.domain.SolverJobAsserts.*;
import static com.unischeduler.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unischeduler.IntegrationTest;
import com.unischeduler.domain.SolverJob;
import com.unischeduler.domain.enumeration.SolverJobStatus;
import com.unischeduler.security.AuthoritiesConstants;
import com.unischeduler.repository.SolverJobRepository;
import com.unischeduler.service.dto.SolverJobDTO;
import com.unischeduler.service.mapper.SolverJobMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link SolverJobResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class SolverJobResourceIT {

    private static final SolverJobStatus DEFAULT_STATUS = SolverJobStatus.CREATED;
    private static final SolverJobStatus UPDATED_STATUS = SolverJobStatus.VALIDATING_INPUT;

    private static final Instant DEFAULT_STARTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FINISHED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FINISHED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_PROGRESS_PERCENT = 0;
    private static final Integer UPDATED_PROGRESS_PERCENT = 1;

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final Integer DEFAULT_HARD_CONFLICT_COUNT = 0;
    private static final Integer UPDATED_HARD_CONFLICT_COUNT = 1;

    private static final Integer DEFAULT_SOFT_PENALTY_SCORE = 0;
    private static final Integer UPDATED_SOFT_PENALTY_SCORE = 1;

    private static final Integer DEFAULT_BACKTRACK_COUNT = 0;
    private static final Integer UPDATED_BACKTRACK_COUNT = 1;

    private static final Integer DEFAULT_DOMAIN_REDUCTION_COUNT = 0;
    private static final Integer UPDATED_DOMAIN_REDUCTION_COUNT = 1;

    private static final Long DEFAULT_RUNTIME_MS = 0L;
    private static final Long UPDATED_RUNTIME_MS = 1L;

    private static final String ENTITY_API_URL = "/api/solver-jobs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SolverJobRepository solverJobRepository;

    @Autowired
    private SolverJobMapper solverJobMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSolverJobMockMvc;

    private SolverJob solverJob;

    private SolverJob insertedSolverJob;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SolverJob createEntity() {
        return new SolverJob()
            .status(DEFAULT_STATUS)
            .startedAt(DEFAULT_STARTED_AT)
            .finishedAt(DEFAULT_FINISHED_AT)
            .progressPercent(DEFAULT_PROGRESS_PERCENT)
            .message(DEFAULT_MESSAGE)
            .hardConflictCount(DEFAULT_HARD_CONFLICT_COUNT)
            .softPenaltyScore(DEFAULT_SOFT_PENALTY_SCORE)
            .backtrackCount(DEFAULT_BACKTRACK_COUNT)
            .domainReductionCount(DEFAULT_DOMAIN_REDUCTION_COUNT)
            .runtimeMs(DEFAULT_RUNTIME_MS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SolverJob createUpdatedEntity() {
        return new SolverJob()
            .status(UPDATED_STATUS)
            .startedAt(UPDATED_STARTED_AT)
            .finishedAt(UPDATED_FINISHED_AT)
            .progressPercent(UPDATED_PROGRESS_PERCENT)
            .message(UPDATED_MESSAGE)
            .hardConflictCount(UPDATED_HARD_CONFLICT_COUNT)
            .softPenaltyScore(UPDATED_SOFT_PENALTY_SCORE)
            .backtrackCount(UPDATED_BACKTRACK_COUNT)
            .domainReductionCount(UPDATED_DOMAIN_REDUCTION_COUNT)
            .runtimeMs(UPDATED_RUNTIME_MS);
    }

    @BeforeEach
    void initTest() {
        solverJob = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSolverJob != null) {
            solverJobRepository.delete(insertedSolverJob);
            insertedSolverJob = null;
        }
    }

    @Test
    @Transactional
    void createSolverJob() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SolverJob
        SolverJobDTO solverJobDTO = solverJobMapper.toDto(solverJob);
        var returnedSolverJobDTO = om.readValue(
            restSolverJobMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solverJobDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SolverJobDTO.class
        );

        // Validate the SolverJob in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSolverJob = solverJobMapper.toEntity(returnedSolverJobDTO);
        assertSolverJobUpdatableFieldsEquals(returnedSolverJob, getPersistedSolverJob(returnedSolverJob));

        insertedSolverJob = returnedSolverJob;
    }

    @Test
    @Transactional
    void createSolverJobWithExistingId() throws Exception {
        // Create the SolverJob with an existing ID
        solverJob.setId(1L);
        SolverJobDTO solverJobDTO = solverJobMapper.toDto(solverJob);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSolverJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solverJobDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SolverJob in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solverJob.setStatus(null);

        // Create the SolverJob, which fails.
        SolverJobDTO solverJobDTO = solverJobMapper.toDto(solverJob);

        restSolverJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solverJobDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSolverJobs() throws Exception {
        // Initialize the database
        insertedSolverJob = solverJobRepository.saveAndFlush(solverJob);

        // Get all the solverJobList
        restSolverJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(solverJob.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startedAt").value(hasItem(DEFAULT_STARTED_AT.toString())))
            .andExpect(jsonPath("$.[*].finishedAt").value(hasItem(DEFAULT_FINISHED_AT.toString())))
            .andExpect(jsonPath("$.[*].progressPercent").value(hasItem(DEFAULT_PROGRESS_PERCENT)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].hardConflictCount").value(hasItem(DEFAULT_HARD_CONFLICT_COUNT)))
            .andExpect(jsonPath("$.[*].softPenaltyScore").value(hasItem(DEFAULT_SOFT_PENALTY_SCORE)))
            .andExpect(jsonPath("$.[*].backtrackCount").value(hasItem(DEFAULT_BACKTRACK_COUNT)))
            .andExpect(jsonPath("$.[*].domainReductionCount").value(hasItem(DEFAULT_DOMAIN_REDUCTION_COUNT)))
            .andExpect(jsonPath("$.[*].runtimeMs").value(hasItem(DEFAULT_RUNTIME_MS.intValue())));
    }

    @Test
    @Transactional
    void getSolverJob() throws Exception {
        // Initialize the database
        insertedSolverJob = solverJobRepository.saveAndFlush(solverJob);

        // Get the solverJob
        restSolverJobMockMvc
            .perform(get(ENTITY_API_URL_ID, solverJob.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(solverJob.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.startedAt").value(DEFAULT_STARTED_AT.toString()))
            .andExpect(jsonPath("$.finishedAt").value(DEFAULT_FINISHED_AT.toString()))
            .andExpect(jsonPath("$.progressPercent").value(DEFAULT_PROGRESS_PERCENT))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.hardConflictCount").value(DEFAULT_HARD_CONFLICT_COUNT))
            .andExpect(jsonPath("$.softPenaltyScore").value(DEFAULT_SOFT_PENALTY_SCORE))
            .andExpect(jsonPath("$.backtrackCount").value(DEFAULT_BACKTRACK_COUNT))
            .andExpect(jsonPath("$.domainReductionCount").value(DEFAULT_DOMAIN_REDUCTION_COUNT))
            .andExpect(jsonPath("$.runtimeMs").value(DEFAULT_RUNTIME_MS.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingSolverJob() throws Exception {
        // Get the solverJob
        restSolverJobMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSolverJob() throws Exception {
        // Initialize the database
        insertedSolverJob = solverJobRepository.saveAndFlush(solverJob);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solverJob
        SolverJob updatedSolverJob = solverJobRepository.findById(solverJob.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSolverJob are not directly saved in db
        em.detach(updatedSolverJob);
        updatedSolverJob
            .status(UPDATED_STATUS)
            .startedAt(UPDATED_STARTED_AT)
            .finishedAt(UPDATED_FINISHED_AT)
            .progressPercent(UPDATED_PROGRESS_PERCENT)
            .message(UPDATED_MESSAGE)
            .hardConflictCount(UPDATED_HARD_CONFLICT_COUNT)
            .softPenaltyScore(UPDATED_SOFT_PENALTY_SCORE)
            .backtrackCount(UPDATED_BACKTRACK_COUNT)
            .domainReductionCount(UPDATED_DOMAIN_REDUCTION_COUNT)
            .runtimeMs(UPDATED_RUNTIME_MS);
        SolverJobDTO solverJobDTO = solverJobMapper.toDto(updatedSolverJob);

        restSolverJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, solverJobDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(solverJobDTO))
            )
            .andExpect(status().isOk());

        // Validate the SolverJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSolverJobToMatchAllProperties(updatedSolverJob);
    }

    @Test
    @Transactional
    void putNonExistingSolverJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solverJob.setId(longCount.incrementAndGet());

        // Create the SolverJob
        SolverJobDTO solverJobDTO = solverJobMapper.toDto(solverJob);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSolverJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, solverJobDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(solverJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolverJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSolverJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solverJob.setId(longCount.incrementAndGet());

        // Create the SolverJob
        SolverJobDTO solverJobDTO = solverJobMapper.toDto(solverJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolverJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(solverJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolverJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSolverJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solverJob.setId(longCount.incrementAndGet());

        // Create the SolverJob
        SolverJobDTO solverJobDTO = solverJobMapper.toDto(solverJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolverJobMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solverJobDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SolverJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSolverJobWithPatch() throws Exception {
        // Initialize the database
        insertedSolverJob = solverJobRepository.saveAndFlush(solverJob);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solverJob using partial update
        SolverJob partialUpdatedSolverJob = new SolverJob();
        partialUpdatedSolverJob.setId(solverJob.getId());

        partialUpdatedSolverJob
            .status(UPDATED_STATUS)
            .startedAt(UPDATED_STARTED_AT)
            .progressPercent(UPDATED_PROGRESS_PERCENT)
            .message(UPDATED_MESSAGE)
            .hardConflictCount(UPDATED_HARD_CONFLICT_COUNT)
            .softPenaltyScore(UPDATED_SOFT_PENALTY_SCORE)
            .backtrackCount(UPDATED_BACKTRACK_COUNT)
            .runtimeMs(UPDATED_RUNTIME_MS);

        restSolverJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSolverJob.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSolverJob))
            )
            .andExpect(status().isOk());

        // Validate the SolverJob in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSolverJobUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSolverJob, solverJob),
            getPersistedSolverJob(solverJob)
        );
    }

    @Test
    @Transactional
    void fullUpdateSolverJobWithPatch() throws Exception {
        // Initialize the database
        insertedSolverJob = solverJobRepository.saveAndFlush(solverJob);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solverJob using partial update
        SolverJob partialUpdatedSolverJob = new SolverJob();
        partialUpdatedSolverJob.setId(solverJob.getId());

        partialUpdatedSolverJob
            .status(UPDATED_STATUS)
            .startedAt(UPDATED_STARTED_AT)
            .finishedAt(UPDATED_FINISHED_AT)
            .progressPercent(UPDATED_PROGRESS_PERCENT)
            .message(UPDATED_MESSAGE)
            .hardConflictCount(UPDATED_HARD_CONFLICT_COUNT)
            .softPenaltyScore(UPDATED_SOFT_PENALTY_SCORE)
            .backtrackCount(UPDATED_BACKTRACK_COUNT)
            .domainReductionCount(UPDATED_DOMAIN_REDUCTION_COUNT)
            .runtimeMs(UPDATED_RUNTIME_MS);

        restSolverJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSolverJob.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSolverJob))
            )
            .andExpect(status().isOk());

        // Validate the SolverJob in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSolverJobUpdatableFieldsEquals(partialUpdatedSolverJob, getPersistedSolverJob(partialUpdatedSolverJob));
    }

    @Test
    @Transactional
    void patchNonExistingSolverJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solverJob.setId(longCount.incrementAndGet());

        // Create the SolverJob
        SolverJobDTO solverJobDTO = solverJobMapper.toDto(solverJob);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSolverJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, solverJobDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(solverJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolverJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSolverJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solverJob.setId(longCount.incrementAndGet());

        // Create the SolverJob
        SolverJobDTO solverJobDTO = solverJobMapper.toDto(solverJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolverJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(solverJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolverJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSolverJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solverJob.setId(longCount.incrementAndGet());

        // Create the SolverJob
        SolverJobDTO solverJobDTO = solverJobMapper.toDto(solverJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolverJobMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(solverJobDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SolverJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSolverJob() throws Exception {
        // Initialize the database
        insertedSolverJob = solverJobRepository.saveAndFlush(solverJob);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the solverJob
        restSolverJobMockMvc
            .perform(delete(ENTITY_API_URL_ID, solverJob.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return solverJobRepository.count();
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

    protected SolverJob getPersistedSolverJob(SolverJob solverJob) {
        return solverJobRepository.findById(solverJob.getId()).orElseThrow();
    }

    protected void assertPersistedSolverJobToMatchAllProperties(SolverJob expectedSolverJob) {
        assertSolverJobAllPropertiesEquals(expectedSolverJob, getPersistedSolverJob(expectedSolverJob));
    }

    protected void assertPersistedSolverJobToMatchUpdatableProperties(SolverJob expectedSolverJob) {
        assertSolverJobAllUpdatablePropertiesEquals(expectedSolverJob, getPersistedSolverJob(expectedSolverJob));
    }
}
