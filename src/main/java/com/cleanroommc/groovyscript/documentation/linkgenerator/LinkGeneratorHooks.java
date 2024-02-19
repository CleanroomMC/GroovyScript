package com.cleanroommc.groovyscript.documentation.linkgenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LinkGeneratorHooks {

    // Matches any class file to strip the .class extension and any nested classes.
    // Such that it can strip "x.class", "x$y.class", and "x$y$z.class" to just "x".
    public static final Pattern END_SEGMENT = Pattern.compile("(?>\\$[a-zA-Z0-9$_ ]+)?\\.class");

    protected static final Map<String, ILinkGenerator> REGISTRY = new HashMap<>();

    private static final BasicLinkGenerator DEFAULT = new BasicLinkGenerator();

    public static void init() {
        registerLinkGenerator(DEFAULT);
    }

    public static void registerLinkGenerator(ILinkGenerator linkGenerator) {
        REGISTRY.put(linkGenerator.id(), linkGenerator);
    }

    public static String convert(String mode, Class<?> clazz) {
        return REGISTRY.getOrDefault(mode, DEFAULT).convert(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

}
