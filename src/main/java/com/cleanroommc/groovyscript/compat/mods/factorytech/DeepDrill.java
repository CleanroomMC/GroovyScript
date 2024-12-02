package com.cleanroommc.groovyscript.compat.mods.factorytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import dalapo.factech.auxiliary.MachineRecipes;
import dalapo.factech.helper.Pair;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class DeepDrill extends StandardListRegistry<Pair<ItemStack, Double>> {

    @Override
    public Collection<Pair<ItemStack, Double>> getRecipes() {
        return MachineRecipes.DEEP_DRILL;
    }

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:diamond')).weight(10)"),
            @Example(".output(item('minecraft:clay')).weight(30)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:gold_ore')"))
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> input.test(r.a) && doAddBackup(r));
    }

    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<Pair<ItemStack, Double>> {

        @Property(comp = @Comp(gte = 0))
        private double weight;

        @RecipeBuilderMethodDescription
        public RecipeBuilder weight(double weight) {
            this.weight = weight;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Factory Tech Deep Drill recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
            msg.add(weight < 0, "weight must be a non-negative double, yet it was {}", weight);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Pair<ItemStack, Double> register() {
            if (!validate()) return null;
            var recipe = new Pair<>(output.get(0), weight);
            ModSupport.FACTORY_TECH.get().deepDrill.add(recipe);
            return recipe;
        }
    }
}
