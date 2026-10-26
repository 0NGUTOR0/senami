package com.example.demo.service;

import com.example.demo.dto.IncidentRequest;
import com.example.demo.model.Incident;
import com.example.demo.model.IncidentOptions;
import com.example.demo.model.Patient;
import com.example.demo.repository.IncidentRepository;
import com.example.demo.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/** Imports incidents from a CSV file: check everything first, then save only on request. */
@Service
public class IncidentImportService {

    private static final int MAX_ISSUES = 500;
    private static final int SAMPLE_SIZE = 8;
    private static final DateTimeFormatter SHOW = DateTimeFormatter.ofPattern("dd MMM uuuu HH:mm", Locale.ENGLISH);

    private final PatientRepository patientRepository;
    private final IncidentRepository incidentRepository;
    private final IncidentService incidentService;
    private final PatientAccessService access;

    public IncidentImportService(PatientRepository patientRepository,
                                 IncidentRepository incidentRepository,
                                 IncidentService incidentService,
                                 PatientAccessService access) {
        this.patientRepository = patientRepository;
        this.incidentRepository = incidentRepository;
        this.incidentService = incidentService;
        this.access = access;
    }

    /** Checks the file and reports what would happen. Nothing is saved. */
    @Transactional(readOnly = true)
    public IncidentImportResult preview(MultipartFile file, Map<String, Map<String, String>> mappings) {
        return run(file, mappings, false, false);
    }

    /** Saves the valid rows. Without skipInvalid, a single bad row stops the whole import. */
    @Transactional
    public IncidentImportResult commit(MultipartFile file, Map<String, Map<String, String>> mappings, boolean skipInvalid) {
        return run(file, mappings, true, skipInvalid);
    }

