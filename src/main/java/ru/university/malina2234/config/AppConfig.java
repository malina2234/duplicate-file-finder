// Эта строка ОБЯЗАНА соответствовать пути к файлу
package ru.university.malina2234.config;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppConfig {
    private final Properties properties = new Properties();

    public AppConfig(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            properties.load(reader);
        }
    }

    public Path getScanDirectory() {
        String dir = properties.getProperty("scan.directory");
        return Paths.get(dir);
    }

    public String getHashAlgorithm() {
        return properties.getProperty("hash.algorithm", "MD5");
    }

    public Set<String> getIgnoredDirs() {
        String dirs = properties.getProperty("scan.ignore.dirs", "");
        return Stream.of(dirs.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}