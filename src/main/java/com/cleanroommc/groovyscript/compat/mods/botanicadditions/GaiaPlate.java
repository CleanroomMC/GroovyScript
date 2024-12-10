package com.cleanroommc.groovyscript.compat.mods.botanicadditions;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;
import tk.zeitheron.botanicadds.api.GaiaPlateRecipes;

import java.util.Collection;

@RegistryDescription
public class GaiaPlate extends StandardListRegistry<GaiaPlateRecipes.RecipeGaiaPlate> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:gold_ingot') * 16).mana(1000)"),
            @Example(".input(item('minecraft:diamond_block'), item('minecraft:gold_block'), item('minecraft:clay')).output(item('minecraft:gold_ingot')).mana(100)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<GaiaPlateRecipes.RecipeGaiaPlate> getRecipes() {
        return GaiaPlateRecipes.gaiaRecipes;
    }

    @MethodDescription(example = @Example("item('botanicadds:gaiasteel_ingot')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('botania:manaresource')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            for (Object ingredient : r.getInputs()) {
                if ((ingredient instanceof String s && (input instanceof OreDictIngredient ore && ore.getOreDict().equals(s) || OreDictionary.getOres(s, false).stream().anyMatch(input))) || (ingredient instanceof ItemStack is && input.test(is))) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<GaiaPlateRecipes.RecipeGaiaPlate> {

        @Property(defaultValue = "1", comp = @Comp(gte = 1))
        private int mana = 1;

        @RecipeBuilderMethodDescription
        public RecipeBuilder mana(int mana) {
            this.mana = mana;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Botanic Additions Gaia Plate recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, Integer.MAX_VALUE, 1, 1);
            validateFluids(msg);
            msg.add(mana <= 0, "mana must be a positive integer greater than 0, yet it was {}", mana);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable GaiaPlateRecipes.RecipeGaiaPlate register() {
            if (!validate()) return null;
            Object[] inputs = input.stream()
                    .map(i -> i instanceof OreDictIngredient oreDictIngredient ? oreDictIngredient.getOreDict() : i.getMatchingStacks()[0])
                    .toArray();
            GaiaPlateRecipes.RecipeGaiaPlate recipe = new GaiaPlateRecipes.RecipeGaiaPlate(output.get(0), mana, inputs);
            ModSupport.BOTANIC_ADDITIONS.get().gaiaPlate.add(recipe);
            return recipe;
        }
    }
}
