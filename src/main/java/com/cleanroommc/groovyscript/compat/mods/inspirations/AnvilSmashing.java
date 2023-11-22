package com.cleanroommc.groovyscript.compat.mods.inspirations;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.inspirations.InspirationsRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class AnvilSmashing extends VirtualizedRegistry<Pair<IBlockState, IBlockState>> {

    private Collection<Pair<Block, IBlockState>> blockBackup;
    private Collection<Pair<Block, IBlockState>> blockScripted;
    private Collection<Material> materialBackup;

    private void addBlockBackup(Pair<Block, IBlockState> recipe) {
        if (blockScripted.stream().anyMatch(r -> r == recipe)) return;
        blockBackup.add(recipe);
    }

    public void addBlockScripted(Pair<Block, IBlockState> recipe) {
        blockScripted.add(recipe);
    }

    public AnvilSmashing() {
        super();
        blockBackup = new ArrayList<>();
        blockScripted = new ArrayList<>();
        materialBackup = new ArrayList<>();
    }


    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> InspirationsRegistryAccessor.getAnvilSmashing().remove(pair.getKey(), pair.getValue()));
        restoreFromBackup().forEach(pair -> InspirationsRegistryAccessor.getAnvilSmashing().put(pair.getKey(), pair.getValue()));
        blockBackup.forEach(pair -> InspirationsRegistryAccessor.getAnvilSmashingBlocks().remove(pair.getKey(), pair.getValue()));
        blockScripted.forEach(pair -> InspirationsRegistryAccessor.getAnvilSmashingBlocks().put(pair.getKey(), pair.getValue()));
        materialBackup.forEach(mat -> InspirationsRegistryAccessor.getAnvilBreaking().add(mat));
        blockBackup = new ArrayList<>();
        blockScripted = new ArrayList<>();
        materialBackup = new ArrayList<>();
    }

    public void add(IBlockState input, IBlockState output) {
        addScripted(Pair.of(input, output));
        InspirationsRegistry.registerAnvilSmashing(input, output);
    }

    public void add(Block input, IBlockState output) {
        addBlockScripted(Pair.of(input, output));
        InspirationsRegistry.registerAnvilSmashing(input, output);
    }

    public boolean remove(IBlockState input, IBlockState output) {
        if (!InspirationsRegistryAccessor.getAnvilSmashing().get(input).equals(output)) return false;
        addBackup(Pair.of(input, output));
        InspirationsRegistryAccessor.getAnvilSmashing().remove(input, output);
        return true;
    }

    public boolean remove(Block input, IBlockState output) {
        if (!InspirationsRegistryAccessor.getAnvilSmashingBlocks().get(input).equals(output)) return false;
        addBlockBackup(Pair.of(input, output));
        InspirationsRegistryAccessor.getAnvilSmashingBlocks().remove(input, output);
        return true;
    }

    public boolean remove(Material material) {
        if (!InspirationsRegistryAccessor.getAnvilBreaking().contains(material)) return false;
        materialBackup.add(material);
        InspirationsRegistryAccessor.getAnvilBreaking().remove(material);
        return true;
    }

    public void removeByInput(IBlockState input) {
        for (Map.Entry<IBlockState, IBlockState> recipe : InspirationsRegistryAccessor.getAnvilSmashing().entrySet().stream()
                .filter(r -> r.getKey().equals(input))
                .collect(Collectors.toList())) {
            addBackup(Pair.of(recipe.getKey(), recipe.getValue()));
            InspirationsRegistryAccessor.getAnvilSmashing().remove(recipe.getKey(), recipe.getValue());
        }
    }

    public void removeByInput(Block input) {
        for (Map.Entry<Block, IBlockState> recipe : InspirationsRegistryAccessor.getAnvilSmashingBlocks().entrySet().stream()
                .filter(r -> r.getKey().equals(input))
                .collect(Collectors.toList())) {
            addBlockBackup(Pair.of(recipe.getKey(), recipe.getValue()));
            InspirationsRegistryAccessor.getAnvilSmashingBlocks().remove(recipe.getKey(), recipe.getValue());
        }
    }

    public void removeByOutput(IBlockState output) {
        for (Map.Entry<IBlockState, IBlockState> recipe : InspirationsRegistryAccessor.getAnvilSmashing().entrySet().stream()
                .filter(r -> r.getValue().equals(output))
                .collect(Collectors.toList())) {
            addBackup(Pair.of(recipe.getKey(), recipe.getValue()));
            InspirationsRegistryAccessor.getAnvilSmashing().remove(recipe.getKey(), recipe.getValue());
        }
        for (Map.Entry<Block, IBlockState> recipe : InspirationsRegistryAccessor.getAnvilSmashingBlocks().entrySet().stream()
                .filter(r -> r.getValue().equals(output))
                .collect(Collectors.toList())) {
            addBlockBackup(Pair.of(recipe.getKey(), recipe.getValue()));
            InspirationsRegistryAccessor.getAnvilSmashingBlocks().remove(recipe.getKey(), recipe.getValue());
        }
    }

    public void removeAll() {
        InspirationsRegistryAccessor.getAnvilSmashing().forEach((a, b) -> addBackup(Pair.of(a, b)));
        InspirationsRegistryAccessor.getAnvilSmashing().clear();
        InspirationsRegistryAccessor.getAnvilSmashingBlocks().forEach((a, b) -> addBlockBackup(Pair.of(a, b)));
        InspirationsRegistryAccessor.getAnvilSmashingBlocks().clear();
        materialBackup.addAll(InspirationsRegistryAccessor.getAnvilBreaking());
        InspirationsRegistryAccessor.getAnvilBreaking().clear();
    }

    public SimpleObjectStream<Map.Entry<IBlockState, IBlockState>> streamRecipes() {
        return new SimpleObjectStream<>(InspirationsRegistryAccessor.getAnvilSmashing().entrySet())
                .setRemover(r -> remove(r.getKey(), r.getValue()));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<Object> {

        private IBlockState inputBlockState;
        private Block inputBlock;
        private IBlockState output;


        public RecipeBuilder input(Block input) {
            this.inputBlock = input;
            return this;
        }

        public RecipeBuilder input(IBlockState input) {
            this.inputBlockState = input;
            return this;
        }

        public RecipeBuilder output(IBlockState output) {
            this.output = output;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Inspirations Anvil Smashing recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(inputBlockState == null && inputBlock == null, "either inputBlockState or inputBlock must be defined");
            msg.add(output == null, "output must be defined");
        }

        @Override
        public @Nullable Object register() {
            if (!validate()) return null;
            if (inputBlock == null) {
                ModSupport.INSPIRATIONS.get().anvilSmashing.add(inputBlockState, output);
                return Pair.of(inputBlockState, output);
            }
            ModSupport.INSPIRATIONS.get().anvilSmashing.add(inputBlock, output);
            return Pair.of(inputBlock, output);
        }
    }

}
