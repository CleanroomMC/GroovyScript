package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.api.IGameObjectParser;
import com.cleanroommc.groovyscript.api.Result;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class EnumHelper {

    private static final Object[] EMPTY = new Object[0];

    private static final Map<Class<? extends Enum<?>>, IGameObjectParser<?>> cs = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<? extends Enum<?>>, IGameObjectParser<?>> ncs = new Object2ObjectOpenHashMap<>();

    @NotNull
    public static <T extends Enum<T>> Result<T> valueOf(Class<T> clazz, String s, boolean caseSensitive) {
        var map = caseSensitive ? cs : ncs;
        IGameObjectParser<?> goh = map.get(clazz);
        if (goh == null) {
            goh = IGameObjectParser.wrapEnum(clazz, caseSensitive);
            map.put(clazz, goh);
        }
        return (Result<T>) goh.parse(s, EMPTY);
    }

    @Nullable
    public static <T extends Enum<T>> T valueOfNullable(Class<T> clazz, String s, boolean caseSensitive) {
        Result<T> res = valueOf(clazz, s, caseSensitive);
        return res.hasError() ? null : res.getValue();
    }
}
