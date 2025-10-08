package com.cleanroommc.groovyscript.compat.mods.armorplus;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.armorplus.LavaInfuserManagerAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.sofodev.armorplus.api.lavainfuser.LavaInfuserManager;
import com.sofodev.armorplus.common.compat.crafttweaker.lavainfuser.LavaInfuserRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription(admonition = @Admonition(type = Admonition.Type.WARNING, value = "groovyscript.wiki.armorplus.lava_infuser.note0"))
public class LavaInfuser extends StandardListRegistry<LavaInfuserRecipe> {

    @Override
    public Collection<LavaInfuserRecipe> getRecipes() {
        return LavaInfuserManager.getInstance().getRecipeList();
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay') * 2)"),
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).experience(5.0d)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void afterScriptLoad() {
        // we have to store duplicates of all the values
        // but still iterate through everything the exact same way.
        var instance = LavaInfuserManager.getInstance();
        var infusing = instance.getInfusingList();
        var experience = ((LavaInfuserManagerAccessor) instance).getExperienceList();
        infusing.clear();
        experience.clear();
        for (var recipe : getRecipes()) {
            infusing.put(recipe.input, recipe.output);
            // you may be thinking "won't this have the potential to override the xp value of prior recipes"
            // yup, that it does. although this actually just copies this problem from the vanilla furnace.
            experience.put(recipe.output, recipe.xp);
        }
    }

    @MethodDescription(example = @Example("item('armorplus:lava_crystal')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(x -> input.test(x.input));
    }

    @MethodDescription(example = @Example("item('armorplus:lava_infused_obsidian')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(x -> output.test(x.output));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<LavaInfuserRecipe> {

        @Property(comp = @Comp(gte = 0))
        private double experience;

        @RecipeBuilderMethodDescription
        public RecipeBuilder experience(double experience) {
            this.experience = experience;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Armor Plus Lava Infuser recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(experience < 0.0d, "experience must be a double greater than or equal to 0, yet it was {}", experience);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable LavaInfuserRecipe register() {
            if (!validate()) return null;
            LavaInfuserRecipe recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = new LavaInfuserRecipe(stack, output.get(0), experience);
                ModSupport.ARMOR_PLUS.get().lavaInfuser.add(recipe);
            }
            return recipe;
        }
    }
}
