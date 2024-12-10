package com.cleanroommc.groovyscript.compat.mods.silentsgems;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gems.api.recipe.altar.RecipeChaosAltar;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.silentgems.chaos_altar.note0", type = Admonition.Type.WARNING))
public class ChaosAltar extends StandardListRegistry<RecipeChaosAltar> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).cost(5)"),
            @Example(".input(item('minecraft:gold_ingot') * 2).output(item('minecraft:clay')).catalyst(item('minecraft:diamond')).cost(5000)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<RecipeChaosAltar> getRecipes() {
        return RecipeChaosAltar.ALL_RECIPES;
    }

    @MethodDescription(example = @Example("item('silentgems:gem')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> input.test(r.getInput()) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:slime_ball')"))
    public boolean removeByCatalyst(IIngredient catalyst) {
        return getRecipes().removeIf(r -> catalyst.test(r.getCatalyst()) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('silentgems:craftingmaterial')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeChaosAltar> {

        @Property(defaultValue = "ItemStack.EMPTY", comp = @Comp(not = "null"))
        private ItemStack catalyst = ItemStack.EMPTY;
        @Property(comp = @Comp(gt = 0))
        private int cost;

        @RecipeBuilderMethodDescription
        public RecipeBuilder catalyst(ItemStack catalyst) {
            this.catalyst = catalyst;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder cost(int cost) {
            this.cost = cost;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Silents Gems Chaos Altar recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(catalyst == null, "catalyst must be defined");
            msg.add(cost <= 0, "cost must be greater than 0, yet it was {}", cost);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeChaosAltar register() {
            if (!validate()) return null;
            RecipeChaosAltar recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = new RecipeChaosAltar(output.get(0), stack, cost, catalyst);
                ModSupport.SILENT_GEMS.get().chaosAltar.add(recipe);
            }
            return recipe;
        }
    }
}
