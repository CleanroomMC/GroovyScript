package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.gameobjects.GameObjectHandlers;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * A bracket handler returns a object based on its input arguments.
 * A bracket handler can be called from groovy lik this:
 * <p>
 * {@code bracket_handler_name(args)}
 * </p>
 * In the first way there is always only one argument which is a String.
 * In the second method the argument size is at least, but not limited to one.
 * The first argument is always a string. The other can be anything.
 */
@FunctionalInterface
public interface IGameObjectParser<T> {

    /**
     * Parses a object based on input arguments
     *
     * @param args arguments. length >= 1 && args[0] instanceof String
     * @return a parsed Object
     */
    @NotNull
    Result<T> parse(String mainArg, Object[] args);

    static <T extends IForgeRegistryEntry<T>> IGameObjectParser<T> wrapForgeRegistry(IForgeRegistry<T> forgeRegistry) {
        return (s, args) -> {
            Result<ResourceLocation> rl = GameObjectHandlers.parseResourceLocation(s, args);
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
