package com.cleanroommc.groovyscript.server;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Completions extends ArrayList<CompletionItem> {

    private final int limit;

    public Completions(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public boolean reachedLimit() {
        return size() >= this.limit;
    }

    public <V> void addAll(Iterable<V> values, Function<V, CompletionItem> toCompletionItem) {
        for (V v : values) {
            if (reachedLimit()) break;
            CompletionItem item = toCompletionItem.apply(v);
            if (item != null) {
                add(item);
            }
        }
    }

    public <V> void addAll(V[] values, Function<V, CompletionItem> toCompletionItem) {
        for (V v : values) {
            if (reachedLimit()) break;
            CompletionItem item = toCompletionItem.apply(v);
            if (item != null) {
                add(item);
            }
        }
    }

    public Either<List<CompletionItem>, CompletionList> getResult(boolean incomplete) {
        return incomplete || reachedLimit() ? Either.forRight(new CompletionList(true, this)) : Either.forLeft(this);
    }
}
