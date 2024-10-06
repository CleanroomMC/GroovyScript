package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.BallOfFurReturn;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class BallOfFur extends StandardListRegistry<BallOfFurReturn> {

    @RecipeBuilderDescription(example = @Example(".output(item('minecraft:clay') * 32).weight(15)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<BallOfFurReturn> getRecipes() {
        return ActuallyAdditionsAPI.BALL_OF_FUR_RETURN_ITEMS;
    }

    public BallOfFurReturn add(ItemStack drop, int chance) {
        BallOfFurReturn recipe = new BallOfFurReturn(drop, chance);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("item('minecraft:feather')"))
    public boolean removeByOutput(ItemStack output) {
        return getRecipes().removeIf(recipe -> {
            boolean found = ItemStack.areItemStacksEqual(recipe.returnItem, output);
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<BallOfFurReturn> {

        @Property(comp = @Comp(gte = 0))
        private int weight;

        @RecipeBuilderMethodDescription
        public RecipeBuilder weight(int weight) {
            this.weight = weight;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Actually Additions Ball Of Fur recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
            msg.add(weight < 0, "weight must be a non negative integer, yet it was {}", weight);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable BallOfFurReturn register() {
            if (!validate()) return null;
            BallOfFurReturn recipe = new BallOfFurReturn(output.get(0), weight);
            ModSupport.ACTUALLY_ADDITIONS.get().ballOfFur.add(recipe);
            return recipe;
        }

    }
}
