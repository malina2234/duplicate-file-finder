package ru.university.malina2234.config;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * класс для чтения и предоставления настроек из файла app properties
 */
public class AppConfig {
    private final Properties properties = new Properties();

    /**
     * загружает настройки из файла при создании объекта
     * @param filePath путь к файлу конфигурации, app properties
     * @throws IOException если файл не найден или не может быть прочитан
     */
    public AppConfig(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            properties.load(reader);
        }
    }

    /**
     * возвращает путь к директории для сканирования
     * @return путь к директории
     */
    public Path getScanDirectory() {
        String dir = properties.getProperty("scan.directory");
        return Paths.get(dir);
    }

    /**
     * возвращает название алгоритма хеширования
     * @return название алгоритма, md5
     */
    public String getHashAlgorithm() {
        // если в файле нет алгоритма, по умолчанию будет md5
        return properties.getProperty("hash.algorithm", "MD5");
    }

    /**
     * возвращает набор названий директорий, которые нужно игнорировать
     * @return набор строк с названиями папок
     */
    public Set<String> getIgnoredDirs() {
        String dirs = properties.getProperty("scan.ignore.dirs", "");
        // разбиваем строку с папками по запятой и убираем лишние пробелы
        return Stream.of(dirs.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}