    /** Removes everything saved by one import. Reviewed incidents are never removed. */
    @Transactional
    public int undo(UUID batchId) {
        List<Incident> list = incidentRepository.findByImportBatchId(batchId);
        if (list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No incidents found for that import.");
        }
        long reviewed = list.stream().filter(i -> i.getReceivedBy() != null).count();
        if (reviewed > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    reviewed + " of these incidents have already been reviewed, so the import cannot be undone.");
        }
        incidentRepository.deleteAll(list);
        return list.size();
    }

    // ------------------------------------------------------------------

    private IncidentImportResult run(MultipartFile file, Map<String, Map<String, String>> mappings,
                                     boolean commit, boolean skipInvalid) {
        String fileName = file == null ? null : file.getOriginalFilename();
        if (file == null || file.isEmpty()) {
            return failed(fileName, "No file was uploaded.", List.of("The file is empty."));
        }
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            return failed(fileName, "The file could not be read.", List.of("The file could not be read."));
        }

        IncidentCsvImporter.Parsed parsed = IncidentCsvImporter.parse(bytes, mappings);
        if (!parsed.fileErrors().isEmpty()) {
            return new IncidentImportResult(fileName, false, "The file cannot be imported.", 0, 0, 0, 0, 0, null,
                    parsed.fileErrors(), parsed.unknownColumns(), Map.of(), List.of(), false, List.of());
        }

        Map<String, Optional<Patient>> patients = new HashMap<>();
        Set<String> seen = new HashSet<>();
        List<IncidentImportResult.RowIssue> issues = new ArrayList<>();
        List<IncidentImportResult.SampleRow> sample = new ArrayList<>();
        List<Object[]> toSave = new ArrayList<>();   // {Patient, IncidentRequest, externalId}
        int invalid = 0, duplicates = 0;

        for (IncidentCsvImporter.Row row : parsed.rows()) {
            List<String> problems = new ArrayList<>(row.errors());
            Patient patient = null;
            if (!row.patientNumber().isEmpty()) {
                patient = patients.computeIfAbsent(row.patientNumber(),
                        n -> patientRepository.findByPatientNumber(n).filter(access::canAccess)).orElse(null);
                if (patient == null) problems.add("Patient number \"" + row.patientNumber() + "\" was not found.");
            }
            if (!problems.isEmpty()) {
                invalid++;
                addIssue(issues, row, "INVALID", patient, problems);
                continue;
            }

            IncidentRequest req = row.request();
            String key = patient.getId() + "|" + req.occurredAt() + "|" + req.description();
            boolean dup = !seen.add(key)
                    || incidentRepository.existsByPatientIdAndOccurredAtAndDescription(patient.getId(), req.occurredAt(), req.description())
                    || (row.externalId() != null && (!seen.add("ext|" + row.externalId())
                    || incidentRepository.existsByExternalId(row.externalId())));
            if (dup) {
                duplicates++;
                addIssue(issues, row, "DUPLICATE", patient, List.of("Already recorded (same patient, date and time, and description). It will be skipped."));
                continue;
            }
            if (sample.size() < SAMPLE_SIZE) {
                sample.add(new IncidentImportResult.SampleRow(row.rowNumber(), patient.getPatientNumber(), patient.getFullName(),
                        SHOW.format(req.occurredAt()), req.severity().label(),
                        req.behaviours().stream().map(IncidentOptions.Behaviour::label).collect(Collectors.joining(", "))));
            }
            toSave.add(new Object[]{patient, req, row.externalId()});
        }

        int total = parsed.rows().size();
        int valid = toSave.size();
        Map<String, List<String>> unmapped = new LinkedHashMap<>();
        parsed.unmapped().forEach((k, v) -> unmapped.put(k, new ArrayList<>(v)));
        boolean truncated = issues.size() > MAX_ISSUES;
        List<IncidentImportResult.RowIssue> shown = truncated ? issues.subList(0, MAX_ISSUES) : issues;

        if (!commit) {
            String msg = valid + " of " + total + " rows are ready to import.";
            return new IncidentImportResult(fileName, false, msg, total, valid, invalid, duplicates, 0, null,
                    List.of(), parsed.unknownColumns(), unmapped, shown, truncated, sample);
        }
        if (invalid > 0 && !skipInvalid) {
            return new IncidentImportResult(fileName, false,
                    "Nothing was imported because " + invalid + " row" + (invalid == 1 ? " has" : "s have") + " problems.",
                    total, valid, invalid, duplicates, 0, null, List.of(), parsed.unknownColumns(), unmapped, shown, truncated, sample);
        }
        if (valid == 0) {
            return new IncidentImportResult(fileName, false, "There were no new rows to import.", total, 0, invalid, duplicates, 0, null,
                    List.of(), parsed.unknownColumns(), unmapped, shown, truncated, sample);
        }

        UUID batch = UUID.randomUUID();
        for (Object[] item : toSave) {
            incidentService.recordImported((Patient) item[0], (IncidentRequest) item[1], batch, (String) item[2]);
        }
        String msg = valid + " incident" + (valid == 1 ? "" : "s") + " imported.";
        return new IncidentImportResult(fileName, true, msg, total, valid, invalid, duplicates, valid, batch,
                List.of(), parsed.unknownColumns(), unmapped, shown, truncated, sample);
    }

    private static void addIssue(List<IncidentImportResult.RowIssue> issues, IncidentCsvImporter.Row row, String status,
                                 Patient patient, List<String> messages) {
        if (issues.size() > MAX_ISSUES) return;   // keep one extra so the caller can tell the list was cut
        issues.add(new IncidentImportResult.RowIssue(row.rowNumber(), status, row.patientNumber(),
                patient == null ? null : patient.getFullName(), messages));
    }

    private static IncidentImportResult failed(String fileName, String message, List<String> fileErrors) {
        return new IncidentImportResult(fileName, false, message, 0, 0, 0, 0, 0, null, fileErrors, List.of(), Map.of(),
                List.of(), false, List.of());
    }
}
