package com.cleanroommc.groovyscript.compat.mods.railcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.IGenRule;
import mods.railcraft.api.crafting.IOutputEntry;
import mods.railcraft.api.crafting.IRockCrusherCrafter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@RegistryDescription
public class RockCrusher extends StandardListRegistry<IRockCrusherCrafter.IRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:stone')).output(item('minecraft:cobblestone'), 1.0).output(item('minecraft:sand'), 0.5).time(200)"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<IRockCrusherCrafter.IRecipe> getRecipes() {
        return Crafters.rockCrusher().getRecipes();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public IRockCrusherCrafter.IRecipe add(IIngredient input, List<ItemStack> outputs, List<Float> chances, int time) {
        if (time <= 0) {
            GroovyLog.msg("Error adding Railcraft Rock Crusher recipe")
                    .error()
                    .add("time must be greater than 0, got: {}", time)
                    .post();
            return null;
        }
        if (outputs == null || outputs.isEmpty()) {
            GroovyLog.msg("Error adding Railcraft Rock Crusher recipe")
                    .error()
                    .add("outputs must not be empty")
                    .post();
            return null;
        }
        if (chances == null || chances.isEmpty()) {
            GroovyLog.msg("Error adding Railcraft Rock Crusher recipe")
                    .error()
                    .add("chances must not be empty")
                    .post();
            return null;
        }
        RecipeBuilder builder = recipeBuilder();
        builder.input(input);
        builder.time = time;
        for (int i = 0; i < outputs.size() && i < chances.size(); i++) {
            builder.output(outputs.get(i), chances.get(i));
        }
        return builder.register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public IRockCrusherCrafter.IRecipe add(IIngredient input, List<ItemStack> outputs, List<Float> chances) {
        return add(input, outputs, chances, IRockCrusherCrafter.PROCESS_TIME);
    }

    @MethodDescription(example = @Example("item('minecraft:cobblestone')"))
    public void removeByOutput(IIngredient output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Railcraft Rock Crusher recipe")
                    .error()
                    .add("output must not be empty")
                    .post();
            return;
        }
        ItemStack outputStack = output.getMatchingStacks()[0];
        if (!getRecipes().removeIf(recipe -> {
            for (IOutputEntry entry : recipe.getOutputs()) {
                if (ItemStack.areItemStacksEqual(entry.getOutput(), outputStack)) {
                    addBackup(recipe);
                    return true;
                }
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Railcraft Rock Crusher recipe")
                    .error()
                    .add("no recipes found for {}", output)
                    .post();
        }
    }

    @MethodDescription(example = @Example("item('minecraft:stone')"))
    public boolean removeByInput(IIngredient input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Railcraft Rock Crusher recipe")
                    .error()
                    .add("input must not be empty")
                    .post();
            return false;
        }
        return getRecipes().removeIf(recipe -> {
            for (ItemStack stack : input.getMatchingStacks()) {
                if (recipe.getInput().test(stack)) {
                    addBackup(recipe);
                    return true;
                }
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IRockCrusherCrafter.IRecipe> {

        @Property(comp = @Comp(gte = 0), defaultValue = "IRockCrusherCrafter.PROCESS_TIME")
        private int time = IRockCrusherCrafter.PROCESS_TIME;
        private final List<IOutputEntry> outputs = new ArrayList<>();

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder output(ItemStack output, float chance) {
            if (!IngredientHelper.isEmpty(output)) {
                outputs.add(new OutputEntry(output.copy(), new RandomChanceGenRule(chance)));
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder output(ItemStack output, IGenRule rule) {
            if (!IngredientHelper.isEmpty(output)) {
                outputs.add(new OutputEntry(output.copy(), rule));
            }
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Railcraft Rock Crusher recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            if (time <= 0) {
                msg.add("time must be greater than 0, got: {}", time);
                time = IRockCrusherCrafter.PROCESS_TIME;
            }
            if (outputs.isEmpty()) {
                msg.add("At least one output must be defined");
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IRockCrusherCrafter.IRecipe register() {
            if (!validate()) return null;
            Ingredient inputIngredient = input.get(0).toMcIngredient();
            List<IOutputEntry> outputsCopy = new ArrayList<>(outputs);

            IRockCrusherCrafter.IRecipe recipe = new IRockCrusherCrafter.IRecipe() {

                @Override
                public ResourceLocation getName() {
                    return new ResourceLocation("groovyscript", "rockcrusher_" + System.currentTimeMillis());
                }

                @Override
                public Ingredient getInput() {
                    return inputIngredient;
                }

                @Override
                public List<IOutputEntry> getOutputs() {
                    return outputsCopy;
                }

                @Override
                public int getTickTime(ItemStack input) {
                    return time;
                }
            };

            ModSupport.RAILCRAFT.get().rockCrusher.add(recipe);
            return recipe;
        }
    }

    private static class RandomChanceGenRule implements IGenRule {

        private final float randomChance;
        private List<ITextComponent> toolTip;

        RandomChanceGenRule(float randomChance) {
            this.randomChance = randomChance;
        }

        @Override
        public boolean test(Random random) {
            return random.nextFloat() < randomChance;
        }

        @Override
        public List<ITextComponent> getToolTip() {
            if (toolTip == null) {
                toolTip = Collections.singletonList(new TextComponentString(new DecimalFormat("(###.###% chance)").format(randomChance)));
            }
            return toolTip;
        }
    }

    private static class OutputEntry implements IOutputEntry {

        private final ItemStack output;
        private final IGenRule genRule;

        OutputEntry(ItemStack output, IGenRule genRule) {
            this.output = output.copy();
            this.genRule = genRule;
        }

        @Override
        public ItemStack getOutput() {
            return output.copy();
        }

        @Override
        public IGenRule getGenRule() {
            return genRule;
        }
    }
}
