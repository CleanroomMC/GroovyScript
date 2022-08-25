package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import groovy.lang.Closure;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.*;
import java.util.function.Predicate;

public class RecipeStream<T> {

    private final List<T> recipes;
    private Predicate<T> remover;

    public RecipeStream(Collection<T> recipes) {
        this.recipes = new ArrayList<>(recipes);
    }

    private RecipeStream(List<T> recipes, boolean copy) {
        this.recipes = copy ? new ArrayList<>(recipes) : recipes;
    }

    @GroovyBlacklist
    public RecipeStream<T> setRemover(Predicate<T> remover) {
        this.remover = remover;
        return this;
    }

    public RecipeStream<T> filter(Closure<Boolean> closure) {
        this.recipes.removeIf(recipe -> !ClosureHelper.call(true, closure, recipe));
        return this;
    }

    public RecipeStream<T> trim() {
        this.recipes.removeIf(Objects::isNull);
        return this;
    }

    public RecipeStream<T> forEach(Closure<Object> closure) {
        this.recipes.forEach(recipe -> ClosureHelper.call(closure, recipe));
        return this;
    }

    public <V> RecipeStream<V> map(Closure<V> closure) {
        List<V> newList = new ArrayList<>();
        for (T recipe : this.recipes) {
            newList.add(ClosureHelper.call(closure, recipe));
        }
        return new RecipeStream<>(newList, false);
    }

    public T findFirst() {
        for (T recipe : this.recipes) {
            if (recipe != null) {
                return recipe;
            }
        }
        throw new NoSuchElementException();
    }

    public List<T> getList() {
        return new ArrayList<>(this.recipes);
    }

    public Set<T> getSet() {
        return new ObjectOpenHashSet<>(this.recipes);
    }

    public RecipeStream<T> removeAll() {
        Objects.requireNonNull(this.remover);
        this.recipes.removeIf(this.remover);
        return this;
    }

    public RecipeStream<T> removeFirst() {
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
}
