package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.mapper.ObjectMappers;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * @deprecated use {@link IObjectParser}
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
@FunctionalInterface
public interface IGameObjectParser<T> extends IObjectParser<T> {

    static <T extends IForgeRegistryEntry<T>> IGameObjectParser<T> wrapForgeRegistry(IForgeRegistry<T> forgeRegistry) {
        return (s, args) -> {
            Result<ResourceLocation> rl = ObjectMappers.parseResourceLocation(s, args);
            if (rl.hasError()) return Result.error(rl.getError());
            T value = forgeRegistry.getValue(rl.getValue());
            return value == null ? Result.error() : Result.some(value);
        };
    }

    static <T extends Enum<T>> IGameObjectParser<T> wrapEnum(Class<T> enumClass, boolean caseSensitive) {
        Map<String, T> map = new Object2ObjectOpenHashMap<>();
        for (T t : enumClass.getEnumConstants()) {
            map.put(caseSensitive ? t.name() : t.name().toUpperCase(Locale.ROOT), t);
        }
        return (s, args) -> {
            T t = map.get(caseSensitive ? s : s.toUpperCase(Locale.ROOT));
            return t == null ? Result.error() : Result.some(t);
        };
    }

    static <T> IGameObjectParser<T> wrapStringGetter(Function<String, T> getter) {
        return wrapStringGetter(getter, false);
    }

    static <T> IGameObjectParser<T> wrapStringGetter(Function<String, T> getter, boolean isUpperCase) {
        return (s, args) -> {
            if (args.length > 0) {
                return Result.error("extra arguments are not allowed");
            }
            T t = getter.apply(isUpperCase ? s.toUpperCase(Locale.ROOT) : s);
            return t == null ? Result.error() : Result.some(t);
        };
    }

    static <T, V> IGameObjectParser<T> wrapStringGetter(Function<String, V> getter, Function<V, @NotNull T> trueTypeFunction) {
        return wrapStringGetter(getter, trueTypeFunction, false);
    }

    static <T, V> IGameObjectParser<T> wrapStringGetter(Function<String, V> getter, Function<V, @NotNull T> trueTypeFunction, boolean isUpperCase) {
        return (s, args) -> {
            if (args.length > 0) {
                return Result.error("extra arguments are not allowed");
            }
            V v = getter.apply(isUpperCase ? s.toUpperCase(Locale.ROOT) : s);
            return v == null ? Result.error() : Result.some(trueTypeFunction.apply(v));
        };
    }
}
