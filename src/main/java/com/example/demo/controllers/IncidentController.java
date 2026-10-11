package com.example.demo.controllers;

import com.example.demo.dto.IncidentRequest;
import com.example.demo.dto.IncidentResponse;
import com.example.demo.dto.IncidentReviewRequest;
import com.example.demo.model.IncidentOptions;
import com.example.demo.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    /** Every radio button and checkbox on the form, with labels, for building the UI. */
    @GetMapping("/incident-options")
    public Map<String, List<IncidentOptions.Option>> options() {
        return IncidentOptions.all();
    }

    @PostMapping("/patients/{patientId}/incidents")
    public ResponseEntity<IncidentResponse> record(@PathVariable UUID patientId,
                                                   @Valid @RequestBody IncidentRequest request) {
        IncidentResponse created = service.record(patientId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/incidents/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/patients/{patientId}/incidents")
    public Page<IncidentResponse> listForPatient(
            @PathVariable UUID patientId,
            @PageableDefault(size = 20, sort = "occurredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listForPatient(patientId, pageable);
    }

    @GetMapping("/incidents/{id}")
    public IncidentResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/incidents/{id}")
    public IncidentResponse update(@PathVariable UUID id, @Valid @RequestBody IncidentRequest request) {
        return service.update(id, request);
    }

    /** Completed by the person receiving the report (team leader / office member). */
    @PutMapping("/incidents/{id}/review")
    public IncidentResponse review(@PathVariable UUID id, @Valid @RequestBody IncidentReviewRequest request) {
        return service.review(id, request);
    }
}
