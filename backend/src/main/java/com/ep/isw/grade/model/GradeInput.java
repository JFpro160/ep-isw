package com.ep.isw.grade.model;

import java.util.Collections;
import java.util.List;

public record GradeInput(List<EvaluationInput> examsStudents, boolean hasReachedMinimumClasses,
        List<Boolean> allYearsTeachers) {

    public List<EvaluationInput> examsStudents() {
        return examsStudents == null ? List.of() : Collections.unmodifiableList(examsStudents);
    }

    public List<Boolean> allYearsTeachers() {
        return allYearsTeachers == null ? List.of() : Collections.unmodifiableList(allYearsTeachers);
    }
}
