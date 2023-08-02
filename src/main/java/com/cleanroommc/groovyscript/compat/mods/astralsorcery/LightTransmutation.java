package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.LightOreTransmutationsAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.base.LightOreTransmutations;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class LightTransmutation extends VirtualizedRegistry<LightOreTransmutations.Transmutation> {

    private static List<LightOreTransmutations.Transmutation> getRegistry() {
        if (LightOreTransmutationsAccessor.getRegisteredTransmutations() == null) {
            throw new IllegalStateException("Astral Sorcery Light Transmutation getRegisteredTransmutations() is not yet initialized!");
        }
        return (ArrayList<LightOreTransmutations.Transmutation>) LightOreTransmutationsAccessor.getRegisteredTransmutations();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(r -> getRegistry().removeIf(recipe -> r == recipe));
        restoreFromBackup().forEach(r -> getRegistry().add(r));
    }

    private void add(LightOreTransmutations.Transmutation recipe) {
        getRegistry().add(recipe);
        addScripted(recipe);
    }

    public LightOreTransmutations.Transmutation add(Block input, IBlockState output,
                                                    @Nonnull ItemStack inputDisplay, @Nonnull ItemStack outputDisplay, double cost) {
        LightOreTransmutations.Transmutation recipe = new LightOreTransmutations.Transmutation(input, output, inputDisplay, outputDisplay, cost);
        addScripted(recipe);
        getRegistry().add(recipe);
        return recipe;
    }

    public LightOreTransmutations.Transmutation add(IBlockState input, IBlockState output,
                                                    @Nonnull ItemStack inputDisplay, @Nonnull ItemStack outputDisplay, double cost) {
        LightOreTransmutations.Transmutation recipe = new LightOreTransmutations.Transmutation(input, output, inputDisplay, outputDisplay, cost);
        addScripted(recipe);
        getRegistry().add(recipe);
        return recipe;
    }

    private boolean remove(LightOreTransmutations.Transmutation recipe) {
        return getRegistry().removeIf(rec -> rec.equals(recipe));
    }

    public void removeByInput(IBlockState block) {
        getRegistry().removeIf(rec -> {
            if (rec.matchesInput(block)) {
                addBackup(rec);
                return true;
            }
            return false;
        });
    }

    public void removeByInput(Block block) {
        removeByInput(block.getDefaultState());
    }

    public void removeByOutput(IBlockState block) {
        getRegistry().removeIf(rec -> {
            if (rec.matchesOutput(block)) {
                addBackup(rec);
                return true;
            }
            return false;
        });
    }

    public void removeByOutput(Block block) {
        removeByOutput(block.getDefaultState());
    }

    public SimpleObjectStream<LightOreTransmutations.Transmutation> streamRecipes() {
        return new SimpleObjectStream<>(getRegistry())
                .setRemover(this::remove);
    }

    public void removeAll() {
        getRegistry().forEach(this::addBackup);
        getRegistry().clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<LightOreTransmutations.Transmutation> {

        private Block inBlock = null;
        private IBlockState input = null;
        private IBlockState output = null;
        private double cost = 0;
        private ItemStack outStack = null;
        private ItemStack inStack = null;
        private IWeakConstellation constellation = null;

        public RecipeBuilder inputDisplayStack(ItemStack item) {
            this.inStack = item;
            return this;
        }

        public RecipeBuilder outputDisplayStack(ItemStack item) {
            this.outStack = item;
            return this;
        }

        public RecipeBuilder input(Block target) {
            this.inBlock = target;
            return this;
        }

        public RecipeBuilder input(IBlockState target) {
            this.input = target;
            return this;
        }

        public RecipeBuilder output(Block target) {
            this.output = target.getDefaultState();
            return this;
        }

        public RecipeBuilder output(IBlockState target) {
            this.output = target;
            return this;
        }

        public RecipeBuilder cost(double cost) {
            this.cost = cost;
            return this;
        }

        public RecipeBuilder constellation(IWeakConstellation constellation) {
            this.constellation = constellation;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Astral Sorcery Light Transmutation recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(input == null && inBlock == null, () -> "Input cannot be null");
            msg.add(output == null, () -> "Output cannot be null");
            msg.add(cost < 0, () -> "Cost cannot be negative");
            if (inStack == null && inBlock != null) inStack = new ItemStack(inBlock);
            if (inStack == null && input != null) inStack = new ItemStack(input.getBlock());
            if (outStack == null && output != null) outStack = new ItemStack(output.getBlock());
        }

        public LightOreTransmutations.Transmutation register() {
            if (!validate()) return null;
            LightOreTransmutations.Transmutation recipe;
            if (inBlock == null) {
                recipe = new LightOreTransmutations.Transmutation(input, output, inStack, outStack, cost);
            } else {
                recipe = new LightOreTransmutations.Transmutation(inBlock, output, inStack, outStack, cost);
            }
            recipe.setRequiredType(constellation);
            ModSupport.ASTRAL_SORCERY.get().lightTransmutation.add(recipe);
            return recipe;
        }
    }
}
