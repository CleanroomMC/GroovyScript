package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import techreborn.api.reactor.FusionReactorRecipe;
import techreborn.api.reactor.FusionReactorRecipeHelper;

@RegistryDescription
public class FusionReactor extends VirtualizedRegistry<FusionReactorRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay'), item('minecraft:diamond') * 2).output(item('minecraft:gold_ingot')).time(10).perTick(-25000).start(200).size(30)"),
            @Example(".input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2).output(item('minecraft:clay') * 2).time(5).perTick(30000).start(1000000)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        FusionReactorRecipeHelper.reactorRecipes.removeAll(removeScripted());
        FusionReactorRecipeHelper.reactorRecipes.addAll(restoreFromBackup());
    }

    public void add(FusionReactorRecipe recipe) {
        FusionReactorRecipeHelper.reactorRecipes.add(recipe);
        addScripted(recipe);
    }

    public boolean remove(FusionReactorRecipe recipe) {
        addBackup(recipe);
        return FusionReactorRecipeHelper.reactorRecipes.remove(recipe);
    }

    @MethodDescription(example = @Example("item('techreborn:part:17')"))
    public boolean removeByInput(IIngredient input) {
        return FusionReactorRecipeHelper.reactorRecipes.removeIf(entry -> {
            if (input.test(entry.getTopInput()) || input.test(entry.getBottomInput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('techreborn:ore:1')"))
    public boolean removeByOutput(IIngredient output) {
        return FusionReactorRecipeHelper.reactorRecipes.removeIf(entry -> {
            if (output.test(entry.getOutput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        FusionReactorRecipeHelper.reactorRecipes.forEach(this::addBackup);
        FusionReactorRecipeHelper.reactorRecipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<FusionReactorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(FusionReactorRecipeHelper.reactorRecipes).setRemover(this::remove);
    }

    @Property(property = "input", comp = @Comp(eq = 2))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<FusionReactorRecipe> {

        @Property(comp = @Comp(gte = 0))
        private int start;
        @Property(comp = @Comp(gte = 0))
        private int time;
        @Property
        private int perTick;
        @Property(comp = @Comp(gte = 0, lte = 50))
        private int size;

        @RecipeBuilderMethodDescription
        public RecipeBuilder start(int start) {
            this.start = start;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder perTick(int perTick) {
            this.perTick = perTick;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder size(int size) {
            this.size = size;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Tech Reborn Fusion Reactor recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
            msg.add(size < 0 || size > 50, "size must greater than or equal to 0 and less than or equal to 50, yet it was {}", size);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable FusionReactorRecipe register() {
            if (!validate()) return null;
            FusionReactorRecipe recipe = null;
            for (ItemStack top : input.get(0).getMatchingStacks()) {
                for (ItemStack bottom : input.get(1).getMatchingStacks()) {
                    recipe = new FusionReactorRecipe(top, bottom, output.get(0), start, perTick, time, size);
                    ModSupport.TECH_REBORN.get().fusionReactor.add(recipe);
                }
            }
            return recipe;
        }

    }

}
