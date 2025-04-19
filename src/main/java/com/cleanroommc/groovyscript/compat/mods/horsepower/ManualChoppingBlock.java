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
import se.gory_moon.horsepower.recipes.ManualChoppingBlockRecipe;

import java.util.Collection;

@RegistryDescription
public class ManualChoppingBlock extends StandardListRegistry<ChoppingBlockRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond') * 5).time(5)"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:gold_ingot')).time(1)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    // have to override onReload and add to add recipes to the registry due to the actual setup being a map
    @Override
    public Collection<ChoppingBlockRecipe> getRecipes() {
        return HPRecipes.instance().getManualChoppingRecipes();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        var recipes = getRecipes();
        recipes.removeAll(removeScripted());
        for (var recipe : restoreFromBackup()) {
            HPRecipes.instance().addChoppingRecipe(recipe, true);
        }
    }

    @Override
    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.add_to_list", priority = 500)
    public boolean add(ChoppingBlockRecipe recipe) {
        HPRecipes.instance().addChoppingRecipe(recipe, true);
        return recipe != null && doAddScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public ChoppingBlockRecipe add(IIngredient input, ItemStack output, int time) {
        return recipeBuilder()
                .time(time)
                .output(output)
                .input(input)
                .register();
    }

    @MethodDescription(example = @Example("item('minecraft:log:3')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(entry -> input.test(entry.getInput()) && doAddBackup(entry));
    }

    @MethodDescription(example = @Example("item('minecraft:planks:4')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(entry -> output.test(entry.getOutput()) && doAddBackup(entry));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ChoppingBlockRecipe> {

        @Property(comp = @Comp(gt = 0))
        private int time;

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
            return "Error adding Horse Power Manual Chopping Block recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 2);
            validateFluids(msg);
            msg.add(time <= 0, "time must be greater than 0, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ChoppingBlockRecipe register() {
            if (!validate()) return null;
            ChoppingBlockRecipe recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = new ManualChoppingBlockRecipe(stack, output.get(0), ItemStack.EMPTY, 0, time);
                ModSupport.HORSE_POWER.get().manualChoppingBlock.add(recipe);
            }
            return recipe;
        }
    }
}
