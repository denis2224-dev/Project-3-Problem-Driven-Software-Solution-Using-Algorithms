package com.unischeduler.service;

import com.unischeduler.domain.CourseEvent;
import com.unischeduler.repository.CourseEventRepository;
import com.unischeduler.service.dto.CourseEventDTO;
import com.unischeduler.service.mapper.CourseEventMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.unischeduler.domain.CourseEvent}.
 */
@Service
@Transactional
public class CourseEventService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseEventService.class);

    private final CourseEventRepository courseEventRepository;

    private final CourseEventMapper courseEventMapper;

    public CourseEventService(CourseEventRepository courseEventRepository, CourseEventMapper courseEventMapper) {
        this.courseEventRepository = courseEventRepository;
        this.courseEventMapper = courseEventMapper;
    }

    /**
     * Save a courseEvent.
     *
     * @param courseEventDTO the entity to save.
     * @return the persisted entity.
     */
    public CourseEventDTO save(CourseEventDTO courseEventDTO) {
        LOG.debug("Request to save CourseEvent : {}", courseEventDTO);
        CourseEvent courseEvent = courseEventMapper.toEntity(courseEventDTO);
        courseEvent = courseEventRepository.save(courseEvent);
        return courseEventMapper.toDto(courseEvent);
    }

    /**
     * Update a courseEvent.
     *
     * @param courseEventDTO the entity to save.
     * @return the persisted entity.
     */
    public CourseEventDTO update(CourseEventDTO courseEventDTO) {
        LOG.debug("Request to update CourseEvent : {}", courseEventDTO);
        CourseEvent courseEvent = courseEventMapper.toEntity(courseEventDTO);
        courseEvent = courseEventRepository.save(courseEvent);
        return courseEventMapper.toDto(courseEvent);
    }

    /**
     * Partially update a courseEvent.
     *
     * @param courseEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CourseEventDTO> partialUpdate(CourseEventDTO courseEventDTO) {
        LOG.debug("Request to partially update CourseEvent : {}", courseEventDTO);

        return courseEventRepository
            .findById(courseEventDTO.getId())
            .map(existingCourseEvent -> {
                courseEventMapper.partialUpdate(existingCourseEvent, courseEventDTO);

                return existingCourseEvent;
            })
            .map(courseEventRepository::save)
            .map(courseEventMapper::toDto);
    }

    /**
     * Get all the courseEvents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseEventDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CourseEvents");
        return courseEventRepository.findAll(pageable).map(courseEventMapper::toDto);
    }

    /**
     * Get all the courseEvents with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CourseEventDTO> findAllWithEagerRelationships(Pageable pageable) {
        return courseEventRepository.findAllWithEagerRelationships(pageable).map(courseEventMapper::toDto);
    }

    /**
     * Get one courseEvent by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CourseEventDTO> findOne(Long id) {
        LOG.debug("Request to get CourseEvent : {}", id);
        return courseEventRepository.findOneWithEagerRelationships(id).map(courseEventMapper::toDto);
    }

    /**
     * Delete the courseEvent by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CourseEvent : {}", id);
        courseEventRepository.deleteById(id);
    }
}
