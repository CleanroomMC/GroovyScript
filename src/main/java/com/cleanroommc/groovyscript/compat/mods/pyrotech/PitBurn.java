package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.ItemStackList;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.ModPyrotech;
import com.codetaylor.mc.pyrotech.library.util.BlockMetaMatcher;
import com.codetaylor.mc.pyrotech.modules.tech.refractory.ModuleTechRefractory;
import com.codetaylor.mc.pyrotech.modules.tech.refractory.recipe.PitBurnRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.refractory.recipe.PitBurnRecipeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

@RegistryDescription
public class PitBurn extends ForgeRegistryWrapper<PitBurnRecipe> {

    public PitBurn() {
        super(ModuleTechRefractory.Registries.BURN_RECIPE);
    }

    @Override
    public boolean isEnabled() {
        return ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechRefractory.class);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:cauldron')).output(item('minecraft:cobblestone')).fluidOutput(fluid('water') * 50).burnStages(6).burnTime(1200).name('water_from_cauldron')"),
            @Example(".input(item('minecraft:soul_sand')).output(item('minecraft:sand')).fluidOutput(fluid('lava') * 200).requiresRefractoryBlocks(true).burnStages(2).burnTime(600).failureChance(0.25F).failureOutput(item('minecraft:gravel') * 2).failureOutput(item('minecraft:dirt') * 3).name('lava_to_sand')"),
            @Example(".input(blockstate('minecraft:sponge', 'wet=true')).output(item('minecraft:sponge')).fluidOutput(fluid('water') * 25).fluidLevelAffectsFailureChance(true).burnStages(10).burnTime(500).name('sponge_dehydrating')"),
            @Example(".input(item('minecraft:chest')).output(item('minecraft:ender_chest')).fluidOutput(fluid('lava') * 125).fluidLevelAffectsFailureChance(true).requiresRefractoryBlocks(true).burnStages(4).burnTime(2000).name('chest_burning')")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:coal', 1) * 10"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing pit burning recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (PitBurnRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:coal_block')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing pit burning recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        if (input.getMetadata() > 15 && input.getMetadata() != OreDictionary.WILDCARD_VALUE) return;
        Block block = getBlock(input);
        if (block == Blocks.AIR) return;
        for (PitBurnRecipe recipe : getRegistry()) {
            BlockMetaMatcher matcher = recipe.getInputMatcher();
            if (matcher.getBlock() == block && (matcher.getMeta() == OreDictionary.WILDCARD_VALUE || input.getMetadata() == OreDictionary.WILDCARD_VALUE || matcher.getMeta() == input.getMetadata())) {
                remove(recipe);
            }
        }
    }

    public static class RecipeBuilder implements IRecipeBuilder<PitBurnRecipe> {

        @Property(comp = @Comp(not = "null"))
        private ResourceLocation name;
        @Property(comp = @Comp(not = "null"))
        private MatcherPredicate input;
        @Property(comp = @Comp(not = "null"))
        private ItemStack output;
        @Property(comp = @Comp(gt = 0))
        private int burnStages;
        @Property(comp = @Comp(gt = 0))
        private int burnTime;
        @Property
        private FluidStack fluidOutput;
        @Property(comp = @Comp(gte = 0, lte = 1))
        private float failureChance;
        @Property
        private final ItemStackList failureOutput = new ItemStackList();
        @Property
        private boolean requiresRefractoryBlocks;
        @Property
        private boolean fluidLevelAffectsFailureChance;

        @RecipeBuilderMethodDescription
        public RecipeBuilder name(String name) {
            if (name.contains(":")) {
                this.name = new ResourceLocation(name);
            } else {
                this.name = new ResourceLocation(GroovyScript.getRunConfig().getPackId(), name);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder name(ResourceLocation name) {
            this.name = name;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(ItemStack input) {
            return input(getBlock(input), input.getMetadata());
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(IBlockState state) {
            return input(state.getBlock(), state.getBlock().getMetaFromState(state));
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(Block block, int metadata) {
            if (block == Blocks.AIR) {
                return this;
            }
            Predicate<IBlockState> predicate = s -> s.getBlock() == block && (metadata == OreDictionary.WILDCARD_VALUE || s.getBlock().getMetaFromState(s) == metadata);
            if (this.input == null) {
                this.input = new MatcherPredicate(block, metadata, predicate);
            } else if (this.input.getBlock() == Blocks.AIR) {
                this.input = new MatcherPredicate(block, metadata, this.input.predicate.or(predicate));
            } else {
                this.input = new MatcherPredicate(this.input.getBlock(), this.input.getMeta(), this.input.predicate.or(predicate));
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder output(ItemStack output) {
            this.output = output;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder burnStages(int burnStages) {
            this.burnStages = burnStages;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder burnTime(int burnTime) {
            this.burnTime = burnTime;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder fluidOutput(FluidStack fluidOutput) {
            this.fluidOutput = fluidOutput;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder failureChance(float failureChance) {
            this.failureChance = failureChance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder failureOutput(ItemStack failureOutputs) {
            this.failureOutput.add(failureOutputs);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder failureOutput(ItemStack... failureOutputs) {
            for (ItemStack itemStack : failureOutputs) {
                failureOutput(itemStack);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder failureOutput(Iterable<ItemStack> failureOutputs) {
            for (ItemStack itemStack : failureOutputs) {
                failureOutput(itemStack);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder requiresRefractoryBlocks(boolean requiresRefractoryBlocks) {
            this.requiresRefractoryBlocks = requiresRefractoryBlocks;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder fluidLevelAffectsFailureChance(boolean fluidLevelAffectsFailureChance) {
            this.fluidLevelAffectsFailureChance = fluidLevelAffectsFailureChance;
            return this;
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding Pyrotech Pit Burn Recipe");
            msg.add(name == null, "name cannot be null");
            msg.add(input == null, "input is null");
            msg.add(output == null, "output is empty");
            msg.add(burnStages <= 0, "burnStages must be a non negative integer that is larger than 0, yet it was {}", burnStages);
            msg.add(burnTime <= 0, "burnTime must be a non negative integer that is larger than 0, yet it was {}", burnTime);
            msg.add(failureChance < 0 || failureChance > 1, "failureChance must not be negative nor larger than 1.0, yet it was {}", failureChance);
            msg.add(fluidOutput != null && fluidOutput.amount * burnStages > 500, "fluidOutput amount must not be larger than 500");
            msg.add(ModSupport.PYROTECH.get().pitBurn.getRegistry().getValue(name) != null, "tried to register {}, but it already exists.", name);
            return false;
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable PitBurnRecipe register() {
            PitBurnRecipeBuilder builder = new PitBurnRecipeBuilder(output, input)
                    .setBurnStages(burnStages)
                    .setTotalBurnTimeTicks(burnTime)
                    .setFluidProduced(fluidOutput)
                    .setFailureChance(failureChance)
                    .setRequiresRefractoryBlocks(requiresRefractoryBlocks)
                    .setFluidLevelAffectsFailureChance(fluidLevelAffectsFailureChance);
            failureOutput.forEach(builder::addFailureItem);
            PitBurnRecipe recipe = builder.create(name);
            ModSupport.PYROTECH.get().pitBurn.add(recipe);
            return recipe;
        }
    }

    private static class MatcherPredicate extends BlockMetaMatcher {

        private final Predicate<IBlockState> predicate;

        public MatcherPredicate(Block block, int metadata, Predicate<IBlockState> predicate) {
            super(block, metadata);
            this.predicate = predicate;
        }

        @Override
        public boolean test(IBlockState blockState) {
            return this.predicate.test(blockState);
        }
    }

    private static Block getBlock(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemBlock itemBlock) {
            return itemBlock.getBlock();
        }
        if (item instanceof ItemBlockSpecial itemBlockSpecial) {
            return itemBlockSpecial.getBlock();
        }
        Block block = ForgeRegistries.BLOCKS.getValue(item.getRegistryName());
        if (block == null) block = Blocks.AIR;
        return block;
    }
}
