package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.config.ModConfig;
import com.blakebr0.extendedcrafting.crafting.CompressorRecipe;
import com.blakebr0.extendedcrafting.crafting.CompressorRecipeManager;
import com.blakebr0.extendedcrafting.item.ItemSingularity;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class CompressionCrafting extends VirtualizedRegistry<CompressorRecipe> {

    public CompressionCrafting() {
        super(Alias.generateOfClassAnd(CompressionCrafting.class, "Compression"));
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> CompressorRecipeManager.getInstance().getRecipes().removeIf(r -> r == recipe));
        CompressorRecipeManager.getInstance().getRecipes().addAll(restoreFromBackup());
    }

    public CompressorRecipe add(CompressorRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            CompressorRecipeManager.getInstance().getRecipes().add(recipe);
        }
        return recipe;
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.compression_crafting.add0", type = MethodDescription.Type.ADDITION)
    public CompressorRecipe add(ItemStack output, IIngredient input, int inputCount, IIngredient catalyst, boolean consumeCatalyst, int powerCost) {
        return add(output, input, inputCount, catalyst, consumeCatalyst, powerCost, ModConfig.confCompressorRFRate);
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.compression_crafting.add1", type = MethodDescription.Type.ADDITION)
    public CompressorRecipe add(ItemStack output, IIngredient input, int inputCount, IIngredient catalyst, boolean consumeCatalyst, int powerCost, int powerRate) {
        return add(new CompressorRecipe(output, input.toMcIngredient(), inputCount, catalyst.toMcIngredient(), consumeCatalyst, powerCost, powerRate));
    }

    @MethodDescription(example = @Example("item('extendedcrafting:singularity:6')"))
    public boolean removeByOutput(ItemStack output) {
        return CompressorRecipeManager.getInstance().getRecipes().removeIf(r -> {
            if (r.getOutput().equals(output)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('extendedcrafting:material:11')"))
    public boolean removeByCatalyst(IIngredient catalyst) {
        return CompressorRecipeManager.getInstance().getRecipes().removeIf(r -> {
            if (r.getCatalyst().equals(catalyst.toMcIngredient())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:gold_ingot')"))
    public boolean removeByInput(IIngredient input) {
        return CompressorRecipeManager.getInstance().getRecipes().removeIf(r -> {
            if (r.getInput().equals(input.toMcIngredient())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean remove(CompressorRecipe recipe) {
        if (CompressorRecipeManager.getInstance().getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<CompressorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(CompressorRecipeManager.getInstance().getRecipes()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        CompressorRecipeManager.getInstance().getRecipes().forEach(this::addBackup);
        CompressorRecipeManager.getInstance().getRecipes().clear();
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).inputCount(100).output(item('minecraft:gold_ingot') * 7).catalyst(item('minecraft:diamond')).consumeCatalyst(true).powerCost(10000).powerRate(1000)"),
            @Example(".input(item('minecraft:clay') * 10).output(item('minecraft:diamond') * 2).powerCost(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "input", valid = @Comp(type = Comp.Type.NOT, value = "null"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CompressorRecipe> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "0"))
        private int inputCount;
        @Property(defaultValue = "IngredientHelper.toIIngredient(ItemSingularity.getCatalystStack())", valid = @Comp(type = Comp.Type.NOT, value = "null"))
        private IIngredient catalyst = IngredientHelper.toIIngredient(ItemSingularity.getCatalystStack());
        @Property
        private boolean consumeCatalyst = false;
        @Property(valid = @Comp(type = Comp.Type.GTE, value = "0"))
        private int powerCost;
        @Property(defaultValue = "ModConfig.confCompressorRFRate", valid = @Comp(type = Comp.Type.GTE, value = "0"))
        private int powerRate = ModConfig.confCompressorRFRate;

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(IIngredient input) {
            if (input == null) return this;
            if (input.getAmount() > 1) {
                this.inputCount = input.getAmount();
            }
            this.input.add(input.withAmount(1));
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inputCount(int inputCount) {
            this.inputCount = inputCount;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder catalyst(IIngredient catalyst) {
            this.catalyst = catalyst.withAmount(1);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder consumeCatalyst(boolean consumeCatalyst) {
            this.consumeCatalyst = consumeCatalyst;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder powerCost(int powerCost) {
            this.powerCost = powerCost;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder powerRate(int powerRate) {
            this.powerRate = powerRate;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Extended Crafting Compression Crafting recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);

            msg.add(IngredientHelper.isEmpty(catalyst), "catalyst must not be empty");
            msg.add(powerCost < 0, "power cost must not be negative");
            msg.add(powerRate < 0, "power rate must not be negative");
        }

        @Nullable
        @Override
        @RecipeBuilderRegistrationMethod
        public CompressorRecipe register() {
            if (!validate()) return null;
            CompressorRecipe recipe = new CompressorRecipe(output.get(0), input.get(0).toMcIngredient(), inputCount, catalyst.toMcIngredient(), consumeCatalyst, powerCost, powerRate);
            ModSupport.EXTENDED_CRAFTING.get().compressionCrafting.add(recipe);
            return recipe;
        }
    }
}
