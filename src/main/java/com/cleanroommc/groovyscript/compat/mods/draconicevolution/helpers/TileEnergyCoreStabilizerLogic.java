package com.cleanroommc.groovyscript.compat.mods.draconicevolution.helpers;

import com.brandon3055.brandonscore.lib.datamanager.ManagedVec3I;
import com.brandon3055.brandonscore.utils.FacingUtils;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.Set;

public class TileEnergyCoreStabilizerLogic {

    public static Iterable<BlockPos> getBlocksForFrameMove(TileEnergyCoreStabilizer tile) {
        TileEnergyStorageCore core = tile.getCore();
        if (core != null && !core.moveBlocksProvided) {
            Set<BlockPos> blocks = new ObjectOpenHashSet<>();
            for (ManagedVec3I offset : core.stabOffsets) {
                BlockPos stabPos = core.getPos().subtract(offset.vec.getPos());
                TileEntity tileWorld = tile.getWorld().getTileEntity(stabPos);
                if (tileWorld instanceof TileEnergyCoreStabilizer stabilizer) {
                    blocks.addAll(getStabilizerBlocks(stabilizer));
                }
            }

            BlockStateEnergyCoreStructure structure = (BlockStateEnergyCoreStructure) core.coreStructure;
            BlockStateMultiblockStorage storage = structure.getStorageForTier(core.tier.value);
            BlockPos start = core.getPos().add(structure.getCoreOffset(core.tier.value));
            storage.forEachBlockStates(start, (e, e2) -> blocks.add(e));

            return blocks;
        }
        return Collections.emptyList();
    }

    private static Set<BlockPos> getStabilizerBlocks(TileEnergyCoreStabilizer stabilizer) {
        Set<BlockPos> blocks = new ObjectOpenHashSet<>();
        blocks.add(stabilizer.getPos());
        if (stabilizer.isValidMultiBlock.value) {
            for (BlockPos offset : FacingUtils.getAroundAxis(stabilizer.multiBlockAxis)) {
                blocks.add(stabilizer.getPos().add(offset));
            }
        }
        return blocks;
    }
}
