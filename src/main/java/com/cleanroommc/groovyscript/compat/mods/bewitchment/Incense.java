package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Incense extends ForgeRegistryWrapper<com.bewitchment.api.registry.Incense> {

    public Incense() {
        super(GameRegistry.findRegistry(com.bewitchment.api.registry.Incense.class));
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay'), item('minecraft:gold_ingot') * 5, item('minecraft:iron_ingot')).potion(potion('minecraft:strength'), potion('minecraft:resistance')).time(10000)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('bewitchment:essence_of_vitality')"))
    public void removeByInput(IIngredient input) {
        getRegistry().forEach(recipe -> {
            if (recipe.input.stream().map(Ingredient::getMatchingStacks).flatMap(Arrays::stream).anyMatch(input)) {
                remove(recipe);
            }
        });
    }

    @MethodDescription(example = @Example("potion('minecraft:haste')"))
    public void removeByPotion(Potion potion) {
        getRegistry().forEach(recipe -> {
            if (recipe.effects != null && recipe.effects.stream().anyMatch(potion::equals)) {
                remove(recipe);
            }
        });
    }

    @Property(property = "name")
    @Property(property = "input", comp = @Comp(gte = 1, lte = 8))
    public static class RecipeBuilder extends AbstractRecipeBuilder<com.bewitchment.api.registry.Incense> {

        @Property(comp = @Comp(gte = 1))
        private final List<Potion> potion = new ArrayList<>();
        @Property(comp = @Comp(gte = 1))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder potion(Potion potion) {
            this.potion.add(potion);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder potion(Potion... potions) {
            for (var potion : potions) {
                potion(potion);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder potion(Collection<Potion> potions) {
            for (var potion : potions) {
                potion(potion);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Incense Recipe";
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_incense_recipe_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 8, 0, 0);
            validateFluids(msg);
            validateCustom(msg, potion, 1, Integer.MAX_VALUE, "potion");
            msg.add(time <= 0, "time must be greater than 0, got {}", time);
            validateName();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable com.bewitchment.api.registry.Incense register() {
            if (!validate()) return null;
            var recipe = new com.bewitchment.api.registry.Incense(super.name, input.stream().map(IIngredient::toMcIngredient).collect(Collectors.toList()), potion, time);
            ModSupport.BEWITCHMENT.get().incense.add(recipe);
            return recipe;
        }
    }
}
