package com.ep.isw.grade.policy;

import java.util.List;

public class ExtraPointsPolicy {

    public static final double EXTRA_POINTS = 1.0;

    public ExtraPointsResult review(List<Boolean> teacherVotes) {
        if (teacherVotes == null || teacherVotes.isEmpty()) {
            return new ExtraPointsResult(false, 0, "No se registraron votos de docentes");
        }
        boolean consensus = teacherVotes.stream().allMatch(Boolean::booleanValue);
        if (consensus) {
            return new ExtraPointsResult(true, EXTRA_POINTS, null);
        }
        return new ExtraPointsResult(false, 0, "No hubo consenso para otorgar puntos extra");
    }

    public record ExtraPointsResult(boolean granted, double value, String reason) {
    }
}
