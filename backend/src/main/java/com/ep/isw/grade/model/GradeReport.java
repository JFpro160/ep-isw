package com.ep.isw.grade.model;

import java.time.Instant;
import java.util.List;

public record GradeReport(double baseScore, double extraPointsApplied, double finalScore, boolean attendanceSatisfied,
        boolean extraGranted, List<EvaluationBreakdown> evaluations, List<String> warnings, List<String> avoidedExtras,
        Instant generatedAt) {

    public record EvaluationBreakdown(String name, double score, double weight, double contribution) {
    }
}
