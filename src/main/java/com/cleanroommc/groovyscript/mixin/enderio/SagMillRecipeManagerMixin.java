package com.cleanroommc.groovyscript.mixin.enderio;

import com.cleanroommc.groovyscript.registry.IReloadableRegistry;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = SagMillRecipeManager.class, remap = false)
public abstract class SagMillRecipeManagerMixin implements IReloadableRegistry<Recipe> {

    @Shadow
    @Final
    @Nonnull
    private NNList<Recipe> recipes;

    @Shadow
    public abstract IRecipe getRecipeForInput(@NotNull RecipeLevel machineLevel, @NotNull ItemStack input);

    @Unique
    @Final
    private List<Recipe> backupRecipes = new ArrayList<>();

    @Override
    public void onReload() {
        recipes.clear();
        recipes.addAll(backupRecipes);
    }

    @Override
    public void removeEntry(Recipe recipe) {
        recipes.removeIf(r -> r == recipe);
    }

    @Inject(method = "addRecipe", at = @At("HEAD"), cancellable = true)
    public void addRecipe(Recipe recipe, CallbackInfo ci) {
        if (!recipe.isValid()) {
            Log.debug("Could not add invalid recipe: " + recipe);
        } else {
            IRecipe rec = getRecipeForInput(RecipeLevel.IGNORE, SagMillRecipeManager.getInput(recipe));
            if (rec != null) {
                Log.warn("Not adding supplied recipe as a recipe already exists for the input: " + SagMillRecipeManager.getInput(recipe));
            } else {
                if (!ReloadableRegistryManager.isShouldRegisterAsReloadable()) {
                    backupRecipes.add(recipe);
                }
                this.recipes.add(recipe);
            }
        }
        ci.cancel();
    }
}
