package com.ep.isw.grade.model;

public record Evaluation(String name, double score, double weightPercentage) {

    public Evaluation {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("La evaluación debe tener un nombre");
        }
        if (score < 0 || score > 20) {
            throw new IllegalArgumentException("La nota debe estar entre 0 y 20");
        }
        if (weightPercentage <= 0 || weightPercentage > 100) {
            throw new IllegalArgumentException("El peso debe estar entre 0 y 100");
        }
    }

    public double contribution() {
        return score * (weightPercentage / 100d);
    }
}
