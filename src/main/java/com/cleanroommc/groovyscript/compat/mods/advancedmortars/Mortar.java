package com.cleanroommc.groovyscript.compat.mods.advancedmortars;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
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

@RegistryDescription
public class Mortar extends VirtualizedRegistry<RecipeMortar> {

    @RecipeBuilderDescription(example = {
            @Example(".type('stone').duration(2).output(item('minecraft:grass')).input(item('minecraft:dirt'))"),
            @Example(".type('emerald').duration(4).output(item('minecraft:wheat_seeds') * 16).secondaryOutput(item('minecraft:melon_seeds')).input(ore('cropWheat'))"),
            @Example(".type('obsidian').duration(8).output(item('minecraft:wheat_seeds') * 16).secondaryOutput(item('minecraft:melon_seeds'), 0.5).input(ore('cropWheat'))"),
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(recipe -> ((RegistryRecipeMortarAccessor) MortarAPI.RECIPE_REGISTRY).getRecipeMap().values().forEach(list -> list.removeIf(r -> r == recipe)));
        restoreFromBackup().forEach(recipe -> getTypes(recipe).forEach(type -> add(type, recipe)));
    }

    @MethodDescription(description = "groovyscript.wiki.advancedmortars.mortar.add0", type = MethodDescription.Type.ADDITION, example = {
            @Example("['stone'], item('minecraft:diamond') * 4, 4, [ore('ingotGold')]"),
            @Example("['stone'], item('minecraft:tnt'), 4, [ore('ingotGold')]")
    })
    public void add(List<String> types, ItemStack output, int duration, List<IIngredient> inputs) {
        add(types, output, duration, ItemStack.EMPTY, 0.0f, inputs);
    }

    @MethodDescription(description = "groovyscript.wiki.advancedmortars.mortar.add1", type = MethodDescription.Type.ADDITION, example = @Example("['iron', 'wood'], item('minecraft:tnt') * 5, 4, item('minecraft:tnt'), 0.7, [ore('ingotIron'), ore('ingotIron'), ore('ingotIron'), ore('ingotIron'),ore('ingotIron'), ore('ingotIron'), ore('ingotIron'), ore('ingotIron')]"))
    public void add(List<String> types, ItemStack output, int duration, ItemStack secondaryOutput, float secondaryOutputChance, List<IIngredient> inputs) {
        if (inputs == null || inputs.isEmpty()) return;
        if (inputs.size() > 8) {
            GroovyLog.msg("Error adding Advanced Mortars recipe")
                    .add("maximum number of 8 input ingredients exceeded: " + inputs.size())
                    .error()
                    .post();
            return;
        }
        for (String type : types) {
            EnumMortarType enumMortarType = EnumMortarType.fromName(type);
            if (enumMortarType == null) {
                GroovyLog.msg("Error adding Advanced Mortars recipe")
                        .add("invalid mortar type: " + type)
                        .add("valid types are: " + Arrays.toString(EnumMortarType.NAMES))
                        .error()
                        .post();
                return;
            }
            add(enumMortarType, new RecipeMortar(output, duration, secondaryOutput, secondaryOutputChance, IngredientHelper.toIngredientNonNullList(inputs)));
        }
    }

    public void add(EnumMortarType type, RecipeMortar recipe) {
        List<RecipeMortar> list = ((RegistryRecipeMortarAccessor) MortarAPI.RECIPE_REGISTRY).getRecipeMap().computeIfAbsent(type, (k) -> new ArrayList<>());
        list.add(recipe);
        addScripted(recipe);
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

    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "8")})
    @Property(property = "output", valid = @Comp("1"))
    public class RecipeBuilder extends AbstractRecipeBuilder<RecipeMortar> {

        @Property(requirement = "groovyscript.wiki.advancedmortars.mortar.types.required")
        private final List<String> types = new ArrayList<>();
        @Property
        private int duration;
        @Property(defaultValue = "ItemStack.EMPTY")
        private ItemStack secondaryOutput = ItemStack.EMPTY;
        @Property(defaultValue = "1.0f")
        private float secondaryOutputChance = 1.0f;

        @RecipeBuilderMethodDescription(field = "types")
        public RecipeBuilder type(String... type) {
            this.types.addAll(Arrays.asList(type));
            return this;
        }

        @RecipeBuilderMethodDescription(field = "types")
        public RecipeBuilder type(List<String> type) {
            this.types.addAll(type);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "types")
        public RecipeBuilder wood() {
            this.types.add("wood");
            return this;
        }

        @RecipeBuilderMethodDescription(field = "types")
        public RecipeBuilder stone() {
            this.types.add("stone");
            return this;
        }

        @RecipeBuilderMethodDescription(field = "types")
        public RecipeBuilder iron() {
            this.types.add("iron");
            return this;
        }

        @RecipeBuilderMethodDescription(field = "types")
        public RecipeBuilder diamond() {
            this.types.add("diamond");
            return this;
        }

        @RecipeBuilderMethodDescription(field = "types")
        public RecipeBuilder gold() {
            this.types.add("gold");
            return this;
        }

        @RecipeBuilderMethodDescription(field = "types")
        public RecipeBuilder obsidian() {
            this.types.add("obsidian");
            return this;
        }

        @RecipeBuilderMethodDescription(field = "types")
        public RecipeBuilder emerald() {
            this.types.add("emerald");
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder secondaryOutput(ItemStack itemStack) {
            this.secondaryOutput = itemStack;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"secondaryOutput", "secondaryOutputChance"})
        public RecipeBuilder secondaryOutput(ItemStack itemStack, float chance) {
            this.secondaryOutput = itemStack;
            this.secondaryOutputChance = chance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder secondaryOutputChance(float chance) {
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
            if (secondaryOutputChance > 1.0f) {
                secondaryOutputChance = 1.0f;
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeMortar register() {
            if (!validate()) return null;
            RecipeMortar recipe = new RecipeMortar(output.get(0), duration, secondaryOutput, secondaryOutputChance, IngredientHelper.toIngredientNonNullList(input));
            types.stream().map(EnumMortarType::fromName).forEach(enumMortarType -> add(enumMortarType, recipe));
            return recipe;
        }
    }

}
