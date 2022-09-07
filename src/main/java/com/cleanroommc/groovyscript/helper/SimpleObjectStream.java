package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import groovy.lang.Closure;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * A object that acts like a {@link java.util.stream.Stream} but is just a {@link List} wrapper.
 * Mainly used for recipes, but can be used for anything.
 * @param <T> type of the stream elements
 */
public class SimpleObjectStream<T> {

    private final List<T> recipes;
    private Predicate<T> remover;

    public SimpleObjectStream(Collection<T> recipes) {
        this.recipes = new ArrayList<>(recipes);
    }

    public SimpleObjectStream(List<T> recipes, boolean copy) {
        this.recipes = copy ? new ArrayList<>(recipes) : recipes;
    }

    @GroovyBlacklist
    public SimpleObjectStream<T> setRemover(Predicate<T> remover) {
        this.remover = remover;
        return this;
    }

    public SimpleObjectStream<T> filter(Closure<Boolean> closure) {
        this.recipes.removeIf(recipe -> !ClosureHelper.call(true, closure, recipe));
        return this;
    }

    public SimpleObjectStream<T> trim() {
        this.recipes.removeIf(Objects::isNull);
        return this;
    }

    public SimpleObjectStream<T> forEach(Closure<Object> closure) {
        this.recipes.forEach(recipe -> ClosureHelper.call(closure, recipe));
        return this;
    }

    public T findFirst() {
        T recipe = getFirst();
        if (recipe == null) {
            throw new NoSuchElementException();
        }
        return recipe;
    }

    @Nullable
    public T getFirst() {
        for (T recipe : this.recipes) {
            if (recipe != null) {
                return recipe;
            }
        }
        return null;
    }

    public List<T> getList() {
        return new ArrayList<>(this.recipes);
    }

    public Set<T> getSet() {
        return new ObjectOpenHashSet<>(this.recipes);
    }

    public SimpleObjectStream<T> removeAll() {
        Objects.requireNonNull(this.remover);
        this.recipes.removeIf(this.remover);
        return this;
    }

    public SimpleObjectStream<T> removeFirst() {
        Objects.requireNonNull(this.remover);
        for (int i = 0, n = this.recipes.size(); i < n; i++) {
            T recipe = this.recipes.get(i);
            if (recipe != null && this.remover.test(recipe)) {
                this.recipes.remove(i);
                return this;
            }
        }
        GroovyLog.LOG.error("No recipe found to remove!");
        return this;
    }

    public int size() {
        return this.recipes.size();
    }

    public boolean isEmpty() {
        return this.recipes.isEmpty();
    }
}
