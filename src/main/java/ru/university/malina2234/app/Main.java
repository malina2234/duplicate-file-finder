package ru.university.malina2234.app;

import ru.university.malina2234.config.AppConfig;
import ru.university.malina2234.service.DuplicateFinder;
import ru.university.malina2234.service.FileScanner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("Zapusk poiska dublikatov");

        // блок для  выполнения кода и нхождение ошибок
        try {
            // создаем объект для чтения файла конфигурации
            AppConfig config = new AppConfig("app.properties");
            // создаем сканер файлов и передаем ему папки для игнорирования
            FileScanner scanner = new FileScanner(config.getIgnoredDirs());
            // создаем поисковик дубликатов и передаем ему алгоритм хеширования
            DuplicateFinder duplicateFinder = new DuplicateFinder(config.getHashAlgorithm());

            System.out.println("Skanirovanie direktorii: " + config.getScanDirectory());
            // запускаем сканирование и получаем список всех файлов
            List<Path> files = scanner.scan(config.getScanDirectory());
            System.out.println("Naydeno faylov dlya analiza: " + files.size());

            // передаем список файлов поисковику дубликатов
            Map<String, List<Path>> duplicates = duplicateFinder.findDuplicates(files);
            printReport(duplicates);

        } catch (IOException e) {
            System.err.println("Oshibka chteniya fayla konfiguratsii ili skanirovaniya direktorii: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Proizoshla nepredvidennaya oshibka: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printReport(Map<String, List<Path>> duplicates) {
        // если список дубликатов пустой
        if (duplicates.isEmpty()) {
            System.out.println("\nDublikaty ne naydeny.");
            return;
        }

        System.out.println("\nNaydeny dublikaty ");
        int groupCount = 1; // счетчик для нумерации групп
        for (Map.Entry<String, List<Path>> entry : duplicates.entrySet()) {
            System.out.printf("\nGruppa %d (Hesh: %s):\n", groupCount++, entry.getKey());
            for (Path path : entry.getValue()) {
                // печатаем полный путь к файлу
                System.out.println(" - " + path.toAbsolutePath());
            }
        }
    }
}