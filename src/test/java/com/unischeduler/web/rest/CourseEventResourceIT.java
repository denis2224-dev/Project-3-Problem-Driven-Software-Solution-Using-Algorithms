package com.unischeduler.web.rest;

import static com.unischeduler.domain.CourseEventAsserts.*;
import static com.unischeduler.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unischeduler.IntegrationTest;
import com.unischeduler.domain.Course;
import com.unischeduler.domain.CourseEvent;
import com.unischeduler.domain.Professor;
import com.unischeduler.domain.StudentGroup;
import com.unischeduler.domain.enumeration.CourseEventType;
import com.unischeduler.repository.CourseEventRepository;
import com.unischeduler.service.CourseEventService;
import com.unischeduler.service.dto.CourseEventDTO;
import com.unischeduler.service.mapper.CourseEventMapper;
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
 * Integration tests for the {@link CourseEventResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CourseEventResourceIT {

    private static final CourseEventType DEFAULT_EVENT_TYPE = CourseEventType.LECTURE;
    private static final CourseEventType UPDATED_EVENT_TYPE = CourseEventType.LABORATORY;

    private static final Integer DEFAULT_DURATION_MINUTES = 30;
    private static final Integer UPDATED_DURATION_MINUTES = 31;

    private static final Integer DEFAULT_EXPECTED_STUDENTS = 1;
    private static final Integer UPDATED_EXPECTED_STUDENTS = 2;

    private static final String DEFAULT_REQUIRED_EQUIPMENT = "AAAAAAAAAA";
    private static final String UPDATED_REQUIRED_EQUIPMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/course-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CourseEventRepository courseEventRepository;

    @Mock
    private CourseEventRepository courseEventRepositoryMock;

    @Autowired
    private CourseEventMapper courseEventMapper;

    @Mock
    private CourseEventService courseEventServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseEventMockMvc;

    private CourseEvent courseEvent;

    private CourseEvent insertedCourseEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseEvent createEntity(EntityManager em) {
        CourseEvent courseEvent = new CourseEvent()
            .eventType(DEFAULT_EVENT_TYPE)
            .durationMinutes(DEFAULT_DURATION_MINUTES)
            .expectedStudents(DEFAULT_EXPECTED_STUDENTS)
            .requiredEquipment(DEFAULT_REQUIRED_EQUIPMENT);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        courseEvent.setCourse(course);
        // Add required entity
        Professor professor;
        if (TestUtil.findAll(em, Professor.class).isEmpty()) {
            professor = ProfessorResourceIT.createEntity(em);
            em.persist(professor);
            em.flush();
        } else {
            professor = TestUtil.findAll(em, Professor.class).get(0);
        }
        courseEvent.setProfessor(professor);
        // Add required entity
        StudentGroup studentGroup;
        if (TestUtil.findAll(em, StudentGroup.class).isEmpty()) {
            studentGroup = StudentGroupResourceIT.createEntity(em);
            em.persist(studentGroup);
            em.flush();
        } else {
            studentGroup = TestUtil.findAll(em, StudentGroup.class).get(0);
        }
        courseEvent.setStudentGroup(studentGroup);
        return courseEvent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseEvent createUpdatedEntity(EntityManager em) {
        CourseEvent updatedCourseEvent = new CourseEvent()
            .eventType(UPDATED_EVENT_TYPE)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .expectedStudents(UPDATED_EXPECTED_STUDENTS)
            .requiredEquipment(UPDATED_REQUIRED_EQUIPMENT);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createUpdatedEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        updatedCourseEvent.setCourse(course);
        // Add required entity
        Professor professor;
        if (TestUtil.findAll(em, Professor.class).isEmpty()) {
            professor = ProfessorResourceIT.createUpdatedEntity(em);
            em.persist(professor);
            em.flush();
        } else {
            professor = TestUtil.findAll(em, Professor.class).get(0);
        }
        updatedCourseEvent.setProfessor(professor);
        // Add required entity
        StudentGroup studentGroup;
        if (TestUtil.findAll(em, StudentGroup.class).isEmpty()) {
            studentGroup = StudentGroupResourceIT.createUpdatedEntity(em);
            em.persist(studentGroup);
            em.flush();
        } else {
            studentGroup = TestUtil.findAll(em, StudentGroup.class).get(0);
        }
        updatedCourseEvent.setStudentGroup(studentGroup);
        return updatedCourseEvent;
    }

    @BeforeEach
    void initTest() {
        courseEvent = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCourseEvent != null) {
            courseEventRepository.delete(insertedCourseEvent);
            insertedCourseEvent = null;
        }
    }

    @Test
    @Transactional
    void createCourseEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CourseEvent
        CourseEventDTO courseEventDTO = courseEventMapper.toDto(courseEvent);
        var returnedCourseEventDTO = om.readValue(
            restCourseEventMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseEventDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CourseEventDTO.class
        );

        // Validate the CourseEvent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCourseEvent = courseEventMapper.toEntity(returnedCourseEventDTO);
        assertCourseEventUpdatableFieldsEquals(returnedCourseEvent, getPersistedCourseEvent(returnedCourseEvent));

        insertedCourseEvent = returnedCourseEvent;
    }

    @Test
    @Transactional
    void createCourseEventWithExistingId() throws Exception {
        // Create the CourseEvent with an existing ID
        courseEvent.setId(1L);
        CourseEventDTO courseEventDTO = courseEventMapper.toDto(courseEvent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseEventDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CourseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEventTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseEvent.setEventType(null);

        // Create the CourseEvent, which fails.
        CourseEventDTO courseEventDTO = courseEventMapper.toDto(courseEvent);

        restCourseEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDurationMinutesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseEvent.setDurationMinutes(null);

        // Create the CourseEvent, which fails.
        CourseEventDTO courseEventDTO = courseEventMapper.toDto(courseEvent);

        restCourseEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExpectedStudentsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        courseEvent.setExpectedStudents(null);

        // Create the CourseEvent, which fails.
        CourseEventDTO courseEventDTO = courseEventMapper.toDto(courseEvent);

        restCourseEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseEventDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCourseEvents() throws Exception {
        // Initialize the database
        insertedCourseEvent = courseEventRepository.saveAndFlush(courseEvent);

        // Get all the courseEventList
        restCourseEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventType").value(hasItem(DEFAULT_EVENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].durationMinutes").value(hasItem(DEFAULT_DURATION_MINUTES)))
            .andExpect(jsonPath("$.[*].expectedStudents").value(hasItem(DEFAULT_EXPECTED_STUDENTS)))
            .andExpect(jsonPath("$.[*].requiredEquipment").value(hasItem(DEFAULT_REQUIRED_EQUIPMENT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseEventsWithEagerRelationshipsIsEnabled() throws Exception {
        when(courseEventServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(courseEventServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCourseEventsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(courseEventServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(courseEventRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCourseEvent() throws Exception {
        // Initialize the database
        insertedCourseEvent = courseEventRepository.saveAndFlush(courseEvent);

        // Get the courseEvent
        restCourseEventMockMvc
            .perform(get(ENTITY_API_URL_ID, courseEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(courseEvent.getId().intValue()))
            .andExpect(jsonPath("$.eventType").value(DEFAULT_EVENT_TYPE.toString()))
            .andExpect(jsonPath("$.durationMinutes").value(DEFAULT_DURATION_MINUTES))
            .andExpect(jsonPath("$.expectedStudents").value(DEFAULT_EXPECTED_STUDENTS))
            .andExpect(jsonPath("$.requiredEquipment").value(DEFAULT_REQUIRED_EQUIPMENT));
    }

    @Test
    @Transactional
    void getNonExistingCourseEvent() throws Exception {
        // Get the courseEvent
        restCourseEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCourseEvent() throws Exception {
        // Initialize the database
        insertedCourseEvent = courseEventRepository.saveAndFlush(courseEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseEvent
        CourseEvent updatedCourseEvent = courseEventRepository.findById(courseEvent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCourseEvent are not directly saved in db
        em.detach(updatedCourseEvent);
        updatedCourseEvent
            .eventType(UPDATED_EVENT_TYPE)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .expectedStudents(UPDATED_EXPECTED_STUDENTS)
            .requiredEquipment(UPDATED_REQUIRED_EQUIPMENT);
        CourseEventDTO courseEventDTO = courseEventMapper.toDto(updatedCourseEvent);

        restCourseEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseEventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseEventDTO))
            )
            .andExpect(status().isOk());

        // Validate the CourseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCourseEventToMatchAllProperties(updatedCourseEvent);
    }

    @Test
    @Transactional
    void putNonExistingCourseEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseEvent.setId(longCount.incrementAndGet());

        // Create the CourseEvent
        CourseEventDTO courseEventDTO = courseEventMapper.toDto(courseEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseEventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourseEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseEvent.setId(longCount.incrementAndGet());

        // Create the CourseEvent
        CourseEventDTO courseEventDTO = courseEventMapper.toDto(courseEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(courseEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourseEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseEvent.setId(longCount.incrementAndGet());

        // Create the CourseEvent
        CourseEventDTO courseEventDTO = courseEventMapper.toDto(courseEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(courseEventDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseEventWithPatch() throws Exception {
        // Initialize the database
        insertedCourseEvent = courseEventRepository.saveAndFlush(courseEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseEvent using partial update
        CourseEvent partialUpdatedCourseEvent = new CourseEvent();
        partialUpdatedCourseEvent.setId(courseEvent.getId());

        partialUpdatedCourseEvent.eventType(UPDATED_EVENT_TYPE);

        restCourseEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseEvent))
            )
            .andExpect(status().isOk());

        // Validate the CourseEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseEventUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCourseEvent, courseEvent),
            getPersistedCourseEvent(courseEvent)
        );
    }

    @Test
    @Transactional
    void fullUpdateCourseEventWithPatch() throws Exception {
        // Initialize the database
        insertedCourseEvent = courseEventRepository.saveAndFlush(courseEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the courseEvent using partial update
        CourseEvent partialUpdatedCourseEvent = new CourseEvent();
        partialUpdatedCourseEvent.setId(courseEvent.getId());

        partialUpdatedCourseEvent
            .eventType(UPDATED_EVENT_TYPE)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .expectedStudents(UPDATED_EXPECTED_STUDENTS)
            .requiredEquipment(UPDATED_REQUIRED_EQUIPMENT);

        restCourseEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCourseEvent))
            )
            .andExpect(status().isOk());

        // Validate the CourseEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCourseEventUpdatableFieldsEquals(partialUpdatedCourseEvent, getPersistedCourseEvent(partialUpdatedCourseEvent));
    }

    @Test
    @Transactional
    void patchNonExistingCourseEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseEvent.setId(longCount.incrementAndGet());

        // Create the CourseEvent
        CourseEventDTO courseEventDTO = courseEventMapper.toDto(courseEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseEventDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourseEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseEvent.setId(longCount.incrementAndGet());

        // Create the CourseEvent
        CourseEventDTO courseEventDTO = courseEventMapper.toDto(courseEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(courseEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourseEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        courseEvent.setId(longCount.incrementAndGet());

        // Create the CourseEvent
        CourseEventDTO courseEventDTO = courseEventMapper.toDto(courseEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseEventMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(courseEventDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourseEvent() throws Exception {
        // Initialize the database
        insertedCourseEvent = courseEventRepository.saveAndFlush(courseEvent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the courseEvent
        restCourseEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, courseEvent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return courseEventRepository.count();
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

    protected CourseEvent getPersistedCourseEvent(CourseEvent courseEvent) {
        return courseEventRepository.findById(courseEvent.getId()).orElseThrow();
    }

    protected void assertPersistedCourseEventToMatchAllProperties(CourseEvent expectedCourseEvent) {
        assertCourseEventAllPropertiesEquals(expectedCourseEvent, getPersistedCourseEvent(expectedCourseEvent));
    }

    protected void assertPersistedCourseEventToMatchUpdatableProperties(CourseEvent expectedCourseEvent) {
        assertCourseEventAllUpdatablePropertiesEquals(expectedCourseEvent, getPersistedCourseEvent(expectedCourseEvent));
    }
}
