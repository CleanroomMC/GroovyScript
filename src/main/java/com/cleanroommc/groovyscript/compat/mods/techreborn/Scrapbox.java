package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import org.jetbrains.annotations.Nullable;
import techreborn.api.Reference;
import techreborn.api.recipe.machines.ScrapboxRecipe;

@RegistryDescription(override = @MethodOverride(method = @MethodDescription(method = "removeByOutput", example = @Example("item('minecraft:diamond')"))))
public class Scrapbox extends AbstractGenericTechRebornRegistry {

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:clay'))"),
            @Example(".output(item('minecraft:gold_block')).time(2).perTick(100)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public String reference() {
        return Reference.SCRAPBOX_RECIPE;
    }

    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ScrapboxRecipe> {

        @Property(comp = @Comp(gte = 0))
        private int time;
        @Property(comp = @Comp(gte = 0))
        private int perTick;

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

        @Override
        public String getErrorMsg() {
            return "Error adding Tech Reborn Scrapbox recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ScrapboxRecipe register() {
            if (!validate()) return null;
            ScrapboxRecipe recipe = new ScrapboxRecipe(output.get(0), time, perTick);
            ModSupport.TECH_REBORN.get().blastFurnace.add(recipe);
            return recipe;
        }
    }
}
