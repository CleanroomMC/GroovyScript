package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.core.mixin.forestry.CarpenterRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.factory.recipes.CarpenterRecipe;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Carpenter extends ForestryRegistry<ICarpenterRecipe> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        if (!isEnabled()) return;
        removeScripted().forEach(CarpenterRecipeManagerAccessor.getRecipes()::remove);
        restoreFromBackup().forEach(CarpenterRecipeManagerAccessor.getRecipes()::add);
    }

    public void add(ICarpenterRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        CarpenterRecipeManagerAccessor.getRecipes().add(recipe);
    }

    public boolean remove(ICarpenterRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return CarpenterRecipeManagerAccessor.getRecipes().remove(recipe);
    }

    public boolean removeByOutput(IIngredient output) {
        if (CarpenterRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = output.test(recipe.getCraftingGridRecipe().getOutput());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Carpenter recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByFluidInput(FluidStack input) {
        if (CarpenterRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = recipe.getFluidResource().isFluidEqual(input);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Carpenter recipe")
                .add("could not find recipe with fluid input {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByBox(IIngredient box) {
        if (CarpenterRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = box.test(recipe.getBox());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Carpenter recipe")
                .add("could not find recipe with box {}", box)
                .error()
                .post();
        return false;
    }

    public boolean removeByInput(IIngredient... inputs) {
        if (CarpenterRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = Arrays.stream(inputs).allMatch(i -> {
                boolean matches = false;
                if (i instanceof OreDictIngredient oreDictIngredient) {
                    matches = recipe.getCraftingGridRecipe().getOreDicts().contains(oreDictIngredient.getOreDict());
                } else {
                    for (int x = 0; x < recipe.getCraftingGridRecipe().getWidth(); x++) {
                        if (recipe.getCraftingGridRecipe().getRawIngredients().get(x).contains(i.getMatchingStacks()[0])) {
                            matches = true;
                            break;
                        }
                    }
                }
                return matches;
            });
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Carpenter recipe")
                .add("could not find recipe with inputs {}", (Object[]) inputs)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        CarpenterRecipeManagerAccessor.getRecipes().forEach(this::addBackup);
        CarpenterRecipeManagerAccessor.getRecipes().clear();
    }

    public SimpleObjectStream<ICarpenterRecipe> streamRecipes() {
        return new SimpleObjectStream<>(CarpenterRecipeManagerAccessor.getRecipes()).setRemover(this::remove);
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public static ShapedRecipeCustom convertPatternToInternal(ItemStack output, String[] pattern, Char2ObjectOpenHashMap<IIngredient> keyMap) {
        List<Object> argList = new ArrayList<>(Arrays.asList(pattern));
        for (Map.Entry<Character, IIngredient> entry : keyMap.entrySet()) {
            argList.add(entry.getKey());
            if (entry.getValue() instanceof OreDictIngredient oreDictIngredient) argList.add(oreDictIngredient.getOreDict());
            else argList.add(entry.getValue().getMatchingStacks()[0]);
        }
        return ShapedRecipeCustom.createShapedRecipe(output, argList.toArray());
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public static void validatePattern(GroovyLog.Msg msg, String[] pattern, Char2ObjectOpenHashMap<IIngredient> keyMap) {
        msg.add(pattern == null || pattern.length == 0, "No pattern was defined.");
        if (pattern != null && pattern.length > 1) {
            int l = pattern[0].length();
            for (int i = 1; i < pattern.length; i++) {
                if (pattern[i].length() != l) {
                    msg.add("All pattern lines must be the same length!");
                    break;
                }
            }
            for (String line : pattern) {
                if (line.length() > 3) {
                    msg.add("At least one pattern line exceeds a length of 3!");
                    break;
                }
            }
        }
        if (pattern != null) {
            for (String line : pattern)
                for (char c : line.toCharArray())
                    msg.add(!keyMap.containsKey(c), "key '" + c + "' is not defined!");
        }
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<ICarpenterRecipe> {

        protected int time = 20;
        protected String[] pattern;
        protected IIngredient box = IIngredient.EMPTY;
        protected Char2ObjectOpenHashMap<IIngredient> keys = new Char2ObjectOpenHashMap<>();

        public RecipeBuilder time(int time) {
            this.time = Math.max(time, 1);
            return this;
        }

        public RecipeBuilder box(IIngredient box) {
            this.box = box;
            return this;
        }

        public RecipeBuilder key(String key, IIngredient item) {
            if (key != null && !" ".equals(key) && !"\\".equals(key)) this.keys.put(key.charAt(0), item);
            return this;
        }

        public RecipeBuilder shape(String... pattern) {
            this.pattern = pattern;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Forestry Carpenter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg, 0, 1, 0, 0);
            validateItems(msg, 0, 0, 1, 1);
            validatePattern(msg, pattern, keys);
            validateStackSize(msg, 1, "grid input", keys.values());
            validateStackSize(msg, 1, "box", box);
        }

        @Override
        public @Nullable ICarpenterRecipe register() {
            if (!validate()) return null;
            ICarpenterRecipe recipe = new CarpenterRecipe(time, fluidInput.getOrEmpty(0), box.getMatchingStacks()[0], convertPatternToInternal(output.get(0), pattern, keys));
            add(recipe);
            return recipe;
        }
    }
}
