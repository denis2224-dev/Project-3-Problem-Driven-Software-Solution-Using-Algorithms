package com.unischeduler.web.rest;

import static com.unischeduler.domain.ExamAsserts.*;
import static com.unischeduler.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unischeduler.IntegrationTest;
import com.unischeduler.domain.Course;
import com.unischeduler.domain.Exam;
import com.unischeduler.domain.StudentGroup;
import com.unischeduler.repository.ExamRepository;
import com.unischeduler.service.ExamService;
import com.unischeduler.service.dto.ExamDTO;
import com.unischeduler.service.mapper.ExamMapper;
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
 * Integration tests for the {@link ExamResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ExamResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_DURATION_MINUTES = 30;
    private static final Integer UPDATED_DURATION_MINUTES = 31;

    private static final Integer DEFAULT_EXPECTED_STUDENTS = 1;
    private static final Integer UPDATED_EXPECTED_STUDENTS = 2;

    private static final String ENTITY_API_URL = "/api/exams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExamRepository examRepository;

    @Mock
    private ExamRepository examRepositoryMock;

    @Autowired
    private ExamMapper examMapper;

    @Mock
    private ExamService examServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExamMockMvc;

    private Exam exam;

    private Exam insertedExam;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exam createEntity(EntityManager em) {
        Exam exam = new Exam().name(DEFAULT_NAME).durationMinutes(DEFAULT_DURATION_MINUTES).expectedStudents(DEFAULT_EXPECTED_STUDENTS);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        exam.setCourse(course);
        // Add required entity
        StudentGroup studentGroup;
        if (TestUtil.findAll(em, StudentGroup.class).isEmpty()) {
            studentGroup = StudentGroupResourceIT.createEntity(em);
            em.persist(studentGroup);
            em.flush();
        } else {
            studentGroup = TestUtil.findAll(em, StudentGroup.class).get(0);
        }
        exam.setStudentGroup(studentGroup);
        return exam;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exam createUpdatedEntity(EntityManager em) {
        Exam updatedExam = new Exam()
            .name(UPDATED_NAME)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .expectedStudents(UPDATED_EXPECTED_STUDENTS);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createUpdatedEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        updatedExam.setCourse(course);
        // Add required entity
        StudentGroup studentGroup;
        if (TestUtil.findAll(em, StudentGroup.class).isEmpty()) {
            studentGroup = StudentGroupResourceIT.createUpdatedEntity(em);
            em.persist(studentGroup);
            em.flush();
        } else {
            studentGroup = TestUtil.findAll(em, StudentGroup.class).get(0);
        }
        updatedExam.setStudentGroup(studentGroup);
        return updatedExam;
    }

    @BeforeEach
    void initTest() {
        exam = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedExam != null) {
            examRepository.delete(insertedExam);
            insertedExam = null;
        }
    }

    @Test
    @Transactional
    void createExam() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Exam
        ExamDTO examDTO = examMapper.toDto(exam);
        var returnedExamDTO = om.readValue(
            restExamMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ExamDTO.class
        );

        // Validate the Exam in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedExam = examMapper.toEntity(returnedExamDTO);
        assertExamUpdatableFieldsEquals(returnedExam, getPersistedExam(returnedExam));

        insertedExam = returnedExam;
    }

    @Test
    @Transactional
    void createExamWithExistingId() throws Exception {
        // Create the Exam with an existing ID
        exam.setId(1L);
        ExamDTO examDTO = examMapper.toDto(exam);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        exam.setName(null);

        // Create the Exam, which fails.
        ExamDTO examDTO = examMapper.toDto(exam);

        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDurationMinutesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        exam.setDurationMinutes(null);

        // Create the Exam, which fails.
        ExamDTO examDTO = examMapper.toDto(exam);

        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExpectedStudentsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        exam.setExpectedStudents(null);

        // Create the Exam, which fails.
        ExamDTO examDTO = examMapper.toDto(exam);

        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExams() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get all the examList
        restExamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(exam.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].durationMinutes").value(hasItem(DEFAULT_DURATION_MINUTES)))
            .andExpect(jsonPath("$.[*].expectedStudents").value(hasItem(DEFAULT_EXPECTED_STUDENTS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExamsWithEagerRelationshipsIsEnabled() throws Exception {
        when(examServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExamMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(examServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExamsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(examServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExamMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(examRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getExam() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        // Get the exam
        restExamMockMvc
            .perform(get(ENTITY_API_URL_ID, exam.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(exam.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.durationMinutes").value(DEFAULT_DURATION_MINUTES))
            .andExpect(jsonPath("$.expectedStudents").value(DEFAULT_EXPECTED_STUDENTS));
    }

    @Test
    @Transactional
    void getNonExistingExam() throws Exception {
        // Get the exam
        restExamMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExam() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exam
        Exam updatedExam = examRepository.findById(exam.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExam are not directly saved in db
        em.detach(updatedExam);
        updatedExam.name(UPDATED_NAME).durationMinutes(UPDATED_DURATION_MINUTES).expectedStudents(UPDATED_EXPECTED_STUDENTS);
        ExamDTO examDTO = examMapper.toDto(updatedExam);

        restExamMockMvc
            .perform(put(ENTITY_API_URL_ID, examDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isOk());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExamToMatchAllProperties(updatedExam);
    }

    @Test
    @Transactional
    void putNonExistingExam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exam.setId(longCount.incrementAndGet());

        // Create the Exam
        ExamDTO examDTO = examMapper.toDto(exam);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(put(ENTITY_API_URL_ID, examDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exam.setId(longCount.incrementAndGet());

        // Create the Exam
        ExamDTO examDTO = examMapper.toDto(exam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exam.setId(longCount.incrementAndGet());

        // Create the Exam
        ExamDTO examDTO = examMapper.toDto(exam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExamWithPatch() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exam using partial update
        Exam partialUpdatedExam = new Exam();
        partialUpdatedExam.setId(exam.getId());

        partialUpdatedExam.expectedStudents(UPDATED_EXPECTED_STUDENTS);

        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExam.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExam))
            )
            .andExpect(status().isOk());

        // Validate the Exam in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExamUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedExam, exam), getPersistedExam(exam));
    }

    @Test
    @Transactional
    void fullUpdateExamWithPatch() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exam using partial update
        Exam partialUpdatedExam = new Exam();
        partialUpdatedExam.setId(exam.getId());

        partialUpdatedExam.name(UPDATED_NAME).durationMinutes(UPDATED_DURATION_MINUTES).expectedStudents(UPDATED_EXPECTED_STUDENTS);

        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExam.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExam))
            )
            .andExpect(status().isOk());

        // Validate the Exam in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExamUpdatableFieldsEquals(partialUpdatedExam, getPersistedExam(partialUpdatedExam));
    }

    @Test
    @Transactional
    void patchNonExistingExam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exam.setId(longCount.incrementAndGet());

        // Create the Exam
        ExamDTO examDTO = examMapper.toDto(exam);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, examDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(examDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exam.setId(longCount.incrementAndGet());

        // Create the Exam
        ExamDTO examDTO = examMapper.toDto(exam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(examDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exam.setId(longCount.incrementAndGet());

        // Create the Exam
        ExamDTO examDTO = examMapper.toDto(exam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(examDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Exam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExam() throws Exception {
        // Initialize the database
        insertedExam = examRepository.saveAndFlush(exam);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the exam
        restExamMockMvc
            .perform(delete(ENTITY_API_URL_ID, exam.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return examRepository.count();
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

    protected Exam getPersistedExam(Exam exam) {
        return examRepository.findById(exam.getId()).orElseThrow();
    }

    protected void assertPersistedExamToMatchAllProperties(Exam expectedExam) {
        assertExamAllPropertiesEquals(expectedExam, getPersistedExam(expectedExam));
    }

    protected void assertPersistedExamToMatchUpdatableProperties(Exam expectedExam) {
        assertExamAllUpdatablePropertiesEquals(expectedExam, getPersistedExam(expectedExam));
    }
}
