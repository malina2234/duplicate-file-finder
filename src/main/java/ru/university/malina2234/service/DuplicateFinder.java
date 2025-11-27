package ru.university.malina2234.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * класс отвечающий за поиск файлов дубликатов
 */
public class DuplicateFinder {
    private final String hashAlgorithm;

    /**
     * конструктор для создания поисковика дубликатов
     * @param hashAlgorithm название алгоритма хеширования,md5
     */
    public DuplicateFinder(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    /**
     * ищет дубликаты в предоставленном списке файлов
     * сначала группирует по размеру для оптимизации, затем по хешу
     * @param files список путей к файлам для проверки
     * @return карта, где ключ - это хеш, а значение - список файлов-дубликатов
     */
    public Map<String, List<Path>> findDuplicates(List<Path> files) {
        // сначала группируем файлы по размеру для оптимизации
        Map<Long, List<Path>> bySize = files.stream()
                .collect(Collectors.groupingBy(this::getFileSize));

        // потом берем только группы с одинаковым размером
        // и уже для них считаем хеш и группируем по нему
        return bySize.values().stream()
                .filter(list -> list.size() > 1)
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(this::calculateHash))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * вспомогательный метод для получения размера файла
     * @param path путь к файлу
     * @return размер файла в байтах или -1 в случае ошибки
     */
    private long getFileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            System.err.println("Не удалось получить размер файла: " + path);
            return -1L;
        }
    }

    /**
     * вычисляет хеш сумму для указанного файла
     * @param path путь к файлу, для которого нужно посчитать хеш
     * @return строковое представление хеша или пустая строка в случае ошибки
     */
    private String calculateHash(Path path) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
            // читаем файл и одновременно считаем хеш, чтобы не загружать весь файл в память
            try (InputStream is = Files.newInputStream(path);
                 DigestInputStream dis = new DigestInputStream(is, md)) {
                byte[] buffer = new byte[8192];
                while (dis.read(buffer) != -1) ; // читаем пока не кончится
            }
            // преобразуем байты хеша в строку и возвращаем
            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            System.err.println("Ошибка вычисления хэша для файла " + path + ": " + e.getMessage());
            return "";
        }
    }

    /**
     * преобразует массив байтов (хеш) в шестнадцатеричную строку
     * @param hash массив байтов для преобразования
     * @return строковое представление хеша
     */
    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}