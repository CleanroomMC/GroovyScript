package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import net.minecraft.item.ItemStack;
import zmaster587.libVulpes.interfaces.IRecipe;

import java.util.*;

public abstract class BaseMultiblockRegistry extends BaseRegistry {

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IRecipe> streamRecipes() {
        List<IRecipe> recipes = getRecipeList();
        return new SimpleObjectStream<>(recipes)
            .setRemover(this::removeRecipe);
    }

    @Property(property = "power", valid = @Comp(type = Comp.Type.GTE, value = "1"), value = "groovyscript.wiki.advancedrocketry.power.value", hierarchy = 5)
    @Property(property = "time", valid = @Comp(type = Comp.Type.GTE, value = "1"), value = "groovyscript.wiki.advancedrocketry.time.value", hierarchy = 5)
    @Property(property = "outputSize", valid = @Comp(type = Comp.Type.GTE, value = "1"), value = "groovyscript.wiki.advancedrocketry.outputSize.value", hierarchy = 5)
    public static abstract class RecipeBuilder extends BaseRegistry.RecipeBuilder {

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder power(int power) {
            this.power = power;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder outputSize(int outputSize) {
            this.outputSize = outputSize;
            return this;
        }

        @Override
        @RecipeBuilderMethodDescription
        public BaseRegistry.RecipeBuilder output(ItemStack output) {
            return output(output, 0.0f);
        }

        @Override
        @RecipeBuilderMethodDescription
        public BaseRegistry.RecipeBuilder output(ItemStack output, float chance) {
            return super.output(output, chance);
        }
    }

}
