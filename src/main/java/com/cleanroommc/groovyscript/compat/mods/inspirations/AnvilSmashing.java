package com.cleanroommc.groovyscript.compat.mods.inspirations;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.inspirations.InspirationsRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

@RegistryDescription
public class AnvilSmashing extends VirtualizedRegistry<Pair<IBlockState, IBlockState>> {

    private final AbstractReloadableStorage<Pair<Block, IBlockState>> blockStorage = new AbstractReloadableStorage<>();
    private final AbstractReloadableStorage<Material> materialStorage = new AbstractReloadableStorage<>();

    @RecipeBuilderDescription(example = {
            @Example(".input(blockstate('minecraft:diamond_block')).output(blockstate('minecraft:clay'))"),
            @Example(".input(blockstate('minecraft:clay')).output(blockstate('minecraft:air'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> InspirationsRegistryAccessor.getAnvilSmashing().remove(pair.getKey(), pair.getValue()));
        restoreFromBackup().forEach(pair -> InspirationsRegistryAccessor.getAnvilSmashing().put(pair.getKey(), pair.getValue()));
        blockStorage.removeScripted().forEach(pair -> InspirationsRegistryAccessor.getAnvilSmashingBlocks().remove(pair.getKey(), pair.getValue()));
        blockStorage.restoreFromBackup().forEach(pair -> InspirationsRegistryAccessor.getAnvilSmashingBlocks().put(pair.getKey(), pair.getValue()));
        materialStorage.restoreFromBackup().forEach(mat -> InspirationsRegistryAccessor.getAnvilBreaking().add(mat));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(IBlockState input, IBlockState output) {
        addScripted(Pair.of(input, output));
        InspirationsRegistry.registerAnvilSmashing(input, output);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(Block input, IBlockState output) {
        blockStorage.addScripted(Pair.of(input, output));
        InspirationsRegistry.registerAnvilSmashing(input, output);
    }

    @MethodDescription
    public boolean remove(IBlockState input, IBlockState output) {
        if (!InspirationsRegistryAccessor.getAnvilSmashing().get(input).equals(output)) return false;
        addBackup(Pair.of(input, output));
        InspirationsRegistryAccessor.getAnvilSmashing().remove(input, output);
        return true;
    }

    @MethodDescription
    public boolean remove(Block input, IBlockState output) {
        if (!InspirationsRegistryAccessor.getAnvilSmashingBlocks().get(input).equals(output)) return false;
        blockStorage.addBackup(Pair.of(input, output));
        InspirationsRegistryAccessor.getAnvilSmashingBlocks().remove(input, output);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.inspirations.anvil_smashing.remove_material")
    public boolean remove(Material material) {
        if (!InspirationsRegistryAccessor.getAnvilBreaking().contains(material)) return false;
        materialStorage.addBackup(material);
        InspirationsRegistryAccessor.getAnvilBreaking().remove(material);
        return true;
    }

    @MethodDescription
    public void removeByInput(IBlockState input) {
        for (Map.Entry<IBlockState, IBlockState> recipe : InspirationsRegistryAccessor.getAnvilSmashing().entrySet().stream()
                .filter(r -> r.getKey().equals(input))
                .collect(Collectors.toList())) {
            addBackup(Pair.of(recipe.getKey(), recipe.getValue()));
            InspirationsRegistryAccessor.getAnvilSmashing().remove(recipe.getKey(), recipe.getValue());
        }
    }

    @MethodDescription(example = @Example("blockstate('minecraft:packed_ice')"))
    public void removeByInput(Block input) {
        for (Map.Entry<Block, IBlockState> recipe : InspirationsRegistryAccessor.getAnvilSmashingBlocks().entrySet().stream()
                .filter(r -> r.getKey().equals(input))
                .collect(Collectors.toList())) {
            blockStorage.addBackup(Pair.of(recipe.getKey(), recipe.getValue()));
            InspirationsRegistryAccessor.getAnvilSmashingBlocks().remove(recipe.getKey(), recipe.getValue());
        }
    }

    @MethodDescription(example = @Example("blockstate('minecraft:cobblestone')"))
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
            blockStorage.addBackup(Pair.of(recipe.getKey(), recipe.getValue()));
            InspirationsRegistryAccessor.getAnvilSmashingBlocks().remove(recipe.getKey(), recipe.getValue());
        }
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        InspirationsRegistryAccessor.getAnvilSmashing().forEach((a, b) -> addBackup(Pair.of(a, b)));
        InspirationsRegistryAccessor.getAnvilSmashing().clear();
        InspirationsRegistryAccessor.getAnvilSmashingBlocks().forEach((a, b) -> blockStorage.addBackup(Pair.of(a, b)));
        InspirationsRegistryAccessor.getAnvilSmashingBlocks().clear();
        InspirationsRegistryAccessor.getAnvilBreaking().forEach(materialStorage::addBackup);
        InspirationsRegistryAccessor.getAnvilBreaking().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<IBlockState, IBlockState>> streamRecipes() {
        return new SimpleObjectStream<>(InspirationsRegistryAccessor.getAnvilSmashing().entrySet())
                .setRemover(r -> remove(r.getKey(), r.getValue()));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<Object> {

        @Property(requirement = "groovyscript.wiki.inspirations.anvil_smashing.input.required")
        private IBlockState inputBlockState;
        @Property(requirement = "groovyscript.wiki.inspirations.anvil_smashing.input.required")
        private Block inputBlock;
        @Property(ignoresInheritedMethods = true, valid = @Comp(value = "null", type = Comp.Type.NOT))
        private IBlockState output;


        @RecipeBuilderMethodDescription(field = "inputBlock")
        public RecipeBuilder input(Block input) {
            this.inputBlock = input;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "inputBlockState")
        public RecipeBuilder input(IBlockState input) {
            this.inputBlockState = input;
            return this;
        }

        @RecipeBuilderMethodDescription
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
        @RecipeBuilderRegistrationMethod
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
