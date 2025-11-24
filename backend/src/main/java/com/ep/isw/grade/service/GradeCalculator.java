package com.ep.isw.grade.service;

import com.ep.isw.grade.model.Evaluation;
import com.ep.isw.grade.model.EvaluationInput;
import com.ep.isw.grade.model.GradeInput;
import com.ep.isw.grade.model.GradeReport;
import com.ep.isw.grade.model.GradeReport.EvaluationBreakdown;
import com.ep.isw.grade.policy.AttendancePolicy;
import com.ep.isw.grade.policy.AttendancePolicy.AttendanceResult;
import com.ep.isw.grade.policy.ExtraPointsPolicy;
import com.ep.isw.grade.policy.ExtraPointsPolicy.ExtraPointsResult;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GradeCalculator {

    private final AttendancePolicy attendancePolicy;
    private final ExtraPointsPolicy extraPointsPolicy;
    private final Clock clock;

    public GradeCalculator(AttendancePolicy attendancePolicy, ExtraPointsPolicy extraPointsPolicy, Clock clock) {
        this.attendancePolicy = Objects.requireNonNull(attendancePolicy, "attendancePolicy");
        this.extraPointsPolicy = Objects.requireNonNull(extraPointsPolicy, "extraPointsPolicy");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public GradeReport calculate(GradeInput input) {
        Objects.requireNonNull(input, "gradeInput");
        List<EvaluationInput> rawEvaluations = input.examsStudents();
        if (rawEvaluations.size() > 10) {
            throw new IllegalArgumentException("Solo se permiten hasta 10 evaluaciones por estudiante");
        }
        List<Evaluation> evaluations = rawEvaluations.stream().map(this::toEvaluation).collect(Collectors.toList());
        double totalWeight = evaluations.stream().mapToDouble(Evaluation::weightPercentage).sum();
        double baseScore = evaluations.stream().mapToDouble(Evaluation::contribution).sum();

        List<String> warnings = new ArrayList<>();
        boolean hasEvaluations = !evaluations.isEmpty();
        if (!hasEvaluations) {
            warnings.add("No se registraron evaluaciones, nota base = 0");
        }
        if (Math.abs(totalWeight - 100.0) > 0.001 && totalWeight > 0) {
            warnings.add("La suma de pesos es " + totalWeight + "%. Se espera 100%.");
        }

        AttendanceResult attendanceResult = attendancePolicy.review(input.hasReachedMinimumClasses());
        ExtraPointsResult extraPointsResult = extraPointsPolicy.review(input.allYearsTeachers());

        double finalScore = baseScore;
        double extraApplied = 0;
        List<String> avoidedExtras = new ArrayList<>();

        if (!attendanceResult.allowsGrading() || !hasEvaluations) {
            finalScore = 0;
            if (!attendanceResult.allowsGrading() && attendanceResult.warning() != null) {
                warnings.add(attendanceResult.warning());
                avoidedExtras.add("La asistencia mínima no se cumplió, no se puede otorgar puntos extra");
            }
            if (!hasEvaluations) {
                avoidedExtras.add("No hay evaluaciones registradas para justificar puntos extra");
            }
        } else {
            if (extraPointsResult.granted()) {
                extraApplied = Math.min(extraPointsResult.value(), Math.max(0, 20 - baseScore));
                finalScore = Math.min(20, baseScore + extraPointsResult.value());
                if (extraPointsResult.value() > extraApplied) {
                    warnings.add("El puntaje extra se recortó para no superar 20 puntos");
                }
            } else if (extraPointsResult.reason() != null) {
                avoidedExtras.add(extraPointsResult.reason());
            }
        }

        finalScore = Math.min(20, Math.max(0, finalScore));

        return new GradeReport(roundTwoDecimals(baseScore), roundTwoDecimals(extraApplied),
                roundTwoDecimals(finalScore), attendanceResult.allowsGrading(), extraPointsResult.granted(),
                evaluations.stream()
                        .map(eval -> new EvaluationBreakdown(eval.name(), eval.score(), eval.weightPercentage(),
                                roundTwoDecimals(eval.contribution())))
                        .toList(),
                warnings, avoidedExtras, Instant.now(clock));
    }

    private Evaluation toEvaluation(EvaluationInput input) {
        return new Evaluation(input.name(), input.score(), input.weightPercentage());
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
