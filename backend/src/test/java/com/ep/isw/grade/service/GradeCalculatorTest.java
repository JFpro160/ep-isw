package com.ep.isw.grade.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ep.isw.grade.model.EvaluationInput;
import com.ep.isw.grade.model.GradeInput;
import com.ep.isw.grade.model.GradeReport;
import com.ep.isw.grade.policy.AttendancePolicy;
import com.ep.isw.grade.policy.ExtraPointsPolicy;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GradeCalculatorTest {

    private GradeCalculator calculator;
    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        calculator = new GradeCalculator(new AttendancePolicy(), new ExtraPointsPolicy(), clock);
    }

    @Test
    void shouldCalculateFinalGradeWithExtraPointsWhenConsensusExists() {
        GradeInput input = new GradeInput(List.of(new EvaluationInput("Parcial", 16, 40),
                new EvaluationInput("Proyecto", 18, 40), new EvaluationInput("Final", 15, 20)), true,
                List.of(true, true));

        GradeReport report = calculator.calculate(input);

        assertThat(report.finalScore()).isEqualTo(17.6);
        assertThat(report.extraPointsApplied()).isEqualTo(1.0);
        assertThat(report.warnings()).isEmpty();
    }

    @Test
    void shouldReturnZeroWhenAttendanceFails() {
        GradeInput input = new GradeInput(List.of(new EvaluationInput("Parcial", 16, 100)), false, List.of(true, true));

        GradeReport report = calculator.calculate(input);

        assertThat(report.finalScore()).isZero();
        assertThat(report.warnings()).anyMatch(message -> message.contains("asistencia"));
    }

    @Test
    void shouldAvoidExtraWhenThereIsNoConsensus() {
        GradeInput input = new GradeInput(List.of(new EvaluationInput("Parcial", 16, 100)), true, List.of(true, false));

        GradeReport report = calculator.calculate(input);

        assertThat(report.extraGranted()).isFalse();
        assertThat(report.avoidedExtras()).isNotEmpty();
    }

    @Test
    void shouldHandleZeroEvaluationsGracefully() {
        GradeInput input = new GradeInput(List.of(), true, List.of(true));

        GradeReport report = calculator.calculate(input);

        assertThat(report.finalScore()).isZero();
        assertThat(report.warnings()).anyMatch(message -> message.contains("No se registraron"));
    }

    @Test
    void shouldCapFinalScoreAtTwentyWhenExtraPointsAreApplied() {
        GradeInput input = new GradeInput(List.of(new EvaluationInput("Proyecto", 19.5, 100)), true,
                List.of(true, true, true));

        GradeReport report = calculator.calculate(input);

        assertThat(report.finalScore()).isEqualTo(20);
        assertThat(report.extraPointsApplied()).isEqualTo(0.5);
        assertThat(report.warnings()).anyMatch(message -> message.contains("recortó"));
    }
}
