package com.cleanroommc.groovyscript.compat.mods.horsepower;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import se.gory_moon.horsepower.recipes.ChoppingBlockRecipe;
import se.gory_moon.horsepower.recipes.HPRecipes;

import java.util.Collection;

@RegistryDescription
public class ChoppingBlock extends StandardListRegistry<ChoppingBlockRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond') * 5).time(5)"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:gold_ingot')).time(1)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay'), item('minecraft:diamond')).chance(50).time(2)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    // have to override onReload and add to add recipes to the registry due to the actual setup being a map
    @Override
    public Collection<ChoppingBlockRecipe> getRecipes() {
        return HPRecipes.instance().getChoppingRecipes();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        var recipes = getRecipes();
        recipes.removeAll(removeScripted());
        for (var recipe : restoreFromBackup()) {
            HPRecipes.instance().addChoppingRecipe(recipe, false);
        }
    }

    @Override
    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.add_to_list", priority = 500)
    public boolean add(ChoppingBlockRecipe recipe) {
        HPRecipes.instance().addChoppingRecipe(recipe, false);
        return recipe != null && doAddScripted(recipe);
    }

    @MethodDescription(description = "groovyscript.wiki.horsepower.chopping_block.add0", type = MethodDescription.Type.ADDITION)
    public ChoppingBlockRecipe add(IIngredient input, ItemStack output, int time) {
        return recipeBuilder()
                .time(time)
                .output(output)
                .input(input)
                .register();
    }

    @MethodDescription(description = "groovyscript.wiki.horsepower.chopping_block.add1", type = MethodDescription.Type.ADDITION)
    public ChoppingBlockRecipe add(IIngredient input, ItemStack output, ItemStack secondary, int chance, int time) {
        return recipeBuilder()
                .time(time)
                .chance(chance)
                .output(output, secondary)
                .input(input)
                .register();
    }

    @MethodDescription(example = @Example("item('minecraft:log:3')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(entry -> input.test(entry.getInput()) && doAddBackup(entry));
    }

    @MethodDescription(example = @Example("item('minecraft:planks:4')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(entry -> (output.test(entry.getOutput()) || output.test(entry.getSecondary())) && doAddBackup(entry));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 2))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ChoppingBlockRecipe> {

        @Property(comp = @Comp(gte = 0, lte = 100))
        private int chance;
        @Property(comp = @Comp(gt = 0))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(int chance) {
            this.chance = chance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Horse Power Chopping Block recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 2);
            validateFluids(msg);
            msg.add(chance < 0 || chance > 100, "chance must be a non negative integer less than 100, yet it was {}", chance);
            msg.add(time <= 0, "time must be greater than 0, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ChoppingBlockRecipe register() {
            if (!validate()) return null;
            ChoppingBlockRecipe recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = new ChoppingBlockRecipe(stack, output.get(0), output.getOrEmpty(1), chance, time);
                ModSupport.HORSE_POWER.get().choppingBlock.add(recipe);
            }
            return recipe;
        }
    }
}
