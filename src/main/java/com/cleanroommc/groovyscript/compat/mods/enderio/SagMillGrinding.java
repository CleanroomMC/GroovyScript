package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
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

public class SagMillGrinding extends VirtualizedRegistry<GrindingBall> {

    public SagMillGrinding() {
        super(Alias.generateOf("Grinding"));
    }

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

    public SimpleObjectStream<GrindingBall> streamRecipes() {
        return new SimpleObjectStream<>(SagMillRecipeManager.getInstance().getBalls())
                .setRemover(this::remove);
    }

    public void removeAll() {
        SagMillRecipeManager.getInstance().getBalls().forEach(this::addBackup);
        SagMillRecipeManager.getInstance().getBalls().clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<GrindingBall> {

        private float chance = 1;
        private float power = 1;
        private float grinding = 1;
        private int duration;

        public RecipeBuilder chance(float chance) {
            this.chance = chance;
            return this;
        }

        public RecipeBuilder power(float power) {
            this.power = power;
            return this;
        }

        public RecipeBuilder grinding(float grinding) {
            this.grinding = grinding;
            return this;
        }

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
        public @Nullable GrindingBall register() {
            if (!validate()) return null;
            GrindingBall recipe = new GrindingBall(new RecipeInput(input.get(0)), grinding, chance, power, duration);
            ModSupport.ENDER_IO.get().sagMillGrinding.add(recipe);
            return recipe;
        }
    }
}
