package com.cleanroommc.groovyscript.compat.mods.additionalenchantedminer;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.yogpc.qp.recipe.IngredientRecipe;
import com.yogpc.qp.recipe.WorkbenchRecipe;
import com.yogpc.qp.tile.ItemDamage;
import com.yogpc.qp.utils.IngredientWithCount;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import scala.collection.JavaConversions;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.collection.SeqLike;
import scala.collection.convert.Decorators;
import scala.collection.immutable.Map;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class WorkbenchPlus extends VirtualizedRegistry<IngredientRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> WorkbenchRecipe.removeRecipe(recipe.location()));
        restoreFromBackup().forEach(recipe -> WorkbenchRecipe.addIngredientRecipe(recipe.location(), recipe.getOutput(), recipe.energy(), recipe.inputs(), recipe.hardCode(), recipe.showInJEI()));
    }

    @MethodDescription(example = @Example("item('quarryplus:quarry')"))
    public void removeByOutput(ItemStack output) {
        ItemDamage itemDamage = ItemDamage.apply(output);
        Map<ResourceLocation, WorkbenchRecipe> recipeMap = WorkbenchRecipe.getRecipeMap();
        Iterable<WorkbenchRecipe> iterable = JavaConversions.asJavaIterable(recipeMap.values());
        iterable.forEach(recipe -> {
            if (recipe.key().equals(itemDamage)) {
                addBackup(new IngredientRecipe(recipe.location(), recipe.getOutput(), recipe.energy(), recipe.showInJEI(), recipe.inputs(), recipe.hardCode()));
            }
        });
        WorkbenchRecipe.removeRecipe(ItemDamage.apply(output));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        Map<ResourceLocation, WorkbenchRecipe> recipeMap = WorkbenchRecipe.getRecipeMap();
        Iterable<ResourceLocation> iterableLocation = JavaConversions.asJavaIterable(recipeMap.keys());
        Iterable<WorkbenchRecipe> iterableRecipe = JavaConversions.asJavaIterable(recipeMap.values());
        iterableLocation.forEach(
                WorkbenchRecipe::removeRecipe
        );
        iterableRecipe.forEach(
                recipe -> addBackup(new IngredientRecipe(recipe.location(), recipe.getOutput(), recipe.energy(), recipe.showInJEI(), recipe.inputs(), recipe.hardCode()))
        );
    }

    private void add(IngredientRecipe recipe) {
        addScripted(recipe);
        WorkbenchRecipe.addIngredientRecipe(recipe.location(), recipe.getOutput(), recipe.energy(), recipe.inputs(), recipe.hardCode(), recipe.showInJEI());
    }

    @RecipeBuilderDescription(example = @Example(".output(item('minecraft:nether_star')).input(item('minecraft:diamond'),item('minecraft:gold_ingot')).energy(10000)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 27))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IngredientRecipe> {

        @Property(comp = @Comp(gt = 0))
        private double energy;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(double energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getRecipeNamePrefix() {
            return "additionalenchantedminer_workbench_";
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Additional Enchanted Miner Workbench recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 27, 1, 1);
            msg.add(energy <= 0, "energy must be an integer greater than 0, yet it was {}", energy);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IngredientRecipe register() {
            if (!validate()) return null;
            //convert Java List to Scala Seq
            List<List<IngredientWithCount>> inputJavaList = input.stream()
                    .map(i -> {
                        IngredientWithCount ingredient = new IngredientWithCount(i.toMcIngredient(), i.getAmount());
                        return Collections.singletonList(ingredient);
                    })
                    .collect(Collectors.toList());
            Seq<Seq<IngredientWithCount>> inputScalaList = JavaConverters.asScalaBufferConverter(
                    inputJavaList.stream()
                            .map(JavaConverters::asScalaBufferConverter)
                            .map(Decorators.AsScala::asScala)
                            .map(SeqLike::toSeq)
                            .collect(Collectors.toList())).asScala().toSeq();
            IngredientRecipe recipe = new IngredientRecipe(this.name, output.get(0), energy, true, inputScalaList, true);
            ModSupport.ADDITIONAL_ENCHANTED_MINER.get().workbenchPlus.add(recipe);
            return recipe;
        }
    }
}
