package com.unischeduler.web.rest;

import static com.unischeduler.domain.FacultyAsserts.*;
import static com.unischeduler.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unischeduler.IntegrationTest;
import com.unischeduler.domain.Faculty;
import com.unischeduler.security.AuthoritiesConstants;
import com.unischeduler.repository.FacultyRepository;
import com.unischeduler.service.dto.FacultyDTO;
import com.unischeduler.service.mapper.FacultyMapper;
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
 * Integration tests for the {@link FacultyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class FacultyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/faculties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private FacultyMapper facultyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFacultyMockMvc;

    private Faculty faculty;

    private Faculty insertedFaculty;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Faculty createEntity() {
        return new Faculty().name(DEFAULT_NAME).code(DEFAULT_CODE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Faculty createUpdatedEntity() {
        return new Faculty().name(UPDATED_NAME).code(UPDATED_CODE);
    }

    @BeforeEach
    void initTest() {
        faculty = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFaculty != null) {
            facultyRepository.delete(insertedFaculty);
            insertedFaculty = null;
        }
    }

    @Test
    @Transactional
    void createFaculty() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Faculty
        FacultyDTO facultyDTO = facultyMapper.toDto(faculty);
        var returnedFacultyDTO = om.readValue(
            restFacultyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facultyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FacultyDTO.class
        );

        // Validate the Faculty in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFaculty = facultyMapper.toEntity(returnedFacultyDTO);
        assertFacultyUpdatableFieldsEquals(returnedFaculty, getPersistedFaculty(returnedFaculty));

        insertedFaculty = returnedFaculty;
    }

    @Test
    @Transactional
    void createFacultyWithExistingId() throws Exception {
        // Create the Faculty with an existing ID
        faculty.setId(1L);
        FacultyDTO facultyDTO = facultyMapper.toDto(faculty);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFacultyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facultyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Faculty in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        faculty.setName(null);

        // Create the Faculty, which fails.
        FacultyDTO facultyDTO = facultyMapper.toDto(faculty);

        restFacultyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facultyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        faculty.setCode(null);

        // Create the Faculty, which fails.
        FacultyDTO facultyDTO = facultyMapper.toDto(faculty);

        restFacultyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facultyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFaculties() throws Exception {
        // Initialize the database
        insertedFaculty = facultyRepository.saveAndFlush(faculty);

        // Get all the facultyList
        restFacultyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(faculty.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));
    }

    @Test
    @Transactional
    void getFaculty() throws Exception {
        // Initialize the database
        insertedFaculty = facultyRepository.saveAndFlush(faculty);

        // Get the faculty
        restFacultyMockMvc
            .perform(get(ENTITY_API_URL_ID, faculty.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(faculty.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE));
    }

    @Test
    @Transactional
    void getNonExistingFaculty() throws Exception {
        // Get the faculty
        restFacultyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFaculty() throws Exception {
        // Initialize the database
        insertedFaculty = facultyRepository.saveAndFlush(faculty);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the faculty
        Faculty updatedFaculty = facultyRepository.findById(faculty.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFaculty are not directly saved in db
        em.detach(updatedFaculty);
        updatedFaculty.name(UPDATED_NAME).code(UPDATED_CODE);
        FacultyDTO facultyDTO = facultyMapper.toDto(updatedFaculty);

        restFacultyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, facultyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facultyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Faculty in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFacultyToMatchAllProperties(updatedFaculty);
    }

    @Test
    @Transactional
    void putNonExistingFaculty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        faculty.setId(longCount.incrementAndGet());

        // Create the Faculty
        FacultyDTO facultyDTO = facultyMapper.toDto(faculty);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacultyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, facultyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facultyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Faculty in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFaculty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        faculty.setId(longCount.incrementAndGet());

        // Create the Faculty
        FacultyDTO facultyDTO = facultyMapper.toDto(faculty);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacultyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(facultyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Faculty in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFaculty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        faculty.setId(longCount.incrementAndGet());

        // Create the Faculty
        FacultyDTO facultyDTO = facultyMapper.toDto(faculty);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacultyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facultyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Faculty in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFacultyWithPatch() throws Exception {
        // Initialize the database
        insertedFaculty = facultyRepository.saveAndFlush(faculty);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the faculty using partial update
        Faculty partialUpdatedFaculty = new Faculty();
        partialUpdatedFaculty.setId(faculty.getId());

        partialUpdatedFaculty.name(UPDATED_NAME).code(UPDATED_CODE);

        restFacultyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFaculty.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFaculty))
            )
            .andExpect(status().isOk());

        // Validate the Faculty in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFacultyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFaculty, faculty), getPersistedFaculty(faculty));
    }

    @Test
    @Transactional
    void fullUpdateFacultyWithPatch() throws Exception {
        // Initialize the database
        insertedFaculty = facultyRepository.saveAndFlush(faculty);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the faculty using partial update
        Faculty partialUpdatedFaculty = new Faculty();
        partialUpdatedFaculty.setId(faculty.getId());

        partialUpdatedFaculty.name(UPDATED_NAME).code(UPDATED_CODE);

        restFacultyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFaculty.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFaculty))
            )
            .andExpect(status().isOk());

        // Validate the Faculty in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFacultyUpdatableFieldsEquals(partialUpdatedFaculty, getPersistedFaculty(partialUpdatedFaculty));
    }

    @Test
    @Transactional
    void patchNonExistingFaculty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        faculty.setId(longCount.incrementAndGet());

        // Create the Faculty
        FacultyDTO facultyDTO = facultyMapper.toDto(faculty);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacultyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, facultyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(facultyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Faculty in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFaculty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        faculty.setId(longCount.incrementAndGet());

        // Create the Faculty
        FacultyDTO facultyDTO = facultyMapper.toDto(faculty);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacultyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(facultyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Faculty in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFaculty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        faculty.setId(longCount.incrementAndGet());

        // Create the Faculty
        FacultyDTO facultyDTO = facultyMapper.toDto(faculty);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacultyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(facultyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Faculty in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFaculty() throws Exception {
        // Initialize the database
        insertedFaculty = facultyRepository.saveAndFlush(faculty);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the faculty
        restFacultyMockMvc
            .perform(delete(ENTITY_API_URL_ID, faculty.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return facultyRepository.count();
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

    protected Faculty getPersistedFaculty(Faculty faculty) {
        return facultyRepository.findById(faculty.getId()).orElseThrow();
    }

    protected void assertPersistedFacultyToMatchAllProperties(Faculty expectedFaculty) {
        assertFacultyAllPropertiesEquals(expectedFaculty, getPersistedFaculty(expectedFaculty));
    }

    protected void assertPersistedFacultyToMatchUpdatableProperties(Faculty expectedFaculty) {
        assertFacultyAllUpdatablePropertiesEquals(expectedFaculty, getPersistedFaculty(expectedFaculty));
    }
}
