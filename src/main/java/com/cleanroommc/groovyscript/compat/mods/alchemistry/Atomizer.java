package com.cleanroommc.groovyscript.compat.mods.alchemistry;

import al132.alchemistry.recipes.AtomizerRecipe;
import al132.alchemistry.recipes.LiquifierRecipe;
import al132.alchemistry.recipes.ModRecipes;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Atomizer extends VirtualizedRegistry<AtomizerRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ModRecipes.INSTANCE.getAtomizerRecipes().removeIf(r -> r == recipe));
        ModRecipes.INSTANCE.getAtomizerRecipes().addAll(restoreFromBackup());
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('water') * 125).output(item('minecraft:clay'))"),
            @Example(".fluidInput(fluid('lava') * 500).output(item('minecraft:gold_ingot')).reversible()")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public AtomizerRecipe add(FluidStack input, ItemStack output) {
        return new RecipeBuilder().fluidInput(input).output(output).register();
    }

    public AtomizerRecipe add(AtomizerRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ModRecipes.INSTANCE.getAtomizerRecipes().add(recipe);
        }
        return recipe;
    }

    public boolean remove(AtomizerRecipe recipe) {
        if (ModRecipes.INSTANCE.getAtomizerRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example(value = "item('alchemistry:compound:7')", commented = true))
    public boolean removeByOutput(IIngredient output) {
        return ModRecipes.INSTANCE.getAtomizerRecipes().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('water')"))
    public boolean removeByInput(FluidStack input) {
        return ModRecipes.INSTANCE.getAtomizerRecipes().removeIf(r -> {
            if (r.getInput().isFluidEqual(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<AtomizerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ModRecipes.INSTANCE.getAtomizerRecipes()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ModRecipes.INSTANCE.getAtomizerRecipes().forEach(this::addBackup);
        ModRecipes.INSTANCE.getAtomizerRecipes().clear();
    }

    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AtomizerRecipe> {

        @Property
        private boolean reversible;

        @RecipeBuilderMethodDescription
        public RecipeBuilder reversible(boolean reversible) {
            this.reversible = reversible;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder reversible() {
            this.reversible = !reversible;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Alchemistry Atomizer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
        }

        @Nullable
        @Override
        @RecipeBuilderRegistrationMethod
        public AtomizerRecipe register() {
            if (!validate()) return null;

            AtomizerRecipe recipe = new AtomizerRecipe(false, fluidInput.get(0), output.get(0));
            if (reversible) ModSupport.ALCHEMISTRY.get().liquifier.add(new LiquifierRecipe(output.get(0), fluidInput.get(0)));
            ModSupport.ALCHEMISTRY.get().atomizer.add(recipe);
            return recipe;
        }
    }
}
