package com.cleanroommc.groovyscript.compat.mods.mekanism.recipe;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.machines.MachineRecipe;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class VirtualizedMekanismRegistry<R extends MachineRecipe<?, ?, R>> extends VirtualizedRegistry<R> {

    protected final RecipeHandler.Recipe<?, ?, R> recipeRegistry;

    public VirtualizedMekanismRegistry(RecipeHandler.Recipe<?, ?, R> recipeRegistry) {
        this(recipeRegistry, null);
    }

    public VirtualizedMekanismRegistry(RecipeHandler.Recipe<?, ?, R> recipeRegistry, @Nullable Collection<String> aliases) {
        super(aliases);
        this.recipeRegistry = recipeRegistry;
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    @Override
    public void onReload() {
        removeScripted().forEach(recipeRegistry::remove);
        restoreFromBackup().forEach(recipeRegistry::put);
    }

    public void add(R recipe) {
        recipeRegistry.put(recipe);
        addScripted(recipe);
    }

    public boolean remove(R recipe) {
        if (recipeRegistry.get().remove(recipe) != null) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<R> streamRecipes() {
        return new SimpleObjectStream<>(recipeRegistry.get().values())
                .setRemover(this::remove);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        recipeRegistry.get().values().forEach(this::addBackup);
        recipeRegistry.get().clear();
    }

    @GroovyBlacklist
    public void removeError(String reason, Object... data) {
        GroovyLog.msg("Error removing Mekanism " + getAliases().get(0) + " recipe")
                .add(reason, data)
                .error()
                .post();
    }
}
