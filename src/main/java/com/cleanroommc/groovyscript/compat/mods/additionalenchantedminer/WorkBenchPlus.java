package com.cleanroommc.groovyscript.compat.mods.additionalenchantedminer;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.yogpc.qp.recipe.WorkbenchRecipe;
import com.yogpc.qp.tile.ItemDamage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import scala.collection.JavaConversions;
import scala.collection.Map;

@RegistryDescription
public class WorkBenchPlus extends VirtualizedRegistry<WorkbenchPlusRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> WorkbenchRecipe.removeRecipe(recipe.getLocation()));
        restoreFromBackup().forEach(ModSupport.ADDITIONAL_ENCHANTED_MINER.get().WorkBenchPlus::add);
    }

    @MethodDescription(example = @Example("item('quarryplus:quarry')"))
    public boolean removeByOutput(ItemStack output) {
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

    @MethodDescription(priority = 2000,example = @Example(commented = true))
    public void removeAll() {
        Map<ResourceLocation, WorkbenchRecipe> recipeMap = WorkbenchRecipe.getRecipeMap();
        Iterable<ResourceLocation> iterableRecipe = JavaConversions.asJavaIterable(recipeMap.keys());
        iterableRecipe.forEach(
                location -> WorkbenchPlusRecipe.removeById(location.toString())
        );
    }

    private void add(WorkbenchPlusRecipe recipe) {
        addScripted(recipe);
        WorkbenchPlusRecipe.addRecipe(recipe);
    }

    @RecipeBuilderDescription(example =
                              @Example(".output(item('minecraft:nether_star')).input(item('minecraft:diamond'),item('minecraft:gold_ingot')).energy(10000)"))
    public RecipeBuilder recipeBuilder(){return new RecipeBuilder();}

    @Property(property = "input", comp = @Comp(gte = 1 , lte = 27))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<WorkbenchPlusRecipe> {

        @Property(comp = @Comp(gt = 0))
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
            return "Error adding Additional Enchanted Miner WorkbenchPlus recipe";
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
            WorkbenchPlusRecipe recipe = new WorkbenchPlusRecipe(this.input, this.output.get(0), this.energy, super.name);
            ModSupport.ADDITIONAL_ENCHANTED_MINER.get().WorkBenchPlus.add(recipe);
            return recipe;
        }
    }
}
