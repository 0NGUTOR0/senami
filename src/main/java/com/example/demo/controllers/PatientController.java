package com.example.demo.controllers;

import com.example.demo.dto.PatientRequest;
import com.example.demo.dto.PatientResponse;
import com.example.demo.service.PatientService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PatientResponse> create(@Valid @RequestBody PatientRequest request) {
        PatientResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public PatientResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/")
    public Page<PatientResponse> getAll(@PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping("/number/{patientNumber}")
    public PatientResponse getByPatientNumber(@PathVariable String patientNumber) {
        return service.getByPatientNumber(patientNumber);
    }

    @GetMapping
    public Page<PatientResponse> search(@RequestParam(required = false) String q,
                                        @PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return service.search(q, pageable);
    }

    @PutMapping("/{id}")
    public PatientResponse update(@PathVariable UUID id, @Valid @RequestBody PatientRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
