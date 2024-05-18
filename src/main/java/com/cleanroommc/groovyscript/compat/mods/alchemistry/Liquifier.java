package com.cleanroommc.groovyscript.compat.mods.alchemistry;

import al132.alchemistry.recipes.LiquifierRecipe;
import al132.alchemistry.recipes.ModRecipes;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Liquifier extends VirtualizedRegistry<LiquifierRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ModRecipes.INSTANCE.getLiquifierRecipes().removeIf(r -> r == recipe));
        ModRecipes.INSTANCE.getLiquifierRecipes().addAll(restoreFromBackup());
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(element('carbon') * 32).fluidOutput(fluid('water') * 1000)"),
            @Example(".input(item('minecraft:magma')).fluidOutput(fluid('lava') * 750)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public LiquifierRecipe add(IIngredient input, FluidStack output) {
        return new RecipeBuilder().input(input).fluidOutput(output).register();
    }

    public LiquifierRecipe add(LiquifierRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ModRecipes.INSTANCE.getLiquifierRecipes().add(recipe);
        }
        return recipe;
    }

    public boolean remove(LiquifierRecipe recipe) {
        if (ModRecipes.INSTANCE.getLiquifierRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example(value = "fluid('water')", commented = true))
    public boolean removeByOutput(FluidStack output) {
        return ModRecipes.INSTANCE.getLiquifierRecipes().removeIf(r -> {
            if (r.getOutput().isFluidEqual(output)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("element('water')"))
    public boolean removeByInput(IIngredient input) {
        return ModRecipes.INSTANCE.getLiquifierRecipes().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<LiquifierRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ModRecipes.INSTANCE.getLiquifierRecipes()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ModRecipes.INSTANCE.getLiquifierRecipes().forEach(this::addBackup);
        ModRecipes.INSTANCE.getLiquifierRecipes().clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "fluidOutput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<LiquifierRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Alchemistry Liquifier recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 0, 0, 1, 1);
        }

        @Nullable
        @Override
        @RecipeBuilderRegistrationMethod
        public LiquifierRecipe register() {
            if (!validate()) return null;
            LiquifierRecipe recipe = new LiquifierRecipe(IngredientHelper.toItemStack(input.get(0)), fluidOutput.get(0));
            ModSupport.ALCHEMISTRY.get().liquifier.add(recipe);
            return recipe;
        }
    }
}
