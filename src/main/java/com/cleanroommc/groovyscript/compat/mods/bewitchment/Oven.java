package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.registry.OvenRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Oven extends ForgeRegistryWrapper<OvenRecipe> {

    public Oven() {
        super(GameRegistry.findRegistry(OvenRecipe.class));
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).requiresJar(false).byproduct(item('minecraft:gold_nugget')).byproductChance(0.2f)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:sapling')"))
    public void removeByInput(IIngredient input) {
        getRegistry().forEach(recipe -> {
            if (input.test(recipe.input)) {
                remove(recipe);
            }
        });
    }

    @MethodDescription(example = {
            @Example("item('bewitchment:garlic_grilled')"), @Example("item('bewitchment:tallow')")
    })
    public void removeByOutput(IIngredient output) {
        getRegistry().forEach(recipe -> {
            if (output.test(recipe.output) || output.test(recipe.byproduct)) {
                remove(recipe);
            }
        });
    }

    @Property(property = "name")
    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<OvenRecipe> {

        @Property(defaultValue = "ItemStack.EMPTY", comp = @Comp(not = "null"))
        private ItemStack byproduct = ItemStack.EMPTY;
        @Property(comp = @Comp(gte = 0))
        private float byproductChance;
        @Property
        private boolean requiresJar;

        @RecipeBuilderMethodDescription
        public RecipeBuilder byproduct(ItemStack byproduct) {
            this.byproduct = byproduct;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder byproductChance(float byproductChance) {
            this.byproductChance = byproductChance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder requiresJar(boolean requiresJar) {
            this.requiresJar = requiresJar;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Oven Recipe";
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_oven_recipe_";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(byproduct == null, "byproduct must not be null, but it was null");
            msg.add(byproductChance < 0, "byproductChance must be greater than or equal to 0, yet it was {}", byproductChance);
            validateName();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable OvenRecipe register() {
            if (!validate()) return null;
            OvenRecipe recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = new OvenRecipe(super.name, stack, output.get(0), byproduct, byproductChance, requiresJar);
                ModSupport.BEWITCHMENT.get().oven.add(recipe);
            }
            return recipe;
        }
    }
}
