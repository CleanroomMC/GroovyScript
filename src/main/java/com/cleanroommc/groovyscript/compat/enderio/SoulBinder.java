package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.compat.enderio.recipe.EnderIORecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.IReloadableRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.soul.BasicSoulBinderRecipe;
import crazypants.enderio.base.recipe.soul.ISoulBinderRecipe;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SoulBinder {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
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

    public static class RecipeBuilder extends EnderIORecipeBuilder<BasicSoulBinderRecipe> {

        private String name;
        private int xp;
        private final NNList<ResourceLocation> entities = new NNList<>();
        private final List<String> entityErrors = new ArrayList<>();

        public RecipeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RecipeBuilder entitySoul(String entity) {
            ResourceLocation rl = new ResourceLocation(entity);
            if (EntityList.getClass(rl) == null) {
                entityErrors.add(entity);
            } else {
                entities.add(rl);
            }
            return this;
        }

        public RecipeBuilder entitySoul(String... entities) {
            for (String entity : entities) {
                entitySoul(entity);
            }
            return this;
        }

        public RecipeBuilder entitySoul(Collection<String> entities) {
            for (String entity : entities) {
                entitySoul(entity);
            }
            return this;
        }

        public RecipeBuilder xp(int xp) {
            this.xp = xp;
            return this;
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = new GroovyLog.Msg("Error adding EnderIO Soul Binder recipe").error();
            input.trim();
            output.trim();
            msg.add(input.size() != 1, () -> "Must have exactly 1 input, but found " + input.size());
            msg.add(output.size() != 1, () -> "Must have exactly 1 output, but found " + output.size());
            if (!entityErrors.isEmpty()) {
                for (String error : entityErrors) {
                    msg.add("could not find entity with name %s", error);
                }
            }

            if (energy <= 0) energy = 5000;
            if (xp <= 0) xp = 2;
            if (name == null || name.isEmpty()) name = RecipeName.generate();

            if (msg.hasSubMessages()) {
                GroovyLog.LOG.log(msg);
                return false;
            }
            return true;
        }

        @Override
        public @Nullable BasicSoulBinderRecipe register() {
            if (!validate()) return null;
            BasicSoulBinderRecipe recipe = new BasicSoulBinderRecipe(
                    input.get(0).getMatchingStacks()[0],
                    output.get(0),
                    energy,
                    xp,
                    name,
                    level,
                    entities,
                    new BasicSoulBinderRecipe.OutputFilter() {
                    });
            MachineRecipeRegistry.instance.registerRecipe(recipe);
            return recipe;
        }
    }
}
