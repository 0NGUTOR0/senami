package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** What an import preview or an import returned, for the import page to show. */
public record IncidentImportResult(
        String fileName,
        boolean committed,
        String message,
        int totalRows,
        int validRows,
        int invalidRows,
        int duplicateRows,
        int importedRows,
        UUID batchId,
        List<String> fileErrors,
        List<String> unknownColumns,
        /** field name -> typed values that matched no option, so the person can map them. */
        Map<String, List<String>> unmapped,
        /** Rows with a problem (invalid or duplicate), capped at 500. */
        List<RowIssue> issues,
        boolean issuesTruncated,
        /** The first few rows that are ready to import. */
        List<SampleRow> sample
) {
    public record RowIssue(int row, String status, String patientNumber, String patientName, List<String> messages) {
    }

    public record SampleRow(int row, String patientNumber, String patientName, String occurredAt,
                            String severity, String behaviours) {
    }
}
