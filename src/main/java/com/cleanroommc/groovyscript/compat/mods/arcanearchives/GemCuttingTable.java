package com.cleanroommc.groovyscript.compat.mods.arcanearchives;

import com.aranaira.arcanearchives.api.IGCTRecipe;
import com.aranaira.arcanearchives.recipe.IngredientStack;
import com.aranaira.arcanearchives.recipe.gct.GCTRecipe;
import com.aranaira.arcanearchives.recipe.gct.GCTRecipeList;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.arcanearchives.gem_cutting_table.note0", type = Admonition.Type.WARNING))
public class GemCuttingTable extends VirtualizedRegistry<IGCTRecipe> {

    public GemCuttingTable() {
        super(Alias.generateOfClass(GemCuttingTable.class).and("GCT", "gct"));
    }

    @RecipeBuilderDescription(example = {
            @Example(".name('clay_craft').input(item('minecraft:stone') * 64).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:stone'),item('minecraft:gold_ingot'),item('minecraft:gold_nugget')).output(item('minecraft:clay') * 4)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(GCTRecipeList.instance::removeRecipe);
        restoreFromBackup().forEach(GCTRecipeList.instance::addRecipe);
    }

    public void add(IGCTRecipe recipe) {
        if (recipe == null) return;
        GCTRecipeList.instance.addRecipe(recipe);
        addScripted(recipe);
    }

    public boolean remove(IGCTRecipe recipe) {
        if (recipe == null) return false;
        GCTRecipeList.instance.removeRecipe(recipe);
        addBackup(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:gold_nugget')"))
    public boolean removeByInput(IIngredient input) {
        return GCTRecipeList.instance.getRecipes().values().removeIf(recipe -> {
            boolean found = recipe.getIngredients().stream()
                    .map(IngredientStack::getIngredient)
                    .map(Ingredient::getMatchingStacks)
                    .flatMap(Arrays::stream)
                    .anyMatch(input);
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    @MethodDescription(example = @Example("item('arcanearchives:shaped_quartz')"))
    public boolean removeByOutput(IIngredient output) {
        return GCTRecipeList.instance.getRecipes().values().removeIf(recipe -> {
            boolean matches = output.test(recipe.getRecipeOutput());
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IGCTRecipe> streamRecipes() {
        return new SimpleObjectStream<>(GCTRecipeList.instance.getRecipeList())
                .setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        for (IGCTRecipe recipe : GCTRecipeList.instance.getRecipeList()) {
            addBackup(recipe);
            GCTRecipeList.instance.removeRecipe(recipe);
        }
    }

    @Property(property = "input", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "Integer.MAX_VALUE", type = Comp.Type.LTE)})
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IGCTRecipe> {

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_gem_cutting_table_";
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Arcane Archives Gem Cutting Table recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, Integer.MAX_VALUE, 1, 1);
            validateFluids(msg);
            validateName();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IGCTRecipe register() {
            if (!validate()) return null;
            IGCTRecipe recipe = new GCTRecipe(name, output.get(0), input.stream().map(x -> new IngredientStack(x.toMcIngredient(), x.getAmount())).collect(Collectors.toList()));
            ModSupport.ARCANE_ARCHIVES.get().gemCuttingTable.add(recipe);
            return recipe;
        }
    }

}
