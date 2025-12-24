package com.cleanroommc.groovyscript.compat.mods.lightningcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import sblectric.lightningcraft.api.recipes.LightningInfusionRecipe;
import sblectric.lightningcraft.recipes.LightningInfusionRecipes;

import java.util.Collection;
import java.util.List;

@RegistryDescription
public class Infusion extends StandardListRegistry<LightningInfusionRecipe> {

    @Override
    public Collection<LightningInfusionRecipe> getRecipes() {
        return LightningInfusionRecipes.instance().getRecipeList();
    }

    @MethodDescription(example = @Example("item('minecraft:diamond')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @RecipeBuilderDescription(example = {
            @Example(".center(item('minecraft:clay')).input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:iron_ingot'), item('minecraft:iron_ingot')).output(item('minecraft:nether_star')).le(500)"),
            @Example(".center(item('minecraft:clay')).input(item('minecraft:gold_ingot'), item('minecraft:potion').withNbt(['Potion': 'minecraft:leaping'])).output(item('minecraft:diamond_block')).le(200)"),
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Property(property = "input", comp = @Comp(gte = 0, lte = 4))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<LightningInfusionRecipe> {

        @Property(comp = @Comp(not = "empty"))
        private IIngredient center;
        @Property(comp = @Comp(gte = 0))
        private int le;
        @Property(defaultValue = "true if any input item has nbt data")
        private boolean nbtSensitive = false;

        private boolean nbtSensitiveChanged = false;

        @RecipeBuilderMethodDescription
        public RecipeBuilder le(int le) {
            this.le = le;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "le")
        public RecipeBuilder cost(int le) {
            this.le = le;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder nbtSensitive(boolean nbtSensitive) {
            this.nbtSensitive = nbtSensitive;
            nbtSensitiveChanged = true;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder center(IIngredient center) {
            this.center = center;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding LightningCraft Infusion Table recipe";
        }

        @Override
        protected int getMaxItemInput() {
            // recipes with more than 1 item in some slot don't get recognized
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 4, 1, 1);
            validateFluids(msg);
            msg.add(IngredientHelper.isEmpty(center), "Center item must not be empty");
            msg.add(le < 0, "LE cost must be positive");
            for (IIngredient it : this.input) {
                msg.add(IngredientHelper.isEmpty(it), "All inputs must not be empty");
            }
            msg.add(IngredientHelper.overMaxSize(center, 1), "centerItem must have a stack size of 1");
            // check if any input items have NBT to enable NBT sensitive mode automatically
            if (!nbtSensitiveChanged) {
                nbtSensitive = hasAnyNbt();
            }
        }

        @GroovyBlacklist
        private boolean hasAnyNbt() {
            for (var stack : center.getMatchingStacks()) {
                if (stack.hasTagCompound()) {
                    return true;
                }
            }
            for (var i : input) {
                for (var stack : i.getMatchingStacks()) {
                    if (stack.hasTagCompound()) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable LightningInfusionRecipe register() {
            if (!validate()) return null;

            LightningInfusionRecipe recipe = null;
            for (var centerStack : center.getMatchingStacks()) {
                for (List<ItemStack> cartesianProductItemStack : IngredientHelper.cartesianProductItemStacks(input)) {
                    recipe = new LightningInfusionRecipe(output.get(0), le, centerStack, cartesianProductItemStack.toArray());
                    if (nbtSensitive) recipe.setNBTSensitive();
                    ModSupport.LIGHTNING_CRAFT.get().infusion.add(recipe);
                }
            }
            return recipe;
        }
    }
}
