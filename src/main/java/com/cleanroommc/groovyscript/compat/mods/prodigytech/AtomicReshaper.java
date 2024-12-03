package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
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
        if (input instanceof OreDictIngredient oreDictIngredient) {
            AtomicReshaperManager.AtomicReshaperRecipe recipe = AtomicReshaperManager.INSTANCE.removeOreRecipe(oreDictIngredient.getOreDict());
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

    private boolean backupAndRemove(AtomicReshaperManager.AtomicReshaperRecipe recipe) {
        AtomicReshaperManager.AtomicReshaperRecipe removed;
        if (recipe.isOreRecipe()) {
            removed = AtomicReshaperManager.INSTANCE.removeOreRecipe(recipe.getOreInput());
        } else {
            removed = AtomicReshaperManager.INSTANCE.removeRecipe(recipe.getInput());
        }
        if (removed == null) {
            return false;
        }
        addBackup(removed);
        return true;
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

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<AtomicReshaperManager.AtomicReshaperRecipe> streamRecipes() {
        return new SimpleObjectStream<>(AtomicReshaperManager.INSTANCE.getAllRecipes())
                .setRemover(this::backupAndRemove);
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AtomicReshaperManager.AtomicReshaperRecipe> {

        @Property(comp = @Comp(gte = 1), defaultValue = "Config.atomicReshaperProcessTime")
        private int time = Config.atomicReshaperProcessTime;

        @Property(comp = @Comp(gte = 1))
        private int primordium;

        private final List<Integer> outputWeights = new ArrayList<>();

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

        @Override
        public AtomicReshaper.RecipeBuilder output(ItemStack output) {
            output(output, 1);
            return this;
        }

        @Override
        public AtomicReshaper.RecipeBuilder output(ItemStack... outputs) {
            for (ItemStack output : outputs) {
                output(output, 1);
            }
            return this;
        }

        @Override
        public AtomicReshaper.RecipeBuilder output(Collection<ItemStack> outputs) {
            for (ItemStack output : outputs) {
                output(output, 1);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public AtomicReshaper.RecipeBuilder output(ItemStack output, int weight) {
            this.output.add(output);
            outputWeights.add(weight);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding ProdigyTech Atomic Reshaper Recipe";
        }

        @Override
        protected int getMaxItemInput() {
            // The recipe correctly requires an increased amount of input items, but only consumes 1
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, Integer.MAX_VALUE);
            validateFluids(msg);

            // I think this check is not possible to fail at all but still, adding it for consistency
            msg.add(output.size() != outputWeights.size(), "Outputs and output weights must be the same size!");

            msg.add(outputWeights.stream().anyMatch(x -> x <= 0), "all weighted outputs must be greater than 0, yet they were {}", outputWeights);
            msg.add(primordium <= 0, "primordium must be greater than or equal to 1, yet it was {}", primordium);
            // 100 is hardcoded in the source
            int capacity = Config.atomicReshaperMaxPrimordium * 100;
            msg.add(primordium > capacity, "primordium must be less than or equal to the Reshaper's capacity {}, yet it was {}", capacity, primordium);
            msg.add(time <= 0, "time must be greater than 0, got {}", time);
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
            if (inputItem instanceof OreDictIngredient oreDictIngredient) {
                String oredict = oreDictIngredient.getOreDict();
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
