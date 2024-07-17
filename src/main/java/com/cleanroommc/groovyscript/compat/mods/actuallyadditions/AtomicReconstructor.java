package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.lens.Lens;
import de.ellpeck.actuallyadditions.api.recipe.LensConversionRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class AtomicReconstructor extends VirtualizedRegistry<LensConversionRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).energyUse(1000)"),
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).energy(1000)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay') * 2)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES::remove);
        ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.addAll(restoreFromBackup());
    }

    public LensConversionRecipe add(IIngredient input, ItemStack output, int energy, Lens type) {
        return recipeBuilder()
                .type(type)
                .energy(energy)
                .input(input)
                .output(output)
                .register();
    }

    public void add(LensConversionRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.add(recipe);
    }

    public boolean remove(LensConversionRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.remove(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOre", example = @Example("item('minecraft:diamond')"))
    public boolean removeByInput(IIngredient input) {
        return ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.removeIf(recipe -> {
            boolean found = recipe.getInput().test(IngredientHelper.toItemStack(input));
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    @MethodDescription(example = @Example("item('actuallyadditions:block_crystal')"))
    public boolean removeByOutput(ItemStack output) {
        return ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.removeIf(recipe -> {
            boolean matches = ItemStack.areItemStacksEqual(recipe.getOutput(), output);
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.forEach(this::addBackup);
        ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<LensConversionRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES)
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<LensConversionRecipe> {

        @Property(defaultValue = "1", valid = @Comp(type = Comp.Type.GT, value = "0"))
        private int energyUse = 1;
        @Property(defaultValue = "ActuallyAdditionsAPI.lensDefaultConversion", valid = @Comp(value = "null", type = Comp.Type.NOT))
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
