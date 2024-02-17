package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.BarrelRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CampfireRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class Barrel extends ForgeRegistryWrapper<BarrelRecipe> {

    public Barrel() {
        super(ModuleTechBasic.Registries.BARREL_RECIPE, Alias.generateOfClass(Barrel.class));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }


    public boolean remove(BarrelRecipe recipe) {
        if (recipe == null) return false;
        remove(recipe.getRegistryName());
        return true;
    }

    public void removeByOutput(FluidStack output) {
        if (GroovyLog.msg("Error removing barrel recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (BarrelRecipe recipe : getRegistry()) {
            if (recipe.getOutput().isFluidEqual(output)) {
                remove(recipe);
            }
        }
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<BarrelRecipe> {

        private int duration;

        public RecipeBuilder duration(int time) {
            this.duration = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Barrel Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 4, 4, 0, 0);
            validateFluids(msg, 1, 1, 1, 1);
            msg.add(duration < 0, "duration must be a non negative integer, yet it was {}", duration);
            msg.add(name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.BARREL_RECIPE.getValue(name) != null, "tried to register {}, but it already exists.", name);

        }

        @Override
        public @Nullable BarrelRecipe register() {
            if (!validate()) return null;

            // Because you need Ingredient[] to register a recipe
            Ingredient[] inputIngredient = input.stream().map(IIngredient::toMcIngredient).toArray(Ingredient[]::new);

            BarrelRecipe recipe = new BarrelRecipe(fluidOutput.get(0), inputIngredient, fluidInput.get(0), duration).setRegistryName(name);
            PyroTech.barrel.add(recipe);

            return recipe;
        }
    }
}
