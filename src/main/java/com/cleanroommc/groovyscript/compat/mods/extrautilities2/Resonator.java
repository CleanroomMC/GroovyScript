package com.cleanroommc.groovyscript.compat.mods.extrautilities2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import com.rwtema.extrautils2.api.resonator.IResonatorRecipe;
import com.rwtema.extrautils2.crafting.ResonatorRecipe;
import com.rwtema.extrautils2.power.PowerManager;
import com.rwtema.extrautils2.tile.TileRainbowGenerator;
import com.rwtema.extrautils2.tile.TileResonator;
import com.rwtema.extrautils2.utils.Lang;
import groovy.lang.Closure;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.codehaus.groovy.runtime.MethodClosure;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

public class Resonator extends VirtualizedRegistry<IResonatorRecipe> {

    public Resonator() {
        super();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> TileResonator.resonatorRecipes.removeIf(r -> r == recipe));
        TileResonator.resonatorRecipes.addAll(restoreFromBackup());
    }

    public IResonatorRecipe add(IResonatorRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            TileResonator.resonatorRecipes.add(recipe);
        }
        return recipe;
    }

    public boolean removeByOutput(ItemStack output) {
        return TileResonator.resonatorRecipes.removeIf(r -> {
            if (r.getOutput().equals(output)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean removeByInput(ItemStack input) {
        return TileResonator.resonatorRecipes.removeIf(r -> {
            if (r.getInputs().contains(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean removeByInput(IIngredient input) {
        return removeByInput(IngredientHelper.toItemStack(input));
    }

    public boolean remove(IResonatorRecipe recipe) {
        if (TileResonator.resonatorRecipes.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public SimpleObjectStream<IResonatorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(TileResonator.resonatorRecipes).setRemover(this::remove);
    }

    public void removeAll() {
        TileResonator.resonatorRecipes.forEach(this::addBackup);
        TileResonator.resonatorRecipes.clear();
    }


    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<IResonatorRecipe> {

        private int energy;
        private boolean ownerTag = false;
        private String requirementText = "";
        private Closure<Boolean> shouldProgress = null;

        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        public RecipeBuilder cost(int totalCost) {
            return energy(totalCost);
        }

        public RecipeBuilder ownerTag(boolean ownerTag) {
            this.ownerTag = ownerTag;
            return this;
        }

        public RecipeBuilder requirementText(String requirementText) {
            this.requirementText = requirementText;
            return this;
        }

        public RecipeBuilder shouldProgress(Closure<Boolean> shouldProgress) {
            if (shouldProgress == null) {
                GroovyLog.msg("Extra Utilities 2 Resonator shouldProgress closure must be defined")
                        .error()
                        .post();
                return this;
            }
            if (Arrays.equals(shouldProgress.getParameterTypes(), new Class[]{TileEntity.class, int.class, ItemStack.class})) {
                this.shouldProgress = shouldProgress;
                return this;
            }
            GroovyLog.msg("Extra Utilities 2 Resonator shouldProgress closure requires a closure with exactly three parameters:")
                    .add("net.minecraft.tileentity.TileEntity resonator, int frequency, net.minecraft.item.ItemStack input in that order.")
                    .add("but had {}, {}, {} instead", (Object[]) shouldProgress.getParameterTypes())
                    .error()
                    .post();
            return this;
        }

        public RecipeBuilder rainbow() {
            this.requirementText = Lang.translate("[Requires an active Rainbow Generator]");

            // TODO figure out if this is the best way to handle this
            ShouldProgress isRainbowRunning = (resonator, frequency, input) -> {
                PowerManager.PowerFreq freq = PowerManager.instance.getPowerFreqRaw(frequency);
                if (freq != null) {
                    Collection<TileRainbowGenerator> s = freq.getSubTypes(TileRainbowGenerator.rainbowGenerators);
                    if (s != null) {
                        for (TileRainbowGenerator power : s) {
                            if (power.providing) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            };
            this.shouldProgress = ((Closure<Boolean>) new MethodClosure(isRainbowRunning, "run"));
            return this;
        }


        @Override
        public String getErrorMsg() {
            return "Error adding Extra Utilities 2 Resonator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(energy < 100, () -> "energy must not be less than 1 GP (100)");
        }

        @Nullable
        @Override
        public IResonatorRecipe register() {
            if (!validate()) return null;
            IResonatorRecipe recipe = new ResonatorRecipe(input.get(0).getMatchingStacks()[0], output.get(0), energy, ownerTag) {
                public String getRequirementText() {
                    return requirementText == null ? "" : requirementText;
                }

                public boolean shouldProgress(TileEntity resonator, int frequency, ItemStack input) {
                    return shouldProgress == null || ClosureHelper.call(true, shouldProgress, resonator, frequency, input);
                }
            };
            ModSupport.EXTRA_UTILITIES_2.get().resonator.add(recipe);
            return recipe;
        }
    }


    public interface ShouldProgress {

        boolean run(TileEntity resonator, int frequency, ItemStack input);
    }
}
