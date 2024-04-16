package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.BarkRecipe;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static epicsquid.roots.init.ModRecipes.*;

@RegistryDescription
public class BarkCarving extends VirtualizedRegistry<Pair<ResourceLocation, BarkRecipe>> {

    public BarkCarving() {
        super(Alias.generateOfClassAnd(BarkCarving.class, "Bark"));
    }

    @RecipeBuilderDescription(example = {
            @Example(".name('gold_bark').input(item('minecraft:clay')).output(item('minecraft:gold_ingot'))"),
            @Example(".blockstate(blockstate('minecraft:gold_block')).output(item('minecraft:diamond'))"),
            @Example(".input(blockstate('minecraft:diamond_block')).output(item('minecraft:clay') * 10)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> getBarkRecipeMap().remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> getBarkRecipeMap().put(pair.getKey(), pair.getValue()));
    }

    public void add(BarkRecipe recipe) {
        add(recipe.getName(), recipe);
    }

    public void add(ResourceLocation name, BarkRecipe recipe) {
        getBarkRecipeMap().put(name, recipe);
        addScripted(Pair.of(name, recipe));
    }

    public ResourceLocation findRecipe(BarkRecipe recipe) {
        return getBarkRecipeByName(recipe.getName()) == null ? null : recipe.getName();
    }

    public ResourceLocation findRecipeByInput(BlockPlanks.EnumType input) {
        for (BarkRecipe entry : getBarkRecipes()) {
            if (entry.getType() == input) return entry.getName();
        }
        return null;
    }

    public ResourceLocation findRecipeByInput(ItemStack input) {
        for (BarkRecipe entry : getBarkRecipes()) {
            if (ItemStack.areItemsEqual(entry.getBlockStack(), input)) return entry.getName();
        }
        return null;
    }

    public ResourceLocation findRecipeByOutput(ItemStack output) {
        for (BarkRecipe entry : getBarkRecipes()) {
            if (ItemStack.areItemsEqual(entry.getItem(), output)) return entry.getName();
        }
        return null;
    }

    @MethodDescription(example = @Example("resource('roots:wildwood')"))
    public boolean removeByName(ResourceLocation name) {
        BarkRecipe recipe = getBarkRecipeByName(name);
        if (recipe == null) return false;
        removeBarkRecipe(recipe.getBlockStack());
        addBackup(Pair.of(name, recipe));
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:log')"))
    public boolean removeByInput(ItemStack input) {
        for (Map.Entry<ResourceLocation, BarkRecipe> x : getBarkRecipeMap().entrySet()) {
            if (ItemStack.areItemsEqual(x.getValue().getBlockStack(), input)) {
                getBarkRecipeMap().remove(x.getKey());
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
        }
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:log:1')"))
    public boolean removeByBlock(ItemStack block) {
        return removeByInput(block);
    }

    @MethodDescription(example = @Example("item('roots:bark_dark_oak')"))
    public boolean removeByOutput(ItemStack output) {
        for (Map.Entry<ResourceLocation, BarkRecipe> x : getBarkRecipeMap().entrySet()) {
            if (ItemStack.areItemsEqual(x.getValue().getItem(), output)) {
                getBarkRecipeMap().remove(x.getKey());
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
        }
        return false;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        getBarkRecipeMap().forEach((key, value) -> addBackup(Pair.of(key, value)));
        getBarkRecipeMap().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<BarkRecipe> streamRecipes() {
        return new SimpleObjectStream<>(getBarkRecipes())
                .setRemover(r -> this.removeByName(r.getName()));
    }

    @Property(property = "name")
    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<BarkRecipe> {

        @RecipeBuilderMethodDescription(field = "input")
        public RecipeBuilder blockstate(IBlockState blockstate) {
            return this.input(blockstate);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(IBlockState blockstate) {
            this.input.add(IngredientHelper.toIIngredient(new ItemStack(blockstate.getBlock(), 1, blockstate.getBlock().damageDropped(blockstate))));
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Bark Carving recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_bark_carving_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable BarkRecipe register() {
            if (!validate()) return null;
            BarkRecipe recipe;
            recipe = new BarkRecipe(name, output.get(0), input.get(0).toMcIngredient().getMatchingStacks()[0]);
            ModSupport.ROOTS.get().barkCarving.add(name, recipe);
            return recipe;
        }
    }
}
