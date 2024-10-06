package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;
import net.minecraft.item.ItemStack;

import java.util.Collection;

@RegistryDescription
public class Grindstone extends StandardListRegistry<GrindstoneRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(ore('blockDiamond')).output(item('minecraft:clay')).weight(1).secondaryChance(1.0F)"),
            @Example(".input(item('minecraft:stone')).output(item('minecraft:cobblestone')).weight(5)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<GrindstoneRecipe> getRecipes() {
        return GrindstoneRecipeRegistry.recipes;
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.grindstone.add0", type = MethodDescription.Type.ADDITION)
    public GrindstoneRecipe add(ItemHandle input, ItemStack output, int weight) {
        return add(input, output, weight, 0);
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.grindstone.add1", type = MethodDescription.Type.ADDITION)
    public GrindstoneRecipe add(ItemHandle input, ItemStack output, int weight, float secondaryChance) {
        GrindstoneRecipe recipe = new GrindstoneRecipe(input, output, weight, secondaryChance);
        addScripted(recipe);
        return GrindstoneRecipeRegistry.registerGrindstoneRecipe(recipe);
    }

    @MethodDescription(example = @Example("item('minecraft:redstone_ore')"))
    public void removeByInput(ItemStack item) {
        getRecipes().removeIf(recipe -> {
            if (recipe.isValid() && recipe.matches(item)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("ore('dustIron')"))
    public void removeByOutput(OreDictIngredient ore) {
        for (ItemStack item : ore.getMatchingStacks())
            this.removeByOutput(item);
    }

    @MethodDescription
    public void removeByOutput(ItemStack item) {
        getRecipes().removeIf(recipe -> {
            if (recipe.isValid() && recipe.getOutputForMatching().isItemEqual(item)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<GrindstoneRecipe> {

        @Property(comp = @Comp(gte = 0))
        private int weight;
        @Property(comp = @Comp(gte = 0, lte = 1))
        private float secondaryChance;

        @RecipeBuilderMethodDescription
        public RecipeBuilder weight(int weight) {
            this.weight = weight;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder secondaryChance(float chance) {
            this.secondaryChance = chance;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            // More than 1 item cannot be placed
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Astral Sorcery Grindstone recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(weight < 0, () -> "Weight cannot be negative");
            msg.add(secondaryChance < 0 || secondaryChance > 1, () -> "Secondary chance must be between [0,1]. Instead found " + secondaryChance + ".");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public GrindstoneRecipe register() {
            if (!validate()) return null;
            GrindstoneRecipe recipe = new GrindstoneRecipe(AstralSorcery.toItemHandle(input.get(0)), output.get(0), weight, secondaryChance);
            ModSupport.ASTRAL_SORCERY.get().grindstone.add(recipe);
            return recipe;
        }

    }
}
