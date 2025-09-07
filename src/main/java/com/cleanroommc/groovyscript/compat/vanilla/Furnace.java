package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RegistryDescription(
        admonition = @Admonition("groovyscript.wiki.minecraft.furnace.note0")
)
public class Furnace extends VirtualizedRegistry<Furnace.Recipe> {

    private static final float EXPERIENCE_DEFAULT = 0.1f;
    private static final int TIME_DEFAULT = 200;

    private final AbstractReloadableStorage<CustomFurnaceManager.FuelConversionRecipe> conversionStorage = new AbstractReloadableStorage<>();

    @RecipeBuilderDescription(example = @Example(".input(ore('ingotGold')).output(item('minecraft:nether_star')).exp(0.5)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.furnace.add0", example = @Example(value = "ore('ingotIron'), item('minecraft:diamond')", commented = true))
    public void add(IIngredient input, ItemStack output) {
        recipeBuilder().input(input).output(output).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.furnace.add1", example = @Example("item('minecraft:nether_star'), item('minecraft:clay') * 64, 13"))
    public void add(IIngredient input, ItemStack output, float exp) {
        recipeBuilder().exp(exp).input(input).output(output).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.furnace.add2", example = @Example("item('minecraft:diamond'), item('minecraft:clay'), 2, 50"))
    public void add(IIngredient input, ItemStack output, float exp, int time) {
        recipeBuilder().time(time).exp(exp).input(input).output(output).register();
    }

    @GroovyBlacklist
    public void add(Recipe recipe) {
        FurnaceRecipes.instance().addSmeltingRecipe(recipe.input, recipe.output, recipe.exp);
        CustomFurnaceManager.TIME_MAP.put(recipe.input, recipe.time);
        addScripted(recipe);
    }

    @GroovyBlacklist
    public boolean remove(Recipe recipe, boolean isScripted) {
        return removeByInput(recipe.input, isScripted, isScripted);
    }

    @GroovyBlacklist
    private ItemStack findTrueInput(ItemStack input) {
        ItemStack trueInput = FurnaceRecipeManager.INPUT_SET.get(input);
        if (trueInput == null && input.getMetadata() != Short.MAX_VALUE) {
            input = new ItemStack(input.getItem(), input.getCount(), Short.MAX_VALUE);
            trueInput = FurnaceRecipeManager.INPUT_SET.get(input);
        }
        return trueInput;
    }

    @MethodDescription(example = @Example("item('minecraft:clay')"))
    public boolean removeByInput(ItemStack input) {
        return removeByInput(input, true);
    }

    public boolean removeByInput(ItemStack input, boolean log) {
        return removeByInput(input, log, true);
    }

    @GroovyBlacklist
    public boolean removeByInput(ItemStack input, boolean log, boolean isScripted) {
        if (IngredientHelper.isEmpty(input)) {
            if (log) {
                GroovyLog.msg("Error adding Minecraft Furnace recipe")
                        .add(IngredientHelper.isEmpty(input), () -> "Input must not be empty")
                        .error()
                        .postIfNotEmpty();
            }
            return false;
        }

        ItemStack trueInput = findTrueInput(input);
        if (trueInput == null) {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Furnace recipe")
                        .add("Can't find recipe for input " + input)
                        .error()
                        .post();
            }
            return false;
        }
        ItemStack output = FurnaceRecipes.instance().getSmeltingList().remove(trueInput);
        if (output != null) {
            if (isScripted) addBackup(Recipe.of(trueInput, output));
            return true;
        } else {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Furnace recipe")
                        .add("Found input, but no output for " + input)
                        .error()
                        .post();
            }
        }

        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:brick')"))
    public boolean removeByOutput(IIngredient output) {
        return removeByOutput(output, true);
    }

    public boolean removeByOutput(IIngredient output, boolean log) {
        return removeByOutput(output, log, true);
    }

    @GroovyBlacklist
    public boolean removeByOutput(IIngredient output, boolean log, boolean isScripted) {
        if (IngredientHelper.isEmpty(output)) {
            if (log) {
                GroovyLog.msg("Error adding Minecraft Furnace recipe")
                        .add(IngredientHelper.isEmpty(output), () -> "Output must not be empty")
                        .error()
                        .postIfNotEmpty();
            }
            return false;
        }

        List<Recipe> recipesToRemove = new ArrayList<>();
        for (Map.Entry<ItemStack, ItemStack> entry : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
            if (output.test(entry.getValue())) {
                recipesToRemove.add(Recipe.of(entry.getKey(), entry.getValue()));
            }
        }
        if (recipesToRemove.isEmpty()) {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Furnace recipe")
                        .add("Can't find recipe for output " + output)
                        .error()
                        .post();
            }
            return false;
        }

        for (Recipe recipe : recipesToRemove) {
            if (isScripted) addBackup(recipe);
            FurnaceRecipes.instance().getSmeltingList().remove(recipe.input);
            CustomFurnaceManager.TIME_MAP.remove(output);
        }

        return true;
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Recipe> streamRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        for (Map.Entry<ItemStack, ItemStack> entry : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
            recipes.add(Recipe.of(entry.getKey(), entry.getValue()));
        }
        return new SimpleObjectStream<>(recipes, false).setRemover(recipe -> remove(recipe, true));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        FurnaceRecipes.instance().getSmeltingList().entrySet().removeIf(entry -> {
            Recipe recipe = Recipe.of(entry.getKey(), entry.getValue());
            addBackup(recipe);
            return true;
        });
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.add_to_list")
    public boolean addFuelConversion(CustomFurnaceManager.FuelConversionRecipe recipe) {
        CustomFurnaceManager.FUEL_TRANSFORMERS.add(recipe);
        return conversionStorage.addScripted(recipe);
    }

    @MethodDescription(description = "groovyscript.wiki.remove_from_list")
    public boolean removeFuelConversion(CustomFurnaceManager.FuelConversionRecipe recipe) {
        return CustomFurnaceManager.FUEL_TRANSFORMERS.remove(recipe) && conversionStorage.addBackup(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:diamond'), item('minecraft:bucket').transform(item('minecraft:lava_bucket'))"))
    public boolean addFuelConversion(IIngredient smelted, IIngredient fuel) {
        return addFuelConversion(new CustomFurnaceManager.FuelConversionRecipe(smelted, fuel));
    }

    @MethodDescription(example = @Example("item('minecraft:sponge', 1)"))
    public boolean removeFuelConversionBySmelted(ItemStack smelted) {
        return CustomFurnaceManager.FUEL_TRANSFORMERS.removeIf(x -> x.smelted().test(smelted) && conversionStorage.addBackup(x));
    }

    @MethodDescription(type = MethodDescription.Type.QUERY, description = "groovyscript.wiki.streamRecipes")
    public SimpleObjectStream<CustomFurnaceManager.FuelConversionRecipe> streamFuelConversions() {
        return new SimpleObjectStream<>(CustomFurnaceManager.FUEL_TRANSFORMERS).setRemover(this::removeFuelConversion);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllFuelConversions() {
        CustomFurnaceManager.FUEL_TRANSFORMERS.removeIf(conversionStorage::addBackup);
    }

    @GroovyBlacklist
    @Override
    public void onReload() {
        getScriptedRecipes().forEach(recipe -> {
            remove(recipe, false);
        });
        getBackupRecipes().forEach(recipe -> {
            FurnaceRecipes.instance().addSmeltingRecipe(recipe.input, recipe.output, recipe.exp);
            CustomFurnaceManager.TIME_MAP.put(recipe.output, recipe.time);
        });
        CustomFurnaceManager.FUEL_TRANSFORMERS.addAll(conversionStorage.restoreFromBackup());
        CustomFurnaceManager.FUEL_TRANSFORMERS.removeAll(conversionStorage.removeScripted());
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<Recipe> {

        @Property(comp = @Comp(gte = 0), defaultValue = "0.1f")
        private float exp = EXPERIENCE_DEFAULT;
        @Property(comp = @Comp(gte = 1), defaultValue = "200")
        private int time = TIME_DEFAULT;

        @RecipeBuilderMethodDescription
        public RecipeBuilder exp(float exp) {
            this.exp = exp;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "exp")
        public RecipeBuilder experience(float exp) {
            this.exp = exp;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Minecraft Furnace recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(exp < 0, "exp must be a float greater than or equal to 0, yet it was {}", exp);
            msg.add(time <= 0, "time must be an integer greater than 1, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Recipe register() {
            if (!validate()) return null;
            Recipe recipe = null;
            var out = output.get(0);
            for (ItemStack input : input.get(0).getMatchingStacks()) {
                recipe = new Recipe(input, out, exp, time);
                VanillaModule.furnace.add(recipe);
            }
            return recipe;
        }
    }

    public static class Recipe {

        private final ItemStack input;
        private final ItemStack output;
        private final float exp;
        private final int time;

        private Recipe(ItemStack input, ItemStack output) {
            this(input, output, EXPERIENCE_DEFAULT);
        }

        private Recipe(ItemStack input, ItemStack output, float exp) {
            this(input, output, exp, TIME_DEFAULT);
        }

        private Recipe(ItemStack input, ItemStack output, float exp, int time) {
            this.input = input;
            this.output = output;
            this.exp = exp;
            this.time = time;
        }

        private static Recipe of(ItemStack input) {
            ItemStack output = FurnaceRecipes.instance().getSmeltingList().get(input);
            float exp = FurnaceRecipes.instance().getSmeltingExperience(output);
            int time = CustomFurnaceManager.TIME_MAP.getInt(output);
            if (time <= 0) time = TIME_DEFAULT;
            return new Recipe(input, output, exp, time);
        }

        private static Recipe of(ItemStack input, ItemStack output) {
            float exp = FurnaceRecipes.instance().getSmeltingExperience(output);
            int time = CustomFurnaceManager.TIME_MAP.getInt(output);
            if (time <= 0) time = TIME_DEFAULT;
            return new Recipe(input, output, exp, time);
        }

        public ItemStack getInput() {
            return input.copy();
        }

        public ItemStack getOutput() {
            return output.copy();
        }

        public float getExp() {
            return exp;
        }

        public int getTime() {
            return time;
        }
    }
}
