package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
    public boolean remove(Recipe recipe) {
        FurnaceRecipes.instance().getSmeltingList().remove(recipe.input, recipe.output);
        CustomFurnaceManager.TIME_MAP.removeInt(recipe.input);
        addBackup(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:clay:*')"))
    public boolean removeByInput(IIngredient input) {
        if (GroovyLog.msg("Error adding Minecraft Furnace recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return false;
        }
        if (FurnaceRecipes.instance().getSmeltingList().entrySet().removeIf(entry -> {
            if (input.test(entry.getKey())) {
                addBackup(Recipe.of(entry.getKey(), entry.getValue()));
                return true;
            }
            return false;
        })) {
            return true;
        }
        //noinspection ConstantValue
        if ((Object) input instanceof ItemStack is && is.getMetadata() != OreDictionary.WILDCARD_VALUE) {
            var wild = new ItemStack(is.getItem(), 1, OreDictionary.WILDCARD_VALUE);
            if (!FurnaceRecipes.instance().getSmeltingResult(wild).isEmpty()) {
                GroovyLog.msg("Error removing Minecraft Furnace recipe")
                        .debug()
                        .add("there was no input found for {}, but there was an input matching the wildcard itemstack {}", GroovyScriptCodeConverter.asGroovyCode(is, false), GroovyScriptCodeConverter.asGroovyCode(wild, false))
                        .add("did you mean to run furnace.removeByInput({}) instead?", GroovyScriptCodeConverter.asGroovyCode(wild, false))
                        .post();
            }
        }
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:brick')"))
    public boolean removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error adding Minecraft Furnace recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output must not be empty")
                .error()
                .postIfNotEmpty()) {
            return false;
        }
        if (FurnaceRecipes.instance().getSmeltingList().entrySet().removeIf(entry -> {
            if (output.test(entry.getValue())) {
                addBackup(Recipe.of(entry.getKey(), entry.getValue()));
                return true;
            }
            return false;
        })) {
            return true;
        }
        GroovyLog.msg("Error removing Minecraft Furnace recipe")
                .add("Can't find recipe for output " + output)
                .error()
                .post();
        return false;
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Recipe> streamRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        FurnaceRecipes.instance().getSmeltingList().forEach((key, value) -> recipes.add(Recipe.of(key, value)));
        return new SimpleObjectStream<>(recipes, false).setRemover(this::remove);
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
    public boolean removeFuelConversionBySmeltedStack(ItemStack smelted) {
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
        // since time is entirely custom, it can be cleared directly
        CustomFurnaceManager.TIME_MAP.clear();
        getScriptedRecipes().forEach(recipe -> FurnaceRecipes.instance().getSmeltingList().remove(recipe.input, recipe.output));
        getBackupRecipes().forEach(recipe -> FurnaceRecipes.instance().addSmeltingRecipe(recipe.input, recipe.output, recipe.exp));
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
                VanillaModule.INSTANCE.furnace.add(recipe);
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
