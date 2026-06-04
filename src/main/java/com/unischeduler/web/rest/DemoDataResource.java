package com.unischeduler.web.rest;

import com.unischeduler.security.AuthoritiesConstants;
import com.unischeduler.service.DemoDataService;
import com.unischeduler.service.dto.DemoDataSummaryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for loading and clearing the university demo dataset.
 */
@RestController
@RequestMapping("/api/demo-data")
public class DemoDataResource {

    private static final Logger LOG = LoggerFactory.getLogger(DemoDataResource.class);

    private final DemoDataService demoDataService;

    public DemoDataResource(DemoDataService demoDataService) {
        this.demoDataService = demoDataService;
    }

    @PostMapping("/load")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<DemoDataSummaryDTO> loadDemoData() {
        LOG.debug("REST request to load demo scheduling data");
        return ResponseEntity.ok(demoDataService.load());
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<DemoDataSummaryDTO> clearDemoData() {
        LOG.debug("REST request to clear demo scheduling data");
        return ResponseEntity.ok(demoDataService.clear());
    }
}
