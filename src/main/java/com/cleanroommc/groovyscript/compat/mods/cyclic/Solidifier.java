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
import com.lothrazar.cyclicmagic.block.solidifier.RecipeSolidifier;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Solidifier extends VirtualizedRegistry<RecipeSolidifier> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).fluidInput(fluid('water') * 175).output(item('minecraft:gold_ingot') * 3)"),
            @Example(".input(ore('logWood'), ore('sand'), ore('gravel'), item('minecraft:diamond')).fluidInput(fluid('lava') * 500).output(item('minecraft:clay') * 2)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public boolean isEnabled() {
        return CyclicContent.solidifier.enabled();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        RecipeSolidifier.recipes.removeAll(removeScripted());
        RecipeSolidifier.recipes.addAll(restoreFromBackup());
    }

    public void add(RecipeSolidifier recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        RecipeSolidifier.recipes.add(recipe);
    }

    public boolean remove(RecipeSolidifier recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        RecipeSolidifier.recipes.remove(recipe);
        return true;
    }

    @MethodDescription(example = {@Example("item('minecraft:bucket')"), @Example("fluid('water')"),})
    public boolean removeByInput(IIngredient input) {
        return RecipeSolidifier.recipes.removeIf(recipe -> {
            if (input.test(recipe.getFluidIngredient()) || recipe.getRecipeInput().stream().anyMatch(input)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('cyclicmagic:crystallized_obsidian')"))
    public boolean removeByOutput(IIngredient output) {
        return RecipeSolidifier.recipes.removeIf(recipe -> {
            if (output.test(recipe.getRecipeOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        RecipeSolidifier.recipes.forEach(this::addBackup);
        RecipeSolidifier.recipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<RecipeSolidifier> streamRecipes() {
        return new SimpleObjectStream<>(RecipeSolidifier.recipes)
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "4")})
    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeSolidifier> {

        @Override
        public String getErrorMsg() {
            return "Error adding Cyclic Solidifier recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 4, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeSolidifier register() {
            if (!validate()) return null;
            RecipeSolidifier recipe = null;
            List<List<ItemStack>> cartesian = Lists.cartesianProduct(input.stream().map(x -> Arrays.asList(x.toMcIngredient().getMatchingStacks())).collect(Collectors.toList()));
            for (List<ItemStack> stacks : cartesian) {
                recipe = new RecipeSolidifier(stacks.toArray(new ItemStack[0]), output.get(0), fluidInput.get(0).getFluid().getName(), fluidInput.get(0).amount);
                ModSupport.CYCLIC.get().solidifier.add(recipe);
            }
            return recipe;
        }
    }
}
