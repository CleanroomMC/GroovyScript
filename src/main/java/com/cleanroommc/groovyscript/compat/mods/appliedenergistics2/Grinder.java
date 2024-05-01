package com.cleanroommc.groovyscript.compat.mods.appliedenergistics2;

import appeng.api.AEApi;
import appeng.api.features.IGrinderRecipe;
import appeng.api.features.IGrinderRecipeBuilder;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Grinder extends VirtualizedRegistry<IGrinderRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'), item('minecraft:gold_ingot'), item('minecraft:diamond')).turns(1).chance1(0.5).chance2(0.3)"),
            @Example(".input(item('minecraft:stone')).output(item('minecraft:clay') * 4).turns(10)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(AEApi.instance().registries().grinder()::removeRecipe);
        restoreFromBackup().forEach(AEApi.instance().registries().grinder()::addRecipe);
    }

    public void add(IGrinderRecipe recipe) {
        AEApi.instance().registries().grinder().addRecipe(recipe);
        addScripted(recipe);
    }

    public void remove(IGrinderRecipe recipe) {
        AEApi.instance().registries().grinder().removeRecipe(recipe);
        addBackup(recipe);
    }

    @MethodDescription(example = @Example("item('minecraft:gold_ingot')"))
    public void removeByInput(ItemStack input) {
        List<IGrinderRecipe> recipes = AEApi.instance().registries().grinder().getRecipes().stream().filter(x -> ItemStack.areItemStacksEqual(x.getInput(), input)).collect(Collectors.toList());
        for (IGrinderRecipe recipe : recipes) {
            AEApi.instance().registries().grinder().removeRecipe(recipe);
            addBackup(recipe);
        }
    }

    @MethodDescription(example = @Example("item('minecraft:quartz')"))
    public void removeByOutput(ItemStack output) {
        List<IGrinderRecipe> recipes = AEApi.instance().registries().grinder().getRecipes().stream().filter(x -> ItemStack.areItemStacksEqual(x.getOutput(), output)).collect(Collectors.toList());
        for (IGrinderRecipe recipe : recipes) {
            AEApi.instance().registries().grinder().removeRecipe(recipe);
            addBackup(recipe);
        }
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        Collection<IGrinderRecipe> recipes = new ArrayList<>(AEApi.instance().registries().grinder().getRecipes());
        for (IGrinderRecipe recipe : recipes) {
            AEApi.instance().registries().grinder().removeRecipe(recipe);
            addBackup(recipe);
        }
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "3")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<IGrinderRecipe> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GT))
        private int turns;
        @Property(defaultValue = "1.0f", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
        private float chance1 = 1.0f;
        @Property(defaultValue = "1.0f", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
        private float chance2 = 1.0f;


        @RecipeBuilderMethodDescription
        public RecipeBuilder turns(int turns) {
            this.turns = turns;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"chance1", "chance2"})
        public RecipeBuilder chance(float chance1, float chance2) {
            this.chance1 = chance1;
            this.chance2 = chance2;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance1(float chance) {
            this.chance1 = chance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance2(float chance) {
            this.chance2 = chance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Applied Energistics 2 Grinder recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 3);
            validateFluids(msg);
            msg.add(turns <= 0, "turns must be an integer greater than 0, yet it was {}", turns);
            msg.add(chance1 < 0 || chance1 > 1, "chance1 must be a float greater than or equal to 0 and less than or equal to 1, yet it was {}", chance1);
            msg.add(chance2 < 0 || chance2 > 1, "chance2 must be a float greater than or equal to 0 and less than or equal to 1, yet it was {}", chance2);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IGrinderRecipe register() {
            if (!validate()) return null;
            IGrinderRecipeBuilder builder = AEApi.instance().registries().grinder().builder()
                    .withInput(input.get(0).toMcIngredient().getMatchingStacks()[0])
                    .withOutput(output.get(0))
                    .withTurns(turns);
            if (output.size() > 1 && !IngredientHelper.isEmpty(output.get(1))) builder.withFirstOptional(output.get(1), chance1);
            if (output.size() > 2 && !IngredientHelper.isEmpty(output.get(2))) builder.withSecondOptional(output.get(2), chance2);
            IGrinderRecipe recipe = builder.build();
            ModSupport.APPLIED_ENERGISTICS_2.get().grinder.add(recipe);
            return recipe;
        }
    }

}
