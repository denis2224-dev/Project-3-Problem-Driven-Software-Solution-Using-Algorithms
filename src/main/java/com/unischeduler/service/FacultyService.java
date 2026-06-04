package com.unischeduler.service;

import com.unischeduler.domain.Faculty;
import com.unischeduler.repository.FacultyRepository;
import com.unischeduler.service.dto.FacultyDTO;
import com.unischeduler.service.mapper.FacultyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.unischeduler.domain.Faculty}.
 */
@Service
@Transactional
public class FacultyService {

    private static final Logger LOG = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    private final FacultyMapper facultyMapper;

    public FacultyService(FacultyRepository facultyRepository, FacultyMapper facultyMapper) {
        this.facultyRepository = facultyRepository;
        this.facultyMapper = facultyMapper;
    }

    /**
     * Save a faculty.
     *
     * @param facultyDTO the entity to save.
     * @return the persisted entity.
     */
    public FacultyDTO save(FacultyDTO facultyDTO) {
        LOG.debug("Request to save Faculty : {}", facultyDTO);
        Faculty faculty = facultyMapper.toEntity(facultyDTO);
        faculty = facultyRepository.save(faculty);
        return facultyMapper.toDto(faculty);
    }

    /**
     * Update a faculty.
     *
     * @param facultyDTO the entity to save.
     * @return the persisted entity.
     */
    public FacultyDTO update(FacultyDTO facultyDTO) {
        LOG.debug("Request to update Faculty : {}", facultyDTO);
        Faculty faculty = facultyMapper.toEntity(facultyDTO);
        faculty = facultyRepository.save(faculty);
        return facultyMapper.toDto(faculty);
    }

    /**
     * Partially update a faculty.
     *
     * @param facultyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FacultyDTO> partialUpdate(FacultyDTO facultyDTO) {
        LOG.debug("Request to partially update Faculty : {}", facultyDTO);

        return facultyRepository
            .findById(facultyDTO.getId())
            .map(existingFaculty -> {
                facultyMapper.partialUpdate(existingFaculty, facultyDTO);

                return existingFaculty;
            })
            .map(facultyRepository::save)
            .map(facultyMapper::toDto);
    }

    /**
     * Get all the faculties.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FacultyDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Faculties");
        return facultyRepository.findAll(pageable).map(facultyMapper::toDto);
    }

    /**
     * Get one faculty by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FacultyDTO> findOne(Long id) {
        LOG.debug("Request to get Faculty : {}", id);
        return facultyRepository.findById(id).map(facultyMapper::toDto);
    }

    /**
     * Delete the faculty by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Faculty : {}", id);
        facultyRepository.deleteById(id);
    }
}
