package com.cleanroommc.groovyscript.compat.mods.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.recipe.RecipeTartaricForge;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.bloodmagic.BloodMagicRecipeRegistrarAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

@RegistryDescription
public class TartaricForge extends VirtualizedRegistry<RecipeTartaricForge> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay')).output(item('minecraft:gold_ingot')).soulDrain(5).minimumSouls(10)"),
            @Example(".input(item('minecraft:gold_ingot'), item('minecraft:clay')).output(item('minecraft:diamond')).drain(200).minimumSouls(500)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getTartaricForgeRecipes()::remove);
        restoreFromBackup().forEach(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getTartaricForgeRecipes()::add);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public RecipeTartaricForge add(NonNullList<Ingredient> input, ItemStack output, double minimumSouls, double soulDrain) {
        double minimum;
        if (minimumSouls < soulDrain) {
            GroovyLog.msg("Warning creating Blood Magic Tartaric Forge recipe")
                    .add("minimumSouls should be greater than soulDrain, yet minimumSouls was {} and soulDrain was {}", minimumSouls, soulDrain)
                    .add("set minimumSouls equal to soulDrain")
                    .warn()
                    .post();
            minimum = soulDrain;
        } else {
            minimum = minimumSouls;
        }
        RecipeTartaricForge recipe = new RecipeTartaricForge(input, output, minimum, soulDrain);
        add(recipe);
        return recipe;
    }

    public void add(RecipeTartaricForge recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getTartaricForgeRecipes().add(recipe);
    }

    public boolean remove(RecipeTartaricForge recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getTartaricForgeRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = {
            @Example("item('minecraft:cauldron'), item('minecraft:stone'), item('minecraft:dye:4'), item('minecraft:diamond')"),
            @Example("item('minecraft:gunpowder'), item('minecraft:redstone')")
    })
    public boolean removeByInput(IIngredient... input) {
        NonNullList<IIngredient> inputs = NonNullList.create();
        Collections.addAll(inputs, input);
        return removeByInput(inputs);
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput")
    public boolean removeByInput(NonNullList<IIngredient> input) {
        // Filters down to only recipes which have inputs that match all the input IIngredients (NOTE: a recipe with ABCD would match an input of AB)
        if (((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getTartaricForgeRecipes().removeIf(recipe -> {
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

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('bloodmagic:demon_crystal')"))
    public boolean removeByOutput(ItemStack output) {
        if (((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getTartaricForgeRecipes().removeIf(recipe -> {
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

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getTartaricForgeRecipes().forEach(this::addBackup);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getTartaricForgeRecipes().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<RecipeTartaricForge> streamRecipes() {
        return new SimpleObjectStream<>(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getTartaricForgeRecipes())
                .setRemover(this::remove);
    }


    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "4")})
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeTartaricForge> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "0"))
        private double minimumSouls;
        @Property(valid = @Comp(type = Comp.Type.GTE, value = "0"))
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
            RecipeTartaricForge recipe = ModSupport.BLOOD_MAGIC.get().tartaricForge.add(IngredientHelper.toIngredientNonNullList(input), output.get(0), minimumSouls, soulDrain);
            return recipe;
        }
    }
}
