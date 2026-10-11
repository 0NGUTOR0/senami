package com.example.demo.controllers;

import com.example.demo.dto.ConsumptionRequest;
import com.example.demo.dto.ConsumptionResponse;
import com.example.demo.service.ConsumptionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ConsumptionController {

    private final ConsumptionService service;

    public ConsumptionController(ConsumptionService service) {
        this.service = service;
    }

    @PostMapping("/patients/{patientId}/consumptions")
    public ResponseEntity<ConsumptionResponse> record(@PathVariable UUID patientId,
                                                      @Valid @RequestBody ConsumptionRequest request) {
        ConsumptionResponse created = service.record(patientId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/consumptions/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /** Optional ?from=2026-10-01T00:00:00&to=2026-10-31T23:59:59 filter on consumption time. */
    @GetMapping("/patients/{patientId}/consumptions")
    public Page<ConsumptionResponse> listForPatient(
            @PathVariable UUID patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20, sort = "consumedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listForPatient(patientId, from, to, pageable);
    }

    @GetMapping("/consumptions/{id}")
    public ConsumptionResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }
}
