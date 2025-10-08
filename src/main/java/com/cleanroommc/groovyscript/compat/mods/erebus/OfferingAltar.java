package com.cleanroommc.groovyscript.compat.mods.erebus;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IOreDicts;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.erebus.OfferingAltarRecipeAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import erebus.recipes.OfferingAltarRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@RegistryDescription
public class OfferingAltar extends StandardListRegistry<OfferingAltarRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:stone'), ore('gemDiamond'), item('minecraft:clay')).output(item('minecraft:gold_ingot'))")
    })
    public OfferingAltar.RecipeBuilder recipeBuilder() {
        return new OfferingAltar.RecipeBuilder();
    }

    @Override
    public Collection<OfferingAltarRecipe> getRecipes() {
        return OfferingAltarRecipeAccessor.getRecipes();
    }

    @MethodDescription(example = @Example(value = "item('minecraft:emerald')", commented = true))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            for (Object object : r.getInputs()) {
                if (object instanceof ItemStack stack && input.test(stack)) return doAddBackup(r);
                else if (object instanceof String s) {
                    if (input instanceof IOreDicts d && d.getOreDicts().contains(s)) {
                        return true;
                    } else if (OreDictionary.getOres(s, false).stream().anyMatch(input)) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('erebus:materials', 38)"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 3))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<OfferingAltarRecipe> {

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Erebus Offering Altar Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 3, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable OfferingAltarRecipe register() {
            if (!validate()) return null;
            OfferingAltarRecipe recipe = null;
            var inputs = IngredientHelper.cartesianProductOres(input);
            for (List<Object> objects : inputs) {
                recipe = OfferingAltarRecipeAccessor.createOfferingAltarRecipe(output.get(0), objects.toArray());
                ModSupport.EREBUS.get().offeringAltar.add(recipe);
            }
            return recipe;
        }
    }
}
