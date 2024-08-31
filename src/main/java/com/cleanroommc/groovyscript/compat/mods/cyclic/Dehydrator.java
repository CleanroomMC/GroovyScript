package com.cleanroommc.groovyscript.compat.mods.cyclic;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.lothrazar.cyclicmagic.CyclicContent;
import com.lothrazar.cyclicmagic.block.dehydrator.RecipeDeHydrate;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Dehydrator extends VirtualizedRegistry<RecipeDeHydrate> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay'))"),
            @Example(".input(ore('logWood')).output(item('minecraft:clay') * 8).time(100).water(30)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public boolean isEnabled() {
        return CyclicContent.dehydrator.enabled();
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

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeDeHydrate> {

        @Property(defaultValue = "100", comp = @Comp(gte = 0))
        private int water = 100;
        @Property(defaultValue = "10", comp = @Comp(gte = 0))
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
