package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import net.minecraft.item.ItemStack;
import zmaster587.libVulpes.interfaces.IRecipe;

import java.util.*;

public abstract class BaseMultiblockRegistry extends BaseRegistry {

    @MethodDescription(example = @Example(priority = 2000, commented = true))
    public void removeAll() {
        List<IRecipe> recipes = getRecipeList();
        recipes.forEach(this::addBackup);
        recipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IRecipe> streamRecipes() {
        List<IRecipe> recipes = getRecipeList();
        return new SimpleObjectStream<>(recipes)
            .setRemover(this::removeRecipe);
    }

    public static abstract class RecipeBuilder extends BaseRegistry.RecipeBuilder {

        // GrS cannot recognize these annotations on the builder class itself, so we have to redefine the fields
        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"), value = "groovyscript.wiki.advancedrocketry.power.value")
        protected int power = 0;

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"), value = "groovyscript.wiki.advancedrocketry.time.value")
        protected int time = 0;

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"), value = "groovyscript.wiki.advancedrocketry.outputSize.value")
        protected int outputSize = 0;

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
