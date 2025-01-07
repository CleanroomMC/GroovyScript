package com.cleanroommc.groovyscript.compat.mods.betweenlands;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thebetweenlands.DruidAltarRecipeAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.recipes.IDruidAltarRecipe;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;

import java.util.Collection;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.thebetweenlands.druid_altar.note0", type = Admonition.Type.WARNING))
public class DruidAltar extends StandardListRegistry<IDruidAltarRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:diamond'), item('minecraft:gold_block'), item('minecraft:gold_ingot'), item('minecraft:clay')).output(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<IDruidAltarRecipe> getRecipes() {
        return DruidAltarRecipeAccessor.getRecipes();
    }

    @MethodDescription(example = @Example(value = "item('thebetweenlands:swamp_talisman:1')", commented = true))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> r instanceof DruidAltarRecipe recipe && recipe.getInputs().stream().anyMatch(input) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('thebetweenlands:swamp_talisman')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> r instanceof DruidAltarRecipe recipe && output.test(recipe.getDefaultOutput()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 4))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IDruidAltarRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Betweenlands Druid Altar recipe";
        }

        @Override
        @GroovyBlacklist
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 4, 4, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IDruidAltarRecipe register() {
            if (!validate()) return null;
            IDruidAltarRecipe recipe = null;
            for (var input1 : input.get(0).getMatchingStacks()) {
                for (var input2 : input.get(1).getMatchingStacks()) {
                    for (var input3 : input.get(2).getMatchingStacks()) {
                        for (var input4 : input.get(3).getMatchingStacks()) {
                            recipe = new DruidAltarRecipe(input1, input2, input3, input4, output.get(0));
                            ModSupport.BETWEENLANDS.get().druidAltar.add(recipe);
                        }
                    }
                }
            }
            return recipe;
        }
    }
}
