package com.cleanroommc.groovyscript.gameobjects;

import com.cleanroommc.groovyscript.server.Completions;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;

import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface Completer {

    void complete(int paramIndex, Completions items);

    static <V> Completer ofNamed(Supplier<Iterable<V>> values, Function<V, String> toString, int preferredParamIndex) {
        return ofValues(values, v -> {
            String s = toString.apply(v);
            if (s != null) {
                var item = new CompletionItem(toString.apply(v));
                item.setKind(CompletionItemKind.Constant);
                return item;
            }
            return null;
        }, preferredParamIndex);
    }

    static <V> Completer ofValues(Supplier<Iterable<V>> values, Function<V, CompletionItem> toCompletionItem, int preferredParamIndex) {
        return (paramIndex, items) -> {
            if (preferredParamIndex < 0 || preferredParamIndex == paramIndex) {
                items.addAll(values.get(), toCompletionItem);
            }
        };
    }

    default Completer and(Completer other) {
        return (paramIndex, items) -> {
            complete(paramIndex, items);
            other.complete(paramIndex, items);
        };
    }
}
