package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import lykrast.prodigytech.common.recipe.AtomicReshaperManager;
import lykrast.prodigytech.common.util.Config;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RegistryDescription
public class AtomicReshaper extends VirtualizedRegistry<AtomicReshaperManager.AtomicReshaperRecipe> {
    @RecipeBuilderDescription(example = {
        @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:emerald_block')).primordium(10).time(50)"),
        @Example(".input(item('minecraft:gold_block')).output(item('minecraft:diamond_block'), 10).output(item('minecraft:carrot'), 3).primordium(7)")
    })
    public AtomicReshaper.RecipeBuilder recipeBuilder() {
        return new AtomicReshaper.RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> {
            if (recipe.isOreRecipe()) {
                AtomicReshaperManager.INSTANCE.removeOreRecipe(recipe.getOreInput());
            } else {
                AtomicReshaperManager.INSTANCE.removeRecipe(recipe.getInput());
            }
        });
        restoreFromBackup().forEach(AtomicReshaperManager.INSTANCE::addRecipe);
    }

    @MethodDescription(example = @Example("ore('paper')"))
    public boolean removeByInput(IIngredient input) {
        if (input instanceof OreDictIngredient) {
            AtomicReshaperManager.AtomicReshaperRecipe recipe = AtomicReshaperManager.INSTANCE.removeOreRecipe(((OreDictIngredient) input).getOreDict());
            if (recipe == null) return false;
            addBackup(recipe);
            return true;
        } else {
            boolean success = false;
            for (ItemStack it : input.getMatchingStacks()) {
                AtomicReshaperManager.AtomicReshaperRecipe recipe = AtomicReshaperManager.INSTANCE.removeRecipe(it);
                if (recipe != null) {
                    success = true;
                    addBackup(recipe);
                }
            }
            return success;
        }
    }

    public void add(AtomicReshaperManager.AtomicReshaperRecipe recipe) {
        AtomicReshaperManager.INSTANCE.addRecipe(recipe);
        addScripted(recipe);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        AtomicReshaperManager.INSTANCE.getAllRecipes().forEach(this::addBackup);
        AtomicReshaperManager.INSTANCE.removeAll();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp(value = "1", type = Comp.Type.GTE))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AtomicReshaperManager.AtomicReshaperRecipe> {

        @Property(valid = @Comp(value = "1", type = Comp.Type.GTE), defaultValue = "-1")
        private int time = -1;

        @Property(valid = @Comp(value = "1", type = Comp.Type.GTE))
        private int primordium;

        private final List<Integer> outputWeights = new ArrayList<>();
        private String errorMessage = "";

        @RecipeBuilderMethodDescription
        public AtomicReshaper.RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public AtomicReshaper.RecipeBuilder primordium(int primordium) {
            this.primordium = primordium;
            return this;
        }

        public AtomicReshaper.RecipeBuilder output(ItemStack output) {
            output(output, 1);
            return this;
        }

        public AtomicReshaper.RecipeBuilder output(ItemStack... outputs) {
            for (ItemStack output : outputs) {
                output(output, 1);
            }
            return this;
        }

        public AtomicReshaper.RecipeBuilder output(Collection<ItemStack> outputs) {
            for (ItemStack output : outputs) {
                output(output, 1);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public AtomicReshaper.RecipeBuilder output(ItemStack output, int weight) {
            if (weight <= 0 && errorMessage.isEmpty()) {
                errorMessage = String.format("ItemStack %s has invalid weight %d, expected >= 1", output, weight);
            } else {
                this.output.add(output);
                outputWeights.add(weight);
            }
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding ProdigyTech Atomic Reshaper Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, Integer.MAX_VALUE);
            validateFluids(msg);

            // I think this check is not possible to fail at all but still, adding it for consistency
            msg.add(output.size() != outputWeights.size(), "Outputs and output weights must be the same size!");

            msg.add(!errorMessage.isEmpty(), "Weighted outputs error: {}", errorMessage);
            msg.add(primordium <= 0, "primordium must be greater than or equal to 1, yet it was {}", primordium);
            // 100 is hardcoded in the source
            int capacity = Config.atomicReshaperMaxPrimordium * 100;
            msg.add(primordium > capacity, "primordium must be less than or equal to the Reshaper's capacity {}, yet it was {}", capacity, primordium);
            if (time <= 0) {
                time = Config.atomicReshaperProcessTime;
            }
        }

        private Object[] getRecipeOutput() {
            List<Object> target = new ArrayList<>();
            for (int i = 0; i < output.size(); i++) {
                target.add(output.get(i));
                target.add(outputWeights.get(i));
            }
            return target.toArray();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable AtomicReshaperManager.AtomicReshaperRecipe register() {
            if (!validate()) return null;
            AtomicReshaperManager.AtomicReshaperRecipe recipe = null;
            IIngredient inputItem = input.get(0);
            if (inputItem instanceof OreDictIngredient) {
                String oredict = ((OreDictIngredient) inputItem).getOreDict();
                recipe = new AtomicReshaperManager.AtomicReshaperRecipe(oredict, time, primordium, getRecipeOutput());
                ModSupport.PRODIGY_TECH.get().atomicReshaper.add(recipe);
            } else {
                for (ItemStack it : inputItem.getMatchingStacks()) {
                    recipe = new AtomicReshaperManager.AtomicReshaperRecipe(it, time, primordium, getRecipeOutput());
                    ModSupport.PRODIGY_TECH.get().atomicReshaper.add(recipe);
                }
            }

            return recipe;
        }
    }
}
