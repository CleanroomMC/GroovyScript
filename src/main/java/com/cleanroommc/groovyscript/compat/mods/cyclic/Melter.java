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
import com.lothrazar.cyclicmagic.block.melter.RecipeMelter;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Melter extends VirtualizedRegistry<RecipeMelter> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).fluidOutput(fluid('water') * 175)"),
            @Example(".input(ore('logWood'), ore('sand'), ore('gravel'), item('minecraft:diamond')).fluidOutput(fluid('lava') * 500)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        RecipeMelter.recipes.removeAll(removeScripted());
        RecipeMelter.recipes.addAll(restoreFromBackup());
    }

    public void add(RecipeMelter recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        RecipeMelter.recipes.add(recipe);
    }

    public boolean remove(RecipeMelter recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        RecipeMelter.recipes.remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:snow')"))
    public boolean removeByInput(IIngredient input) {
        return RecipeMelter.recipes.removeIf(recipe -> {
            if (recipe.getRecipeInput().stream().anyMatch(input)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('amber')"))
    public boolean removeByOutput(IIngredient output) {
        return RecipeMelter.recipes.removeIf(recipe -> {
            if (output.test(recipe.getOutputFluid())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        RecipeMelter.recipes.forEach(this::addBackup);
        RecipeMelter.recipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<RecipeMelter> streamRecipes() {
        return new SimpleObjectStream<>(RecipeMelter.recipes)
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "4")})
    @Property(property = "fluidOutput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeMelter> {

        @Override
        public String getErrorMsg() {
            return "Error adding Cyclic Melter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 4, 0, 0);
            validateFluids(msg, 0, 0, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeMelter register() {
            if (!validate()) return null;
            RecipeMelter recipe = null;
            List<List<ItemStack>> cartesian = Lists.cartesianProduct(input.stream().map(x -> Arrays.asList(x.toMcIngredient().getMatchingStacks())).collect(Collectors.toList()));
            for (List<ItemStack> stacks : cartesian) {
                recipe = new RecipeMelter(stacks.toArray(new ItemStack[0]), fluidOutput.get(0).getFluid().getName(), fluidOutput.get(0).amount);
                ModSupport.CYCLIC.get().melter.add(recipe);
            }
            return recipe;
        }
    }
}
