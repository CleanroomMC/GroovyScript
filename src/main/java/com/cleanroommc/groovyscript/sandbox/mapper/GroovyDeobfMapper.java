package com.cleanroommc.groovyscript.sandbox.mapper;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Parameter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroovyDeobfMapper {

    private static final Map<String, Map<String, String>> DEOBF_METHODS = new Object2ObjectOpenHashMap<>();
    private static final Map<String, Map<String, String>> DEOBF_FIELDS = new Object2ObjectOpenHashMap<>();
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

    @ApiStatus.Internal
    public static void init() {
        GroovyScript.LOGGER.info("Generating obfuscation map...");
        DEOBF_METHODS.clear();
        DEOBF_FIELDS.clear();
        OBF_FIELD_NAMES.clear();
        OBF_METHOD_NAMES.clear();
        try {
            String splitter = " ";
            String method = "MD:";
            String field = "FD:";

            InputStream stream = GroovyDeobfMapper.class.getResourceAsStream("/assets/groovyscript/mappings.srg");
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
                            DEOBF_FIELDS.computeIfAbsent(className, key -> new Object2ObjectOpenHashMap<>()).put(fieldObf, fieldDeobf);
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
                            DEOBF_METHODS.computeIfAbsent(className, key -> new Object2ObjectOpenHashMap<>()).put(methodObf, methodDeobf);
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
            GroovyScript.LOGGER.info("Read {} field and {} method mapping names", DEOBF_FIELDS.size(), DEOBF_METHODS.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static String getDeobfMethod(Class<?> clazz, String obfMethod) {
        return DEOBF_METHODS.getOrDefault(clazz.getName(), Object2ObjectMaps.emptyMap()).get(obfMethod);
    }

    @Nullable
    public static String getDeobfField(Class<?> clazz, String obfField) {
        return DEOBF_FIELDS.getOrDefault(clazz.getName(), Object2ObjectMaps.emptyMap()).get(obfField);
    }

    public static Map<String, String> getDeobfMethods(Class<?> clazz) {
        return DEOBF_METHODS.get(clazz.getName());
    }

    public static Map<String, String> getDeobfFields(Class<?> clazz) {
        return DEOBF_FIELDS.get(clazz.getName());
    }

    public static String getObfuscatedMethodName(ClassNode receiver, String method, Parameter[] args) {
        ClassNode objClass = ClassHelper.OBJECT_TYPE;
        Map<String, MethodInfo> obfNames;
        String obfName = null;

        do {
            obfNames = OBF_METHOD_NAMES.get(receiver.getName());
            if (obfNames != null) {
                MethodInfo methodInfo = obfNames.get(method);
                if (methodInfo != null) {
                    obfName = methodInfo.findMethod(args);
                }
            }
            receiver = receiver.getSuperClass();
        } while (obfName == null && receiver != null && receiver != objClass);

        return obfName;
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
        //private Object2ObjectOpenCustomHashMap<Object[], String> obfNames;
        private List<Pair<String[], String>> obfNames;
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
                //obfNames = new Object2ObjectOpenCustomHashMap<>(PARAM_HASH_STRATEGY);
                //obfNames.put(makeClassArray(defArgs), defObfName);
                obfNames = new ArrayList<>();
                obfNames.add(Pair.of(makeClassArray(defArgs), defObfName));
            }
            obfNames.add(Pair.of(makeClassArray(args), obfName));
        }

        public String findMethod(Parameter[] args) {
            if (this.obfNames == null) {
                return this.defObfName;
            }
            List<String> results = this.obfNames.stream()
                    .filter(pair -> pair.getKey().length == args.length)
                    .filter(pair -> {
                        for (int i = 0; i < args.length; i++) {
                            String origParam = pair.getKey()[i];
                            if (!matches(origParam, args[i])) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .map(Pair::getValue)
                    .collect(Collectors.toList());
            if (results.isEmpty()) return null;
            if (results.size() == 1) return results.get(0);
            GroovyLog.get().errorMC("Multiple methods match the name {} and params {}", this.deobfName, Arrays.toString(args));
            return results.get(0);
        }

        public static boolean matches(String original, Parameter param) {
            if (original.equals(Object.class.getName())) {
                return true;
            }
            ClassNode possibleMatch = param.getOriginType();

            while (possibleMatch != null) {
                if (original.equals(possibleMatch.getName())) {
                    return true;
                }
                possibleMatch = possibleMatch.getSuperClass();
            }
            return false;
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

}
