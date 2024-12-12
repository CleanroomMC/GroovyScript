package com.cleanroommc.groovyscript.compat.mods.draconicevolution.helpers;

import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.IMultiBlockPart;
import com.brandon3055.draconicevolution.blocks.tileentity.TileInvisECoreBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InvisECoreBlockLogic {

    @SuppressWarnings("deprecation")
    public static void onBlockHarvested(World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileInvisECoreBlock invis) {
            var invisState = (TileInvisECoreBlockState) invis;
            if (!invis.blockName.isEmpty() && !player.capabilities.isCreativeMode) {
                Block trueBlock = Block.REGISTRY.getObject(new ResourceLocation(invis.blockName));
                if (!trueBlock.equals(Blocks.AIR)) {
                    if (invisState.getDefault()) {
                        if (invis.blockName.equals("draconicevolution:particle_generator")) {
                            Block.spawnAsEntity(world, pos, new ItemStack(trueBlock, 1, 2));
                        } else {
                            Block.spawnAsEntity(world, pos, new ItemStack(trueBlock));
                        }
                    } else
                        Block.spawnAsEntity(world, pos, new ItemStack(trueBlock, 1, invisState.getMetadata()));
                }
            }

            IMultiBlockPart master = invis.getController();
            if (master != null) {
                world.setBlockToAir(pos);
                master.validateStructure();
            }
        }
    }

    public static ItemStack getPickBlock(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileInvisECoreBlock invis) {
            if (invis.blockName.isEmpty())
                return ItemStack.EMPTY;

            if (invis.blockName.equals("draconicevolution:particle_generator")) {
                return new ItemStack(DEFeatures.particleGenerator, 1, 2);
            }

            Block block = Block.REGISTRY.getObject(new ResourceLocation(invis.blockName));
            if (block.equals(Blocks.AIR))
                return ItemStack.EMPTY;

            var invisState = (TileInvisECoreBlockState) invis;

            if (invisState.getDefault())
                return new ItemStack(block);
            else
                return new ItemStack(block, 1, invisState.getMetadata());
        }
        return ItemStack.EMPTY;
    }
}
