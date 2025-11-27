package ru.university.malina2234.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileScanner {
    private final Set<String> ignoredDirs;

    //принимает список игнорируемых папок
    public FileScanner(Set<String> ignoredDirs) {
        this.ignoredDirs = ignoredDirs;
    }

    // сканирует папку и возвращает список файлов
    public List<Path> scan(Path startDir) throws IOException {
        if (!Files.isDirectory(startDir)) {
            throw new IllegalArgumentException("Указанный путь не является директорией: " + startDir);
        }

        List<Path> fileList;
        // используем stream api для обхода всех файлов и папок
        try (Stream<Path> stream = Files.walk(startDir)) {
            fileList = stream
                    // оставляем только обычные файлы
                    .filter(Files::isRegularFile)
                    // убираем файлы из игнорируемых папок
                    .filter(path -> !isInIgnoredDir(path, startDir))
                    // собираем все в один список
                    .collect(Collectors.toList());
        }
        return fileList;
    }

    //для проверки, находится ли файл в игнорируемой папке
    private boolean isInIgnoredDir(Path path, Path startDir) {
        Path relativePath = startDir.relativize(path.getParent());
        // проверяем каждую часть пути
        for (Path part : relativePath) {
            if (ignoredDirs.contains(part.toString())) {
                return true; // если нашли совпадение, возвращаем true
            }
        }
        return false;
    }
}