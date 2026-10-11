package com.cleanroommc.groovyscript.sandbox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Content identity for a complete compilation environment, independent of file timestamps. */
final class CacheFingerprint {

    private CacheFingerprint() {}

    static Path classpathFile(URL url) throws IOException {
        // Some coremods return a class-resource URL as their source File; Forge adds it to the classpath.
        // Recover the containing archive, including a file URL accidentally wrapped inside another file URL.
        String location = url.toExternalForm();
        int archive = location.indexOf("!/");
        int file = location.lastIndexOf("file:", archive < 0 ? location.length() : archive);
        if (archive >= 0 && file >= 0) url = new URL(location.substring(file, archive));
        try {
            return new File(url.toURI()).toPath();
        } catch (URISyntaxException | IllegalArgumentException e) {
            throw new IOException("Cannot fingerprint compilation input " + url, e);
        }
    }

    static String capture(Map<String, Path> roots, Map<String, String> environment) throws IOException {
        MessageDigest digest = digest();
        for (Map.Entry<String, String> entry : new TreeMap<>(environment).entrySet()) {
            text(digest, entry.getKey());
            text(digest, entry.getValue());
        }
        for (Map.Entry<String, Path> entry : new TreeMap<>(roots).entrySet()) {
            text(digest, entry.getKey());
            tree(digest, entry.getValue(), Collections.emptySet());
        }
        return hex(digest.digest());
    }

    static String cacheContents(Path root) throws IOException {
        MessageDigest digest = digest();
        tree(digest, root, Collections.singleton("_index.json"));
        return hex(digest.digest());
    }

    private static void tree(MessageDigest digest, Path root, Set<String> excluded) throws IOException {
        // exists() also returns false for unreadable paths; only an explicit missing path is absent.
        if (Files.notExists(root)) {
            text(digest, "missing");
            return;
        }
        if (Files.isRegularFile(root)) {
            text(digest, "file");
            file(digest, root);
            return;
        }
        text(digest, "directory");
        List<Path> files;
        try (Stream<Path> stream = Files.walk(root, FileVisitOption.FOLLOW_LINKS)) {
            files = stream.filter(path -> !Files.isDirectory(path))
                    .sorted((a, b) -> relative(root, a).compareTo(relative(root, b)))
                    .collect(Collectors.toList());
        }
        for (Path path : files) {
            String name = relative(root, path);
            if (!excluded.contains(name)) {
                text(digest, name);
                file(digest, path);
            }
        }
    }

    private static String relative(Path root, Path path) {
        return root.relativize(path).toString().replace('\\', '/');
    }

    private static void file(MessageDigest digest, Path path) throws IOException {
        if (!Files.isRegularFile(path)) throw new IOException("Cache input is not a regular file: " + path);
        MessageDigest content = digest();
        try (InputStream input = Files.newInputStream(path)) {
            byte[] buffer = new byte[65536];
            int length;
            while ((length = input.read(buffer)) != -1) content.update(buffer, 0, length);
        }
        digest.update(content.digest());
    }

    private static void text(MessageDigest digest, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        digest.update((byte) (length >>> 24));
        digest.update((byte) (length >>> 16));
        digest.update((byte) (length >>> 8));
        digest.update((byte) length);
        digest.update(bytes);
    }

    private static MessageDigest digest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    private static String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            result.append(Character.forDigit((value >>> 4) & 15, 16));
            result.append(Character.forDigit(value & 15, 16));
        }
        return result.toString();
    }
}
