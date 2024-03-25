package com.cleanroommc.groovyscript.compat.mods.draconicevolution;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.mods.draconicevolution.helpers.BlockStateEnergyCoreStructure;
import com.cleanroommc.groovyscript.compat.mods.draconicevolution.helpers.BlockStateMultiblockStorage;
import com.cleanroommc.groovyscript.compat.mods.draconicevolution.helpers.BlockStates;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ArrayUtils;
import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collection;

@RegistryDescription
public class EnergyCore implements IScriptReloadable {

    private int version = 0;
    private BlockStates[][][][] original;
    private BlockStates[][][][] edited;
    private BlockStates[] inner;
    private BlockStates[] outer;

    private void init() {
        if (this.original != null) return;
        BlockStateEnergyCoreStructure bsecs = new BlockStateEnergyCoreStructure(null);
        this.original = new BlockStates[bsecs.getStructureTiers().length][][][];
        BlockStateMultiblockStorage[] structureTiers = bsecs.getStructureTiers();
        // deep copy structure
        for (int i = 0; i < structureTiers.length; i++) {
            this.original[i] = ArrayUtils.deepCopy3d(structureTiers[i].getStructure(), null);
        }
        this.inner = new BlockStates[this.original.length];
        this.outer = new BlockStates[this.original.length];
        onReload(); // increases version to 1
    }

    @Override
    public Collection<String> getAliases() {
        return Alias.generateOfClass(EnergyCore.class);
    }

    @Override
    public void onReload() {
        if (this.original == null) return;
        this.edited = ArrayUtils.deepCopy4d(this.original, this.edited);
        Arrays.fill(this.inner, BlockStates.redstone());
        Arrays.fill(this.outer, BlockStates.draconium());
        this.inner[this.inner.length - 1] = BlockStates.draconium();
        this.outer[this.outer.length - 1] = BlockStates.draconic();
        this.version++;
    }

    @Override
    public void afterScriptLoad() {}

    @GroovyBlacklist
    public int getVersion() {
        return version;
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public void applyEdit(BlockStateMultiblockStorage[] mbs) {
        for (int i = 0; i < mbs.length; i++) {
            mbs[i].setStructure(this.edited[i]);
        }
    }

    private void replaceBlock(int tier, BlockStates edit, boolean inner) {
        if (tier < 1 || tier > 8) {
            GroovyLog.msg("Error setting block of Draconic Evolution Energy Core")
                    .add("Tier {} is invalid. Must be between 1 and 8")
                    .error()
                    .post();
            return;
        }
        init();
        BlockStates old = inner ? this.inner[tier - 1] : this.outer[tier - 1];
        BlockStates[][][] blocks = this.edited[tier - 1];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                for (int k = 0; k < blocks[i][j].length; k++) {
                    if (old == blocks[i][j][k]) {
                        blocks[i][j][k] = edit;
                    }
                }
            }
        }
        (inner ? this.inner : this.outer)[tier - 1] = edit;
    }

    @MethodDescription(description = "groovyscript.wiki.draconicevolution.inner_block", type = MethodDescription.Type.VALUE, example = {
            @Example("7, blockstate('minecraft:stone', 1)")
    })
    public EnergyCore setInnerBlock(int tier, IBlockState... blockStates) {
        if (blockStates == null || blockStates.length == 0) {
            GroovyLog.msg("Error setting inner block of tier {} Draconic Evolution Energy Core", tier)
                    .add("block states must not be null or empty")
                    .error()
                    .post();
            return this;
        }
        replaceBlock(tier, BlockStates.of(blockStates), true);
        return this;
    }

    @MethodDescription(description = "groovyscript.wiki.draconicevolution.outer_block", type = MethodDescription.Type.VALUE, example = {
            @Example("7, blockstate('minecraft:diamond_block')"),
            @Example("2, blockstate('minecraft:diamond_block')")
    })
    public EnergyCore setOuterBlock(int tier, IBlockState... blockStates) {
        if (blockStates == null || blockStates.length == 0) {
            GroovyLog.msg("Error setting outer block of tier {} Draconic Evolution Energy Core", tier)
                    .add("block states must not be null or empty")
                    .error()
                    .post();
            return this;
        }
        replaceBlock(tier, BlockStates.of(blockStates), false);
        return this;
    }
}
