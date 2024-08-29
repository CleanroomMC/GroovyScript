package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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

    public abstract Collection<R> getRecipes();

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        getRecipes().removeAll(removeScripted());
        getRecipes().addAll(restoreFromBackup());
    }

    public boolean add(R recipe) {
        return recipe != null && getRecipes().add(recipe) && addScripted(recipe);
    }

    public boolean remove(R recipe) {
        return recipe != null && getRecipes().removeIf(r -> r == recipe) && addBackup(recipe);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        getRecipes().forEach(this::addBackup);
        getRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<R> streamRecipes() {
        return new SimpleObjectStream<>(getRecipes()).setRemover(this::remove);
    }

}
