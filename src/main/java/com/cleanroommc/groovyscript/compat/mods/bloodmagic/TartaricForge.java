package com.cleanroommc.groovyscript.compat.mods.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.recipe.RecipeTartaricForge;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.bloodmagic.BloodMagicRecipeRegistrarAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

@RegistryDescription
public class TartaricForge extends StandardListRegistry<RecipeTartaricForge> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay')).output(item('minecraft:gold_ingot')).soulDrain(5).minimumSouls(10)"),
            @Example(".input(item('minecraft:gold_ingot'), item('minecraft:clay')).output(item('minecraft:diamond')).drain(200).minimumSouls(500)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<RecipeTartaricForge> getRecipes() {
        return ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getTartaricForgeRecipes();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public RecipeTartaricForge add(Collection<IIngredient> input, ItemStack output, double minimumSouls, double soulDrain) {
        return recipeBuilder()
                .soulDrain(soulDrain)
                .minimumSouls(minimumSouls)
                .output(output)
                .input(input)
                .register();
    }

    @MethodDescription(example = {
            @Example("item('minecraft:cauldron'), item('minecraft:stone'), item('minecraft:dye:4'), item('minecraft:diamond')"),
            @Example("item('minecraft:gunpowder'), item('minecraft:redstone')")
    })
    public boolean removeByInput(IIngredient... input) {
        NonNullList<IIngredient> inputs = NonNullList.create();
        Collections.addAll(inputs, input);
        return removeByInput(inputs);
    }

    @MethodDescription
    public boolean removeByInput(NonNullList<IIngredient> input) {
        // Filters down to only recipes which have inputs that match all the input IIngredients (NOTE: a recipe with ABCD would match an input of AB)
        if (getRecipes().removeIf(recipe -> {
            boolean removeRecipe = false;
            for (IIngredient match : input) {
                boolean foundInputMatch = false;
                for (Ingredient target : recipe.getInput()) {
                    if (target.test(IngredientHelper.toItemStack(match))) foundInputMatch = true;
                }
                removeRecipe = foundInputMatch;
            }
            if (removeRecipe) {
                addBackup(recipe);
            }
            return removeRecipe;
        })) {
            return true;
        }

        GroovyLog.msg("Error removing Blood Magic Tartaric Forge recipe")
                .add("could not find recipe with inputs including all of {}", input)
                .error()
                .post();
        return false;
    }

    @MethodDescription(example = @Example("item('bloodmagic:demon_crystal')"))
    public boolean removeByOutput(ItemStack output) {
        if (getRecipes().removeIf(recipe -> {
            boolean matches = ItemStack.areItemStacksEqual(recipe.getOutput(), output);
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        })) {
            return true;
        }
        GroovyLog.msg("Error removing Blood Magic Tartaric Forge recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 4))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeTartaricForge> {

        @Property(comp = @Comp(gte = 0))
        private double minimumSouls;
        @Property(comp = @Comp(gte = 0))
        private double soulDrain;

        @RecipeBuilderMethodDescription
        public RecipeBuilder minimumSouls(double minimumSouls) {
            this.minimumSouls = minimumSouls;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder soulDrain(double soulDrain) {
            this.soulDrain = soulDrain;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "soulDrain")
        public RecipeBuilder drain(int drain) {
            return soulDrain(drain);
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Blood Magic Tartaric Forge recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 4, 1, 1);
            msg.add(minimumSouls < 0, "minimumSouls must be a nonnegative integer, yet it was {}", minimumSouls);
            msg.add(soulDrain < 0, "soulDrain must be a nonnegative integer, yet it was {}", soulDrain);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeTartaricForge register() {
            if (!validate()) return null;
            RecipeTartaricForge recipe = new RecipeTartaricForge(IngredientHelper.toIngredientNonNullList(input), output.get(0), minimumSouls, soulDrain);
            ModSupport.BLOOD_MAGIC.get().tartaricForge.add(recipe);
            return recipe;
        }
    }
}
