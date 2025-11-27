package ru.university.malina2234.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * класс отвечающий за рекурсивное сканирование директории и сбор всех файлов
 */
public class FileScanner {
    private final Set<String> ignoredDirs;

    /**
     * конструктор для создания сканера файлов
     * @param ignoredDirs набор строк с названиями папок, которые нужно проигнорировать
     */
    public FileScanner(Set<String> ignoredDirs) {
        this.ignoredDirs = ignoredDirs;
    }

    /**
     * сканирует указанную директорию и возвращает список всех найденных файлов
     * @param startDir путь к начальной директории для сканирования
     * @return список путей к найденным файлам
     * @throws IOException если возникает ошибка при доступе к файловой системе
     */
    public List<Path> scan(Path startDir) throws IOException {
        // проверяем, что нам передали именно папку, а не файл
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

    /**
     * вспомогательный метод для проверки, находится ли файл в одной из игнорируемых папок
     * @param path путь к файлу, который нужно проверить
     * @param startDir корневая директория сканирования, для вычисления относительного пути
     * @return true, если файл находится в игнорируемой папке, иначе фолс
     */
    private boolean isInIgnoredDir(Path path, Path startDir) {
        // получаем относительный путь к файлу
        Path relativePath = startDir.relativize(path.getParent());
        // проверяем каждую часть пути
        for (Path part : relativePath) {
            if (ignoredDirs.contains(part.toString())) {
                return true; // если нашли совпаден`ие, возвращаем true
            }
        }
        return false;
    }
}