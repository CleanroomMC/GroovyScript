package com.cleanroommc.groovyscript.compat.mods.extrautils2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
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
import net.minecraftforge.items.ItemHandlerHelper;
import org.codehaus.groovy.runtime.MethodClosure;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

@RegistryDescription
public class Resonator extends VirtualizedRegistry<IResonatorRecipe> {

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

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('extrautils2:ingredients:4')"))
    public boolean removeByOutput(ItemStack output) {
        return TileResonator.resonatorRecipes.removeIf(r -> {
            if (ItemHandlerHelper.canItemStacksStack(r.getOutput(), output)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:quartz_block')"))
    public boolean removeByInput(IIngredient input) {
        return TileResonator.resonatorRecipes.removeIf(r -> {
            if (input.test(r.getInputs().get(0))) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean remove(IResonatorRecipe recipe) {
        if (TileResonator.resonatorRecipes.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IResonatorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(TileResonator.resonatorRecipes).setRemover(this::remove);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        TileResonator.resonatorRecipes.forEach(this::addBackup);
        TileResonator.resonatorRecipes.clear();
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay')).rainbow().energy(1000)"),
            @Example(".input(item('minecraft:gold_block')).output(item('minecraft:clay') * 5).energy(100)"),
            @Example(".input(item('minecraft:redstone')).output(item('extrautils2:ingredients:4')).ownerTag().energy(5000)"),
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public interface ShouldProgress {

        boolean run(TileEntity resonator, int frequency, ItemStack input);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IResonatorRecipe> {

        @Property(valid = @Comp(value = "100", type = Comp.Type.GTE))
        private int energy;
        @Property
        private boolean ownerTag = false;
        @Property
        private String requirementText = "";
        @Property
        private Closure<Boolean> shouldProgress = null;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "energy")
        public RecipeBuilder cost(int totalCost) {
            return energy(totalCost);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder ownerTag() {
            this.ownerTag = !ownerTag;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder ownerTag(boolean ownerTag) {
            this.ownerTag = ownerTag;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder requirementText(String requirementText) {
            this.requirementText = requirementText;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder shouldProgress(Closure<Boolean> shouldProgress) {
            if (shouldProgress == null) {
                GroovyLog.msg("Extra Utilities 2 Resonator shouldProgress closure must be defined")
                        .error()
                        .post();
                return this;
            }
            if (!Arrays.equals(shouldProgress.getParameterTypes(), new Class[]{TileEntity.class, int.class, ItemStack.class})) {
                GroovyLog.msg("Extra Utilities 2 Resonator shouldProgress closure should be a closure with exactly three parameters:")
                        .add("net.minecraft.tileentity.TileEntity resonator, int frequency, net.minecraft.item.ItemStack input in that order.")
                        .add("but had {}, {}, {} instead", (Object[]) shouldProgress.getParameterTypes())
                        .debug()
                        .post();
            }
            this.shouldProgress = shouldProgress;
            return this;
        }

        @SuppressWarnings("unchecked")
        @RecipeBuilderMethodDescription(field = {"requirementText", "shouldProgress"})
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
        @RecipeBuilderRegistrationMethod
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
}
