package com.cleanroommc.groovyscript.compat.mods.pneumaticcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import me.desht.pneumaticcraft.common.recipes.ExplosionCraftingRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription(
        admonition = @Admonition(value = "groovyscript.wiki.pneumaticcraft.explosion.note0", type = Admonition.Type.TIP)
)
public class Explosion extends StandardListRegistry<ExplosionCraftingRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:gold_ingot')).lossRate(40)"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:obsidian'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<ExplosionCraftingRecipe> getRecipes() {
        return ExplosionCraftingRecipe.recipes;
    }

    @MethodDescription(example = @Example("item('pneumaticcraft:compressed_iron_block')"))
    public boolean removeByOutput(IIngredient output) {
        return ExplosionCraftingRecipe.recipes.removeIf(entry -> {
            if (output.test(entry.getOutput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "item('minecraft:iron_block')", commented = true))
    public boolean removeByInput(IIngredient input) {
        return ExplosionCraftingRecipe.recipes.removeIf(entry -> {
            if (input.test(entry.getInput()) || input instanceof OreDictIngredient oreDictIngredient && oreDictIngredient.getOreDict().equals(entry.getOreDictKey())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ExplosionCraftingRecipe> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "0"))
        private int lossRate;

        @RecipeBuilderMethodDescription
        public RecipeBuilder lossRate(int lossRate) {
            this.lossRate = lossRate;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding PneumaticCraft Explosion recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(lossRate < 0, "lossRate must be a non negative integer, yet it was {}", lossRate);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ExplosionCraftingRecipe register() {
            if (!validate()) return null;
            ExplosionCraftingRecipe recipe = null;
            for (ItemStack stack : input.get(0).getMatchingStacks()) {
                ExplosionCraftingRecipe recipe1 = new ExplosionCraftingRecipe(stack, output.get(0), lossRate);
                ModSupport.PNEUMATIC_CRAFT.get().explosion.add(recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }

}
