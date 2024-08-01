package com.cleanroommc.groovyscript.compat.mods.cyclic;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.IJEIRemoval;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.OperationHandler;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.collect.ImmutableList;
import com.lothrazar.cyclicmagic.block.dehydrator.RecipeDeHydrate;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class Dehydrator extends VirtualizedRegistry<RecipeDeHydrate> implements IJEIRemoval.Default {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay'))"),
            @Example(".input(ore('logWood')).output(item('minecraft:clay') * 8).time(100).water(30)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        RecipeDeHydrate.recipes.removeAll(removeScripted());
        RecipeDeHydrate.recipes.addAll(restoreFromBackup());
    }

    public void add(RecipeDeHydrate recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        RecipeDeHydrate.recipes.add(recipe);
    }

    public boolean remove(RecipeDeHydrate recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        RecipeDeHydrate.recipes.remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:clay')"))
    public boolean removeByInput(IIngredient input) {
        return RecipeDeHydrate.recipes.removeIf(recipe -> {
            if (input.test(recipe.getRecipeInput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:deadbush')"))
    public boolean removeByOutput(IIngredient output) {
        return RecipeDeHydrate.recipes.removeIf(recipe -> {
            if (output.test(recipe.getRecipeOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        RecipeDeHydrate.recipes.forEach(this::addBackup);
        RecipeDeHydrate.recipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<RecipeDeHydrate> streamRecipes() {
        return new SimpleObjectStream<>(RecipeDeHydrate.recipes)
                .setRemover(this::remove);
    }

    /**
     * @see com.lothrazar.cyclicmagic.compat.jei.JEIPlugin
     */
    @Override
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList("dehydrator");
    }

    @Override
    public @NotNull List<OperationHandler.IOperation> getJEIOperations() {
        return ImmutableList.of(OperationHandler.ItemOperation.defaultItemOperation(), OperationHandler.FluidOperation.defaultFluidOperation().exclude(0));
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeDeHydrate> {

        @Property(defaultValue = "100", valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int water = 100;
        @Property(defaultValue = "10", valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int time = 10;

        @RecipeBuilderMethodDescription
        public RecipeBuilder water(int water) {
            this.water = water;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Cyclic Dehydrator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(water < 0, "water must be a non-negative integer, yet it was {}", water);
            msg.add(time < 0, "time must be a non-negative integer, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeDeHydrate register() {
            if (!validate()) return null;
            RecipeDeHydrate recipe = null;
            for (ItemStack matchingStack : input.get(0).toMcIngredient().getMatchingStacks()) {
                recipe = new RecipeDeHydrate(matchingStack, output.get(0), time, water);
                ModSupport.CYCLIC.get().dehydrator.add(recipe);
            }
            return recipe;
        }
    }
}
