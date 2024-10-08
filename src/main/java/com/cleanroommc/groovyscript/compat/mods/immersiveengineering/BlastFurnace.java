package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@RegistryDescription
public class BlastFurnace extends StandardListRegistry<BlastFurnaceRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay')).time(100).slag(item('minecraft:gold_nugget'))"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<BlastFurnaceRecipe> getRecipes() {
        return BlastFurnaceRecipe.recipeList;
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public BlastFurnaceRecipe add(ItemStack output, IIngredient input, int time, @NotNull ItemStack slag) {
        BlastFurnaceRecipe recipe = new BlastFurnaceRecipe(output.copy(), ImmersiveEngineering.toIEInput(input), time, IngredientHelper.copy(slag));
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("item('immersiveengineering:metal:8')"))
    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Immersive Engineering Blast Furnace recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        List<BlastFurnaceRecipe> list = BlastFurnaceRecipe.removeRecipes(output);
        if (list.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Blast Furnace recipe")
                    .add("no recipes found for {}", output)
                    .error()
                    .post();
            return;
        }
        list.forEach(this::addBackup);
    }

    @MethodDescription(example = @Example("item('minecraft:iron_block')"))
    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Immersive Engineering Blast Furnace recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        BlastFurnaceRecipe recipe = BlastFurnaceRecipe.findRecipe(input);
        if (recipe != null) {
            remove(recipe);
        } else {
            GroovyLog.msg("Error removing Immersive Engineering Blast Furnace recipe")
                    .add("no recipes found for {}", input)
                    .error()
                    .post();
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<BlastFurnaceRecipe> {

        @Property(comp = @Comp(gte = 0))
        private int time;
        @Property
        private ItemStack slag;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder slag(ItemStack slag) {
            this.slag = slag;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Blast Furnace recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (time < 0) time = 200;
            if (slag == null) slag = ItemStack.EMPTY;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable BlastFurnaceRecipe register() {
            if (!validate()) return null;
            return ModSupport.IMMERSIVE_ENGINEERING.get().blastFurnace.add(output.get(0), input.get(0), time, slag);
        }
    }
}
