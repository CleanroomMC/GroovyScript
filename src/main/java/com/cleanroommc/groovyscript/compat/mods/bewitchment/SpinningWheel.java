package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.registry.SpinningWheelRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class SpinningWheel extends ForgeRegistryWrapper<SpinningWheelRecipe> {

    public SpinningWheel() {
        super(GameRegistry.findRegistry(SpinningWheelRecipe.class));
    }

    @RecipeBuilderDescription
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Property(property = "name")
    @Property(property = "input", comp = @Comp(gte = 1, lte = 4))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 2))
    public static class RecipeBuilder extends AbstractRecipeBuilder<SpinningWheelRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Spinning Wheel Recipe";
        }

        @Override
        public String getRecipeNamePrefix() {
            return "spinning_wheel_recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 4, 1, 2);
            validateFluids(msg);
            validateName();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SpinningWheelRecipe register() {
            if (!validate()) return null;
            List<Ingredient> inputs = input.stream().map(IIngredient::toMcIngredient).collect(Collectors.toList());
            SpinningWheelRecipe recipe = new SpinningWheelRecipe(super.name, inputs, output);
            ModSupport.BEWITCHMENT.get().spinningWheel.add(recipe);
            return recipe;
        }
    }
}
