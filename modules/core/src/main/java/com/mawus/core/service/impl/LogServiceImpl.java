package com.mawus.core.service.impl;

import com.mawus.core.service.LogService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class LogServiceImpl implements LogService {

    private static final String LOG_DIR = "logs";

    @Override
    public Set<String> getLogFiles() {
        File logDir = new File(LOG_DIR);

        Set<String> fileNames = new HashSet<>();
        listFilesRecursive(logDir, fileNames);

        return fileNames;
    }

    @Override
    public List<String> getLogFileContent(String fileName) {
        return List.of();
    }

    @Override
    public Stream<String> getLogFileContentAsStream(String fileName) {
        Path logDir = Paths.get(LOG_DIR);
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be null or blank.");
        }

        try {
            Optional<Path> logFile = Files.walk(logDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals(fileName))
                    .findFirst();

            if (logFile.isPresent()) {
                return Files.lines(logFile.get(), StandardCharsets.UTF_8)
                        .onClose(() -> {});
            } else {
                throw new FileNotFoundException("Файл не найден: " + fileName);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Ошибка при чтении файла: " + fileName, e);
        }
    }

    public static void listFilesRecursive(File directory, Set<String> fileNames) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    listFilesRecursive(file, fileNames);
                } else {
                    fileNames.add(file.getName());
                }
            }
        }
    }
}
