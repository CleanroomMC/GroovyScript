package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GroovyDeobfuscationMapper {

    private static final Map<String, Map<String, String>> OBF_FIELD_NAMES = new Object2ObjectOpenHashMap<>();
    private static final Map<String, Map<String, MethodInfo>> OBF_METHOD_NAMES = new Object2ObjectOpenHashMap<>();
    private static final Char2ObjectOpenHashMap<String> PRIMITIVE_DESC_MAP = new Char2ObjectOpenHashMap<>();

    static {
        PRIMITIVE_DESC_MAP.put('I', "java.lang.Integer");
        PRIMITIVE_DESC_MAP.put('J', "java.lang.Long");
        PRIMITIVE_DESC_MAP.put('F', "java.lang.Float");
        PRIMITIVE_DESC_MAP.put('D', "java.lang.Double");
        PRIMITIVE_DESC_MAP.put('B', "java.lang.Byte");
        PRIMITIVE_DESC_MAP.put('S', "java.lang.Short");
        PRIMITIVE_DESC_MAP.put('C', "java.lang.Character");
        PRIMITIVE_DESC_MAP.put('Z', "java.lang.Boolean");
    }

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
                            OBF_FIELD_NAMES.computeIfAbsent(className, key -> new Object2ObjectOpenHashMap<>()).put(fieldDeobf, fieldObf);
                        }
                    } else if (parts[0].equals(method)) {
                        int index = parts[1].lastIndexOf("/");
                        String className = parts[1].substring(0, index);
                        String methodObf = parts[1].substring(index + 1);
                        String methodArgs = parts[2];
                        String methodDeobf = parts[3].substring(index + 1);
                        if (!methodObf.equals(methodDeobf)) {
                            className = className.replace('/', '.');
                            Map<String, MethodInfo> map = OBF_METHOD_NAMES.computeIfAbsent(className, key -> new Object2ObjectOpenHashMap<>());
                            MethodInfo info = map.get(methodDeobf);
                            if (info == null) {
                                info = new MethodInfo(methodDeobf, methodObf, methodArgs);
                                map.put(methodDeobf, info);
                            } else {
                                info.registerOverloadedMethod(methodObf, methodArgs);
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

    public static String getObfuscatedMethodName(Class<?> receiver, String method, Object[] args) {
        Class<Object> objClass = Object.class;
        Map<String, MethodInfo> obfNames;
        String obfName = null;

        do {
            obfNames = OBF_METHOD_NAMES.get(receiver.getName());
            if (obfNames != null) {
                MethodInfo methodInfo = obfNames.get(method);
                if (methodInfo != null) {
                    obfName = methodInfo.getMethod(args);
                }
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
            obfNames = OBF_FIELD_NAMES.get(receiver.getName());
            if (obfNames != null) {
                obfName = obfNames.get(field);
            }
            receiver = receiver.getSuperclass();
        } while (obfName == null && receiver != null && receiver != objClass);

        return obfName != null ? obfName : field;
    }

    private static class MethodInfo {

        private final String deobfName;
        private Object2ObjectOpenCustomHashMap<Object[], String> obfNames;
        private final String defObfName;
        private final String defArgs;

        private MethodInfo(String deobfName, String defObfName, String defArgs) {
            this.deobfName = deobfName;
            this.defObfName = defObfName;
            this.defArgs = defArgs;
        }

        public void registerOverloadedMethod(String obfName, String args) {
            if (obfName.equals(defObfName)) return;
            if (obfNames == null) {
                obfNames = new Object2ObjectOpenCustomHashMap<>(PARAM_HASH_STRATEGY);
                obfNames.put(makeClassArray(defArgs), defObfName);
            }
            obfNames.put(makeClassArray(args), obfName);
        }

        @Nullable
        public String getMethod(Object[] args) {
            return obfNames == null ? defObfName : obfNames.get(args);
        }

        private static String[] makeClassArray(String descriptor) {
            descriptor = descriptor.substring(1, descriptor.indexOf(")"));
            List<String> classes = new ArrayList<>();
            try {
                for (int i = 0; i < descriptor.length(); i++) {
                    char c = descriptor.charAt(i);
                    if (c == '[') {
                        char c1 = descriptor.charAt(i + 1);
                        if (c1 == 'L') {
                            int last = descriptor.indexOf(';', i + 1);
                            classes.add(descriptor.substring(i + 2, last).replace('/', '.'));
                            i = last + 1;
                        } else {
                            String name = '[' + PRIMITIVE_DESC_MAP.get(c1);
                            classes.add(name);
                            i++;
                        }
                        continue;
                    } else if (c == 'L') {
                        int last = descriptor.indexOf(';', i + 1);
                        classes.add(descriptor.substring(i + 1, last).replace('/', '.'));
                        i = last + 1;
                        continue;
                    }
                    String className = PRIMITIVE_DESC_MAP.get(c);
                    if (className != null) {
                        classes.add(className);
                    }
                }
            } catch (Exception e) {
                GroovyScript.LOGGER.info("An exception occured while creating a class array of arguments for {}", descriptor);
                e.printStackTrace();
            }

            return classes.toArray(new String[0]);
        }
    }

    private static final Hash.Strategy<Object[]> PARAM_HASH_STRATEGY = new Hash.Strategy<Object[]>() {
        @Override
        public int hashCode(Object[] o) {
            if (o == null || o.length == 0) {
                return 0;
            }
            int result = 1;
            if (o.getClass() == String[].class) {
                for (Object element : o)
                    result = 31 * result + (element == null ? 0 : element.hashCode());
            } else {
                for (Object element : o)
                    result = 31 * result + (element == null ? 0 : element.getClass().getName().hashCode());
            }
            return result;
        }

        @Override
        public boolean equals(Object[] a, Object[] b) {
            if ((a == null || a.length == 0) && (b == null || b.length == 0)) return true;
            if (a == null || b == null || a.length != b.length) return false;
            if (a.getClass() == String[].class) {
                if (b.getClass() == String[].class) {
                    return Arrays.equals(a, b);
                }
                for (int i = 0; i < a.length; i++) {
                    String a1 = (String) a[i];
                    Object b1 = b[i];
                    if (b1 == null) continue;
                    if (!a1.equals(b1.getClass().getName())) return false;
                }
                return true;
            }
            if (b.getClass() == String[].class) {
                for (int i = 0; i < a.length; i++) {
                    String b1 = (String) b[i];
                    Object a1 = a[i];
                    if (a1 == null) continue;
                    if (!b1.equals(a1.getClass().getName())) return false;
                }
                return true;
            }
            return Arrays.equals(a, b);
        }
    };
}
