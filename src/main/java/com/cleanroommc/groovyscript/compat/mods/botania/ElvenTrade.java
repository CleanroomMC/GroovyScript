package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeElvenTrade;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RegistryDescription
public class ElvenTrade extends StandardListRegistry<RecipeElvenTrade> {

    @RecipeBuilderDescription(example = @Example(".input(ore('ingotGold'), ore('ingotIron')).output(item('botania:manaresource:7'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<RecipeElvenTrade> getRecipes() {
        return BotaniaAPI.elvenTradeRecipes;
    }

    protected Object[] convertIngredients(IIngredient[] inputs) {
        return Arrays.stream(inputs)
                .map(
                        input -> input instanceof OreDictIngredient oreDictIngredient
                                ? oreDictIngredient.getOreDict()
                                : input.getMatchingStacks()[0])
                .toArray();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public RecipeElvenTrade add(ItemStack[] outputs, IIngredient... inputs) {
        return recipeBuilder().input(inputs).output(outputs).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public RecipeElvenTrade add(Collection<ItemStack> outputs, Collection<IIngredient> inputs) {
        return recipeBuilder().input(inputs).output(outputs).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public RecipeElvenTrade add(ItemStack output, IIngredient... inputs) {
        return recipeBuilder().input(inputs).output(output).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public RecipeElvenTrade add(ItemStack output, Collection<IIngredient> inputs) {
        return recipeBuilder().input(inputs).output(output).register();
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('botania:dreamwood')"))
    public boolean removeByOutputs(ItemStack... outputs) {
        if (getRecipes().removeIf(recipe -> {
            boolean found = Arrays.stream(outputs).allMatch(output -> recipe.getOutputs().stream().anyMatch(o -> ItemStack.areItemStacksEqual(o, output)));
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Elven Trade recipe")
                .add("could not find recipe with outputs {}", Arrays.toString(outputs))
                .error()
                .post();
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("ore('ingotManasteel')"))
    public boolean removeByInputs(IIngredient... inputs) {
        List<Object> converted = Arrays.asList(convertIngredients(inputs));
        List<IIngredient> list = Arrays.asList(inputs);
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.getInputs()
                    .stream()
                    .allMatch(
                            input -> input instanceof String
                                    ? converted.contains(input)
                                    : list.stream().anyMatch(i -> i.test((ItemStack) input)));
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Elven Trade recipe")
                .add("could not find recipe with inputs {}", converted)
                .error()
                .post();
        return false;
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 99))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 99))
    public class RecipeBuilder extends AbstractRecipeBuilder<RecipeElvenTrade> {

        @Override
        public String getErrorMsg() {
            return "Error adding Botania Elven Trade recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg);
            validateItems(msg, 1, 99, 1, 99);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeElvenTrade register() {
            if (!validate()) return null;
            RecipeElvenTrade recipe = new RecipeElvenTrade(output.toArray(new ItemStack[0]), convertIngredients(input.toArray(new IIngredient[0])));
            add(recipe);
            return recipe;
        }
    }
}
