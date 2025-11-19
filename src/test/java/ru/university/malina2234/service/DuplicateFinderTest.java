package ru.university.malina2234.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса DuplicateFinder.
 */
class DuplicateFinderTest {

    // JUnit 5 автоматически создаст временную директорию для теста и удалит ее после.
    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Должен найти одну группу дубликатов")
    void shouldFindOneGroupOfDuplicates() throws IOException {
        // Arrange (Подготовка)
        // Создаем файлы: два одинаковых и один уникальный
        Path file1 = Files.createFile(tempDir.resolve("file1.txt"));
        Files.write(file1, "same content".getBytes());

        Path file2 = Files.createFile(tempDir.resolve("file2.txt"));
        Files.write(file2, "different content".getBytes());

        Path file3 = Files.createFile(tempDir.resolve("file3.txt"));
        Files.write(file3, "same content".getBytes());

        List<Path> files = Arrays.asList(file1, file2, file3);
        DuplicateFinder finder = new DuplicateFinder("MD5");

        // Act (Действие)
        Map<String, List<Path>> duplicates = finder.findDuplicates(files);

        // Assert (Проверка)
        assertNotNull(duplicates, "Карта дубликатов не должна быть null");
        assertEquals(1, duplicates.size(), "Должна быть найдена одна группа дубликатов");

        // Проверяем, что в найденной группе именно те файлы, которые мы ожидаем
        List<Path> foundFiles = duplicates.values().iterator().next();
        assertEquals(2, foundFiles.size(), "В группе должно быть 2 файла");
        assertTrue(foundFiles.contains(file1), "Список должен содержать file1.txt");
        assertTrue(foundFiles.contains(file3), "Список должен содержать file3.txt");
    }

    @Test
    @DisplayName("Не должен находить дубликаты, если все файлы уникальны")
    void shouldNotFindDuplicatesWhenFilesAreUnique() throws IOException {
        // Arrange
        Path file1 = Files.createFile(tempDir.resolve("unique1.txt"));
        Files.write(file1, "content 1".getBytes());

        Path file2 = Files.createFile(tempDir.resolve("unique2.txt"));
        Files.write(file2, "content 2".getBytes());

        List<Path> files = Arrays.asList(file1, file2);
        DuplicateFinder finder = new DuplicateFinder("SHA-256");

        // Act
        Map<String, List<Path>> duplicates = finder.findDuplicates(files);

        // Assert
        assertNotNull(duplicates);
        assertTrue(duplicates.isEmpty(), "Список дубликатов должен быть пустым");
    }
}