package com.cleanroommc.groovyscript.compat.mods.mekanism.recipe;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.api.GroovyLog;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.machines.MachineRecipe;
import org.jetbrains.annotations.ApiStatus;

public abstract class VirtualizedMekanismRegistry<R extends MachineRecipe<?, ?, R>> extends VirtualizedRegistry<R> {

    protected final RecipeHandler.Recipe<?, ?, R> recipeRegistry;

    public VirtualizedMekanismRegistry(RecipeHandler.Recipe<?, ?, R> recipeRegistry, String... aliases) {
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

    public boolean remove(R recipe) {
        if(recipeRegistry.get().remove(recipe) != null) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public SimpleObjectStream<R> streamRecipes() {
        return new SimpleObjectStream<>(recipeRegistry.get().values())
                .setRemover(this::remove);
    }

    @GroovyBlacklist
    public void removeError(String reason, Object... data) {
        GroovyLog.msg("Error removing Mekanism " + getAliases().get(0) + " recipe")
                .add(reason, data)
                .error()
                .post();
    }
}
