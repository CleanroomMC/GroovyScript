package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.api.Result;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class EnumHelper {

    private static final Object[] EMPTY = new Object[0];

    private static final Map<Class<? extends Enum<?>>, IObjectParser<?>> cs = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<? extends Enum<?>>, IObjectParser<?>> ncs = new Object2ObjectOpenHashMap<>();

    public static @NotNull <T extends Enum<T>> Result<T> valueOf(Class<T> clazz, String s, boolean caseSensitive) {
        var map = caseSensitive ? cs : ncs;
        IObjectParser<?> goh = map.get(clazz);
        if (goh == null) {
            goh = IObjectParser.wrapEnum(clazz, caseSensitive);
            map.put(clazz, goh);
        }
        return (Result<T>) goh.parse(s, EMPTY);
    }

    public static @Nullable <T extends Enum<T>> T valueOfNullable(Class<T> clazz, String s, boolean caseSensitive) {
        Result<T> res = valueOf(clazz, s, caseSensitive);
        return res.hasError() ? null : res.getValue();
    }
}
