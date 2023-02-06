package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.LightOreTransmutationsAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.base.LightOreTransmutations;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class LightTransmutation extends VirtualizedRegistry<LightOreTransmutations.Transmutation> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(this::remove);
        restoreFromBackup().forEach(this::add);
    }

    private void add(LightOreTransmutations.Transmutation recipe) {
        ArrayList<LightOreTransmutations.Transmutation> rts = (ArrayList<LightOreTransmutations.Transmutation>) LightOreTransmutationsAccessor.getRegisteredTransmutations();

        if (rts == null) rts = new ArrayList<>();
        rts.add(recipe);
    }

    public LightOreTransmutations.Transmutation add(Block input, IBlockState output, @Nonnull ItemStack inputDisplay, @Nonnull ItemStack outputDisplay, double cost) {
        LightOreTransmutations.Transmutation recipe = new LightOreTransmutations.Transmutation(input, output, inputDisplay, outputDisplay, cost);
        addScripted(recipe);

        ArrayList<LightOreTransmutations.Transmutation> rts = (ArrayList<LightOreTransmutations.Transmutation>) LightOreTransmutationsAccessor.getRegisteredTransmutations();

        if (rts == null) rts = new ArrayList<>();
        rts.add(recipe);

        return recipe;
    }

    public LightOreTransmutations.Transmutation add(IBlockState input, IBlockState output, @Nonnull ItemStack inputDisplay, @Nonnull ItemStack outputDisplay, double cost) {
        LightOreTransmutations.Transmutation recipe = new LightOreTransmutations.Transmutation(input, output, inputDisplay, outputDisplay, cost);
        addScripted(recipe);

        ArrayList<LightOreTransmutations.Transmutation> rts = (ArrayList<LightOreTransmutations.Transmutation>) LightOreTransmutationsAccessor.getRegisteredTransmutations();

        if (rts == null) rts = new ArrayList<>();
        rts.add(recipe);

        return recipe;
    }

    private void remove(LightOreTransmutations.Transmutation recipe) {
        ArrayList<LightOreTransmutations.Transmutation> rts = (ArrayList<LightOreTransmutations.Transmutation>) LightOreTransmutationsAccessor.getRegisteredTransmutations();

        if (rts == null) return;
        rts.removeIf(rec -> rec.equals(recipe));
    }

    public void removeByOutput(IBlockState block) {
        ArrayList<LightOreTransmutations.Transmutation> rts = (ArrayList<LightOreTransmutations.Transmutation>) LightOreTransmutationsAccessor.getRegisteredTransmutations();

        if (rts == null) return;
        rts.forEach(rec -> {
            if (rec.matchesOutput(block)) {
                addBackup(rec);
            }
        });
        rts.removeIf(rec -> rec.matchesOutput(block));
    }

    public void removeByOutput(Block block) {
        this.removeByOutput(block.getDefaultState());
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<LightOreTransmutations.Transmutation> {

        private Block inBlock = null;
        private IBlockState input = null;
        private IBlockState output = null;
        private double cost = 0;
        private ItemStack outStack = null;
        private ItemStack inStack = null;

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

        @Override
        public String getErrorMsg() {
            return "Error adding Astral Sorcery Light Transmutation recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(this.input == null && this.inBlock == null, () -> "Input cannot be null");
            msg.add(this.output == null, () -> "Output cannot be null");
            msg.add(this.cost < 0, () -> "Cost cannot be negative");
            if (this.inStack == null && this.inBlock != null) this.inStack = new ItemStack(this.inBlock);
            if (this.inStack == null && this.input != null) this.inStack = new ItemStack(this.input.getBlock());
            if (this.outStack == null && this.output != null) this.outStack = new ItemStack(this.output.getBlock());
        }

        public LightOreTransmutations.Transmutation register() {
            if(!validate()) return null;
            if (inBlock == null) {
                return ModSupport.ASTRAL_SORCERY.get().lightTransmutation.add(input, output, inStack, outStack, cost);
            }
            return ModSupport.ASTRAL_SORCERY.get().lightTransmutation.add(inBlock, output, inStack, outStack, cost);
        }
    }
}
