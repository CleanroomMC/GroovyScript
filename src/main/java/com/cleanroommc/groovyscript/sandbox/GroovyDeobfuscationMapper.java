package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class GroovyDeobfuscationMapper {

    private static final Map<Class<?>, Map<String, String>> OBF_FIELD_NAMES = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<?>, Map<String, String>> OBF_METHOD_NAMES = new Object2ObjectOpenHashMap<>();

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
                            try {
                                Class<?> clazz = Class.forName(className.replace('/', '.'));
                                OBF_FIELD_NAMES.computeIfAbsent(clazz, key -> new Object2ObjectOpenHashMap<>()).put(fieldDeobf, fieldObf);
                            } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                            }
                        }
                    } else if (parts[0].equals(method)) {
                        int index = parts[1].lastIndexOf("/");
                        String className = parts[1].substring(0, index);
                        String methodObf = parts[1].substring(index + 1);
                        String methodDeobf = parts[3].substring(index + 1);
                        if (!methodObf.equals(methodDeobf)) {
                            try {
                                Class<?> clazz = Class.forName(className.replace('/', '.'));
                                OBF_METHOD_NAMES.computeIfAbsent(clazz, key -> new Object2ObjectOpenHashMap<>()).put(methodDeobf, methodObf);
                            } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                            }
                        }
                    }
                }
            }
            GroovyScript.LOGGER.info("Read {} field and {} method mapping names", OBF_FIELD_NAMES.size(), OBF_METHOD_NAMES.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getObfuscatedMethodName(Class<?> receiver, String method) {
        Class<Object> objClass = Object.class;
        Map<String, String> obfNames;
        String obfName = null;

        do {
            obfNames = OBF_METHOD_NAMES.get(receiver);
            if (obfNames != null) {
                obfName = obfNames.get(method);
            }
            receiver = receiver.getSuperclass();
        } while (obfName == null && receiver != null && receiver != objClass);

        return obfName != null ? obfName : method;
    }

    public static String getObfuscatedFieldName(Class<?> receiver, String field) {
        Class<Object> objClass = Object.class;
        Map<String, String> obfNames;
        String obfName = null;

        do {
            obfNames = OBF_FIELD_NAMES.get(receiver);
            if (obfNames != null) {
                obfName = obfNames.get(field);
            }
            receiver = receiver.getSuperclass();
        } while (obfName == null && receiver != null && receiver != objClass);

        return obfName != null ? obfName : field;
    }
}
