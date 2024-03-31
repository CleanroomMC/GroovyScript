package com.cleanroommc.groovyscript.compat.mods.draconicevolution.helpers;

import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.ParticleGenerator;
import com.brandon3055.draconicevolution.blocks.tileentity.TileInvisECoreBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;

public class TileInvisECoreBlockLogic {

    @SuppressWarnings("deprecation")
    public static void revert(TileInvisECoreBlock invis) {
        if (invis.blockName.equals("draconicevolution:particle_generator")) {
            invis.getWorld().setBlockState(invis.getPos(), DEFeatures.particleGenerator.getDefaultState().withProperty(ParticleGenerator.TYPE, "stabilizer"));
            return;
        }
        Block block = Block.REGISTRY.getObject(new ResourceLocation(invis.blockName));
        IBlockState state;
        if (!block.equals(Blocks.AIR)) {
            if (!((TileInvisECoreBlockState) invis).getDefault())
                state = block.getStateFromMeta(((TileInvisECoreBlockState) invis).getMetadata());
            else
                state = block.getDefaultState();

            invis.getWorld().setBlockState(invis.getPos(), state);
        } else {
            invis.getWorld().setBlockToAir(invis.getPos());
        }
    }

    public static SPacketUpdateTileEntity getUpdatePacket(TileInvisECoreBlock invis) {
        NBTTagCompound compound = new NBTTagCompound();
        var invisState = (TileInvisECoreBlockState) invis;
        if (invisState.getDefault())
            compound.setString("BlockName", invis.blockName);
        else
            compound.setString("BlockName", invis.blockName + " " + invisState.getMetadata());

        invis.coreOffset.toNBT(compound);
        return new SPacketUpdateTileEntity(invis.getPos(), 0, compound);
    }

    public static void onDataPacket(TileInvisECoreBlock invis, SPacketUpdateTileEntity pkt) {
        String[] input = pkt.getNbtCompound().getString("BlockName").split(" ");
        var invisState = (TileInvisECoreBlockState) invis;
        if (input.length != 2) {
            invis.blockName = input[0];
            invisState.setIsDefault();
        } else {
            invis.blockName = input[0];
            invisState.setMetadata(Integer.parseInt(input[1]));
        }
        invis.coreOffset.fromNBT(pkt.getNbtCompound());
    }

    public static void writeExtraNBT(TileInvisECoreBlock invis, NBTTagCompound compound) {
        var invisState = (TileInvisECoreBlockState) invis;
        if (invisState.getDefault())
            compound.setString("BlockName", invis.blockName);
        else
            compound.setString("BlockName", invis.blockName + " " + invisState.getMetadata());
    }

    public static void readExtraNBT(TileInvisECoreBlock invis, NBTTagCompound compound) {
        String[] input = compound.getString("BlockName").split(" ");
        var invisState = (TileInvisECoreBlockState) invis;
        if (input.length != 2) {
            invis.blockName = input[0];
            invisState.setIsDefault();
        } else {
            invis.blockName = input[0];
            invisState.setMetadata(Integer.parseInt(input[1]));
        }
    }
}
