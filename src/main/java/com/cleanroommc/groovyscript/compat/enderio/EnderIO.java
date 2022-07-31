package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.cleanroommc.groovyscript.helper.recipe.IIngredient;
import com.cleanroommc.groovyscript.registry.IReloadableRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.base.recipe.soul.BasicSoulBinderRecipe;
import crazypants.enderio.base.recipe.soul.ISoulBinderRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class EnderIO {

    public final AlloySmelter alloySmelter = new AlloySmelter();
    public final SoulBinder soulBinder = new SoulBinder();

    public static class AlloySmelter {
        public void add(ItemStack output, List<IIngredient> input, int energyCost) {
            AlloyRecipeManager.getInstance().addRecipe(true, RecipeUtils.toEIOInputsNN(input),
                    output.copy(), energyCost <= 0 ? 5000 : energyCost, 0, RecipeLevel.IGNORE);
        }

        public void remove(ItemStack output) {
            ((IEnderIORecipes) AlloyRecipeManager.getInstance()).removeRecipes(output);
        }
    }

    public static class SoulBinder {
        public void add(String recipeName, ItemStack output, IIngredient input, List<String> entities, int energy, int xp) {
            MachineRecipeRegistry.instance.registerRecipe(new BasicSoulBinderRecipe(
                    input.getMatchingStacks()[0],
                    output,
                    energy,
                    xp,
                    recipeName,
                    RecipeLevel.IGNORE,
                    ArrayUtils.mapToList(entities, ResourceLocation::new, new NNList<>()),
                    new BasicSoulBinderRecipe.OutputFilter() {
                    }));
        }

        public void remove(ItemStack output) {
            List<IMachineRecipe> recipes = new ArrayList<>();
            for (IMachineRecipe recipe : MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.SOULBINDER).values()) {
                if (OreDictionary.itemMatches(output, ((ISoulBinderRecipe) recipe).getOutputStack(), false)) {
                    recipes.add(recipe);
                }
            }
            if (recipes.isEmpty()) {
                GroovyLog.LOG.error("No Soul Binder recipe found for " + output.getDisplayName());
            } else {
                IReloadableRegistry<IMachineRecipe> registry = (IReloadableRegistry<IMachineRecipe>) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.SOULBINDER);
                recipes.forEach(registry::removeEntry);
            }
        }
    }
}
