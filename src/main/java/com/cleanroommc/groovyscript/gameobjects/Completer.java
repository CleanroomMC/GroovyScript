package com.cleanroommc.groovyscript.gameobjects;

import org.eclipse.lsp4j.CompletionItem;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;
import java.util.function.Supplier;

@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
@FunctionalInterface
public interface Completer extends com.cleanroommc.groovyscript.mapper.Completer {

    static <V> Completer ofNamed(Supplier<Iterable<V>> values, Function<V, String> toString, int preferredParamIndex) {
        return (Completer) com.cleanroommc.groovyscript.mapper.Completer.ofNamed(values, toString, preferredParamIndex);
    }

    static <V> Completer ofValues(Supplier<Iterable<V>> values, Function<V, CompletionItem> toCompletionItem, int preferredParamIndex) {
        return (Completer) com.cleanroommc.groovyscript.mapper.Completer.ofValues(values, toCompletionItem, preferredParamIndex);
    }
}
