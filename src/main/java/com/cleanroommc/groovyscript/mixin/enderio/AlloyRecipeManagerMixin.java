package com.cleanroommc.groovyscript.mixin.enderio;

import com.cleanroommc.groovyscript.compat.enderio.IEnderIORecipes;
import com.cleanroommc.groovyscript.registry.IReloadableRegistry;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import crazypants.enderio.base.recipe.IManyToOneRecipe;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.base.recipe.lookup.TriItemLookup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
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
import java.util.Collections;
import java.util.List;

@Mixin(value = AlloyRecipeManager.class, remap = false)
public abstract class AlloyRecipeManagerMixin implements IReloadableRegistry<IManyToOneRecipe>, IEnderIORecipes {

    @Shadow
    @Nonnull
    private TriItemLookup<IManyToOneRecipe> lookup;

    @Shadow
    private static void addRecipeToLookup(@NotNull TriItemLookup<IManyToOneRecipe> lookup, @NotNull IManyToOneRecipe recipe) {
    }

    @Shadow
    protected abstract void dupeCheckRecipe(IRecipe recipe);

    @Shadow
    protected abstract void addJEIIntegration(@NotNull IManyToOneRecipe recipe);

    @Final
    @Unique
    private List<IManyToOneRecipe> backupRecipes = new ArrayList<>();

    @Final
    @Unique
    private List<IManyToOneRecipe> recipes = new ArrayList<>();

    @Override
    public void onReload() {
        recipes.clear();
        recipes.addAll(backupRecipes);
    }

    @Override
    public void removeEntry(IManyToOneRecipe iManyToOneRecipe) {
        recipes.removeIf(recipe -> recipe == iManyToOneRecipe);
    }

    @Override
    public void afterScript() {
        lookup = new TriItemLookup<>();
        for (IManyToOneRecipe recipe : recipes) {
            dupeCheckRecipe(recipe);
            addRecipeToLookup(this.lookup, recipe);
            addJEIIntegration(recipe);
        }
    }

    @Inject(method = "addRecipe(Lcrazypants/enderio/base/recipe/IManyToOneRecipe;)V", at = @At("HEAD"), cancellable = true)
    public void addRecipe(IManyToOneRecipe recipe, CallbackInfo ci) {
        if (!ReloadableRegistryManager.isShouldRegisterAsReloadable()) {
            backupRecipes.add(recipe);
        }
        recipes.add(recipe);
        ci.cancel();
    }

    @Override
    public List<IRecipe> findRecipes(Object... data) {
        if (data.length == 1 && data[0] instanceof ItemStack) {
            List<IRecipe> removals = new ArrayList<>();
            ItemStack output = (ItemStack) data[0];
            for (IManyToOneRecipe r : recipes) {
                if (OreDictionary.itemMatches(output, r.getOutput(), false)) {
                    removals.add(r);
                }
            }
            if (removals.isEmpty()) {
                GroovyLog.LOG.error("No Alloy Smelter recipe found for " + output.getDisplayName());
                return Collections.emptyList();
            }
            return removals;
        }
        GroovyLog.LOG.error("Invalid arguments for alloy recipe removal!");
        return Collections.emptyList();
    }

    @Override
    public void removeRecipes(Object... data) {
        for (IRecipe recipe : findRecipes(data)) {
            if (recipe instanceof IManyToOneRecipe) {
                removeEntry((IManyToOneRecipe) recipe);
            }
        }
    }
}
