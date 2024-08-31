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
import com.lothrazar.cyclicmagic.block.packager.RecipePackager;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Packager extends VirtualizedRegistry<RecipePackager> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay'))"),
            @Example(".input(ore('logWood'), ore('sand'), ore('gravel'), item('minecraft:diamond'), item('minecraft:diamond_block'), item('minecraft:gold_block')).output(item('minecraft:clay') * 4)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public boolean isEnabled() {
        return CyclicContent.packager.enabled();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        RecipePackager.recipes.removeAll(removeScripted());
        RecipePackager.recipes.addAll(restoreFromBackup());
    }

    public void add(RecipePackager recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        RecipePackager.recipes.add(recipe);
    }

    public boolean remove(RecipePackager recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        RecipePackager.recipes.remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:grass')"))
    public boolean removeByInput(IIngredient input) {
        return RecipePackager.recipes.removeIf(recipe -> {
            if (recipe.getInput().stream().anyMatch(input)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:melon_block')"))
    public boolean removeByOutput(IIngredient output) {
        return RecipePackager.recipes.removeIf(recipe -> {
            if (output.test(recipe.getRecipeOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        RecipePackager.recipes.forEach(this::addBackup);
        RecipePackager.recipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<RecipePackager> streamRecipes() {
        return new SimpleObjectStream<>(RecipePackager.recipes)
                .setRemover(this::remove);
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 6))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipePackager> {

        @Override
        public String getErrorMsg() {
            return "Error adding Cyclic Packager recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 6, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipePackager register() {
            if (!validate()) return null;
            RecipePackager recipe = null;
            List<List<ItemStack>> cartesian = Lists.cartesianProduct(input.stream().map(x -> Arrays.asList(x.toMcIngredient().getMatchingStacks())).collect(Collectors.toList()));
            for (List<ItemStack> stacks : cartesian) {
                recipe = new RecipePackager(output.get(0), stacks.toArray(new ItemStack[0]));
                ModSupport.CYCLIC.get().packager.add(recipe);
            }
            return recipe;
        }
    }
}
