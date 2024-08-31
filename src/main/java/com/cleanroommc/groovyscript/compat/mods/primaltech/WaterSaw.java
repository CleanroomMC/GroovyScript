package com.cleanroommc.groovyscript.compat.mods.primaltech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.primal_tech.WaterSawRecipesAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import primal_tech.recipes.WaterSawRecipes;

@RegistryDescription
public class WaterSaw extends VirtualizedRegistry<WaterSawRecipes> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay')).choppingTime(50)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond') * 4).choppingTime(100)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        WaterSawRecipesAccessor.getRecipes().removeAll(removeScripted());
        WaterSawRecipesAccessor.getRecipes().addAll(restoreFromBackup());
    }

    public void add(WaterSawRecipes recipe) {
        if (recipe != null) {
            addScripted(recipe);
            WaterSawRecipesAccessor.getRecipes().add(recipe);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public WaterSawRecipes add(ItemStack output, IIngredient input, int choppingTime) {
        return recipeBuilder()
                .choppingTime(choppingTime)
                .input(input)
                .output(output)
                .register();
    }

    public boolean remove(WaterSawRecipes recipe) {
        if (WaterSawRecipesAccessor.getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:log')"))
    public boolean removeByInput(IIngredient input) {
        return WaterSawRecipesAccessor.getRecipes().removeIf(recipe -> {
            if (input.test(recipe.getInput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:planks:1')"))
    public boolean removeByOutput(IIngredient output) {
        return WaterSawRecipesAccessor.getRecipes().removeIf(recipe -> {
            if (output.test(recipe.getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<WaterSawRecipes> streamRecipes() {
        return new SimpleObjectStream<>(WaterSawRecipesAccessor.getRecipes()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        WaterSawRecipesAccessor.getRecipes().forEach(this::addBackup);
        WaterSawRecipesAccessor.getRecipes().clear();
    }

    @Property(property = "input", comp = @Comp(types = Comp.Type.EQ, eq = 1))
    @Property(property = "output", comp = @Comp(types = Comp.Type.EQ, eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<WaterSawRecipes> {

        @Property(comp = @Comp(types = Comp.Type.GTE))
        private int choppingTime;

        @RecipeBuilderMethodDescription
        public RecipeBuilder choppingTime(int choppingTime) {
            this.choppingTime = choppingTime;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Primal Tech Water Saw recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(choppingTime < 0, "choppingTime must be greater than or equal to 0, yet it was {}", choppingTime);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable WaterSawRecipes register() {
            if (!validate()) return null;
            WaterSawRecipes recipe = null;
            for (ItemStack matchingStack : input.get(0).getMatchingStacks()) {
                recipe = WaterSawRecipesAccessor.createWaterSawRecipes(output.get(0), matchingStack, choppingTime);
                ModSupport.PRIMAL_TECH.get().waterSaw.add(recipe);
            }
            return recipe;
        }
    }
}
