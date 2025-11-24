package com.ep.isw.grade.policy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AttendancePolicyTest {

    private final AttendancePolicy policy = new AttendancePolicy();

    @Test
    void shouldAllowGradingWhenAttendanceIsReached() {
        AttendancePolicy.AttendanceResult result = policy.review(true);

        assertThat(result.allowsGrading()).isTrue();
    }

    @Test
    void shouldBlockGradingWhenAttendanceIsNotReached() {
        AttendancePolicy.AttendanceResult result = policy.review(false);

        assertThat(result.allowsGrading()).isFalse();
        assertThat(result.warning()).contains("asistencia mínima");
    }
}
