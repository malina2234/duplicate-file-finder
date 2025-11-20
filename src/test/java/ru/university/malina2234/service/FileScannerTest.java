package ru.university.malina2234.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class FileScannerTest {

    @TempDir
    Path tempDir;

    private Path fileInRoot;
    private Path fileInSubdir;
    private Path fileInIgnoredDir;

    @BeforeEach
    void setUp() throws IOException {
        // Создаем сложную структуру папок и файлов перед каждым тестом
        fileInRoot = Files.createFile(tempDir.resolve("root.txt"));

        Path subdir = Files.createDirectory(tempDir.resolve("subdir"));
        fileInSubdir = Files.createFile(subdir.resolve("sub.txt"));

        Path ignoredDir = Files.createDirectory(tempDir.resolve(".git"));
        fileInIgnoredDir = Files.createFile(ignoredDir.resolve("config"));
    }

    @Test
    @DisplayName("Должен найти все файлы, кроме тех, что в игнорируемой директории")
    void shouldFindAllFilesExceptIgnored() throws IOException {
        // Arrange
        Set<String> ignored = new HashSet<>(Collections.singletonList(".git"));
        FileScanner scanner = new FileScanner(ignored);

        // Act
        List<Path> foundFiles = scanner.scan(tempDir);

        // Assert
        assertEquals(2, foundFiles.size(), "Должно быть найдено 2 файла");
        assertTrue(foundFiles.contains(fileInRoot), "Должен найти файл в корне");
        assertTrue(foundFiles.contains(fileInSubdir), "Должен найти файл в поддиректории");
        assertFalse(foundFiles.contains(fileInIgnoredDir), "Не должен находить файл в игнорируемой директории");
    }
}