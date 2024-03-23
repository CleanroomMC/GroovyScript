package com.cleanroommc.groovyscript.compat.mods.rustic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import org.jetbrains.annotations.Nullable;
import rustic.common.crafting.IEvaporatingBasinRecipe;
import rustic.common.crafting.Recipes;

@RegistryDescription
public class EvaporatingBasin extends VirtualizedRegistry<IEvaporatingBasinRecipe> {

    public EvaporatingBasin() {
        super(Alias.generateOfClass(EvaporatingBasin.class).andGenerate("DryingBasin"));
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('water') * 200).output(item('minecraft:clay'))"),
            @Example(".fluidInput(fluid('lava') * 50).output(item('minecraft:iron_ingot'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        Recipes.evaporatingRecipes.removeAll(removeScripted());
        Recipes.evaporatingRecipes.addAll(restoreFromBackup());
    }

    @Override
    public void afterScriptLoad() {
        Recipes.evaporatingRecipesMap.clear();
        Recipes.evaporatingRecipes.forEach(recipe -> Recipes.evaporatingRecipesMap.put(recipe.getFluid(), recipe));
    }

    public void add(IEvaporatingBasinRecipe recipe) {
        Recipes.evaporatingRecipes.add(recipe);
        addScripted(recipe);
    }

    public boolean remove(IEvaporatingBasinRecipe recipe) {
        addBackup(recipe);
        return Recipes.evaporatingRecipes.remove(recipe);
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example(value = "item('rustic:dust_tiny_iron')", commented = true))
    public boolean removeByOutput(IIngredient output) {
        return Recipes.evaporatingRecipes.removeIf(entry -> {
            if (output.test(entry.getOutput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("fluid('ironberryjuice')"))
    public boolean removeByInput(IIngredient input) {
        return Recipes.evaporatingRecipes.removeIf(entry -> {
            if (input.test(entry.getInput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        Recipes.evaporatingRecipes.forEach(this::addBackup);
        Recipes.evaporatingRecipes.clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IEvaporatingBasinRecipe> streamRecipes() {
        return new SimpleObjectStream<>(Recipes.evaporatingRecipes).setRemover(this::remove);
    }

    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "fluidInput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IEvaporatingBasinRecipe> {

        @Property(defaultValue = "fluidInput amount")
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Rustic Evaporating Basin recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
            if (time <= 0) time = fluidInput.get(0).amount;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IEvaporatingBasinRecipe register() {
            if (!validate()) return null;
            IEvaporatingBasinRecipe recipe = new ExtendedEvaporatingBasinRecipe(output.get(0), fluidInput.get(0), time);
            ModSupport.RUSTIC.get().evaporatingBasin.add(recipe);
            return recipe;
        }
    }

}
