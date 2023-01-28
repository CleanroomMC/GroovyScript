package com.cleanroommc.groovyscript.sandbox.mapper;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.sandbox.GroovyDeobfuscationMapper;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class GroovyDeobfMapper {

    private static final Map<String, Map<String, String>> METHODS = new Object2ObjectOpenHashMap<>();
    private static final Map<String, Map<String, String>> FIELDS = new Object2ObjectOpenHashMap<>();

    public static void init() {
        GroovyScript.LOGGER.info("Generating obfuscation map...");
        try {
            String splitter = " ";
            String method = "MD:";
            String field = "FD:";

            InputStream stream = GroovyDeobfuscationMapper.class.getResourceAsStream("/assets/groovyscript/mappings.srg");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(splitter);

                    if (parts[0].equals(field)) {
                        int index = parts[1].lastIndexOf("/");
                        String className = parts[1].substring(0, index);
                        String fieldObf = parts[1].substring(index + 1);
                        String fieldDeobf = parts[2].substring(index + 1);
                        if (!fieldObf.equals(fieldDeobf)) {
                            className = className.replace('/', '.');
                            FIELDS.computeIfAbsent(className, key -> new Object2ObjectOpenHashMap<>()).put(fieldObf, fieldDeobf);
                        }
                    } else if (parts[0].equals(method)) {
                        int index = parts[1].lastIndexOf("/");
                        String className = parts[1].substring(0, index);
                        String methodObf = parts[1].substring(index + 1);
                        String methodArgs = parts[2];
                        String methodDeobf = parts[3].substring(index + 1);
                        if (!methodObf.equals(methodDeobf)) {
                            className = className.replace('/', '.');
                            METHODS.computeIfAbsent(className, key -> new Object2ObjectOpenHashMap<>()).put(methodObf, methodDeobf);
                        }
                    }
                }
            }
            GroovyScript.LOGGER.info("Read {} field and {} method mapping names", FIELDS.size(), METHODS.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static String getDeobfMethod(Class<?> clazz, String obfMethod) {
        return METHODS.getOrDefault(clazz.getName(), Object2ObjectMaps.emptyMap()).get(obfMethod);
    }

    @Nullable
    public static String getDeobfField(Class<?> clazz, String obfField) {
        return FIELDS.getOrDefault(clazz.getName(), Object2ObjectMaps.emptyMap()).get(obfField);
    }
}
