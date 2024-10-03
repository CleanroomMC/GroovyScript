package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.lens.Lens;
import de.ellpeck.actuallyadditions.api.recipe.LensConversionRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class AtomicReconstructor extends StandardListRegistry<LensConversionRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).energyUse(1000)"),
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).energy(1000)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay') * 2)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<LensConversionRecipe> getRecipes() {
        return ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES;
    }

    public LensConversionRecipe add(IIngredient input, ItemStack output, int energy, Lens type) {
        return recipeBuilder()
                .type(type)
                .energy(energy)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOre", example = @Example("item('minecraft:diamond')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(recipe -> {
            boolean found = recipe.getInput().test(IngredientHelper.toItemStack(input));
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    @MethodDescription(example = @Example("item('actuallyadditions:block_crystal')"))
    public boolean removeByOutput(ItemStack output) {
        return getRecipes().removeIf(recipe -> {
            boolean matches = ItemStack.areItemStacksEqual(recipe.getOutput(), output);
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<LensConversionRecipe> {

        @Property(defaultValue = "1", comp = @Comp(gt = 0))
        private int energyUse = 1;
        @Property(defaultValue = "ActuallyAdditionsAPI.lensDefaultConversion", comp = @Comp(not = "null"))
        private Lens type = ActuallyAdditionsAPI.lensDefaultConversion;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energyUse(int energyUse) {
            this.energyUse = energyUse;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "energyUse")
        public RecipeBuilder energy(int energy) {
            this.energyUse = energy;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder type(Lens type) {
            this.type = type;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Actually Additions Atomic Reconstructor recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(energyUse <= 0, "energyUse must be an integer greater than 0, yet it was {}", energyUse);
            msg.add(type == null, "type must not be null!");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable LensConversionRecipe register() {
            if (!validate()) return null;
            LensConversionRecipe recipe = new LensConversionRecipe(input.get(0).toMcIngredient(), output.get(0), energyUse, type);
            ModSupport.ACTUALLY_ADDITIONS.get().atomicReconstructor.add(recipe);
            return recipe;
        }

    }

}
