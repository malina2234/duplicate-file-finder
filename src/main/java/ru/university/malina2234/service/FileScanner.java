// Эта строка ОБЯЗАНА соответствовать пути к файлу
package ru.university.malina2234.service;

import java.io.IOException;
// ... (все импорты класса) ...
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FileScanner {
    private final Set<String> ignoredDirs;

    public FileScanner(Set<String> ignoredDirs) {
        this.ignoredDirs = ignoredDirs;
    }

    public List<Path> scan(Path startDir) throws IOException {
        if (!Files.isDirectory(startDir)) {
            throw new IllegalArgumentException("Указанный путь не является директорией: " + startDir);
        }

        List<Path> fileList;
        try (Stream<Path> stream = Files.walk(startDir)) {
            fileList = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> !isInIgnoredDir(path, startDir))
                    .collect(Collectors.toList());
        }
        return fileList;
    }

    private boolean isInIgnoredDir(Path path, Path startDir) {
        Path relativePath = startDir.relativize(path.getParent());
        for (Path part : relativePath) {
            if (ignoredDirs.contains(part.toString())) {
                return true;
            }
        }
        return false;
    }
}