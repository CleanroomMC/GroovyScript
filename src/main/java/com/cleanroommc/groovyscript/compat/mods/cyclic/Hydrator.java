package com.cleanroommc.groovyscript.compat.mods.cyclic;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.collect.Lists;
import com.lothrazar.cyclicmagic.CyclicContent;
import com.lothrazar.cyclicmagic.block.hydrator.RecipeHydrate;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Hydrator extends VirtualizedRegistry<RecipeHydrate> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay'))"),
            @Example(".input(ore('logWood'), ore('sand'), ore('gravel'), item('minecraft:diamond')).output(item('minecraft:clay') * 8).water(100)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public boolean isEnabled() {
        return CyclicContent.hydrator.enabled();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        RecipeHydrate.recipes.removeAll(removeScripted());
        RecipeHydrate.recipes.addAll(restoreFromBackup());
    }

    public void add(RecipeHydrate recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        RecipeHydrate.recipes.add(recipe);
    }

    public boolean remove(RecipeHydrate recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        RecipeHydrate.recipes.remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:dirt')"))
    public boolean removeByInput(IIngredient input) {
        return RecipeHydrate.recipes.removeIf(recipe -> {
            if (recipe.getRecipeInput().stream().anyMatch(input)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:clay_ball')"))
    public boolean removeByOutput(IIngredient output) {
        return RecipeHydrate.recipes.removeIf(recipe -> {
            if (output.test(recipe.getRecipeOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        RecipeHydrate.recipes.forEach(this::addBackup);
        RecipeHydrate.recipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<RecipeHydrate> streamRecipes() {
        return new SimpleObjectStream<>(RecipeHydrate.recipes)
                .setRemover(this::remove);
    }

    @Property(property = "input", comp = @Comp(types = {Comp.Type.GTE, Comp.Type.LTE}, gte = 1, lte = 6))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeHydrate> {

        @Property(defaultValue = "25", comp = @Comp(types = Comp.Type.GTE))
        private int water = 25;

        @RecipeBuilderMethodDescription
        public RecipeBuilder water(int water) {
            this.water = water;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Cyclic Hydrator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 6, 1, 1);
            validateFluids(msg);
            msg.add(water < 0, "water must be a non-negative integer, yet it was {}", water);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeHydrate register() {
            if (!validate()) return null;
            RecipeHydrate recipe = null;
            List<List<ItemStack>> cartesian = Lists.cartesianProduct(input.stream().map(x -> Arrays.asList(x.toMcIngredient().getMatchingStacks())).collect(Collectors.toList()));
            for (List<ItemStack> stacks : cartesian) {
                recipe = new RecipeHydrate(stacks.toArray(new ItemStack[0]), output.get(0), water);
                ModSupport.CYCLIC.get().hydrator.add(recipe);
            }
            return recipe;
        }
    }
}
