package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.LightOreTransmutationsAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import hellfirepvp.astralsorcery.common.base.LightOreTransmutations;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collection;

@RegistryDescription
public class LightTransmutation extends StandardListRegistry<LightOreTransmutations.Transmutation> {

    @Override
    public Collection<LightOreTransmutations.Transmutation> getRegistry() {
        if (LightOreTransmutationsAccessor.getRegisteredTransmutations() == null) {
            throw new IllegalStateException("Astral Sorcery Light Transmutation getRegisteredTransmutations() is not yet initialized!");
        }
        return LightOreTransmutationsAccessor.getRegisteredTransmutations();
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(block('minecraft:stone')).output(block('astralsorcery:blockmarble')).cost(100.0).constellation(constellation('armara')).inputDisplayStack(item('minecraft:stone')).outputDisplayStack(item('minecraft:dye:15').withNbt([display:[Name:'Marble']])) "),
            @Example(".input(blockstate('minecraft:pumpkin')).output(blockstate('minecraft:diamond_block')).cost(0)")})
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
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

    @MethodDescription(example = @Example("blockstate('minecraft:sandstone')"))
    public void removeByInput(IBlockState block) {
        getRegistry().removeIf(rec -> {
            if (rec.matchesInput(block)) {
                addBackup(rec);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("block('minecraft:netherrack')"))
    public void removeByInput(Block block) {
        removeByInput(block.getDefaultState());
    }

    @MethodDescription(example = @Example("blockstate('minecraft:cake')"))
    public void removeByOutput(IBlockState block) {
        getRegistry().removeIf(rec -> {
            if (rec.matchesOutput(block)) {
                addBackup(rec);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("block('minecraft:lapis_block')"))
    public void removeByOutput(Block block) {
        removeByOutput(block.getDefaultState());
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<LightOreTransmutations.Transmutation> {

        @Property(valid = {@Comp(value = "null", type = Comp.Type.NOT), @Comp(value = "input", type = Comp.Type.NOT)})
        private Block inBlock;
        @Property(ignoresInheritedMethods = true, valid = {@Comp(value = "null", type = Comp.Type.NOT), @Comp(value = "inBlock", type = Comp.Type.NOT)})
        private IBlockState input;
        @Property(ignoresInheritedMethods = true, valid = @Comp(value = "null", type = Comp.Type.NOT))
        private IBlockState output;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private double cost;
        @Property
        private ItemStack outStack;
        @Property
        private ItemStack inStack;
        @Property
        private IWeakConstellation constellation;

        @RecipeBuilderMethodDescription(field = "inStack")
        public RecipeBuilder inputDisplayStack(ItemStack item) {
            this.inStack = item;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "outStack")
        public RecipeBuilder outputDisplayStack(ItemStack item) {
            this.outStack = item;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "inBlock")
        public RecipeBuilder input(Block target) {
            this.inBlock = target;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(IBlockState target) {
            this.input = target;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder output(Block target) {
            this.output = target.getDefaultState();
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder output(IBlockState target) {
            this.output = target;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder cost(double cost) {
            this.cost = cost;
            return this;
        }

        @RecipeBuilderMethodDescription
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

        @Override
        @RecipeBuilderRegistrationMethod
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
