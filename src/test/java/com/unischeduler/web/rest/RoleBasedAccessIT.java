package com.unischeduler.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.unischeduler.IntegrationTest;
import com.unischeduler.security.AuthoritiesConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for role-based access rules between ROLE_ADMIN and ROLE_USER.
 */
@IntegrationTest
@AutoConfigureMockMvc
class RoleBasedAccessIT {

    @Autowired
    private MockMvc restMockMvc;

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    void userCannotLoadDemoData() throws Exception {
        restMockMvc.perform(post("/api/demo-data/load")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    void userCannotGenerateTimetable() throws Exception {
        restMockMvc.perform(post("/api/solver-jobs/generate-timetable")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    void userCannotApproveOrPublishTimetable() throws Exception {
        restMockMvc.perform(post("/api/timetables/1/approve")).andExpect(status().isForbidden());
        restMockMvc.perform(post("/api/timetables/1/publish")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    void userCannotCreateAcademicEntity() throws Exception {
        restMockMvc
            .perform(post("/api/courses").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    void userCanReadTimetableAndExamEndpoints() throws Exception {
        restMockMvc.perform(get("/api/timetable-versions")).andExpect(status().isOk());
        restMockMvc.perform(get("/api/exams/coloring")).andExpect(status().isOk());
    }
}
