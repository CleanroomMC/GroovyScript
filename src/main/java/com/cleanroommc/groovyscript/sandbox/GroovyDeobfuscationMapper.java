package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Map;

public class GroovyDeobfuscationMapper {

    private static final Map<Class<?>, Map<String, String>> OBF_FIELD_NAMES = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<?>, Map<String, String>> OBF_METHOD_NAMES = new Object2ObjectOpenHashMap<>();

    private static final boolean outputSrgNames = false;

    public static void init() {
        GroovyScript.LOGGER.info("Generating obfuscation map...");
        try {
            Field rawFieldMapsField = FMLDeobfuscatingRemapper.class.getDeclaredField("rawFieldMaps");
            Field rawMethodMapsField = FMLDeobfuscatingRemapper.class.getDeclaredField("rawMethodMaps");
            rawFieldMapsField.setAccessible(true);
            rawMethodMapsField.setAccessible(true);
            Map<String, Map<String, String>> rawFieldMaps = (Map<String, Map<String, String>>) rawFieldMapsField.get(FMLDeobfuscatingRemapper.INSTANCE);
            Map<String, Map<String, String>> rawMethodMaps = (Map<String, Map<String, String>>) rawMethodMapsField.get(FMLDeobfuscatingRemapper.INSTANCE);

            String splitter = ";";
            String func = "func";
            String field = "field";
            String clazz = "CLASS";
            if (FMLLaunchHandler.isDeobfuscatedEnvironment()) {
                if (outputSrgNames) {
                    File file = Loader.instance().getConfigDir().getParentFile().toPath().resolve("srgNames.txt").toFile();
                    file.delete();
                    file.createNewFile();

                    FileWriter writer = new FileWriter(file);
                    int c = 0;
                    for (Map.Entry<String, Map<String, String>> classEntry : rawFieldMaps.entrySet()) {
                        StringBuilder builder = new StringBuilder();
                        for (Map.Entry<String, String> methodEntry : classEntry.getValue().entrySet()) {
                            String obfName = methodEntry.getKey().substring(0, methodEntry.getKey().indexOf(':'));
                            if (obfName.startsWith(field)) {
                                builder.append(methodEntry.getValue()).append(splitter).append(obfName).append('\n');
                                c++;
                            }
                        }
                        if (builder.length() > 0) {
                            writer.write(clazz + splitter + classEntry.getKey().replace('/', '.') + '\n');
                            writer.write(builder.toString());
                        }
                    }
                    for (Map.Entry<String, Map<String, String>> classEntry : rawMethodMaps.entrySet()) {
                        StringBuilder builder = new StringBuilder();
                        for (Map.Entry<String, String> methodEntry : classEntry.getValue().entrySet()) {
                            String obfName = methodEntry.getKey().substring(0, methodEntry.getKey().indexOf('('));
                            if (obfName.startsWith(func)) {
                                builder.append(methodEntry.getValue()).append(splitter).append(obfName).append('\n');
                                c++;
                            }
                        }
                        if (builder.length() > 0) {
                            writer.write(clazz + splitter + classEntry.getKey().replace('/', '.') + '\n');
                            writer.write(builder.toString());
                        }
                    }

                    writer.flush();
                    writer.close();
                    GroovyScript.LOGGER.info("Saved {} mapping names", c);
                }
            } else {
                InputStream stream = GroovyDeobfuscationMapper.class.getResourceAsStream("/assets/groovyscript/srgNames.txt");
                GroovyScript.LOGGER.info(".");
                try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
                    Class<?> currentClass = null;
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(splitter);
                        if (parts[0].equals(clazz)) {
                            try {
                                currentClass = Class.forName(parts[1]);
                            } catch (Exception e) {
                                GroovyScript.LOGGER.info(" - cant find class for {}", parts[1]);
                            }
                            continue;
                        }
                        if (currentClass == null) continue;
                        if (parts[1].startsWith(func)) {
                            OBF_METHOD_NAMES.computeIfAbsent(currentClass, key -> new Object2ObjectOpenHashMap<>()).put(parts[0], parts[1]);
                        } else {
                            OBF_FIELD_NAMES.computeIfAbsent(currentClass, key -> new Object2ObjectOpenHashMap<>()).put(parts[0], parts[1]);
                        }
                    }
                }
                GroovyScript.LOGGER.info("Read {} field and {} method mapping names", OBF_FIELD_NAMES.size(), OBF_METHOD_NAMES.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getObfuscatedMethodName(Class<?> receiver, String method) {
        Map<String, String> obfNames = OBF_METHOD_NAMES.get(receiver);
        if (obfNames == null) return method;
        return obfNames.getOrDefault(method, method);
    }

    public static String getObfuscatedFieldName(Class<?> receiver, String field) {
        Map<String, String> obfNames = OBF_FIELD_NAMES.get(receiver);
        if (obfNames == null) return field;
        return obfNames.getOrDefault(field, field);
    }
}
