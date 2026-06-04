package com.unischeduler.web.rest;

import static com.unischeduler.domain.StudentGroupAsserts.*;
import static com.unischeduler.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unischeduler.IntegrationTest;
import com.unischeduler.domain.Department;
import com.unischeduler.domain.StudentGroup;
import com.unischeduler.repository.StudentGroupRepository;
import com.unischeduler.service.StudentGroupService;
import com.unischeduler.service.dto.StudentGroupDTO;
import com.unischeduler.service.mapper.StudentGroupMapper;
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
 * Integration tests for the {@link StudentGroupResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StudentGroupResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_YEAR = 1;
    private static final Integer UPDATED_YEAR = 2;

    private static final Integer DEFAULT_GROUP_SIZE = 1;
    private static final Integer UPDATED_GROUP_SIZE = 2;

    private static final String ENTITY_API_URL = "/api/student-groups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StudentGroupRepository studentGroupRepository;

    @Mock
    private StudentGroupRepository studentGroupRepositoryMock;

    @Autowired
    private StudentGroupMapper studentGroupMapper;

    @Mock
    private StudentGroupService studentGroupServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStudentGroupMockMvc;

    private StudentGroup studentGroup;

    private StudentGroup insertedStudentGroup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentGroup createEntity(EntityManager em) {
        StudentGroup studentGroup = new StudentGroup().name(DEFAULT_NAME).year(DEFAULT_YEAR).groupSize(DEFAULT_GROUP_SIZE);
        // Add required entity
        Department department;
        if (TestUtil.findAll(em, Department.class).isEmpty()) {
            department = DepartmentResourceIT.createEntity(em);
            em.persist(department);
            em.flush();
        } else {
            department = TestUtil.findAll(em, Department.class).get(0);
        }
        studentGroup.setDepartment(department);
        return studentGroup;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentGroup createUpdatedEntity(EntityManager em) {
        StudentGroup updatedStudentGroup = new StudentGroup().name(UPDATED_NAME).year(UPDATED_YEAR).groupSize(UPDATED_GROUP_SIZE);
        // Add required entity
        Department department;
        if (TestUtil.findAll(em, Department.class).isEmpty()) {
            department = DepartmentResourceIT.createUpdatedEntity(em);
            em.persist(department);
            em.flush();
        } else {
            department = TestUtil.findAll(em, Department.class).get(0);
        }
        updatedStudentGroup.setDepartment(department);
        return updatedStudentGroup;
    }

    @BeforeEach
    void initTest() {
        studentGroup = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedStudentGroup != null) {
            studentGroupRepository.delete(insertedStudentGroup);
            insertedStudentGroup = null;
        }
    }

    @Test
    @Transactional
    void createStudentGroup() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StudentGroup
        StudentGroupDTO studentGroupDTO = studentGroupMapper.toDto(studentGroup);
        var returnedStudentGroupDTO = om.readValue(
            restStudentGroupMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentGroupDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StudentGroupDTO.class
        );

        // Validate the StudentGroup in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStudentGroup = studentGroupMapper.toEntity(returnedStudentGroupDTO);
        assertStudentGroupUpdatableFieldsEquals(returnedStudentGroup, getPersistedStudentGroup(returnedStudentGroup));

        insertedStudentGroup = returnedStudentGroup;
    }

    @Test
    @Transactional
    void createStudentGroupWithExistingId() throws Exception {
        // Create the StudentGroup with an existing ID
        studentGroup.setId(1L);
        StudentGroupDTO studentGroupDTO = studentGroupMapper.toDto(studentGroup);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStudentGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentGroupDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StudentGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        studentGroup.setName(null);

        // Create the StudentGroup, which fails.
        StudentGroupDTO studentGroupDTO = studentGroupMapper.toDto(studentGroup);

        restStudentGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentGroupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkYearIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        studentGroup.setYear(null);

        // Create the StudentGroup, which fails.
        StudentGroupDTO studentGroupDTO = studentGroupMapper.toDto(studentGroup);

        restStudentGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentGroupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGroupSizeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        studentGroup.setGroupSize(null);

        // Create the StudentGroup, which fails.
        StudentGroupDTO studentGroupDTO = studentGroupMapper.toDto(studentGroup);

        restStudentGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentGroupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStudentGroups() throws Exception {
        // Initialize the database
        insertedStudentGroup = studentGroupRepository.saveAndFlush(studentGroup);

        // Get all the studentGroupList
        restStudentGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(studentGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].groupSize").value(hasItem(DEFAULT_GROUP_SIZE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStudentGroupsWithEagerRelationshipsIsEnabled() throws Exception {
        when(studentGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStudentGroupMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(studentGroupServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStudentGroupsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(studentGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStudentGroupMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(studentGroupRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStudentGroup() throws Exception {
        // Initialize the database
        insertedStudentGroup = studentGroupRepository.saveAndFlush(studentGroup);

        // Get the studentGroup
        restStudentGroupMockMvc
            .perform(get(ENTITY_API_URL_ID, studentGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(studentGroup.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR))
            .andExpect(jsonPath("$.groupSize").value(DEFAULT_GROUP_SIZE));
    }

    @Test
    @Transactional
    void getNonExistingStudentGroup() throws Exception {
        // Get the studentGroup
        restStudentGroupMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStudentGroup() throws Exception {
        // Initialize the database
        insertedStudentGroup = studentGroupRepository.saveAndFlush(studentGroup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentGroup
        StudentGroup updatedStudentGroup = studentGroupRepository.findById(studentGroup.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStudentGroup are not directly saved in db
        em.detach(updatedStudentGroup);
        updatedStudentGroup.name(UPDATED_NAME).year(UPDATED_YEAR).groupSize(UPDATED_GROUP_SIZE);
        StudentGroupDTO studentGroupDTO = studentGroupMapper.toDto(updatedStudentGroup);

        restStudentGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studentGroupDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentGroupDTO))
            )
            .andExpect(status().isOk());

        // Validate the StudentGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStudentGroupToMatchAllProperties(updatedStudentGroup);
    }

    @Test
    @Transactional
    void putNonExistingStudentGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentGroup.setId(longCount.incrementAndGet());

        // Create the StudentGroup
        StudentGroupDTO studentGroupDTO = studentGroupMapper.toDto(studentGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studentGroupDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStudentGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentGroup.setId(longCount.incrementAndGet());

        // Create the StudentGroup
        StudentGroupDTO studentGroupDTO = studentGroupMapper.toDto(studentGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStudentGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentGroup.setId(longCount.incrementAndGet());

        // Create the StudentGroup
        StudentGroupDTO studentGroupDTO = studentGroupMapper.toDto(studentGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentGroupMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentGroupDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudentGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStudentGroupWithPatch() throws Exception {
        // Initialize the database
        insertedStudentGroup = studentGroupRepository.saveAndFlush(studentGroup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentGroup using partial update
        StudentGroup partialUpdatedStudentGroup = new StudentGroup();
        partialUpdatedStudentGroup.setId(studentGroup.getId());

        partialUpdatedStudentGroup.year(UPDATED_YEAR).groupSize(UPDATED_GROUP_SIZE);

        restStudentGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudentGroup.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudentGroup))
            )
            .andExpect(status().isOk());

        // Validate the StudentGroup in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentGroupUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStudentGroup, studentGroup),
            getPersistedStudentGroup(studentGroup)
        );
    }

    @Test
    @Transactional
    void fullUpdateStudentGroupWithPatch() throws Exception {
        // Initialize the database
        insertedStudentGroup = studentGroupRepository.saveAndFlush(studentGroup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentGroup using partial update
        StudentGroup partialUpdatedStudentGroup = new StudentGroup();
        partialUpdatedStudentGroup.setId(studentGroup.getId());

        partialUpdatedStudentGroup.name(UPDATED_NAME).year(UPDATED_YEAR).groupSize(UPDATED_GROUP_SIZE);

        restStudentGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudentGroup.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudentGroup))
            )
            .andExpect(status().isOk());

        // Validate the StudentGroup in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentGroupUpdatableFieldsEquals(partialUpdatedStudentGroup, getPersistedStudentGroup(partialUpdatedStudentGroup));
    }

    @Test
    @Transactional
    void patchNonExistingStudentGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentGroup.setId(longCount.incrementAndGet());

        // Create the StudentGroup
        StudentGroupDTO studentGroupDTO = studentGroupMapper.toDto(studentGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, studentGroupDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studentGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStudentGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentGroup.setId(longCount.incrementAndGet());

        // Create the StudentGroup
        StudentGroupDTO studentGroupDTO = studentGroupMapper.toDto(studentGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studentGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStudentGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentGroup.setId(longCount.incrementAndGet());

        // Create the StudentGroup
        StudentGroupDTO studentGroupDTO = studentGroupMapper.toDto(studentGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentGroupMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(studentGroupDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudentGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStudentGroup() throws Exception {
        // Initialize the database
        insertedStudentGroup = studentGroupRepository.saveAndFlush(studentGroup);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the studentGroup
        restStudentGroupMockMvc
            .perform(delete(ENTITY_API_URL_ID, studentGroup.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return studentGroupRepository.count();
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

    protected StudentGroup getPersistedStudentGroup(StudentGroup studentGroup) {
        return studentGroupRepository.findById(studentGroup.getId()).orElseThrow();
    }

    protected void assertPersistedStudentGroupToMatchAllProperties(StudentGroup expectedStudentGroup) {
        assertStudentGroupAllPropertiesEquals(expectedStudentGroup, getPersistedStudentGroup(expectedStudentGroup));
    }

    protected void assertPersistedStudentGroupToMatchUpdatableProperties(StudentGroup expectedStudentGroup) {
        assertStudentGroupAllUpdatablePropertiesEquals(expectedStudentGroup, getPersistedStudentGroup(expectedStudentGroup));
    }
}
