package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.RecipeInput;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import crazypants.enderio.base.recipe.sagmill.GrindingBall;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.enderio.sag_mill.note", type = Admonition.Type.WARNING))
public class SagMillGrinding extends StandardListRegistry<GrindingBall> {

    public SagMillGrinding() {
        super(Alias.generateOfClassAnd(SagMillGrinding.class, "Grinding"));
    }

    @Override
    public Collection<GrindingBall> getRecipes() {
        return SagMillRecipeManager.getInstance().getBalls();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay_ball')).chance(6.66).power(0.001).grinding(3.33).duration(10000)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:flint')"))
    public boolean remove(ItemStack grindingBall) {
        for (GrindingBall ball : getRecipes()) {
            if (ball.isInput(grindingBall)) {
                remove(ball);
                return true;
            }
        }
        return false;
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<GrindingBall> {

        @Property(defaultValue = "1", comp = @Comp(gt = 0))
        private float chance = 1;
        @Property(defaultValue = "1", comp = @Comp(gt = 0))
        private float power = 1;
        @Property(defaultValue = "1", comp = @Comp(gt = 0))
        private float grinding = 1;
        @Property(comp = @Comp(gt = 0))
        private int duration;

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(float chance) {
            this.chance = chance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder power(float power) {
            this.power = power;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder grinding(float grinding) {
            this.grinding = grinding;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding EnderIO Sag Mill Grinding Ball entry";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            msg.add(chance <= 0, "chance must be an integer greater than 0, yet it was {}", chance);
            msg.add(power <= 0, "power must be an integer greater than 0, yet it was {}", power);
            msg.add(grinding <= 0, "grinding must be an integer greater than 0, yet it was {}", grinding);
            msg.add(duration <= 0, "duration must be an integer greater than 0, yet it was {}", duration);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable GrindingBall register() {
            if (!validate()) return null;
            GrindingBall recipe = new GrindingBall(new RecipeInput(input.get(0)), grinding, chance, power, duration);
            ModSupport.ENDER_IO.get().sagMillGrinding.add(recipe);
            return recipe;
        }
    }
}
