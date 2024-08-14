package com.cleanroommc.groovyscript.compat.mods.primaltech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.primal_tech.WoodenBasinRecipesAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import primal_tech.recipes.WoodenBasinRecipes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription(
        admonition = @Admonition(type = Admonition.Type.WARNING, value = "groovyscript.wiki.primal_tech.wooden_basin.note0")
)
public class WoodenBasin extends VirtualizedRegistry<WoodenBasinRecipes> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).fluidInput(fluid('lava')).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:gold_ingot'), item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:clay')).fluidInput(fluid('water')).output(item('minecraft:diamond') * 4)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        WoodenBasinRecipesAccessor.getRecipes().removeAll(removeScripted());
        WoodenBasinRecipesAccessor.getRecipes().addAll(restoreFromBackup());
    }

    public void add(WoodenBasinRecipes recipe) {
        if (recipe != null) {
            addScripted(recipe);
            WoodenBasinRecipesAccessor.getRecipes().add(recipe);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public WoodenBasinRecipes add(ItemStack output, FluidStack fluid, IIngredient... inputs) {
        return recipeBuilder()
                .input(inputs)
                .fluidInput(fluid)
                .output(output)
                .register();
    }

    public boolean remove(WoodenBasinRecipes recipe) {
        if (WoodenBasinRecipesAccessor.getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = {@Example("fluid('lava')"), @Example(value = "item('minecraft:cobblestone')", commented = true)})
    public boolean removeByInput(IIngredient input) {
        return WoodenBasinRecipesAccessor.getRecipes().removeIf(recipe -> {
            if (input.test(recipe.getFluidStack()) ||
                Arrays.stream(recipe.getInputs()).anyMatch(x -> {
                    if (x instanceof ItemStack is) return input.test(is);
                    if (x instanceof List<?> list) return list.stream().map(i -> (ItemStack) i).anyMatch(input);
                    return false;
                })) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "item('minecraft:obsidian')", commented = true))
    public boolean removeByOutput(IIngredient output) {
        return WoodenBasinRecipesAccessor.getRecipes().removeIf(recipe -> {
            if (output.test(recipe.getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<WoodenBasinRecipes> streamRecipes() {
        return new SimpleObjectStream<>(WoodenBasinRecipesAccessor.getRecipes()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        WoodenBasinRecipesAccessor.getRecipes().forEach(this::addBackup);
        WoodenBasinRecipesAccessor.getRecipes().clear();
    }

    @Property(property = "input", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "4", type = Comp.Type.LTE)})
    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<WoodenBasinRecipes> {

        @Override
        public String getErrorMsg() {
            return "Error adding Primal Tech Wooden Basin recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 4, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable WoodenBasinRecipes register() {
            if (!validate()) return null;
            WoodenBasinRecipes recipe = null;
            List<List<Object>> cartesian = Lists.cartesianProduct(
                    input.stream().map(x -> x instanceof OreDictIngredient ore
                                            ? Collections.singletonList(ore.getOreDict())
                                            : Arrays.asList(x.toMcIngredient().getMatchingStacks()))
                            .collect(Collectors.toList()));
            for (List<Object> entry : cartesian) {
                recipe = WoodenBasinRecipesAccessor.createWoodenBasinRecipes(output.get(0), fluidInput.get(0), entry.toArray());
                ModSupport.PRIMAL_TECH.get().woodenBasin.add(recipe);
            }
            return recipe;
        }
    }
}
