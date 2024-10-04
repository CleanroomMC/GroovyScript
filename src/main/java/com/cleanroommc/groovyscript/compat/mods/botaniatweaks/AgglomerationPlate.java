package com.cleanroommc.groovyscript.compat.mods.botaniatweaks;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import quaternary.botaniatweaks.modules.botania.recipe.AgglomerationRecipe;
import quaternary.botaniatweaks.modules.botania.recipe.AgglomerationRecipes;
import vazkii.botania.common.block.ModBlocks;

import java.util.Collection;
import java.util.Objects;

@RegistryDescription
public class AgglomerationPlate extends StandardListRegistry<AgglomerationRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond') * 3).baseStructure().mana(100000).color(0xff00ff, 0x00ffff)"),
            @Example(".input(item('minecraft:diamond'), item('minecraft:gold_ingot'), item('minecraft:gold_block')).output(item('minecraft:clay') * 32).colorStart(0x000000).colorEnd(0x0000ff).center(blockstate('minecraft:gold_block')).edge(blockstate('botania:livingwood:variant=glimmering')).corner(blockstate('botania:livingwood:variant=glimmering'))"),
            @Example(".input(item('minecraft:clay'), item('minecraft:gold_ingot')).output(item('minecraft:clay')).mana(50000).baseStructure().center(blockstate('minecraft:diamond_block')).centerReplacement(blockstate('minecraft:clay')).edgeReplacement(blockstate('botania:livingrock:variant=default')).cornerReplacement(blockstate('minecraft:lapis_block'))"),
            @Example(".input(item('minecraft:clay')).output(item('minecraft:clay')).mana(1000).center(blockstate('minecraft:clay')).edge(blockstate('botania:livingrock:variant=default')).corner(blockstate('minecraft:lapis_block')).centerReplacement(blockstate('minecraft:diamond_block')).edgeReplacement(blockstate('minecraft:lapis_block')).cornerReplacement(blockstate('botania:livingrock:variant=default'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<AgglomerationRecipe> getRecipes() {
        return AgglomerationRecipes.recipes;
    }

    @MethodDescription(example = @Example("item('botania:manaresource:4')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getRecipeOutputCopy()) && addBackup(r));
    }

    @MethodDescription(example = @Example(value = "item('botania:manaresource:2')", commented = true))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            for (var stack : r.getRecipeStacks()) {
                if (input.test(stack)) {
                    return addBackup(r);
                }
            }
            for (var string : r.getRecipeOreKeys()) {
                if (input instanceof OreDictIngredient ore && ore.getOreDict().equals(string)) {
                    return addBackup(r);
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "blockstate('botania:livingrock')", commented = true))
    public boolean removeByCenter(IBlockState center) {
        return getRecipes().removeIf(x -> x.multiblockCenter == center && addBackup(x));
    }

    @MethodDescription(example = @Example(value = "blockstate('minecraft:lapis_block')", commented = true))
    public boolean removeByEdge(IBlockState edge) {
        return getRecipes().removeIf(x -> x.multiblockEdge == edge && addBackup(x));
    }

    @MethodDescription(example = @Example(value = "blockstate('botania:livingrock')", commented = true))
    public boolean removeByCorner(IBlockState corner) {
        return getRecipes().removeIf(x -> x.multiblockCorner == corner && addBackup(x));
    }

    @Property(property = "input", comp = @Comp(gte = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AgglomerationRecipe> {

        private static final int DEFAULT_MANA = 500_000;

        @Property(defaultValue = "500,000", comp = @Comp(gte = 1))
        private int mana = DEFAULT_MANA;
        @Property
        private int colorStart;
        @Property
        private int colorEnd;
        @Property(comp = @Comp(not = "null"))
        private IBlockState center;
        @Property(comp = @Comp(not = "null"))
        private IBlockState edge;
        @Property(comp = @Comp(not = "null"))
        private IBlockState corner;
        @Property
        private IBlockState centerReplacement;
        @Property
        private IBlockState edgeReplacement;
        @Property
        private IBlockState cornerReplacement;

        @RecipeBuilderMethodDescription
        public RecipeBuilder mana(int mana) {
            this.mana = mana;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder colorStart(int colorStart) {
            this.colorStart = colorStart;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder colorEnd(int colorEnd) {
            this.colorEnd = colorEnd;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"colorStart", "colorEnd"})
        public RecipeBuilder color(int colorStart, int colorEnd) {
            this.colorStart = colorStart;
            this.colorEnd = colorEnd;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"center", "edge", "corner"})
        public RecipeBuilder baseStructure() {
            this.center = ModBlocks.livingrock.getDefaultState();
            this.edge = Objects.requireNonNull(Blocks.LAPIS_BLOCK).getDefaultState();
            this.corner = ModBlocks.livingrock.getDefaultState();
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder center(IBlockState center) {
            this.center = center;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder edge(IBlockState edge) {
            this.edge = edge;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder corner(IBlockState corner) {
            this.corner = corner;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder centerReplacement(IBlockState centerReplacement) {
            this.centerReplacement = centerReplacement;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder edgeReplacement(IBlockState edgeReplacement) {
            this.edgeReplacement = edgeReplacement;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder cornerReplacement(IBlockState cornerReplacement) {
            this.cornerReplacement = cornerReplacement;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Botania Tweaks Agglomeration Plate recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, Integer.MAX_VALUE, 1, 1);
            validateFluids(msg);
            msg.add(mana <= 0, "mana must be a positive integer greater than 0, yet it was {}", mana);
            msg.add(center == null, "center must not be null");
            msg.add(edge == null, "edge must not be null");
            msg.add(corner == null, "corner must not be null");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable AgglomerationRecipe register() {
            if (!validate()) return null;

            //noinspection UnstableApiUsage
            var recipeInputs = input.stream()
                    .map(x -> x instanceof OreDictIngredient ore ? ore.getOreDict() : x.getMatchingStacks()[0])
                    .collect(ImmutableList.toImmutableList());

            var recipe = new AgglomerationRecipe(recipeInputs, output.get(0), mana, colorStart, colorEnd, center, edge, corner, centerReplacement, edgeReplacement, cornerReplacement);
            ModSupport.BOTANIA_TWEAKS.get().agglomerationPlate.add(recipe);
            return recipe;
        }

    }

}
