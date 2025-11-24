package com.ep.isw.grade.cli;

import com.ep.isw.grade.model.GradeInput;
import com.ep.isw.grade.model.GradeReport;
import com.ep.isw.grade.policy.AttendancePolicy;
import com.ep.isw.grade.policy.ExtraPointsPolicy;
import com.ep.isw.grade.service.GradeCalculator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;

public final class GradeCli {

    private GradeCli() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Uso: java -cp <jar> com.ep.isw.grade.cli.GradeCli <ruta-json>");
            System.exit(1);
        }
        Path inputPath = Path.of(args[0]);
        if (!Files.exists(inputPath)) {
            System.err.println("No se encontró el archivo: " + inputPath);
            System.exit(1);
        }
        ObjectMapper mapper = new ObjectMapper();
        GradeInput input = mapper.readValue(Files.readAllBytes(inputPath), GradeInput.class);
        GradeCalculator calculator = new GradeCalculator(new AttendancePolicy(), new ExtraPointsPolicy(),
                Clock.systemUTC());
        GradeReport report = calculator.calculate(input);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report));
    }
}
