package com.unischeduler.web.rest;

import static com.unischeduler.domain.ProfessorAsserts.*;
import static com.unischeduler.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unischeduler.IntegrationTest;
import com.unischeduler.domain.Department;
import com.unischeduler.domain.Professor;
import com.unischeduler.security.AuthoritiesConstants;
import com.unischeduler.repository.ProfessorRepository;
import com.unischeduler.service.ProfessorService;
import com.unischeduler.service.dto.ProfessorDTO;
import com.unischeduler.service.mapper.ProfessorMapper;
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
 * Integration tests for the {@link ProfessorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class ProfessorResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/professors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProfessorRepository professorRepository;

    @Mock
    private ProfessorRepository professorRepositoryMock;

    @Autowired
    private ProfessorMapper professorMapper;

    @Mock
    private ProfessorService professorServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProfessorMockMvc;

    private Professor professor;

    private Professor insertedProfessor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Professor createEntity(EntityManager em) {
        Professor professor = new Professor()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .title(DEFAULT_TITLE);
        // Add required entity
        Department department;
        if (TestUtil.findAll(em, Department.class).isEmpty()) {
            department = DepartmentResourceIT.createEntity(em);
            em.persist(department);
            em.flush();
        } else {
            department = TestUtil.findAll(em, Department.class).get(0);
        }
        professor.setDepartment(department);
        return professor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Professor createUpdatedEntity(EntityManager em) {
        Professor updatedProfessor = new Professor()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .title(UPDATED_TITLE);
        // Add required entity
        Department department;
        if (TestUtil.findAll(em, Department.class).isEmpty()) {
            department = DepartmentResourceIT.createUpdatedEntity(em);
            em.persist(department);
            em.flush();
        } else {
            department = TestUtil.findAll(em, Department.class).get(0);
        }
        updatedProfessor.setDepartment(department);
        return updatedProfessor;
    }

    @BeforeEach
    void initTest() {
        professor = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedProfessor != null) {
            professorRepository.delete(insertedProfessor);
            insertedProfessor = null;
        }
    }

    @Test
    @Transactional
    void createProfessor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Professor
        ProfessorDTO professorDTO = professorMapper.toDto(professor);
        var returnedProfessorDTO = om.readValue(
            restProfessorMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(professorDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProfessorDTO.class
        );

        // Validate the Professor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProfessor = professorMapper.toEntity(returnedProfessorDTO);
        assertProfessorUpdatableFieldsEquals(returnedProfessor, getPersistedProfessor(returnedProfessor));

        insertedProfessor = returnedProfessor;
    }

    @Test
    @Transactional
    void createProfessorWithExistingId() throws Exception {
        // Create the Professor with an existing ID
        professor.setId(1L);
        ProfessorDTO professorDTO = professorMapper.toDto(professor);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfessorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(professorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Professor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        professor.setFirstName(null);

        // Create the Professor, which fails.
        ProfessorDTO professorDTO = professorMapper.toDto(professor);

        restProfessorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(professorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        professor.setLastName(null);

        // Create the Professor, which fails.
        ProfessorDTO professorDTO = professorMapper.toDto(professor);

        restProfessorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(professorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        professor.setEmail(null);

        // Create the Professor, which fails.
        ProfessorDTO professorDTO = professorMapper.toDto(professor);

        restProfessorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(professorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProfessors() throws Exception {
        // Initialize the database
        insertedProfessor = professorRepository.saveAndFlush(professor);

        // Get all the professorList
        restProfessorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(professor.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProfessorsWithEagerRelationshipsIsEnabled() throws Exception {
        when(professorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProfessorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(professorServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProfessorsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(professorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProfessorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(professorRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProfessor() throws Exception {
        // Initialize the database
        insertedProfessor = professorRepository.saveAndFlush(professor);

        // Get the professor
        restProfessorMockMvc
            .perform(get(ENTITY_API_URL_ID, professor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(professor.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE));
    }

    @Test
    @Transactional
    void getNonExistingProfessor() throws Exception {
        // Get the professor
        restProfessorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProfessor() throws Exception {
        // Initialize the database
        insertedProfessor = professorRepository.saveAndFlush(professor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the professor
        Professor updatedProfessor = professorRepository.findById(professor.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProfessor are not directly saved in db
        em.detach(updatedProfessor);
        updatedProfessor.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL).title(UPDATED_TITLE);
        ProfessorDTO professorDTO = professorMapper.toDto(updatedProfessor);

        restProfessorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, professorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(professorDTO))
            )
            .andExpect(status().isOk());

        // Validate the Professor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProfessorToMatchAllProperties(updatedProfessor);
    }

    @Test
    @Transactional
    void putNonExistingProfessor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        professor.setId(longCount.incrementAndGet());

        // Create the Professor
        ProfessorDTO professorDTO = professorMapper.toDto(professor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfessorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, professorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(professorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Professor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProfessor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        professor.setId(longCount.incrementAndGet());

        // Create the Professor
        ProfessorDTO professorDTO = professorMapper.toDto(professor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfessorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(professorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Professor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProfessor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        professor.setId(longCount.incrementAndGet());

        // Create the Professor
        ProfessorDTO professorDTO = professorMapper.toDto(professor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfessorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(professorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Professor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProfessorWithPatch() throws Exception {
        // Initialize the database
        insertedProfessor = professorRepository.saveAndFlush(professor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the professor using partial update
        Professor partialUpdatedProfessor = new Professor();
        partialUpdatedProfessor.setId(professor.getId());

        partialUpdatedProfessor.firstName(UPDATED_FIRST_NAME);

        restProfessorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfessor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfessor))
            )
            .andExpect(status().isOk());

        // Validate the Professor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfessorUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProfessor, professor),
            getPersistedProfessor(professor)
        );
    }

    @Test
    @Transactional
    void fullUpdateProfessorWithPatch() throws Exception {
        // Initialize the database
        insertedProfessor = professorRepository.saveAndFlush(professor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the professor using partial update
        Professor partialUpdatedProfessor = new Professor();
        partialUpdatedProfessor.setId(professor.getId());

        partialUpdatedProfessor.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL).title(UPDATED_TITLE);

        restProfessorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfessor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfessor))
            )
            .andExpect(status().isOk());

        // Validate the Professor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfessorUpdatableFieldsEquals(partialUpdatedProfessor, getPersistedProfessor(partialUpdatedProfessor));
    }

    @Test
    @Transactional
    void patchNonExistingProfessor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        professor.setId(longCount.incrementAndGet());

        // Create the Professor
        ProfessorDTO professorDTO = professorMapper.toDto(professor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfessorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, professorDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(professorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Professor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProfessor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        professor.setId(longCount.incrementAndGet());

        // Create the Professor
        ProfessorDTO professorDTO = professorMapper.toDto(professor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfessorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(professorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Professor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProfessor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        professor.setId(longCount.incrementAndGet());

        // Create the Professor
        ProfessorDTO professorDTO = professorMapper.toDto(professor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfessorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(professorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Professor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProfessor() throws Exception {
        // Initialize the database
        insertedProfessor = professorRepository.saveAndFlush(professor);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the professor
        restProfessorMockMvc
            .perform(delete(ENTITY_API_URL_ID, professor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return professorRepository.count();
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

    protected Professor getPersistedProfessor(Professor professor) {
        return professorRepository.findById(professor.getId()).orElseThrow();
    }

    protected void assertPersistedProfessorToMatchAllProperties(Professor expectedProfessor) {
        assertProfessorAllPropertiesEquals(expectedProfessor, getPersistedProfessor(expectedProfessor));
    }

    protected void assertPersistedProfessorToMatchUpdatableProperties(Professor expectedProfessor) {
        assertProfessorAllUpdatablePropertiesEquals(expectedProfessor, getPersistedProfessor(expectedProfessor));
    }
}
