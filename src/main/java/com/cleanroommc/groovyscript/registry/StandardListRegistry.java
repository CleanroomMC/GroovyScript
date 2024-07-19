package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * Boilerplate code for a registry where the registry is a mutable Collection that stores recipes of type {@link R}.
 * Will automatically handle {@link #add(R)}, {@link #remove(R)}, {@link #removeAll()}, {@link #streamRecipes()}, and {@link #onReload()}.
 */
public abstract class StandardListRegistry<R> extends VirtualizedRegistry<R> {

    public StandardListRegistry() {
        this(null);
    }

    public StandardListRegistry(@Nullable Collection<String> aliases) {
        super(aliases);
    }

    public abstract Collection<R> getRegistry();

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        getRegistry().removeAll(removeScripted());
        getRegistry().addAll(restoreFromBackup());
    }

    public boolean add(R recipe) {
        return recipe != null && getRegistry().add(recipe) && addScripted(recipe);
    }

    public boolean remove(R recipe) {
        return recipe != null && getRegistry().removeIf(r -> r == recipe) && addBackup(recipe);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        getRegistry().forEach(this::addBackup);
        getRegistry().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<R> streamRecipes() {
        return new SimpleObjectStream<>(getRegistry()).setRemover(this::remove);
    }
}
