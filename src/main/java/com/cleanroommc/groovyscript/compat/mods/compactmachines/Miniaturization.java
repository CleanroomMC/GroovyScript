package com.cleanroommc.groovyscript.compat.mods.compactmachines;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.dave.compactmachines3.miniaturization.MultiblockRecipe;
import org.dave.compactmachines3.miniaturization.MultiblockRecipes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Miniaturization extends StandardListRegistry<MultiblockRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".name('diamond_rectangle').input(item('minecraft:clay')).output(item('minecraft:clay')).symmetrical().ticks(10).shape([['www', 'www']]).key('w', blockstate('minecraft:diamond_block'))"),
            @Example(".name('groovy_rocket').input(item('minecraft:diamond')).output(item('minecraft:clay') * 64).symmetrical().ticks(5400).key('a', blockstate('minecraft:stained_glass:0')).key('b', blockstate('minecraft:stained_glass:1')).key('c', blockstate('minecraft:stained_glass:2')).key('d', blockstate('minecraft:stained_glass:3')).key('e', blockstate('minecraft:diamond_block')).key('f', blockstate('minecraft:stained_glass:5')).key('g', blockstate('minecraft:stained_glass:6')).layer('       ', '       ', '   a   ', '  aaa  ', '   a   ', '       ', '       ').layer('       ', '   b   ', '  aaa  ', ' baaab ', '  aaa  ', '   b   ', '       ').layer('       ', '   c   ', '  cac  ', ' caeac ', '  cac  ', '   c   ', '       ').layer('       ', '   a   ', '  aaa  ', ' aaeaa ', '  aaa  ', '   a   ', '       ').layer('       ', '   a   ', '  aaa  ', ' aaeaa ', '  aaa  ', '   a   ', '       ').layer('       ', '   a   ', '  aaa  ', ' aaeaa ', '  aaa  ', '   a   ', '       ').layer('       ', '   g   ', '  cac  ', ' caeac ', '  cac  ', '   f   ', '       ').layer('       ', '   a   ', '  aaa  ', ' aaeaa ', '  aaa  ', '   a   ', '       ').layer('       ', '   a   ', '  aaa  ', ' aaeaa ', '  aaa  ', '   a   ', '       ').layer('       ', '   a   ', '  aaa  ', ' aaeaa ', '  aaa  ', '   a   ', '       ').layer('       ', '   c   ', '  cac  ', ' caeac ', '  cac  ', '   c   ', '       ').layer('       ', '   a   ', '  aaa  ', ' aaaaa ', '  aaa  ', '   a   ', '       ').layer('   a   ', '  ccc  ', ' cdddc ', 'acdddca', ' cdddc ', '  ccc  ', '   a   ')\n")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<MultiblockRecipe> getRecipes() {
        return MultiblockRecipes.getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:ender_pearl')"))
    public void removeByInput(ItemStack input) {
        for (org.dave.compactmachines3.miniaturization.MultiblockRecipe recipe : getRecipes().stream().filter(r -> r.getCatalystStack().isItemEqual(input)).collect(Collectors.toList())) {
            addBackup(recipe);
            getRecipes().removeIf(r -> r == recipe);
        }
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:redstone')"))
    public void removeByCatalyst(ItemStack catalyst) {
        removeByInput(catalyst);
    }

    @MethodDescription(example = @Example("item('compactmachines3:machine:3')"))
    public void removeByOutput(ItemStack output) {
        for (org.dave.compactmachines3.miniaturization.MultiblockRecipe recipe : getRecipes().stream().filter(r -> r.getTargetStack().isItemEqual(output)).collect(Collectors.toList())) {
            addBackup(recipe);
            getRecipes().removeIf(r -> r == recipe);
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<org.dave.compactmachines3.miniaturization.MultiblockRecipe> {

        @Property(defaultValue = "' ' = air, '_' = air")
        private final Char2ObjectOpenHashMap<ReferenceValues> keyMap = new Char2ObjectOpenHashMap<>();
        private final List<String> errors = new ArrayList<>();
        @Property
        List<List<String>> shape = new ArrayList<>();
        @Property
        private boolean symmetrical;
        @Property(defaultValue = "100")
        private int ticks = 100;

        @RecipeBuilderMethodDescription
        public RecipeBuilder shape(List<List<String>> shape) {
            this.shape = shape;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "shape")
        public RecipeBuilder layer(List<String> layer) {
            this.shape.add(layer);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "shape")
        public RecipeBuilder layer(String... layer) {
            return layer(Arrays.asList(layer));
        }

        // groovy doesn't have char literals
        @RecipeBuilderMethodDescription(field = "keyMap")
        public RecipeBuilder key(String c, IBlockState state, NBTTagCompound nbt, boolean ignoreMeta, ItemStack reference) {
            if (c == null || c.length() != 1) {
                errors.add("key must be a single char, but found '" + c + "'");
                return this;
            }
            if ("_".equals(c) || " ".equals(c)) {
                errors.add("key cannot be an underscore('_') or a space(' ')");
                return this;
            }
            this.keyMap.put(c.charAt(0), new ReferenceValues(state, nbt, ignoreMeta, reference));
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public RecipeBuilder key(String c, IBlockState state, NBTTagCompound nbt, boolean ignoreMeta) {
            return key(c, state, nbt, ignoreMeta, null);
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public RecipeBuilder key(String c, IBlockState state, NBTTagCompound nbt) {
            return key(c, state, nbt, false, null);
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public RecipeBuilder key(String c, IBlockState state, boolean ignoreMeta) {
            return key(c, state, null, ignoreMeta, null);
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public RecipeBuilder key(String c, IBlockState state) {
            return key(c, state, null, false, null);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder symmetrical(boolean symmetrical) {
            this.symmetrical = symmetrical;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder symmetrical() {
            this.symmetrical = !symmetrical;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder ticks(int ticks) {
            this.ticks = ticks;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ticks")
        public RecipeBuilder duration(int duration) {
            return ticks(duration);
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Compact Machines Multiblock recipe";
        }

        @Override
        protected int getMaxItemInput() {
            // GS's code throws the quantity away
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            for (String error : errors) {
                msg.add(error);
            }
            String missingKeys = shape.stream()
                    .flatMap(l -> l.stream().flatMap(g -> Arrays.stream(g.split("")).map(q -> q.charAt(0))))
                    .distinct()
                    .filter(x -> !(keyMap.containsKey(x) || x == ' ' || x == '_'))
                    .map(String::valueOf)
                    .collect(Collectors.joining());
            msg.add(!missingKeys.isEmpty(), "shape must contain only characters that are underscore('_'), space(' '), or declared via a key, but the following keys were not declared: {}", missingKeys);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable org.dave.compactmachines3.miniaturization.MultiblockRecipe register() {
            if (!validate()) return null;

            org.dave.compactmachines3.miniaturization.MultiblockRecipe recipe = new org.dave.compactmachines3.miniaturization.MultiblockRecipe(
                    super.name.toString(),
                    output.get(0),
                    input.get(0).getMatchingStacks()[0].getItem(),
                    input.get(0).getMatchingStacks()[0].getMetadata(),
                    input.get(0).getMatchingStacks()[0].getTagCompound(),
                    symmetrical,
                    ticks
            );

            String[][][] target = shape.stream()
                    .map(
                            l -> l.stream()
                                    .map(g -> g.replace(" ", "_").split(""))
                                    .toArray(String[][]::new))
                    .toArray(String[][][]::new);

            recipe.setPositionMap(target);

            keyMap.forEach((character, value) -> {
                String ref = String.valueOf(character);
                recipe.addBlockReference(ref, value.getState());
                if (value.getNbt() != null) recipe.addBlockVariation(ref, value.getNbt());
                recipe.setIgnoreMeta(ref, value.isIgnoreMeta());
                if (value.getReference() != null) recipe.setReferenceStack(ref, value.getReference());
            });

            ModSupport.COMPACT_MACHINES.get().miniaturization.add(recipe);
            return recipe;
        }

        @SuppressWarnings("ClassCanBeRecord")
        public static class ReferenceValues {

            private final IBlockState state;
            private final NBTTagCompound nbt;
            private final boolean ignoreMeta;
            private final ItemStack reference;

            public ReferenceValues(IBlockState state, NBTTagCompound nbt, boolean ignoreMeta, ItemStack reference) {
                this.state = state;
                this.nbt = nbt;
                this.ignoreMeta = ignoreMeta;
                this.reference = reference;
            }

            public IBlockState getState() {
                return state;
            }

            public NBTTagCompound getNbt() {
                return nbt;
            }

            public boolean isIgnoreMeta() {
                return ignoreMeta;
            }

            public ItemStack getReference() {
                return reference;
            }
        }
    }
}
