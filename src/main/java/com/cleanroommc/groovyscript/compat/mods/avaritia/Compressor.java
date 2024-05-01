package com.cleanroommc.groovyscript.compat.mods.avaritia;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import morph.avaritia.recipe.AvaritiaRecipeManager;
import morph.avaritia.recipe.compressor.CompressorRecipe;
import morph.avaritia.recipe.compressor.ICompressorRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

@RegistryDescription
public class Compressor extends VirtualizedRegistry<ICompressorRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> AvaritiaRecipeManager.COMPRESSOR_RECIPES.values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(recipe -> AvaritiaRecipeManager.COMPRESSOR_RECIPES.put(recipe.getRegistryName(), recipe));
    }

    public boolean remove(ICompressorRecipe recipe) {
        recipe = AvaritiaRecipeManager.COMPRESSOR_RECIPES.remove(recipe.getRegistryName());
        if (recipe != null) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("item('avaritia:singularity', 0)"))
    public boolean removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing avaritia compressor recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return false;
        }
        return AvaritiaRecipeManager.COMPRESSOR_RECIPES.values().removeIf(recipe -> {
            if (recipe != null && recipe.getResult().isItemEqual(output)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        AvaritiaRecipeManager.COMPRESSOR_RECIPES.values().forEach(this::addBackup);
        AvaritiaRecipeManager.COMPRESSOR_RECIPES.values().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ICompressorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(AvaritiaRecipeManager.COMPRESSOR_RECIPES.values()).setRemover(this::remove);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay_ball') * 100).output(item('minecraft:nether_star')).inputCount(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(ICompressorRecipe recipe) {
        AvaritiaRecipeManager.COMPRESSOR_RECIPES.put(recipe.getRegistryName(), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:nether_star'), item('minecraft:clay_ball'), 100"))
    public void add(ItemStack output, IIngredient input, int cost) {
        recipeBuilder()
                .inputCount(cost)
                .input(input)
                .output(output)
                .register();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public class RecipeBuilder extends AbstractRecipeBuilder<ICompressorRecipe> {

        @Property(defaultValue = "300")
        private int inputCount = 300;

        @Override
        @RecipeBuilderMethodDescription(field = {"input", "inputCount"})
        public AbstractRecipeBuilder<ICompressorRecipe> input(IIngredient ingredient) {
            if (ingredient == null) return this;
            if (ingredient.getAmount() > 1) {
                this.inputCount = ingredient.getAmount();
            }
            return super.input(ingredient.withAmount(1));
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inputCount(int inputCount) {
            this.inputCount = inputCount;
            return this;
        }

        @Override
        public String getRecipeNamePrefix() {
            return "avaritia_compressor_";
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Avaritia compressor recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            validateName();
            if (this.inputCount <= 0) {
                this.inputCount = 1;
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ICompressorRecipe register() {
            if (!validate()) return null;
            CompressorRecipe recipe = new CompressorRecipe(this.output.get(0), this.inputCount, true, Collections.singletonList(this.input.get(0).toMcIngredient()));
            recipe.setRegistryName(this.name);
            add(recipe);
            return recipe;
        }
    }
}
