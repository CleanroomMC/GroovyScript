package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.OilGenRecipe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class OilGen extends VirtualizedRegistry<OilGenRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('water')).amount(1000).time(50)"),
            @Example(".fluidInput(fluid('lava') * 50).time(100)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES::remove);
        ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES.addAll(restoreFromBackup());
    }

    public OilGenRecipe add(Fluid input, int amount, int time) {
        return add(input.getName(), amount, time);
    }

    public OilGenRecipe add(String input, int amount, int time) {
        OilGenRecipe recipe = new OilGenRecipe(input, amount, time);
        add(recipe);
        return recipe;
    }

    public void add(OilGenRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES.add(recipe);
    }

    public boolean remove(OilGenRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES.remove(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("fluid('canolaoil')"))
    public boolean removeByInput(FluidStack fluid) {
        return this.removeByInput(fluid.getFluid());
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("fluid('canolaoil').getFluid()"))
    public boolean removeByInput(Fluid fluid) {
        return this.removeByInput(fluid.getName());
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("'refinedcanolaoil'"))
    public boolean removeByInput(String fluid) {
        return ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES.removeIf(recipe -> {
            boolean found = fluid.equals(recipe.fluidName);
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES.forEach(this::addBackup);
        ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES.clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<OilGenRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES)
                .setRemover(this::remove);
    }


    @Property(property = "fluidInput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<OilGenRecipe> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "0"))
        private int amount;
        @Property(valid = @Comp(type = Comp.Type.GTE, value = "0"))
        private int time;

        @RecipeBuilderMethodDescription(field = {"fluidInput", "amount"})
        public RecipeBuilder fluidInput(FluidStack fluid) {
            this.fluidInput.add(fluid);
            if (this.amount == 0) this.amount = fluid.amount;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Actually Additions Oil Gen recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 0, 0);
            msg.add(amount < 0, "amount must be a non negative integer, yet it was {}", amount);
            msg.add(time < 0, "time must be a non negative integer, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable OilGenRecipe register() {
            if (!validate()) return null;
            OilGenRecipe recipe = new OilGenRecipe(fluidInput.get(0).getFluid().getName(), amount, time);
            ModSupport.ACTUALLY_ADDITIONS.get().oilGen.add(recipe);
            return recipe;
        }
    }
}
