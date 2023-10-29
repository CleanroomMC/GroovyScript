package com.cleanroommc.groovyscript.documentation;

import com.cleanroommc.groovyscript.Tags;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Parses a class and converts it into a relative link to a website.
 * By default, converts files to a location on CleanroomMC's Groovyscript repository and uses Groovyscript's version,
 * but can be completely overwritten to link to anywhere.
 * <br>
 * An addon should replace {@link #domain()} and {@link #version()} with a link to their own repo, e.g.
 * <a href="https://github.com/WaitingIdly/GroovyScript/">https://github.com/WaitingIdly/GroovyScript/</a>
 * and the version of the addon.
 */
public class LinkGenerator {

    // Matches any class file to strip the .class extension and any nested classes.
    // Strips "x.class", "x$y.class", and "x$y$z.class" to just "x".
    protected static final Pattern END_SEGMENT = Pattern.compile("(?>\\$[a-zA-Z0-9$_ ]+)?\\.class");

    protected static final Map<String, LinkGenerator> REGISTRY = new HashMap<>();

    private static final LinkGenerator DEFAULT = new LinkGenerator();

    public static void init() {
        REGISTRY.put(Tags.ID, DEFAULT);
    }

    public static String convert(String mode, Class<?> clazz) {
        return REGISTRY.getOrDefault(mode, DEFAULT).convert(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    protected String domain() {
        return "https://github.com/CleanroomMC/GroovyScript/";
    }

    protected String version() {
        return "v" + Tags.VERSION;
    }

    protected String path() {
        return String.format("blob/%s/src/main/java/", version());
    }

    protected String extension() {
        return ".java";
    }

    protected String internalLocation(String location) {
        return location.substring(location.indexOf(".jar!/") + 6);
    }

    protected String trimmedLocation(String location) {
        return END_SEGMENT.matcher(internalLocation(location)).replaceFirst("");
    }

    protected String convert(String location) {
        return domain() + path() + trimmedLocation(location) + extension();
    }

}
