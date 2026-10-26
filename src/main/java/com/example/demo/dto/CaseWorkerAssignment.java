package com.example.demo.dto;

/** Body for assigning a case worker. A null caseWorkerId removes the assignment. */
public record CaseWorkerAssignment(Long caseWorkerId) {
}
