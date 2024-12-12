package com.cleanroommc.groovyscript.helper;

import com.google.common.base.CaseFormat;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

public class Alias extends ArrayList<String> {

    public Alias and(Collection<String> c) {
        addAll(c);
        return this;
    }

    public Alias and(String... a) {
        Collections.addAll(this, a);
        return this;
    }

    public Alias and(String s) {
        add(s);
        return this;
    }

    public Alias andGenerate(String name, CaseFormat caseFormat) {
        generateAliases(this, name, caseFormat);
        return this;
    }

    public Alias andGenerate(String name) {
        return andGenerate(name, CaseFormat.UPPER_CAMEL);
    }

    public Alias andGenerateOfClass(Object o) {
        Class<?> clazz = o instanceof Class<?>c ? c : o.getClass();
        return andGenerate(clazz.getSimpleName());
    }

    public static Alias generateOfClass(Object o) {
        Class<?> clazz = o instanceof Class<?>c ? c : o.getClass();
        return generateOf(clazz.getSimpleName(), CaseFormat.UPPER_CAMEL);
    }

    public static Alias generateOfClassAnd(Object o, String... names) {
        Class<?> clazz = o instanceof Class<?>c ? c : o.getClass();
        Alias alias = new Alias();
        generateAliases(alias, clazz.getSimpleName(), CaseFormat.UPPER_CAMEL);
        for (String name : names) {
            generateAliases(alias, name, CaseFormat.UPPER_CAMEL);
        }
        return alias;
    }

    public static Alias generateOf(String name) {
        return generateOf(name, CaseFormat.UPPER_CAMEL);
    }

    public static Alias generateOf(String name, CaseFormat caseFormat) {
        return generateAliases(new Alias(), name, caseFormat);
    }

    public static Alias generateOf(String... names) {
        return generateOf(CaseFormat.UPPER_CAMEL, names);
    }

    public static Alias generateOf(CaseFormat caseFormat, String... names) {
        Alias alias = new Alias();
        for (String name : names) {
            generateAliases(alias, name, caseFormat);
        }
        return alias;
    }

    public static Alias of(String... aliases) {
        return new Alias().and(aliases);
    }

    public static <T extends Collection<String>> T generateAliases(T aliases, String name, CaseFormat caseFormat) {
        String baseName = caseFormat == CaseFormat.UPPER_CAMEL ? name : caseFormat.to(CaseFormat.UPPER_CAMEL, name);
        if (baseName.split("[A-Z]").length > 2) {
            aliases.add(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, baseName));
            aliases.add(baseName.toLowerCase(Locale.ROOT));
            aliases.add(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, baseName));
            aliases.add(baseName);
        } else {
            aliases.add(baseName.toLowerCase(Locale.ROOT));
            aliases.add(baseName);
        }
        return aliases;
    }

    public static <T> void putAll(String name, T object, Map<String, T> map) {
        putAll(name, CaseFormat.UPPER_CAMEL, object, map);
    }

    public static <T> void putAll(String name, CaseFormat caseFormat, T object, Map<String, T> map) {
        for (String alias : generateAliases(new ArrayList<>(), name, caseFormat)) {
            map.put(alias, object);
        }
    }

    private static final Pattern lowerCamel = Pattern.compile("[a-z]+[a-zA-Z]*");
    private static final Pattern upperCamel = Pattern.compile("[A-Z][a-zA-Z]*");
    private static final Pattern lowerUnderscore = Pattern.compile("[a-z]+(_[a-z]+)*");
    private static final Pattern upperUnderscore = Pattern.compile("[A-Z]+(_[A-Z]+)*");

    public static @NotNull CaseFormat detectCaseFormat(String s) {
        if (s == null || s.isEmpty()) throw new IllegalArgumentException("String must not be empty");
        if (lowerCamel.matcher(s).matches()) return CaseFormat.LOWER_CAMEL;
        if (upperCamel.matcher(s).matches()) return CaseFormat.UPPER_CAMEL;
        if (lowerUnderscore.matcher(s).matches()) return CaseFormat.LOWER_UNDERSCORE;
        if (upperUnderscore.matcher(s).matches()) return CaseFormat.UPPER_UNDERSCORE;
        throw new IllegalArgumentException("String has invalid format!");
    }

    public static String autoConvertTo(String s, CaseFormat format) {
        CaseFormat fromFormat = detectCaseFormat(s);
        if (fromFormat == format) return s;
        return fromFormat.to(format, s);
    }
}
