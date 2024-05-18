package com.cleanroommc.groovyscript.compat.mods.alchemistry;

import al132.alchemistry.recipes.EvaporatorRecipe;
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
public class Evaporator extends VirtualizedRegistry<EvaporatorRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ModRecipes.INSTANCE.getEvaporatorRecipes().removeIf(r -> r == recipe));
        ModRecipes.INSTANCE.getEvaporatorRecipes().addAll(restoreFromBackup());
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('lava') * 100).output(item('minecraft:redstone') * 8)"),
            @Example(".fluidInput(fluid('water') * 10).output(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public EvaporatorRecipe add(FluidStack input, ItemStack output) {
        return new RecipeBuilder().fluidInput(input).output(output).register();
    }

    public EvaporatorRecipe add(EvaporatorRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ModRecipes.INSTANCE.getEvaporatorRecipes().add(recipe);
        }
        return recipe;
    }

    public boolean remove(EvaporatorRecipe recipe) {
        if (ModRecipes.INSTANCE.getEvaporatorRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("item('alchemistry:mineral_salt')"))
    public boolean removeByOutput(IIngredient output) {
        return ModRecipes.INSTANCE.getEvaporatorRecipes().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('lava')"))
    public boolean removeByInput(FluidStack input) {
        return ModRecipes.INSTANCE.getEvaporatorRecipes().removeIf(r -> {
            if (r.getInput().isFluidEqual(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<EvaporatorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ModRecipes.INSTANCE.getEvaporatorRecipes()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ModRecipes.INSTANCE.getEvaporatorRecipes().forEach(this::addBackup);
        ModRecipes.INSTANCE.getEvaporatorRecipes().clear();
    }

    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<EvaporatorRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Alchemistry Evaporator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
        }

        @Nullable
        @Override
        @RecipeBuilderRegistrationMethod
        public EvaporatorRecipe register() {
            if (!validate()) return null;
            EvaporatorRecipe recipe = new EvaporatorRecipe(fluidInput.get(0), output.get(0));
            ModSupport.ALCHEMISTRY.get().evaporator.add(recipe);
            return recipe;
        }
    }
}
