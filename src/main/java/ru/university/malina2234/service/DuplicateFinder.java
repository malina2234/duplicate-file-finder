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

    public DuplicateFinder(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public Map<String, List<Path>> findDuplicates(List<Path> files) {
        Map<Long, List<Path>> bySize = files.stream()
                .collect(Collectors.groupingBy(this::getFileSize));

        return bySize.values().stream()
                .filter(list -> list.size() > 1)
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(this::calculateHash))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private long getFileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            System.err.println("Не удалось получить размер файла: " + path);
            return -1L;
        }
    }

    private String calculateHash(Path path) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
            try (InputStream is = Files.newInputStream(path);
                 DigestInputStream dis = new DigestInputStream(is, md)) {
                byte[] buffer = new byte[8192];
                while (dis.read(buffer) != -1) ;
            }
            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            System.err.println("Ошибка вычисления хэша для файла " + path + ": " + e.getMessage());
            return "";
        }
    }

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