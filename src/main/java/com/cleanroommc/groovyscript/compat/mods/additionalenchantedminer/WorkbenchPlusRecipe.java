package com.cleanroommc.groovyscript.compat.mods.additionalenchantedminer;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.yogpc.qp.recipe.WorkbenchRecipe;
import com.yogpc.qp.tile.ItemDamage;
import com.yogpc.qp.utils.IngredientWithCount;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.Map;
import scala.collection.Seq;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.yogpc.qp.recipe.WorkbenchRecipe.addIngredientRecipe;
import static com.yogpc.qp.recipe.WorkbenchRecipe.getRecipeFromResult;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;

public class WorkbenchPlusRecipe {
    private final List<List<IngredientWithCount>> input;
    private final ItemStack output;
    private final Double energy;
    private final ResourceLocation location;

    public WorkbenchPlusRecipe(Collection<IIngredient> input, ItemStack output, Double energy , ResourceLocation location) {
        List<List<IngredientWithCount>> inputList = input.stream()
                .map(i -> new IngredientWithCount(i.toMcIngredient(), i.getAmount()))
                .map(Collections::singletonList)
                .collect(Collectors.toList());

        this.input = inputList;
        this.output = output;
        this.energy = energy;
        this.location = location;

    }

    public WorkbenchPlusRecipe(Seq<Seq<IngredientWithCount>> input, ItemStack output, Double energy , ResourceLocation location) {
        this.input = JavaConversions.seqAsJavaList(input).stream()
                .map(JavaConversions::seqAsJavaList)
                .collect(Collectors.toList());
        this.output = output;
        this.energy = energy;
        this.location = location;

    }

    public static WorkbenchPlusRecipe addRecipe(WorkbenchPlusRecipe recipe) {
        Map<ResourceLocation, WorkbenchRecipe> recipeMap = WorkbenchRecipe.getRecipeMap();
        ResourceLocation resourceLocation = new ResourceLocation(recipe.location.toString());
        Option<WorkbenchRecipe> optionalRecipe = recipeMap.get(resourceLocation);
        if (optionalRecipe.isDefined()) {
            addIngredientRecipe(optionalRecipe.get().location(), optionalRecipe.get().getOutput(), optionalRecipe.get().energy(), optionalRecipe.get().inputs(), true , true);
        } else {
            addIngredientRecipe(recipe.location, recipe.output, recipe.energy, recipe.input, true);
        }
        return recipe;
    }

    public static Boolean removeByOutput(ItemStack output) {
        ItemDamage itemDamage = ItemDamage.apply(output);
        WorkbenchRecipe.removeRecipe(itemDamage);
        return true;
    }

    public static Boolean removeById(String id) {
        ResourceLocation resourceLocation = new ResourceLocation(id);
        WorkbenchRecipe.removeRecipe(resourceLocation);
        return true;
    }

    public ResourceLocation getLocation() {
        return location;
    }
}
