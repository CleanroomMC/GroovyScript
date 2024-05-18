package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.RunicShearRecipe;
import epicsquid.roots.recipe.transmutation.BlockStatePredicate;
import epicsquid.roots.recipe.transmutation.StatePredicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static epicsquid.roots.init.ModRecipes.getRunicShearRecipes;

@RegistryDescription
public class RunicShearBlock extends VirtualizedRegistry<Pair<ResourceLocation, RunicShearRecipe>> {

    @RecipeBuilderDescription(example = {
            @Example(".name('clay_from_runic_diamond').state(blockstate('minecraft:diamond_block')).replacementState(blockstate('minecraft:air')).output(item('minecraft:clay') * 64).displayItem(item('minecraft:diamond') * 9)"),
            @Example(".state(mods.roots.predicates.stateBuilder().blockstate(blockstate('minecraft:yellow_flower:type=dandelion')).properties('type').register()).replacementState(blockstate('minecraft:red_flower:type=poppy')).output(item('minecraft:gold_ingot'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> getRunicShearRecipes().remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> getRunicShearRecipes().put(pair.getKey(), pair.getValue()));
    }

    public void add(RunicShearRecipe recipe) {
        add(recipe.getRegistryName(), recipe);
    }

    public void add(ResourceLocation name, RunicShearRecipe recipe) {
        getRunicShearRecipes().put(name, recipe);
        addScripted(Pair.of(name, recipe));
    }

    public ResourceLocation findRecipe(RunicShearRecipe recipe) {
        for (Map.Entry<ResourceLocation, RunicShearRecipe> entry : getRunicShearRecipes().entrySet()) {
            if (entry.getValue().equals(recipe)) return entry.getKey();
        }
        return null;
    }

    @MethodDescription(example = @Example("resource('roots:wildewheet')"))
    public boolean removeByName(ResourceLocation name) {
        RunicShearRecipe recipe = getRunicShearRecipes().get(name);
        if (recipe == null) return false;
        getRunicShearRecipes().remove(name);
        addBackup(Pair.of(name, recipe));
        return true;
    }

    @MethodDescription(example = @Example("blockstate('minecraft:beetroots:age=3')"))
    public boolean removeByState(IBlockState state) {
        for (Map.Entry<ResourceLocation, RunicShearRecipe> x : getRunicShearRecipes().entrySet()) {
            if (x.getValue().matches(state)) {
                getRunicShearRecipes().remove(x.getKey());
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
        }
        return false;
    }

    @MethodDescription(example = @Example("item('roots:spirit_herb')"))
    public boolean removeByOutput(ItemStack output) {
        for (Map.Entry<ResourceLocation, RunicShearRecipe> x : getRunicShearRecipes().entrySet()) {
            if (ItemStack.areItemsEqual(x.getValue().getDrop(), output)) {
                getRunicShearRecipes().remove(x.getKey());
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
        }
        return false;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        getRunicShearRecipes().forEach((key, value) -> addBackup(Pair.of(key, value)));
        getRunicShearRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, RunicShearRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(getRunicShearRecipes().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    @Property(property = "name")
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RunicShearRecipe> {

        @Property
        private ItemStack displayItem;
        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private BlockStatePredicate state;
        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private IBlockState replacementState;

        @RecipeBuilderMethodDescription
        public RecipeBuilder displayItem(ItemStack displayItem) {
            this.displayItem = displayItem;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder state(IBlockState state) {
            this.state = new StatePredicate(state);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder state(BlockStatePredicate state) {
            this.state = state;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder replacementState(IBlockState replacementState) {
            this.replacementState = replacementState;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Runic Shear Block recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_runic_shear_block_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
            msg.add(state == null, "state must be defined");
            msg.add(replacementState == null, "replacementState must be defined");
            if (displayItem == null) {
                displayItem = state.matchingItems().get(0);
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RunicShearRecipe register() {
            if (!validate()) return null;
            RunicShearRecipe recipe = new RunicShearRecipe(name, state, replacementState, output.get(0), displayItem);
            ModSupport.ROOTS.get().runicShearBlock.add(name, recipe);
            return recipe;
        }
    }
}
