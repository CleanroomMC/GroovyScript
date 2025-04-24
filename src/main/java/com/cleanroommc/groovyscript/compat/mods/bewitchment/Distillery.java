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

import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Distillery extends ForgeRegistryWrapper<DistilleryRecipe> {

    public Distillery() {
        super(GameRegistry.findRegistry(DistilleryRecipe.class));
    }

    @RecipeBuilderDescription
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
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
            return "distillery_recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 6, 1, 6);
            validateFluids(msg);
            validateName();
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
