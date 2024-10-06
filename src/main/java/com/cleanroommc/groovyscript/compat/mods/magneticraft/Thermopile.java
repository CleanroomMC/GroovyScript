package com.cleanroommc.groovyscript.compat.mods.magneticraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.magneticraft.ThermopileRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.cout970.magneticraft.api.MagneticraftApi;
import com.cout970.magneticraft.api.registries.generators.thermopile.IThermopileRecipe;
import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Thermopile extends StandardListRegistry<IThermopileRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".state(blockstate('minecraft:clay')).conductivity(10).temperature(500)"),
            @Example(".state(blockstate('minecraft:diamond_block')).conductivity(70).temperature(700)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    /**
     * Internally, the API method uses {@link kotlin.collections.CollectionsKt#toMutableList},
     * so a mixin is required to access the actual list.
     *
     * @see com.cout970.magneticraft.api.internal.registries.generators.thermopile.ThermopileRecipeManager#getRecipes() MagneticraftApi.getThermopileRecipeManager().getRecipes()
     */
    @Override
    public Collection<IThermopileRecipe> getRecipes() {
        return ThermopileRecipeManagerAccessor.getRecipeList();
    }

    @MethodDescription(example = @Example("blockstate('minecraft:ice')"))
    public boolean removeByInput(IBlockState input) {
        return getRecipes().removeIf(r -> input == r.getBlockState() && doAddBackup(r));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<IThermopileRecipe> {

        @Property(comp = @Comp(not = "null"))
        private IBlockState state;
        @Property
        private float conductivity;
        @Property
        private float temperature;

        @RecipeBuilderMethodDescription
        public RecipeBuilder state(IBlockState state) {
            this.state = state;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder conductivity(float conductivity) {
            this.conductivity = conductivity;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder temperature(float temperature) {
            this.temperature = temperature;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Magneticraft Thermopile recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg);
            msg.add(state == null, "state must be defined");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IThermopileRecipe register() {
            if (!validate()) return null;
            IThermopileRecipe recipe = MagneticraftApi.getThermopileRecipeManager().createRecipe(state, temperature, conductivity);
            ModSupport.MAGNETICRAFT.get().thermopile.add(recipe);
            return recipe;
        }

    }

}
