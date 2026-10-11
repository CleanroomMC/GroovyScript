package com.cleanroommc.groovyscript.sandbox;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

/** A persisted generation is reusable only after its inputs and all its class files are verified. */
final class ScriptCacheStore {

    private final Path root;
    private final int version;

    ScriptCacheStore(Path root, int version) {
        this.root = root.toAbsolutePath().normalize();
        this.version = version;
    }

    JsonObject open(String inputs) throws IOException {
        validateRoot();
        Path index = this.root.resolve("_index.json");
        JsonObject accepted = null;
        if (Files.exists(index)) {
            try (Reader reader = Files.newBufferedReader(index, StandardCharsets.UTF_8)) {
                JsonElement element = new JsonParser().parse(reader);
                if (element.isJsonObject()) {
                    JsonObject json = element.getAsJsonObject();
                    if (json.get("version").getAsInt() == this.version && inputs.equals(json.get("inputs").getAsString()) && json.get("index").isJsonArray() && CacheFingerprint.cacheContents(this.root).equals(json.get("classes").getAsString())) {
                        accepted = json;
                    }
                }
            } catch (JsonParseException | IllegalStateException | IllegalArgumentException | NullPointerException e) {
                // A missing, older or interrupted index is a cache miss.
            }
        }
        if (accepted == null) clear();
        // Remove the commit record before any class can be rewritten. A crash now forces a rebuild.
        Files.deleteIfExists(index);
        return accepted;
    }

    void publish(String inputs, JsonObject index) throws IOException {
        validateRoot();
        Files.createDirectories(this.root);
        index.addProperty("version", this.version);
        index.addProperty("inputs", inputs);
        index.addProperty("classes", CacheFingerprint.cacheContents(this.root));
        Path temporary = Files.createTempFile(this.root, "_index-", ".tmp");
        try {
            Files.write(temporary, index.toString().getBytes(StandardCharsets.UTF_8));
            Files.move(temporary, this.root.resolve("_index.json"), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    void clear() throws IOException {
        validateRoot();
        if (Files.notExists(this.root)) return;
        Files.walkFileTree(this.root, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path directory, IOException error) throws IOException {
                if (error != null) throw error;
                Files.delete(directory);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void validateRoot() throws IOException {
        Path path = this.root;
        while (path != null && Files.notExists(path)) path = path.getParent();
        if (path == null || !Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS) || !path.toRealPath().equals(path)) {
            throw new IOException("Groovy cache must be a directory without redirected parents: " + this.root);
        }
    }
}
