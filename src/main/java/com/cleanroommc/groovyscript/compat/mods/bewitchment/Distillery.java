package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.registry.DistilleryRecipe;
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

@RegistryDescription(override = @MethodOverride(method = @MethodDescription(method = "remove(Lnet/minecraft/util/ResourceLocation;)V", example = @Example("resource('bewitchment:bottled_frostfire')"))))
public class Distillery extends ForgeRegistryWrapper<DistilleryRecipe> {

    public Distillery() {
        super(GameRegistry.findRegistry(DistilleryRecipe.class));
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:glass_bottle')).input(item('minecraft:snow')).input(item('bewitchment:cleansing_balm')).input(item('bewitchment:fiery_unguent')).output(item('bewitchment:bottled_frostfire')).output(item('bewitchment:empty_jar') * 2)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('bewitchment:perpetual_ice')"))
    public void removeByInput(IIngredient input) {
        getRegistry().forEach(recipe -> {
            if (recipe.input.stream().map(Ingredient::getMatchingStacks).flatMap(Arrays::stream).anyMatch(input)) {
                remove(recipe);
            }
        });
    }

    @MethodDescription(example = @Example("item('bewitchment:demonic_elixir')"))
    public void removeByOutput(IIngredient output) {
        getRegistry().forEach(recipe -> {
            if (recipe.output.stream().anyMatch(output)) {
                remove(recipe);
            }
        });
    }

    @Property(property = "name")
    @Property(property = "input", comp = @Comp(gte = 1, lte = 6))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 6))
    public static class RecipeBuilder extends AbstractRecipeBuilder<DistilleryRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Distillery Recipe";
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_distillery_recipe_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 6, 1, 6);
            validateFluids(msg);
            validateName();
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable DistilleryRecipe register() {
            if (!validate()) return null;
            List<Ingredient> inputs = input.stream().map(IIngredient::toMcIngredient).collect(Collectors.toList());
            DistilleryRecipe recipe = new DistilleryRecipe(super.name, inputs, output);
            ModSupport.BEWITCHMENT.get().distillery.add(recipe);
            return recipe;
        }
    }
}
