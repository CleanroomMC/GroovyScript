package com.cleanroommc.groovyscript.compat.mods.appliedenergistics2;

import appeng.api.AEApi;
import appeng.api.features.IInscriberRecipe;
import appeng.api.features.IInscriberRecipeBuilder;
import appeng.api.features.InscriberProcessType;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@RegistryDescription
public class Inscriber extends VirtualizedRegistry<IInscriberRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(ore('blockGlass')).output(item('minecraft:diamond')).top(item('minecraft:diamond')).bottom(item('minecraft:diamond')).inscribe()"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond')).top(item('minecraft:diamond'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(AEApi.instance().registries().inscriber()::removeRecipe);
        restoreFromBackup().forEach(AEApi.instance().registries().inscriber()::addRecipe);
    }

    public void add(IInscriberRecipe recipe) {
        AEApi.instance().registries().inscriber().addRecipe(recipe);
        addScripted(recipe);
    }

    public void remove(IInscriberRecipe recipe) {
        AEApi.instance().registries().inscriber().removeRecipe(recipe);
        addBackup(recipe);
    }

    @MethodDescription(example = @Example("item('appliedenergistics2:material:59')"))
    public void removeByOutput(ItemStack output) {
        List<IInscriberRecipe> recipes = AEApi.instance().registries().inscriber().getRecipes().stream().filter(x -> ItemStack.areItemStacksEqual(x.getOutput(), output)).collect(Collectors.toList());
        for (IInscriberRecipe recipe : recipes) {
            AEApi.instance().registries().inscriber().removeRecipe(recipe);
            addBackup(recipe);
        }
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        Collection<IInscriberRecipe> recipes = new ArrayList<>(AEApi.instance().registries().inscriber().getRecipes());
        for (IInscriberRecipe recipe : recipes) {
            AEApi.instance().registries().inscriber().removeRecipe(recipe);
            addBackup(recipe);
        }
    }


    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IInscriberRecipe> {

        @Property(defaultValue = "InscriberProcessType.PRESS", valid = @Comp(value = "null", type = Comp.Type.NOT))
        private InscriberProcessType type = InscriberProcessType.PRESS;
        @Property(defaultValue = "ItemStack.EMPTY", requirement = "groovyscript.wiki.appliedenergistics2.inscriber.top_bottom.required")
        private ItemStack top = ItemStack.EMPTY;
        @Property(defaultValue = "ItemStack.EMPTY", requirement = "groovyscript.wiki.appliedenergistics2.inscriber.top_bottom.required")
        private ItemStack bottom = ItemStack.EMPTY;


        @RecipeBuilderMethodDescription
        public RecipeBuilder type(InscriberProcessType type) {
            this.type = type;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder type(String type) {
            this.type = InscriberProcessType.valueOf(type.toUpperCase(Locale.ROOT));
            return this;
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder press() {
            this.type = InscriberProcessType.PRESS;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder inscribe() {
            this.type = InscriberProcessType.INSCRIBE;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder top(ItemStack top) {
            this.top = top;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder bottom(ItemStack bottom) {
            this.bottom = bottom;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Applied Energistics 2 Inscriber recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(type == null, "type must be defined");
            msg.add(IngredientHelper.isEmpty(top) && IngredientHelper.isEmpty(bottom), "either top or bottom must be defined, yet neither were");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IInscriberRecipe register() {
            if (!validate()) return null;
            IInscriberRecipeBuilder builder = AEApi.instance().registries().inscriber().builder()
                    .withInputs(input.stream().flatMap(x -> Arrays.stream(x.toMcIngredient().getMatchingStacks())).collect(Collectors.toList()))
                    .withOutput(output.get(0))
                    .withProcessType(type);
            if (!IngredientHelper.isEmpty(top)) builder.withTopOptional(top);
            if (!IngredientHelper.isEmpty(bottom)) builder.withBottomOptional(bottom);
            IInscriberRecipe recipe = builder.build();
            ModSupport.APPLIED_ENERGISTICS_2.get().inscriber.add(recipe);
            return recipe;
        }
    }

}
