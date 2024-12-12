package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipePetals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Apothecary extends StandardListRegistry<RecipePetals> {

    public Apothecary() {
        super(Alias.generateOfClassAnd(Apothecary.class, "PetalApothecary"));
    }

    @Override
    public Collection<RecipePetals> getRecipes() {
        return BotaniaAPI.petalRecipes;
    }

    @RecipeBuilderDescription(example = @Example(".input(ore('blockGold'), ore('ingotIron'), item('minecraft:apple')).output(item('minecraft:golden_apple'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public RecipePetals add(ItemStack output, IIngredient... inputs) {
        return recipeBuilder()
                .output(output)
                .input(inputs)
                .register();
    }

    @MethodDescription(example = @Example("item('botania:specialflower').withNbt(['type': 'puredaisy'])"))
    public boolean removeByOutput(IIngredient output) {
        if (getRecipes().removeIf(recipe -> {
            boolean found = output.test(recipe.getOutput());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Apothecary recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    @MethodDescription(example = @Example("ore('runeFireB')"))
    public boolean removeByInput(IIngredient... inputs) {
        List<Object> converted = Arrays.stream(inputs)
                .map(i -> i instanceof OreDictIngredient oreDictIngredient ? oreDictIngredient.getOreDict() : i.getMatchingStacks()[0])
                .collect(Collectors.toList());
        if (getRecipes().removeIf(recipe -> {
            boolean found = converted.stream()
                    .allMatch(
                            o -> recipe.getInputs()
                                    .stream()
                                    .anyMatch(i -> o instanceof String || i instanceof String ? o.equals(i) : ItemStack.areItemStacksEqual((ItemStack) i, (ItemStack) o)));
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Apothecary recipe")
                .add("could not find recipe with inputs {}", converted)
                .error()
                .post();
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("ore('petalYellow'), ore('petalBrown')"))
    public boolean removeByInputs(IIngredient... inputs) {
        return removeByInput(inputs);
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 20))
    @Property(property = "output", comp = @Comp(eq = 1))
    public class RecipeBuilder extends AbstractRecipeBuilder<RecipePetals> {

        @Override
        protected int getMaxItemInput() {
            // Each slot of Apothecary can only contain 1 item
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Botania Apothecary recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg);
            validateItems(msg, 1, 20, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipePetals register() {
            if (!validate()) return null;
            RecipePetals recipe = new RecipePetals(
                    output.get(0),
                    input.stream()
                            .map(
                                    i -> i instanceof OreDictIngredient oreDictIngredient
                                            ? oreDictIngredient.getOreDict()
                                            : i.getMatchingStacks()[0])
                            .toArray());
            add(recipe);
            return recipe;
        }
    }
}
