package com.example.demo.controllers;

import com.example.demo.service.IncidentCsvImporter;
import com.example.demo.service.IncidentImportResult;
import com.example.demo.service.IncidentImportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** CSV import of incidents: preview, import, undo, and a template file. */
@RestController
@RequestMapping("/api/incidents/import")
public class IncidentImportController {

    private final IncidentImportService service;

    public IncidentImportController(IncidentImportService service) {
        this.service = service;
    }

    /**
     * Checks a file and reports what would be imported. Nothing is saved.
     * "mappings" is optional: field name -> (typed value -> option value), e.g. {"severity": {"med": "MODERATE"}}.
     */
    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public IncidentImportResult preview(@RequestPart("file") MultipartFile file,
                                        @RequestPart(value = "mappings", required = false) Map<String, Map<String, String>> mappings) {
        return service.preview(file, mappings);
    }

    /** Saves the valid rows. With skipInvalid=false, any bad row stops the whole import. */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public IncidentImportResult importFile(@RequestPart("file") MultipartFile file,
                                           @RequestPart(value = "mappings", required = false) Map<String, Map<String, String>> mappings,
                                           @RequestParam(defaultValue = "false") boolean skipInvalid) {
        return service.commit(file, mappings, skipInvalid);
    }

    /** Removes everything one import saved. */
    @DeleteMapping("/{batchId}")
    public Map<String, Integer> undo(@PathVariable UUID batchId) {
        return Map.of("removed", service.undo(batchId));
    }

    /** An empty file with the right columns and one example row. */
    @GetMapping("/template")
    public ResponseEntity<byte[]> template() {
        List<String> headers = IncidentCsvImporter.templateHeaders();
        String example = String.join(",",
                "PT-2026-XXXXXXXX", "2026-03-05", "14:30", "Home",
                "\"Describe what happened.\"",
                "Routine / Time-Based Triggers", "Poor sleep / fatigue", "Verbal Aggression|Property Damage/Destruction",
                "", "", "Staff member", "5-10 mins", "Two", "Moderate", "No", "None involved", "No",
                "Regulation & De-Escalation", "Used calm, reassuring tone", "Yes", "\"What staff did.\"", "");
        String csv = "﻿" + String.join(",", headers) + "\r\n" + example + "\r\n";
        HttpHeaders h = new HttpHeaders();
        h.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        h.setContentDisposition(ContentDisposition.attachment().filename("incident-import-template.csv").build());
        return new ResponseEntity<>(csv.getBytes(StandardCharsets.UTF_8), h, HttpStatus.OK);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ProblemDetail tooLarge(MaxUploadSizeExceededException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.PAYLOAD_TOO_LARGE, "That file is too large to upload.");
    }
}
