package com.cleanroommc.groovyscript.sandbox;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScriptCacheStoreTest {

    @TempDir
    Path temporary;

    private Path write(String name, String contents) throws IOException {
        Path file = this.temporary.resolve(name);
        Files.createDirectories(file.getParent());
        Files.write(file, contents.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private String fingerprint() throws IOException {
        Map<String, Path> inputs = new LinkedHashMap<>();
        inputs.put("scripts", this.temporary.resolve("groovy"));
        inputs.put("mods", this.temporary.resolve("mods"));
        inputs.put("config", this.temporary.resolve("config/groovyscript.cfg"));
        return CacheFingerprint.capture(inputs, Collections.singletonMap("runtime", "runtime-1"));
    }

    private ScriptCacheStore store() {
        return new ScriptCacheStore(this.temporary.resolve("cache"), 5);
    }

    private void publish() throws IOException {
        write("cache/Script.clz", "compiled script");
        write("cache/Helper.clz", "compiled helper");
        JsonObject index = new JsonObject();
        index.add("index", new JsonArray());
        store().publish(fingerprint(), index);
    }

    @Test
    void unchangedInputsReuseClassesWithoutRewritingThem() throws IOException {
        Path source = write("groovy/Main.groovy", "println 'current'");
        publish();
        FileTime modified = Files.getLastModifiedTime(this.temporary.resolve("cache/Script.clz"));
        // A timestamp-only change does not invalidate identical compilation inputs.
        Files.setLastModifiedTime(source, FileTime.fromMillis(1));
        assertNotNull(store().open(fingerprint()));
        assertEquals(modified, Files.getLastModifiedTime(this.temporary.resolve("cache/Script.clz")));
        assertFalse(Files.exists(this.temporary.resolve("cache/_index.json")));
    }

    @Test
    void equalTimestampSourceChangeInvalidatesEveryDependentClass() throws IOException {
        Path source = write("groovy/Helper.groovy", "return 'old'");
        FileTime time = Files.getLastModifiedTime(source);
        publish();
        write("groovy/Helper.groovy", "return 'new'");
        Files.setLastModifiedTime(source, time);
        assertNull(store().open(fingerprint()));
        assertFalse(Files.exists(this.temporary.resolve("cache/Script.clz")));
        assertFalse(Files.exists(this.temporary.resolve("cache/Helper.clz")));
    }

    @Test
    void backdatedSourceChangeInvalidates() throws IOException {
        Path source = write("groovy/Main.groovy", "old");
        publish();
        write("groovy/Main.groovy", "new");
        Files.setLastModifiedTime(source, FileTime.fromMillis(1));
        assertNull(store().open(fingerprint()));
    }

    @Test
    void addedRemovedAndRenamedSourcesChangeIdentity() throws IOException {
        Path file = write("groovy/First.groovy", "class First {}");
        String original = fingerprint();
        Path second = write("groovy/Second.groovy", "class Second {}");
        assertNotEquals(original, fingerprint());
        Files.delete(second);
        assertEquals(original, fingerprint());
        Files.move(file, this.temporary.resolve("groovy/Renamed.groovy"));
        assertNotEquals(original, fingerprint());
        Files.delete(this.temporary.resolve("groovy/Renamed.groovy"));
        assertNotEquals(original, fingerprint());
    }

    @Test
    void dependencyReplacementWithSameSizeAndTimestampInvalidates() throws IOException {
        Path dependency = write("mods/dependency.jar", "old bytes");
        FileTime time = Files.getLastModifiedTime(dependency);
        publish();
        write("mods/dependency.jar", "new bytes");
        Files.setLastModifiedTime(dependency, time);
        assertNull(store().open(fingerprint()));
    }

    @Test
    void runConfigurationAndCompilerConfigurationAffectIdentity() throws IOException {
        write("groovy/runConfig.json", "{}");
        String first = fingerprint();
        write("groovy/runConfig.json", "{\"debug\":true}");
        assertNotEquals(first, fingerprint());
        String second = fingerprint();
        write("config/groovyscript.cfg", "packmode=expert");
        assertNotEquals(second, fingerprint());
    }

    @Test
    void generatedReportsDoNotInvalidateDeclaredCompilationInputs() throws IOException {
        write("config/groovyscript.cfg", "packmode=normal");
        String original = fingerprint();
        write("config/generated/output.json", "new report");
        write("config/load-timings.dat", "new timings");
        assertEquals(original, fingerprint());
    }

    @Test
    void runtimeChangeAffectsIdentity() throws IOException {
        Map<String, Path> roots = Collections.singletonMap("scripts", this.temporary);
        assertNotEquals(
                CacheFingerprint.capture(roots, Collections.singletonMap("runtime", "one")),
                CacheFingerprint.capture(roots, Collections.singletonMap("runtime", "two")));
    }

    @Test
    void oldCacheWithoutContentIdentityIsDiscarded() throws IOException {
        write("cache/Script.clz", "old class");
        write("cache/_index.json", "{\"version\":4,\"index\":[]}");
        assertNull(store().open(fingerprint()));
        assertFalse(Files.exists(this.temporary.resolve("cache/Script.clz")));
    }

    @Test
    void damagedClassCannotBeReused() throws IOException {
        publish();
        write("cache/Script.clz", "damaged class");
        assertNull(store().open(fingerprint()));
    }

    @Test
    void missingClassCannotBeReused() throws IOException {
        publish();
        Files.delete(this.temporary.resolve("cache/Helper.clz"));
        assertNull(store().open(fingerprint()));
    }

    @Test
    void interruptedRunIsNotConsideredCommitted() throws IOException {
        publish();
        assertNotNull(store().open(fingerprint()));
        write("cache/Script.clz", "partly replaced");
        assertNull(store().open(fingerprint()));
        assertFalse(Files.exists(this.temporary.resolve("cache/Script.clz")));
    }

    @Test
    void truncatedIndexIsDiscarded() throws IOException {
        publish();
        write("cache/_index.json", "{\"version\":");
        assertNull(store().open(fingerprint()));
    }

    @Test
    void invalidCacheParentFailsWithoutRemovingUnrelatedData() throws IOException {
        Path unrelated = write("blocked", "preserve me");
        ScriptCacheStore blocked = new ScriptCacheStore(unrelated.resolve("cache"), 5);
        assertThrows(IOException.class, () -> blocked.open(fingerprint()));
        assertEquals("preserve me", new String(Files.readAllBytes(unrelated), StandardCharsets.UTF_8));
    }

    @Test
    void classResourceSourcesResolveToTheirContainingArchive() throws IOException {
        Path jar = write("mods/library with spaces.jar", "library");
        URL resource = new URL("jar:" + jar.toUri().toURL() + "!/some/Container.class");
        assertEquals(jar, CacheFingerprint.classpathFile(resource));
        URL malformed = new URL(this.temporary.toUri().toURL() + jar.toUri().toURL().toString() + "!/some/Container.class");
        assertEquals(jar, CacheFingerprint.classpathFile(malformed));
    }
}
