package com.ep.isw.grade.policy;

public class AttendancePolicy {

    public AttendanceResult review(boolean hasReachedMinimumClasses) {
        if (hasReachedMinimumClasses) {
            return new AttendanceResult(true, null);
        }
        return new AttendanceResult(false, "El estudiante no alcanzó la asistencia mínima, nota final = 0");
    }

    public record AttendanceResult(boolean allowsGrading, String warning) {
    }
}
