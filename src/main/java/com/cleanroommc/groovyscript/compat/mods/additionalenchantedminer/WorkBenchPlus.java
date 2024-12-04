package com.cleanroommc.groovyscript.compat.mods.additionalenchantedminer;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.yogpc.qp.recipe.WorkbenchRecipe;
import com.yogpc.qp.tile.ItemDamage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.Map;

@RegistryDescription
public class WorkBenchPlus extends VirtualizedRegistry<WorkbenchPlusRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> WorkbenchPlusRecipe.removeById(recipe.getLocation().toString()));
        restoreFromBackup().forEach(WorkbenchPlusRecipe::addRecipe);
    }

    @MethodDescription(example = @Example("item('quarryplus:quarry')"))
    public Boolean removeByOutput(ItemStack output) {
        ItemDamage itemDamage = ItemDamage.apply(output);
        Map<ResourceLocation, WorkbenchRecipe> recipeMap = WorkbenchRecipe.getRecipeMap();
        Iterable<WorkbenchRecipe> iterable  = JavaConversions.asJavaIterable(recipeMap.values());
        iterable.forEach(recipe -> {
            if (recipe.key().equals(itemDamage)) {
                addBackup(new WorkbenchPlusRecipe(recipe.inputs(), recipe.getOutput(), recipe.energy(), recipe.location()));
            }
        });
        return WorkbenchPlusRecipe.removeByOutput(output);
    }

    public Boolean removeById(String id) {
        Map<ResourceLocation, WorkbenchRecipe> OptinalList = WorkbenchRecipe.getRecipeMap();
        ResourceLocation location = new ResourceLocation(id);
        Option<WorkbenchRecipe> recipe = OptinalList.get(location);
        if (recipe.isDefined()) addBackup(new WorkbenchPlusRecipe(recipe.get().inputs(), recipe.get().getOutput(), recipe.get().energy(), recipe.get().location()));
        return WorkbenchPlusRecipe.removeById(id);
    }

    public void removeAll() {
        Map<ResourceLocation, WorkbenchRecipe> OptinalList = WorkbenchRecipe.getRecipeMap();
        Iterable<ResourceLocation> iterable  = JavaConversions.asJavaIterable(OptinalList.keys());
        iterable.forEach(
                recipe -> removeById(recipe.toString())
        );
    }

    @RecipeBuilderDescription(example =
            @Example(".output(item('minecraft:nether_star')).input(item('minecraft:diamond'),item('minecraft:gold_ingot')).energy(10000)")
    )
    public RecipeBuilder recipeBuilder(){return new RecipeBuilder();}

    @Property(property = "input", comp = @Comp(not = "null", gte = 1 , lte = 27))
    @Property(property = "output", comp = @Comp(not = "null" , eq = 1))
    public class RecipeBuilder extends AbstractRecipeBuilder<WorkbenchPlusRecipe> {

        @Property(property = "energy", comp = @Comp(gt = 0))
        private double energy;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(double energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getRecipeNamePrefix() {
            return "additionalenchantedminer_workbenchplus_";
        }


        @Override
        public String getErrorMsg() {
            return "Error adding AdditionalEnchantedMiner WorkbenchPlus recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 27, 1, 1);
            msg.add(energy <= 0, "energy must be an integer greater than 0, yet it was {}", energy);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable WorkbenchPlusRecipe register() {
            if (!validate()) return null;
            WorkbenchPlusRecipe recipe = new WorkbenchPlusRecipe(this.input, this.output.get(0), this.energy, name);
            addScripted(recipe);
            WorkbenchPlusRecipe.addRecipe(recipe);
            return recipe;
        }
    }
}
