package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.config.ModConfig;
import com.blakebr0.extendedcrafting.crafting.CombinationRecipe;
import com.blakebr0.extendedcrafting.crafting.CombinationRecipeManager;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription(
        admonition = @Admonition(value = "groovyscript.wiki.extendedcrafting.combination_crafting.note0", type = Admonition.Type.INFO, format = Admonition.Format.STANDARD)
)
public class CombinationCrafting extends StandardListRegistry<CombinationRecipe> {

    public CombinationCrafting() {
        super(Alias.generateOfClassAnd(CombinationCrafting.class, "Combination"));
    }

    @Override
    public Collection<CombinationRecipe> getRecipes() {
        return CombinationRecipeManager.getInstance().getRecipes();
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.combination_crafting.add0", type = MethodDescription.Type.ADDITION)
    public CombinationRecipe add(ItemStack output, long cost, Ingredient input, NonNullList<Ingredient> pedestals) {
        return add(output, cost, ModConfig.confCraftingCoreRFRate, input, pedestals);
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.combination_crafting.add1", type = MethodDescription.Type.ADDITION)
    public CombinationRecipe add(ItemStack output, long cost, int perTick, Ingredient input, NonNullList<Ingredient> pedestals) {
        CombinationRecipe recipe = new CombinationRecipe(output, cost, perTick, input, pedestals);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example(value = "item('minecraft:gold_ingot')", commented = true))
    public boolean removeByOutput(ItemStack output) {
        return getRecipes().removeIf(r -> {
            if (r.getOutput().equals(output)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "item('minecraft:pumpkin')", commented = true))
    public boolean removeByInput(ItemStack input) {
        return getRecipes().removeIf(r -> {
            if (r.getInput().equals(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription
    public boolean removeByInput(IIngredient input) {
        return removeByInput(IngredientHelper.toItemStack(input));
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:pumpkin')).pedestals(item('minecraft:pumpkin') * 8).output(item('minecraft:diamond') * 2).cost(100).perTick(100)"),
            @Example(".input(item('minecraft:pumpkin')).pedestals(item('minecraft:pumpkin'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:pumpkin')).output(item('minecraft:gold_ingot') * 2).cost(10000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CombinationRecipe> {

        @Property
        private final NonNullList<IIngredient> pedestals = NonNullList.create();
        @Property(comp = @Comp(gte = 0))
        private long cost;
        @Property(defaultValue = "ModConfig.confCraftingCoreRFRate", comp = @Comp(gte = 0))
        private int perTick = ModConfig.confCraftingCoreRFRate;

        @RecipeBuilderMethodDescription
        public RecipeBuilder cost(long cost) {
            this.cost = cost;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "cost")
        public RecipeBuilder totalCost(long totalCost) {
            return cost(totalCost);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder perTick(int perTick) {
            this.perTick = perTick;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "perTick")
        public RecipeBuilder costPerTick(int costPerTick) {
            return perTick(costPerTick);
        }

        @Override
        @RecipeBuilderMethodDescription
        public RecipeBuilder input(IIngredient ingredient) {
            this.input.add(ingredient.withAmount(1));
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder pedestals(IIngredient pedestals) {
            for (int x = 0; x < pedestals.getAmount(); x++) {
                this.pedestals.add(pedestals.withAmount(1));
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder pedestals(Collection<IIngredient> pedestals) {
            for (IIngredient x : pedestals) {
                this.pedestals(x);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder pedestals(IIngredient... pedestals) {
            for (IIngredient x : pedestals) {
                this.pedestals(x);
            }
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Extended Crafting Combination Crafting recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(cost < 0, () -> "cost must not be negative");
            msg.add(perTick < 0, () -> "per tick must not be negative");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CombinationRecipe register() {
            if (!validate()) return null;
            CombinationRecipe recipe = new CombinationRecipe(output.get(0), cost, perTick, input.get(0).toMcIngredient(), IngredientHelper.toIngredientNonNullList(pedestals));
            ModSupport.EXTENDED_CRAFTING.get().combinationCrafting.add(recipe);
            return recipe;
        }
    }
}
