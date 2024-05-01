package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.device;

import cofh.core.util.BlockWrapper;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.TapperManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.github.bsideup.jabel.Desugar;
import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class TapperTree extends VirtualizedRegistry<TapperTree.TapperTreeEntry> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> TapperManagerAccessor.getLeafMap().entries().removeIf(r -> r.getKey().equals(recipe.log()) && r.getValue().equals(recipe.leaf())));
        restoreFromBackup().forEach(r -> TapperManagerAccessor.getLeafMap().put(r.log(), r.leaf()));
    }

    public void add(TapperTreeEntry recipe) {
        TapperManagerAccessor.getLeafMap().put(recipe.log(), recipe.leaf());
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("blockstate('minecraft:clay'), blockstate('minecraft:gold_block')"))
    public void add(IBlockState log, IBlockState leaf) {
        add(new TapperTreeEntry(new BlockWrapper(log), new BlockWrapper(leaf)));
    }

    public boolean remove(TapperTreeEntry entry) {
        return TapperManagerAccessor.getLeafMap().entries().removeIf(r -> {
            if (entry.log().equals(r.getKey()) && entry.leaf().equals(r.getValue())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    public boolean remove(BlockWrapper input, BlockWrapper output) {
        return remove(new TapperTreeEntry(input, output));
    }

    @MethodDescription
    public boolean removeByLog(BlockWrapper input) {
        return TapperManagerAccessor.getLeafMap().entries().removeIf(r -> {
            if (input.equals(r.getKey())) {
                addBackup(new TapperTreeEntry(r.getKey(), r.getValue()));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("blockstate('minecraft:log', 'variant=spruce')"))
    public boolean removeByLog(IBlockState input) {
        return removeByLog(new BlockWrapper(input));
    }

    @MethodDescription
    public boolean removeByLeaf(BlockWrapper output) {
        return TapperManagerAccessor.getLeafMap().entries().removeIf(r -> {
            if (output.equals(r.getValue())) {
                addBackup(new TapperTreeEntry(r.getKey(), r.getValue()));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("blockstate('minecraft:leaves', 'variant=birch')"))
    public boolean removeByLeaf(IBlockState output) {
        return removeByLeaf(new BlockWrapper(output));
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<BlockWrapper, BlockWrapper>> streamRecipes() {
        return new SimpleObjectStream<>(TapperManagerAccessor.getLeafMap().entries())
                .setRemover(x -> remove(x.getKey(), x.getValue()));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        TapperManagerAccessor.getLeafMap().entries().forEach(x -> addBackup(new TapperTreeEntry(x.getKey(), x.getValue())));
        TapperManagerAccessor.getLeafMap().clear();
    }

    @Desugar
    public record TapperTreeEntry(BlockWrapper log, BlockWrapper leaf) {

    }

}
