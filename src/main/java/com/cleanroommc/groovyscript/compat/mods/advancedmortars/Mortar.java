package com.cleanroommc.groovyscript.compat.mods.advancedmortars;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.core.mixin.advancedmortars.RegistryRecipeMortarAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.codetaylor.mc.advancedmortars.modules.mortar.api.MortarAPI;
import com.codetaylor.mc.advancedmortars.modules.mortar.recipe.RecipeMortar;
import com.codetaylor.mc.advancedmortars.modules.mortar.reference.EnumMortarType;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mortar extends VirtualizedRegistry<RecipeMortar> {

    public Mortar() {
        super();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(this::remove);
        restoreFromBackup().forEach(recipe -> getTypes(recipe).forEach(type -> add(type, recipe)));
    }

    public void add(String[] types, ItemStack output, int duration, List<IIngredient> inputs) {
        add(types, output, duration, ItemStack.EMPTY, 0.0f, inputs);
    }

    public void add(String[] types, ItemStack output, int duration, ItemStack secondaryOutput, float secondaryOutputChance, List<IIngredient> inputs) {
        if (inputs != null && inputs.size() != 0) {
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
                    } else {
                        add(enumMortarType, new RecipeMortar(
                                output, duration, secondaryOutput, secondaryOutputChance, IngredientHelper.toIngredientNonNullList(inputs)
                        ));
                    }
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

    public static List<EnumMortarType> getTypes(RecipeMortar value) {
        List<EnumMortarType> list = new ArrayList<>();
        ((RegistryRecipeMortarAccessor) MortarAPI.RECIPE_REGISTRY)
                .getRecipeMap()
                .forEach((key, value1) -> value1
                        .stream()
                        .filter(recipe -> recipe == value)
                        .map(recipe -> key)
                        .forEach(list::add));
        return list;
    }

}
