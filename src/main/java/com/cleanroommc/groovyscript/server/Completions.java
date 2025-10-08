package com.cleanroommc.groovyscript.server;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class Completions extends ArrayList<CompletionItem> {

    private final int limit;
    private String filter;

    public Completions(int limit) {
        this.limit = limit;
        this.filter = null;
    }

    public int getLimit() {
        return limit;
    }

    public boolean reachedLimit() {
        return size() >= this.limit;
    }

    public void setFilter(String filter) {
        this.filter = filter.toLowerCase(Locale.ENGLISH);
    }

    @Override
    public boolean add(CompletionItem item) {
        if (filter == null || item.getLabel().toLowerCase(Locale.ENGLISH).contains(filter)) {
            return super.add(item);
        }
        return true;
    }

    public <V> void addAll(Iterable<V> values, Function<V, @Nullable CompletionItem> toCompletionItem) {
        for (V v : values) {
            if (reachedLimit()) break;
            CompletionItem item = toCompletionItem.apply(v);
            if (item != null) {
                add(item);
            }
        }
    }

    public <V> void addAll(V[] values, Function<V, @Nullable CompletionItem> toCompletionItem) {
        for (V v : values) {
            if (reachedLimit()) break;
            CompletionItem item = toCompletionItem.apply(v);
            if (item != null) {
                add(item);
            }
        }
    }

    public <V> void addAllNamed(Iterable<V> values, Function<V, String> toString) {
        addAll(values, v -> {
            String s = toString.apply(v);
            if (s != null) {
                var item = new CompletionItem(toString.apply(v));
                item.setKind(CompletionItemKind.Constant);
                return item;
            }
            return null;
        });
    }

    public void addAllNamed(Iterable<ResourceLocation> values) {
        addAllNamed(values, ResourceLocation::toString);
    }

    public void addAllOfRegistry(IForgeRegistry<?> registry) {
        addAllNamed(registry.getKeys());
    }

    public Either<List<CompletionItem>, CompletionList> getResult(boolean incomplete) {
        return incomplete || reachedLimit() ? Either.forRight(new CompletionList(true, this)) : Either.forLeft(this);
    }
}
