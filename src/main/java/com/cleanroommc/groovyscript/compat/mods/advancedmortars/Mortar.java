package com.cleanroommc.groovyscript.compat.mods.advancedmortars;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.core.mixin.advancedmortars.RegistryRecipeMortarAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.codetaylor.mc.advancedmortars.modules.mortar.api.MortarAPI;
import com.codetaylor.mc.advancedmortars.modules.mortar.recipe.RecipeMortar;
import com.codetaylor.mc.advancedmortars.modules.mortar.reference.EnumMortarType;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Mortar extends VirtualizedRegistry<RecipeMortar> {

    public Mortar() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(this::remove);
        restoreFromBackup().forEach(recipe -> getTypes(recipe).forEach(type -> add(type, recipe)));
    }

    public void add(List<String> types, ItemStack output, int duration, List<IIngredient> inputs) {
        add(types, output, duration, ItemStack.EMPTY, 0.0f, inputs);
    }

    public void add(List<String> types, ItemStack output, int duration, ItemStack secondaryOutput, float secondaryOutputChance, List<IIngredient> inputs) {
        if (inputs == null || inputs.size() == 0) return;
        if (inputs.size() > 8) {
            GroovyLog.msg("Error adding Advanced Mortars recipe")
                    .add("maximum number of 8 input ingredients exceeded: " + inputs.size())
                    .error()
                    .post();
        } else {
            for (String type : types) {
                EnumMortarType enumMortarType = EnumMortarType.fromName(type);
                if (enumMortarType == null) {
                    GroovyLog.msg("Error adding Advanced Mortars recipe")
                            .add("invalid mortar type: " + type)
                            .add("valid types are: " + Arrays.toString(EnumMortarType.NAMES))
                            .error()
                            .post();
                    return;
                } else {
                    add(enumMortarType, new RecipeMortar(output, duration, secondaryOutput, secondaryOutputChance, IngredientHelper.toIngredientNonNullList(inputs)));
                }
            }
        }
    }

    public void add(EnumMortarType type, RecipeMortar recipe) {
        List<RecipeMortar> list = ((RegistryRecipeMortarAccessor) MortarAPI.RECIPE_REGISTRY).getRecipeMap().computeIfAbsent(type, (k) -> new ArrayList<>());
        list.add(recipe);
        addScripted(recipe);
    }

    public void remove(RecipeMortar recipe) {
        ((RegistryRecipeMortarAccessor) MortarAPI.RECIPE_REGISTRY).getRecipeMap().values().forEach(list -> list.removeIf(r -> r == recipe));
        addBackup(recipe);
    }

    public static List<EnumMortarType> getTypes(RecipeMortar recipe) {
        return ((RegistryRecipeMortarAccessor) MortarAPI.RECIPE_REGISTRY)
                .getRecipeMap()
                .entrySet()
                .stream()
                .filter(x -> x.getValue().contains(recipe))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<RecipeMortar> {

        private final List<String> types = new ArrayList<>();
        private int duration;
        private ItemStack secondaryOutput;
        private float secondaryOutputChance;

        public RecipeBuilder type(String type) {
            this.types.add(type);
            return this;
        }

        public RecipeBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public RecipeBuilder secondaryOutput(ItemStack itemStack) {
            this.secondaryOutput = itemStack;
            return this;
        }

        public RecipeBuilder secondaryOutput(ItemStack itemStack, float chance) {
            this.secondaryOutput = itemStack;
            this.secondaryOutputChance = chance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Advanced Mortars recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 8, 1, 1);
            for (String type : types) {
                EnumMortarType enumMortarType = EnumMortarType.fromName(type);
                if (enumMortarType == null) {
                    msg.add("invalid mortar type: " + type).add("valid types are: " + Arrays.toString(EnumMortarType.NAMES));
                }
            }
            if (secondaryOutput == null) secondaryOutput = ItemStack.EMPTY;
            if (secondaryOutput.isEmpty()) {
                secondaryOutputChance = 0.0f;
            } else if (secondaryOutputChance <= 0.0f) {
                secondaryOutputChance = 1.0f;
            }
        }

        @Override
        public @Nullable RecipeMortar register() {
            if (!validate()) return null;
            RecipeMortar recipe = new RecipeMortar(output.get(0), duration, secondaryOutput, secondaryOutputChance, IngredientHelper.toIngredientNonNullList(input));
            types.stream().map(EnumMortarType::fromName).forEach(enumMortarType -> add(enumMortarType, recipe));
            return recipe;
        }
    }

}