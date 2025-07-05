package org.example.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.*;

public class CSVUtils {

    public static Stream<Arguments> loadLoginData() {
        try {
            return Files.lines(Paths.get("src/test/resources/loginData.csv"))
                    .skip(1)
                    .map(line -> line.split(",", -1))
                    .map(parts -> Arguments.of(parts[0], parts[1]));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load login data", e);
        }
    }

    public static Stream<Arguments> loadCandidateSearchData() {
        try {
            return Files.lines(Paths.get("src/test/resources/candidateSearchData.csv"))
                    .skip(1)
                    .map(line -> line.split(",", -1))
                    .map(parts -> Arguments.of(parts[0], parts[1]));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load candidate search data", e);
        }
    }

    public static Stream<Arguments> loadCandidateData() {
        try {
            return Files.lines(Paths.get("src/test/resources/candidates.csv"))
                    .skip(1) // skip header
                    .map(line -> line.split(",", -1))
                    .map(parts -> Arguments.of(parts[0], parts[1], parts[2]));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load candidate data", e);
        }
    }

    public static Stream<Arguments> loadVacancyData() {
        try {
            return Files.lines(Paths.get("src/test/resources/vacancies.csv"))
                    .skip(1)
                    .map(line -> line.split(",", -1))
                    .map(parts -> Arguments.of(parts[0], parts[1], parts[2]));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load vacancy data", e);
        }
    }
    public static Stream<Arguments> loadVacancySearchData() {
        try {
            Path path = Paths.get(ClassLoader.getSystemResource("vacancySearchData.csv").toURI());
            return Files.lines(path)
                    .skip(1)
                    .map(line -> line.split(",", -1))
                    .map(parts -> Arguments.of(parts[0], parts[1]));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load vacancy search data", e);
        }
    }





}
