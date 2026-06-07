package com.unischeduler.web.rest;

import static com.unischeduler.domain.ProfessorPreferenceAsserts.*;
import static com.unischeduler.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unischeduler.IntegrationTest;
import com.unischeduler.domain.Professor;
import com.unischeduler.domain.ProfessorPreference;
import com.unischeduler.domain.Timeslot;
import com.unischeduler.domain.enumeration.ProfessorPreferenceType;
import com.unischeduler.security.AuthoritiesConstants;
import com.unischeduler.repository.ProfessorPreferenceRepository;
import com.unischeduler.service.ProfessorPreferenceService;
import com.unischeduler.service.dto.ProfessorPreferenceDTO;
import com.unischeduler.service.mapper.ProfessorPreferenceMapper;
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
 * Integration tests for the {@link ProfessorPreferenceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class ProfessorPreferenceResourceIT {

    private static final ProfessorPreferenceType DEFAULT_PREFERENCE_TYPE = ProfessorPreferenceType.PREFERRED;
    private static final ProfessorPreferenceType UPDATED_PREFERENCE_TYPE = ProfessorPreferenceType.UNAVAILABLE;

    private static final String ENTITY_API_URL = "/api/professor-preferences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProfessorPreferenceRepository professorPreferenceRepository;

    @Mock
    private ProfessorPreferenceRepository professorPreferenceRepositoryMock;

    @Autowired
    private ProfessorPreferenceMapper professorPreferenceMapper;

    @Mock
    private ProfessorPreferenceService professorPreferenceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProfessorPreferenceMockMvc;

    private ProfessorPreference professorPreference;

    private ProfessorPreference insertedProfessorPreference;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfessorPreference createEntity(EntityManager em) {
        ProfessorPreference professorPreference = new ProfessorPreference().preferenceType(DEFAULT_PREFERENCE_TYPE);
        // Add required entity
        Professor professor;
        if (TestUtil.findAll(em, Professor.class).isEmpty()) {
            professor = ProfessorResourceIT.createEntity(em);
            em.persist(professor);
            em.flush();
        } else {
            professor = TestUtil.findAll(em, Professor.class).get(0);
        }
        professorPreference.setProfessor(professor);
        // Add required entity
        Timeslot timeslot;
        if (TestUtil.findAll(em, Timeslot.class).isEmpty()) {
            timeslot = TimeslotResourceIT.createEntity();
            em.persist(timeslot);
            em.flush();
        } else {
            timeslot = TestUtil.findAll(em, Timeslot.class).get(0);
        }
        professorPreference.setTimeslot(timeslot);
        return professorPreference;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfessorPreference createUpdatedEntity(EntityManager em) {
        ProfessorPreference updatedProfessorPreference = new ProfessorPreference().preferenceType(UPDATED_PREFERENCE_TYPE);
        // Add required entity
        Professor professor;
        if (TestUtil.findAll(em, Professor.class).isEmpty()) {
            professor = ProfessorResourceIT.createUpdatedEntity(em);
            em.persist(professor);
            em.flush();
        } else {
            professor = TestUtil.findAll(em, Professor.class).get(0);
        }
        updatedProfessorPreference.setProfessor(professor);
        // Add required entity
        Timeslot timeslot;
        if (TestUtil.findAll(em, Timeslot.class).isEmpty()) {
            timeslot = TimeslotResourceIT.createUpdatedEntity();
            em.persist(timeslot);
            em.flush();
        } else {
            timeslot = TestUtil.findAll(em, Timeslot.class).get(0);
        }
        updatedProfessorPreference.setTimeslot(timeslot);
        return updatedProfessorPreference;
    }

    @BeforeEach
    void initTest() {
        professorPreference = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedProfessorPreference != null) {
            professorPreferenceRepository.delete(insertedProfessorPreference);
            insertedProfessorPreference = null;
        }
    }

    @Test
    @Transactional
    void createProfessorPreference() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProfessorPreference
        ProfessorPreferenceDTO professorPreferenceDTO = professorPreferenceMapper.toDto(professorPreference);
        var returnedProfessorPreferenceDTO = om.readValue(
            restProfessorPreferenceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(professorPreferenceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProfessorPreferenceDTO.class
        );

        // Validate the ProfessorPreference in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProfessorPreference = professorPreferenceMapper.toEntity(returnedProfessorPreferenceDTO);
        assertProfessorPreferenceUpdatableFieldsEquals(
            returnedProfessorPreference,
            getPersistedProfessorPreference(returnedProfessorPreference)
        );

        insertedProfessorPreference = returnedProfessorPreference;
    }

    @Test
    @Transactional
    void createProfessorPreferenceWithExistingId() throws Exception {
        // Create the ProfessorPreference with an existing ID
        professorPreference.setId(1L);
        ProfessorPreferenceDTO professorPreferenceDTO = professorPreferenceMapper.toDto(professorPreference);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfessorPreferenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(professorPreferenceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProfessorPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPreferenceTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        professorPreference.setPreferenceType(null);

        // Create the ProfessorPreference, which fails.
        ProfessorPreferenceDTO professorPreferenceDTO = professorPreferenceMapper.toDto(professorPreference);

        restProfessorPreferenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(professorPreferenceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProfessorPreferences() throws Exception {
        // Initialize the database
        insertedProfessorPreference = professorPreferenceRepository.saveAndFlush(professorPreference);

        // Get all the professorPreferenceList
        restProfessorPreferenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(professorPreference.getId().intValue())))
            .andExpect(jsonPath("$.[*].preferenceType").value(hasItem(DEFAULT_PREFERENCE_TYPE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProfessorPreferencesWithEagerRelationshipsIsEnabled() throws Exception {
        when(professorPreferenceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProfessorPreferenceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(professorPreferenceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProfessorPreferencesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(professorPreferenceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProfessorPreferenceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(professorPreferenceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProfessorPreference() throws Exception {
        // Initialize the database
        insertedProfessorPreference = professorPreferenceRepository.saveAndFlush(professorPreference);

        // Get the professorPreference
        restProfessorPreferenceMockMvc
            .perform(get(ENTITY_API_URL_ID, professorPreference.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(professorPreference.getId().intValue()))
            .andExpect(jsonPath("$.preferenceType").value(DEFAULT_PREFERENCE_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingProfessorPreference() throws Exception {
        // Get the professorPreference
        restProfessorPreferenceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProfessorPreference() throws Exception {
        // Initialize the database
        insertedProfessorPreference = professorPreferenceRepository.saveAndFlush(professorPreference);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the professorPreference
        ProfessorPreference updatedProfessorPreference = professorPreferenceRepository.findById(professorPreference.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProfessorPreference are not directly saved in db
        em.detach(updatedProfessorPreference);
        updatedProfessorPreference.preferenceType(UPDATED_PREFERENCE_TYPE);
        ProfessorPreferenceDTO professorPreferenceDTO = professorPreferenceMapper.toDto(updatedProfessorPreference);

        restProfessorPreferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, professorPreferenceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(professorPreferenceDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProfessorPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProfessorPreferenceToMatchAllProperties(updatedProfessorPreference);
    }

    @Test
    @Transactional
    void putNonExistingProfessorPreference() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        professorPreference.setId(longCount.incrementAndGet());

        // Create the ProfessorPreference
        ProfessorPreferenceDTO professorPreferenceDTO = professorPreferenceMapper.toDto(professorPreference);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfessorPreferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, professorPreferenceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(professorPreferenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfessorPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProfessorPreference() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        professorPreference.setId(longCount.incrementAndGet());

        // Create the ProfessorPreference
        ProfessorPreferenceDTO professorPreferenceDTO = professorPreferenceMapper.toDto(professorPreference);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfessorPreferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(professorPreferenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfessorPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProfessorPreference() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        professorPreference.setId(longCount.incrementAndGet());

        // Create the ProfessorPreference
        ProfessorPreferenceDTO professorPreferenceDTO = professorPreferenceMapper.toDto(professorPreference);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfessorPreferenceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(professorPreferenceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProfessorPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProfessorPreferenceWithPatch() throws Exception {
        // Initialize the database
        insertedProfessorPreference = professorPreferenceRepository.saveAndFlush(professorPreference);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the professorPreference using partial update
        ProfessorPreference partialUpdatedProfessorPreference = new ProfessorPreference();
        partialUpdatedProfessorPreference.setId(professorPreference.getId());

        partialUpdatedProfessorPreference.preferenceType(UPDATED_PREFERENCE_TYPE);

        restProfessorPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfessorPreference.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfessorPreference))
            )
            .andExpect(status().isOk());

        // Validate the ProfessorPreference in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfessorPreferenceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProfessorPreference, professorPreference),
            getPersistedProfessorPreference(professorPreference)
        );
    }

    @Test
    @Transactional
    void fullUpdateProfessorPreferenceWithPatch() throws Exception {
        // Initialize the database
        insertedProfessorPreference = professorPreferenceRepository.saveAndFlush(professorPreference);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the professorPreference using partial update
        ProfessorPreference partialUpdatedProfessorPreference = new ProfessorPreference();
        partialUpdatedProfessorPreference.setId(professorPreference.getId());

        partialUpdatedProfessorPreference.preferenceType(UPDATED_PREFERENCE_TYPE);

        restProfessorPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfessorPreference.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfessorPreference))
            )
            .andExpect(status().isOk());

        // Validate the ProfessorPreference in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfessorPreferenceUpdatableFieldsEquals(
            partialUpdatedProfessorPreference,
            getPersistedProfessorPreference(partialUpdatedProfessorPreference)
        );
    }

    @Test
    @Transactional
    void patchNonExistingProfessorPreference() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        professorPreference.setId(longCount.incrementAndGet());

        // Create the ProfessorPreference
        ProfessorPreferenceDTO professorPreferenceDTO = professorPreferenceMapper.toDto(professorPreference);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfessorPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, professorPreferenceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(professorPreferenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfessorPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProfessorPreference() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        professorPreference.setId(longCount.incrementAndGet());

        // Create the ProfessorPreference
        ProfessorPreferenceDTO professorPreferenceDTO = professorPreferenceMapper.toDto(professorPreference);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfessorPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(professorPreferenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfessorPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProfessorPreference() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        professorPreference.setId(longCount.incrementAndGet());

        // Create the ProfessorPreference
        ProfessorPreferenceDTO professorPreferenceDTO = professorPreferenceMapper.toDto(professorPreference);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfessorPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(professorPreferenceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProfessorPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProfessorPreference() throws Exception {
        // Initialize the database
        insertedProfessorPreference = professorPreferenceRepository.saveAndFlush(professorPreference);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the professorPreference
        restProfessorPreferenceMockMvc
            .perform(delete(ENTITY_API_URL_ID, professorPreference.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return professorPreferenceRepository.count();
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

    protected ProfessorPreference getPersistedProfessorPreference(ProfessorPreference professorPreference) {
        return professorPreferenceRepository.findById(professorPreference.getId()).orElseThrow();
    }

    protected void assertPersistedProfessorPreferenceToMatchAllProperties(ProfessorPreference expectedProfessorPreference) {
        assertProfessorPreferenceAllPropertiesEquals(
            expectedProfessorPreference,
            getPersistedProfessorPreference(expectedProfessorPreference)
        );
    }

    protected void assertPersistedProfessorPreferenceToMatchUpdatableProperties(ProfessorPreference expectedProfessorPreference) {
        assertProfessorPreferenceAllUpdatablePropertiesEquals(
            expectedProfessorPreference,
            getPersistedProfessorPreference(expectedProfessorPreference)
        );
    }
}
