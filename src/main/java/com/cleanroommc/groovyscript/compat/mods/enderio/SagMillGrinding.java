package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.RecipeInput;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import crazypants.enderio.base.recipe.sagmill.GrindingBall;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class SagMillGrinding extends VirtualizedRegistry<GrindingBall> {

    public SagMillGrinding() {
        super(Alias.generateOfClassAnd(SagMillGrinding.class, "Grinding"));
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay_ball')).chance(6.66).power(0.001).grinding(3.33).duration(10000)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(GrindingBall recipe) {
        SagMillRecipeManager.getInstance().addBall(recipe);
        addScripted(recipe);
    }

    public boolean remove(GrindingBall recipe) {
        if (recipe == null) return false;
        SagMillRecipeManager.getInstance().getBalls().remove(recipe);
        addBackup(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:flint')"))
    public boolean remove(ItemStack grindingBall) {
        for (GrindingBall ball : SagMillRecipeManager.getInstance().getBalls()) {
            if (ball.isInput(grindingBall)) {
                remove(ball);
                return true;
            }
        }
        return false;
    }

    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(SagMillRecipeManager.getInstance().getBalls()::remove);
        restoreFromBackup().forEach(SagMillRecipeManager.getInstance().getBalls()::add);
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<GrindingBall> streamRecipes() {
        return new SimpleObjectStream<>(SagMillRecipeManager.getInstance().getBalls())
                .setRemover(this::remove);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        SagMillRecipeManager.getInstance().getBalls().forEach(this::addBackup);
        SagMillRecipeManager.getInstance().getBalls().clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<GrindingBall> {

        @Property(defaultValue = "1", valid = @Comp(value = "0", type = Comp.Type.GT))
        private float chance = 1;
        @Property(defaultValue = "1", valid = @Comp(value = "0", type = Comp.Type.GT))
        private float power = 1;
        @Property(defaultValue = "1", valid = @Comp(value = "0", type = Comp.Type.GT))
        private float grinding = 1;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GT))
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
