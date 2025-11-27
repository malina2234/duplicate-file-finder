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

public class DuplicateFinder {
    private final String hashAlgorithm;

    //  принимает название алгоритма хеширования
    public DuplicateFinder(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    // ищет дубликаты в списке файлов
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

    //  для получения размера файла
    private long getFileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            // если не удалось получить размер, выводим ошибку
            System.err.println("Не удалось получить размер файла: " + path);
            return -1L;
        }
    }

    // для вычисления хеш-суммы файла
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

    //для преобразования байтов хеша в красивую строку (hex)
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