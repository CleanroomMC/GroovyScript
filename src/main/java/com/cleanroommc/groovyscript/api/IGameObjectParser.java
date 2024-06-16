package com.cleanroommc.groovyscript.api;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @deprecated use {@link IObjectParser}
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
@FunctionalInterface
public interface IGameObjectParser<T> extends IObjectParser<T> {

    static <T extends IForgeRegistryEntry<T>> IObjectParser<T> wrapForgeRegistry(IForgeRegistry<T> forgeRegistry) {
        return IObjectParser.wrapForgeRegistry(forgeRegistry);
    }

    static <T extends Enum<T>> IObjectParser<T> wrapEnum(Class<T> enumClass, boolean caseSensitive) {
        return IObjectParser.wrapEnum(enumClass, caseSensitive);
    }

    static <T> IObjectParser<T> wrapStringGetter(Function<String, T> getter) {
        return IObjectParser.wrapStringGetter(getter);
    }

    static <T> IObjectParser<T> wrapStringGetter(Function<String, T> getter, boolean isUpperCase) {
        return IObjectParser.wrapStringGetter(getter, isUpperCase);
    }

    static <T, V> IObjectParser<T> wrapStringGetter(Function<String, V> getter, Function<V, @NotNull T> trueTypeFunction) {
        return IObjectParser.wrapStringGetter(getter, trueTypeFunction);
    }

    static <T, V> IObjectParser<T> wrapStringGetter(Function<String, V> getter, Function<V, @NotNull T> trueTypeFunction, boolean isUpperCase) {
        return IObjectParser.wrapStringGetter(getter, trueTypeFunction, isUpperCase);
    }
}
