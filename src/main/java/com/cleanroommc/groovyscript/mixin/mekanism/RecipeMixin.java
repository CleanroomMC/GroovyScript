package com.cleanroommc.groovyscript.mixin.mekanism;

import com.cleanroommc.groovyscript.registry.IReloadableRegistry;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.MachineInput;
import mekanism.common.recipe.machines.MachineRecipe;
import mekanism.common.recipe.outputs.MachineOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(value = RecipeHandler.Recipe.class, remap = false)
public class RecipeMixin<INPUT extends MachineInput<INPUT>, OUTPUT extends MachineOutput<OUTPUT>, RECIPE extends MachineRecipe<INPUT, OUTPUT, RECIPE>> implements IReloadableRegistry<INPUT> {

    @Shadow
    @Final
    private HashMap<INPUT, RECIPE> recipes;
    @Unique
    @Final
    private final HashMap<INPUT, RECIPE> backupRecipes = new HashMap<>();

    @Inject(method = "put", at = @At("HEAD"))
    public void put(RECIPE recipe, CallbackInfo ci) {
        if (!ReloadableRegistryManager.isShouldRegisterAsReloadable()) {
            backupRecipes.put(recipe.recipeInput, recipe);
        }
    }

    @Override
    public void onReload() {
        recipes.clear();
        recipes.putAll(backupRecipes);
    }

    @Override
    public void removeEntry(INPUT key) {
        recipes.remove(key);
    }
}
