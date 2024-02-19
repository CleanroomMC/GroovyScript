package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.roots.ModRecipesAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.TransmutationRecipe;
import epicsquid.roots.recipe.transmutation.BlockStatePredicate;
import epicsquid.roots.recipe.transmutation.StatePredicate;
import epicsquid.roots.recipe.transmutation.WorldBlockStatePredicate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static epicsquid.roots.init.ModRecipes.removeTransmutationRecipe;

@RegistryDescription
public class Transmutation extends VirtualizedRegistry<Pair<ResourceLocation, TransmutationRecipe>> {

    @RecipeBuilderDescription(example = {
            @Example(".name('clay_duping').start(blockstate('minecraft:clay')).output(item('minecraft:clay_ball') * 30).condition(mods.roots.predicates.stateBuilder().blockstate(blockstate('minecraft:gold_block')).below().register())"),
            @Example(".start(mods.roots.predicates.stateBuilder().blockstate(blockstate('minecraft:yellow_flower:type=dandelion')).properties('type').register()).state(blockstate('minecraft:gold_block')).condition(mods.roots.predicates.above(mods.roots.predicates.LEAVES))"),
            @Example(".start(blockstate('minecraft:diamond_block')).state(blockstate('minecraft:gold_block'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> removeTransmutationRecipe(pair.getKey()));
        restoreFromBackup().forEach(pair -> ModRecipesAccessor.getTransmutationRecipes().put(pair.getKey(), pair.getValue()));
    }

    public void add(TransmutationRecipe recipe) {
        add(recipe.getRegistryName(), recipe);
    }

    public void add(ResourceLocation name, TransmutationRecipe recipe) {
        ModRecipesAccessor.getTransmutationRecipes().put(name, recipe);
        addScripted(Pair.of(name, recipe));
    }

    public ResourceLocation findRecipe(TransmutationRecipe recipe) {
        for (Map.Entry<ResourceLocation, TransmutationRecipe> entry : ModRecipesAccessor.getTransmutationRecipes().entrySet()) {
            if (entry.getValue().equals(recipe)) return entry.getKey();
        }
        return null;
    }

    @MethodDescription(example = @Example("resource('roots:redstone_block_to_glowstone')"))
    public boolean removeByName(ResourceLocation name) {
        TransmutationRecipe recipe = ModRecipesAccessor.getTransmutationRecipes().get(name);
        if (recipe == null) return false;
        removeTransmutationRecipe(name);
        addBackup(Pair.of(name, recipe));
        return true;
    }

    @MethodDescription
    public boolean removeByInput(IBlockState input) {
        return ModRecipesAccessor.getTransmutationRecipes().entrySet().removeIf(x -> {
            if (x.getValue().getStartPredicate().test(input)) {
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.roots.transmutation.removeByOutput0", example = @Example("item('minecraft:dye:3')"))
    public boolean removeByOutput(ItemStack output) {
        return ModRecipesAccessor.getTransmutationRecipes().entrySet().removeIf(x -> {
            if (ItemStack.areItemsEqual(x.getValue().getStack(), output)) {
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.roots.transmutation.removeByOutput1", example = @Example("blockstate('minecraft:log:variant=jungle')"))
    public boolean removeByOutput(IBlockState output) {
        return ModRecipesAccessor.getTransmutationRecipes().entrySet().removeIf(x -> {
            Optional<IBlockState> state = x.getValue().getState();
            if (!state.isPresent()) return false;

            Collection<IProperty<?>> incoming = output.getPropertyKeys();
            Collection<IProperty<?>> current = state.get().getPropertyKeys();

            if (state.get().getBlock() == output.getBlock() &&
                output.getPropertyKeys().stream().allMatch(prop -> incoming.contains(prop) &&
                                                                   current.contains(prop) &&
                                                                   state.get().getValue(prop).equals(output.getValue(prop)))
            ) {
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ModRecipesAccessor.getTransmutationRecipes().forEach((key, value) -> addBackup(Pair.of(key, value)));
        ModRecipesAccessor.getTransmutationRecipes().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, TransmutationRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(ModRecipesAccessor.getTransmutationRecipes().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    @Property(property = "name")
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<TransmutationRecipe> {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private BlockStatePredicate start;
        @Property
        private IBlockState state;
        @Property(defaultValue = "WorldBlockStatePredicate.TRUE")
        private WorldBlockStatePredicate condition = WorldBlockStatePredicate.TRUE;

        @RecipeBuilderMethodDescription
        public RecipeBuilder start(BlockStatePredicate start) {
            this.start = start;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder start(IBlockState start) {
            this.start = new StatePredicate(start);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder state(IBlockState state) {
            this.state = state;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "state")
        public RecipeBuilder output(IBlockState state) {
            this.state = state;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder condition(WorldBlockStatePredicate condition) {
            this.condition = condition;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Transmutation recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_transmutation_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 0, 0, 0, 1);
            validateFluids(msg);
            msg.add(start == null, "start must be defined");
            msg.add(state == null && (output.size() != 1 || output.get(0).isEmpty()), "either state must be defined or recipe must have an output");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable TransmutationRecipe register() {
            if (!validate()) return null;
            TransmutationRecipe recipe = new TransmutationRecipe(start);
            recipe.setRegistryName(name);
            if (state == null) recipe.item(output.get(0));
            else recipe.state(state);
            recipe.condition(condition);
            ModSupport.ROOTS.get().transmutation.add(name, recipe);
            return recipe;
        }
    }
}
