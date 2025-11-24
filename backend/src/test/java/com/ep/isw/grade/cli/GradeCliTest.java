package com.ep.isw.grade.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class GradeCliTest {

    @Test
    void shouldPrintReportGivenValidJson() throws Exception {
        String json = """
                {
                  \"examsStudents\": [
                    {\"name\": \"Parcial\", \"score\": 15, \"weightPercentage\": 50},
                    {\"name\": \"Final\", \"score\": 16, \"weightPercentage\": 50}
                  ],
                  \"hasReachedMinimumClasses\": true,
                  \"allYearsTeachers\": [true, true]
                }
                """;
        Path temp = Files.createTempFile("grade-input", ".json");
        Files.writeString(temp, json);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        try {
            GradeCli.main(new String[]{temp.toString()});
        } finally {
            System.setOut(originalOut);
            Files.deleteIfExists(temp);
        }

        assertThat(out.toString()).contains("finalScore");
    }
}
