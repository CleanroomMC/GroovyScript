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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription(override = @MethodOverride(method = @MethodDescription(method = "remove(Lnet/minecraft/util/ResourceLocation;)V", example = @Example("resource('bewitchment:cobweb')"))))
public class SpinningWheel extends ForgeRegistryWrapper<SpinningWheelRecipe> {

    public SpinningWheel() {
        super(GameRegistry.findRegistry(SpinningWheelRecipe.class));
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:string'), item('minecraft:string'), item('minecraft:string'), item('minecraft:string')).output(item('minecraft:gold_ingot') * 4, item('minecraft:web'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:string')"))
    public void removeByInput(IIngredient input) {
        getRegistry().forEach(recipe -> {
            if (recipe.input.stream().map(Ingredient::getMatchingStacks).flatMap(Arrays::stream).anyMatch(input)) {
                remove(recipe);
            }
        });
    }

    @MethodDescription(example = @Example("item('bewitchment:spirit_string')"))
    public void removeByOutput(IIngredient output) {
        getRegistry().forEach(recipe -> {
            if (recipe.output.stream().anyMatch(output)) {
                remove(recipe);
            }
        });
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
            return "groovyscript_spinning_wheel_recipe_";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
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
