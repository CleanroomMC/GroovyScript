package com.cleanroommc.groovyscript.compat.mods.essentialcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import essentialcraft.api.RadiatingChamberRecipe;
import essentialcraft.api.RadiatingChamberRecipes;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class RadiatingChamber extends StandardListRegistry<RadiatingChamberRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:nether_star'), item('minecraft:stone')).output(item('minecraft:beacon')).time(100).mruPerTick(10.0f).upperBalance(1.5f).lowerBalance(0.25f)"))
    public RadiatingChamber.RecipeBuilder recipeBuilder() {
        return new RadiatingChamber.RecipeBuilder();
    }

    @Override
    public Collection<RadiatingChamberRecipe> getRecipes() {
        return RadiatingChamberRecipes.RECIPES;
    }

    @MethodDescription(example = @Example("item('essentialcraft:genitem', 42)"))
    public boolean removeByOutput(IIngredient x) {
        return getRecipes().removeIf(r -> {
            if (x.test(r.getRecipeOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "2", type = Comp.Type.LTE)})
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RadiatingChamberRecipe> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"))
        private int time;

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"), defaultValue = "1.0f")
        private float mruPerTick = 1.0f;

        @Property(valid = {@Comp(type = Comp.Type.GTE, value = "0.0f"), @Comp(type = Comp.Type.LTE, value = "2.0f")})
        private float lowerBalance;

        @Property(valid = {@Comp(type = Comp.Type.GTE, value = "0.0f"), @Comp(type = Comp.Type.LTE, value = "2.0f")}, defaultValue = "2.0f")
        private float upperBalance = 2.0f;

        @RecipeBuilderMethodDescription
        public RadiatingChamber.RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RadiatingChamber.RecipeBuilder mruPerTick(float mruPerTick) {
            this.mruPerTick = mruPerTick;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RadiatingChamber.RecipeBuilder lowerBalance(float lowerBalance) {
            this.lowerBalance = lowerBalance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RadiatingChamber.RecipeBuilder upperBalance(float upperBalance) {
            this.upperBalance = upperBalance;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Radiating Chamber Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 2, 1, 1);
            validateFluids(msg);
            msg.add(time < 1, "time must be 1 or greater, got {}", time);
            msg.add(mruPerTick < 1.0f, "mru per tick must be at least 1.0f, got {}", mruPerTick);
            msg.add(lowerBalance < 0.0f || lowerBalance > 2.0f, "lower balance must be between 0.0f and 2.0f, got {}", lowerBalance);
            msg.add(upperBalance < 0.0f || upperBalance > 2.0f, "upper balance must be between 0.0f and 2.0f, got {}", upperBalance);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RadiatingChamberRecipe register() {
            if (!validate()) return null;
            Ingredient[] inputIngredient = input.stream().map(IIngredient::toMcIngredient).toArray(Ingredient[]::new);
            // the attribute names lie to the devs, they're called 'int mruRequired' and 'float costModifier'
            // but actually they're 'int timeInTicks' and 'float mruPerTick'
            RadiatingChamberRecipe recipe = new RadiatingChamberRecipe(inputIngredient, output.get(0), time, upperBalance, lowerBalance, mruPerTick);
            ModSupport.ESSENTIALCRAFT.get().radiatingChamber.addScripted(recipe);
            RadiatingChamberRecipes.addRecipe(recipe);
            return recipe;
        }
    }
}
