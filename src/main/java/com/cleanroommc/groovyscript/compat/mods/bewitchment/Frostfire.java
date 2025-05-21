package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.registry.FrostfireRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@RegistryDescription
public class Frostfire extends ForgeRegistryWrapper<FrostfireRecipe> {

    public Frostfire() {
        super(GameRegistry.findRegistry(FrostfireRecipe.class));
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:water_bucket')).output(item('minecraft:ice'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:iron_ore')"))
    public void removeByInput(IIngredient input) {
        getRegistry().forEach(recipe -> {
            if (Arrays.stream(recipe.input.getMatchingStacks()).anyMatch(input)) {
                remove(recipe);
            }
        });
    }

    @MethodDescription(example = @Example(value = "item('bewitchment:cold_iron_ingot')", commented = true))
    public void removeByOutput(IIngredient output) {
        getRegistry().forEach(recipe -> {
            if (output.test(recipe.output)) {
                remove(recipe);
            }
        });
    }

    @Property(property = "name")
    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<FrostfireRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Frostfire recipe";
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_frostfire_";
        }

        /**
         * {@link com.bewitchment.common.block.tile.entity.TileEntityFrostfire TileEntityFrostfire} does {@code shrink(1)}
         */
        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            validateName();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable FrostfireRecipe register() {
            if (!validate()) return null;
            FrostfireRecipe recipe = new FrostfireRecipe(super.name, input.get(0).toMcIngredient(), output.get(0));
            ModSupport.BEWITCHMENT.get().frostfire.add(recipe);
            return recipe;
        }
    }
}
