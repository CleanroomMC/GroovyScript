package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import techreborn.api.fluidreplicator.FluidReplicatorRecipe;
import techreborn.api.fluidreplicator.FluidReplicatorRecipeList;

@RegistryDescription
public class FluidReplicator extends VirtualizedRegistry<FluidReplicatorRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".matter(10).fluidOutput(fluid('water')).time(100).perTick(10)"),
            @Example(".matter(1).fluidOutput(fluid('fluidmethane')).time(5).perTick(1000)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        FluidReplicatorRecipeList.recipes.removeAll(removeScripted());
        FluidReplicatorRecipeList.recipes.addAll(restoreFromBackup());
    }

    public void add(FluidReplicatorRecipe recipe) {
        FluidReplicatorRecipeList.recipes.add(recipe);
        addScripted(recipe);
    }

    public boolean remove(FluidReplicatorRecipe recipe) {
        addBackup(recipe);
        return FluidReplicatorRecipeList.recipes.remove(recipe);
    }

    @MethodDescription(example = @Example("fluid('water')"))
    public boolean removeByOutput(FluidStack output) {
        return FluidReplicatorRecipeList.recipes.removeIf(entry -> {
            if (output.getFluid().equals(entry.getFluid())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        FluidReplicatorRecipeList.recipes.forEach(this::addBackup);
        FluidReplicatorRecipeList.recipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<FluidReplicatorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(FluidReplicatorRecipeList.recipes).setRemover(this::remove);
    }

    @Property(property = "fluidOutput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<FluidReplicatorRecipe> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int matter;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int time;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int perTick;

        @RecipeBuilderMethodDescription
        public RecipeBuilder matter(int matter) {
            this.matter = matter;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder perTick(int perTick) {
            this.perTick = perTick;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Tech Reborn Fluid Replicator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 0, 0, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable FluidReplicatorRecipe register() {
            if (!validate()) return null;
            FluidReplicatorRecipe recipe = new FluidReplicatorRecipe(matter, fluidOutput.get(0).getFluid(), time, perTick);
            ModSupport.TECH_REBORN.get().fluidReplicator.add(recipe);
            return recipe;
        }
    }

}
