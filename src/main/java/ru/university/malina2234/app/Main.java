// Эта строка ОБЯЗАНА соответствовать пути к файлу
package ru.university.malina2234.app;

// А эти строки говорят, откуда брать другие классы. Они КРИТИЧЕСКИ ВАЖНЫ.
import ru.university.malina2234.config.AppConfig;
import ru.university.malina2234.service.DuplicateFinder;
import ru.university.malina2234.service.FileScanner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Main {
    // ... (весь остальной код класса Main без изменений) ...
    public static void main(String[] args) {
        System.out.println("Запуск поиска дубликатов...");

        try {
            AppConfig config = new AppConfig("app.properties");
            FileScanner scanner = new FileScanner(config.getIgnoredDirs());
            DuplicateFinder duplicateFinder = new DuplicateFinder(config.getHashAlgorithm());

            System.out.println("Сканирование директории: " + config.getScanDirectory());
            List<Path> files = scanner.scan(config.getScanDirectory());
            System.out.println("Найдено файлов для анализа: " + files.size());

            Map<String, List<Path>> duplicates = duplicateFinder.findDuplicates(files);
            printReport(duplicates);

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла конфигурации или сканирования директории: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printReport(Map<String, List<Path>> duplicates) {
        if (duplicates.isEmpty()) {
            System.out.println("\nДубликаты не найдены.");
            return;
        }

        System.out.println("\n--- Найдены дубликаты ---");
        int groupCount = 1;
        for (Map.Entry<String, List<Path>> entry : duplicates.entrySet()) {
            System.out.printf("\nГруппа %d (Хэш: %s):\n", groupCount++, entry.getKey());
            for (Path path : entry.getValue()) {
                System.out.println(" - " + path.toAbsolutePath());
            }
        }
    }
}