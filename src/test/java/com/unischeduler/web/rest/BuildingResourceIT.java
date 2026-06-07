package com.unischeduler.web.rest;

import static com.unischeduler.domain.BuildingAsserts.*;
import static com.unischeduler.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unischeduler.IntegrationTest;
import com.unischeduler.domain.Building;
import com.unischeduler.security.AuthoritiesConstants;
import com.unischeduler.repository.BuildingRepository;
import com.unischeduler.service.dto.BuildingDTO;
import com.unischeduler.service.mapper.BuildingMapper;
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
 * Integration tests for the {@link BuildingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class BuildingResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/buildings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private BuildingMapper buildingMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBuildingMockMvc;

    private Building building;

    private Building insertedBuilding;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Building createEntity() {
        return new Building().name(DEFAULT_NAME).code(DEFAULT_CODE).address(DEFAULT_ADDRESS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Building createUpdatedEntity() {
        return new Building().name(UPDATED_NAME).code(UPDATED_CODE).address(UPDATED_ADDRESS);
    }

    @BeforeEach
    void initTest() {
        building = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBuilding != null) {
            buildingRepository.delete(insertedBuilding);
            insertedBuilding = null;
        }
    }

    @Test
    @Transactional
    void createBuilding() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Building
        BuildingDTO buildingDTO = buildingMapper.toDto(building);
        var returnedBuildingDTO = om.readValue(
            restBuildingMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(buildingDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BuildingDTO.class
        );

        // Validate the Building in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBuilding = buildingMapper.toEntity(returnedBuildingDTO);
        assertBuildingUpdatableFieldsEquals(returnedBuilding, getPersistedBuilding(returnedBuilding));

        insertedBuilding = returnedBuilding;
    }

    @Test
    @Transactional
    void createBuildingWithExistingId() throws Exception {
        // Create the Building with an existing ID
        building.setId(1L);
        BuildingDTO buildingDTO = buildingMapper.toDto(building);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBuildingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(buildingDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Building in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        building.setName(null);

        // Create the Building, which fails.
        BuildingDTO buildingDTO = buildingMapper.toDto(building);

        restBuildingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(buildingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        building.setCode(null);

        // Create the Building, which fails.
        BuildingDTO buildingDTO = buildingMapper.toDto(building);

        restBuildingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(buildingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBuildings() throws Exception {
        // Initialize the database
        insertedBuilding = buildingRepository.saveAndFlush(building);

        // Get all the buildingList
        restBuildingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(building.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)));
    }

    @Test
    @Transactional
    void getBuilding() throws Exception {
        // Initialize the database
        insertedBuilding = buildingRepository.saveAndFlush(building);

        // Get the building
        restBuildingMockMvc
            .perform(get(ENTITY_API_URL_ID, building.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(building.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS));
    }

    @Test
    @Transactional
    void getNonExistingBuilding() throws Exception {
        // Get the building
        restBuildingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBuilding() throws Exception {
        // Initialize the database
        insertedBuilding = buildingRepository.saveAndFlush(building);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the building
        Building updatedBuilding = buildingRepository.findById(building.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBuilding are not directly saved in db
        em.detach(updatedBuilding);
        updatedBuilding.name(UPDATED_NAME).code(UPDATED_CODE).address(UPDATED_ADDRESS);
        BuildingDTO buildingDTO = buildingMapper.toDto(updatedBuilding);

        restBuildingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, buildingDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(buildingDTO))
            )
            .andExpect(status().isOk());

        // Validate the Building in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBuildingToMatchAllProperties(updatedBuilding);
    }

    @Test
    @Transactional
    void putNonExistingBuilding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        building.setId(longCount.incrementAndGet());

        // Create the Building
        BuildingDTO buildingDTO = buildingMapper.toDto(building);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBuildingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, buildingDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(buildingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Building in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBuilding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        building.setId(longCount.incrementAndGet());

        // Create the Building
        BuildingDTO buildingDTO = buildingMapper.toDto(building);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBuildingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(buildingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Building in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBuilding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        building.setId(longCount.incrementAndGet());

        // Create the Building
        BuildingDTO buildingDTO = buildingMapper.toDto(building);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBuildingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(buildingDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Building in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBuildingWithPatch() throws Exception {
        // Initialize the database
        insertedBuilding = buildingRepository.saveAndFlush(building);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the building using partial update
        Building partialUpdatedBuilding = new Building();
        partialUpdatedBuilding.setId(building.getId());

        partialUpdatedBuilding.address(UPDATED_ADDRESS);

        restBuildingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBuilding.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBuilding))
            )
            .andExpect(status().isOk());

        // Validate the Building in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBuildingUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBuilding, building), getPersistedBuilding(building));
    }

    @Test
    @Transactional
    void fullUpdateBuildingWithPatch() throws Exception {
        // Initialize the database
        insertedBuilding = buildingRepository.saveAndFlush(building);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the building using partial update
        Building partialUpdatedBuilding = new Building();
        partialUpdatedBuilding.setId(building.getId());

        partialUpdatedBuilding.name(UPDATED_NAME).code(UPDATED_CODE).address(UPDATED_ADDRESS);

        restBuildingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBuilding.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBuilding))
            )
            .andExpect(status().isOk());

        // Validate the Building in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBuildingUpdatableFieldsEquals(partialUpdatedBuilding, getPersistedBuilding(partialUpdatedBuilding));
    }

    @Test
    @Transactional
    void patchNonExistingBuilding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        building.setId(longCount.incrementAndGet());

        // Create the Building
        BuildingDTO buildingDTO = buildingMapper.toDto(building);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBuildingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, buildingDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(buildingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Building in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBuilding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        building.setId(longCount.incrementAndGet());

        // Create the Building
        BuildingDTO buildingDTO = buildingMapper.toDto(building);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBuildingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(buildingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Building in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBuilding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        building.setId(longCount.incrementAndGet());

        // Create the Building
        BuildingDTO buildingDTO = buildingMapper.toDto(building);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBuildingMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(buildingDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Building in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBuilding() throws Exception {
        // Initialize the database
        insertedBuilding = buildingRepository.saveAndFlush(building);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the building
        restBuildingMockMvc
            .perform(delete(ENTITY_API_URL_ID, building.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return buildingRepository.count();
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

    protected Building getPersistedBuilding(Building building) {
        return buildingRepository.findById(building.getId()).orElseThrow();
    }

    protected void assertPersistedBuildingToMatchAllProperties(Building expectedBuilding) {
        assertBuildingAllPropertiesEquals(expectedBuilding, getPersistedBuilding(expectedBuilding));
    }

    protected void assertPersistedBuildingToMatchUpdatableProperties(Building expectedBuilding) {
        assertBuildingAllUpdatablePropertiesEquals(expectedBuilding, getPersistedBuilding(expectedBuilding));
    }
}
