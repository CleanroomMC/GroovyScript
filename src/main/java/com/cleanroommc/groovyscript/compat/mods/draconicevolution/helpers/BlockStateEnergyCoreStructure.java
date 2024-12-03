package com.cleanroommc.groovyscript.compat.mods.draconicevolution.helpers;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.ModelUtils;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileInvisECoreBlock;
import com.brandon3055.draconicevolution.client.gui.GuiEnergyCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.world.EnergyCoreStructure;
import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Objects;

public class BlockStateEnergyCoreStructure extends EnergyCoreStructure {

    private static final int FLAG_RENDER = 0;
    private static final int FLAG_FORM = 1;
    private static final int FLAG_REVERT = 2;
    private static final int TIER_AMOUNT = 8;

    private final TileEnergyStorageCore core;
    private final BlockStateMultiblockHelper helper;
    private final BlockStateMultiblockStorage[] structureTiers;
    private int version;

    private static BlockStates coreBlock;

    public static BlockStates coreBlock() {
        if (coreBlock == null) coreBlock = BlockStates.of(DEFeatures.energyStorageCore);
        return coreBlock;
    }

    public BlockStateEnergyCoreStructure(TileEnergyStorageCore core) {
        this.core = core;
        this.helper = new BlockStateMultiblockHelper();
        this.structureTiers = new BlockStateMultiblockStorage[TIER_AMOUNT];

        // Initialize Tiers
        structureTiers[0] = buildTier1();
        structureTiers[1] = buildTier2();
        structureTiers[2] = buildTier3();
        structureTiers[3] = buildTier4();
        structureTiers[4] = buildTier5();
        structureTiers[5] = buildTier6();
        structureTiers[6] = buildTier7();
        structureTiers[7] = buildTierOMG();
    }

    public BlockStateMultiblockHelper getHelper() {
        return helper;
    }

    public boolean checkVersion() {
        if (version != ModSupport.DRACONIC_EVOLUTION.get().energyCore.getVersion()) {
            version = ModSupport.DRACONIC_EVOLUTION.get().energyCore.getVersion();
            ModSupport.DRACONIC_EVOLUTION.get().energyCore.applyEdit(structureTiers);
            return true;
        }
        return false;
    }

    @Override
    public boolean checkTier(int tier) {
        checkVersion();
        if (tier <= 0) {
            GroovyScript.LOGGER.error("[EnergyCoreStructure] Tier value to small. As far as TileEnergyStorageCore is concerned the tiers now start at 1 not 0. This class automatically handles the conversion now");
            return false;
        }
        if (tier > 8) {
            GroovyScript.LOGGER.error("[EnergyCoreStructure] What exactly were you expecting after Tier 8? Infinity.MAX_VALUE?");
            return false;
        }
        return structureTiers[tier - 1].checkStructure(core.getWorld(), core.getPos().add(getCoreOffset(tier)));
    }

    @Override
    public void placeTier(int tier) {
        BlockPos offset = getCoreOffset(tier);

        if (tier <= 0) {
            GroovyScript.LOGGER.error("[EnergyCoreStructure] Tier value to small. As far as TileEnergyStorageCore is concerned the tiers now start at 1 not 0. This class automatically handles the conversion now");
            return;
        }
        if (tier > 8) {
            GroovyScript.LOGGER.error("[EnergyCoreStructure] What exactly were you expecting after Tier 8? Infinity.MAX_VALUE?");
            return;
        }
        structureTiers[tier - 1].placeStructure(core.getWorld(), core.getPos().add(offset));
    }

    @Override
    public void renderTier(int tier) {
        forTier(tier, FLAG_RENDER);
    }

    @Override
    public void formTier(int tier) {
        coreForming = true;
        forTier(tier, FLAG_FORM);
        coreForming = false;
    }

    @Override
    public void revertTier(int tier) {
        forTier(tier, FLAG_REVERT);
    }

    private void forTier(int tier, int flag) {
        int checkTier = tier - 1;
        checkVersion();
        if (checkTier < 0) {
            GroovyScript.LOGGER.error("[EnergyCoreStructure] Tier value to small. As far as TileEnergyStorageCore is concerned the tiers now start at 1 not 0. This class automatically handles the conversion now");
        } else if (checkTier >= structureTiers.length) {
            GroovyScript.LOGGER.error("[EnergyCoreStructure#placeTier] There are only 8 tiers, but tried to use tier {}", checkTier);
        } else {
            structureTiers[checkTier].forEachInStructure(core.getWorld(), core.getPos().add(getCoreOffset(checkTier + 1)), flag);
        }
    }

    @Override
    public BlockStateMultiblockStorage getStorageForTier(int tier) {
        return structureTiers[tier - 1];
    }

    public void forBlock(BlockStates states, World world, BlockPos pos, BlockPos startPos, int flag) {
        if (states == null)
            return;

        if (states.isWildcard() || states.equals(coreBlock()))
            return;

        //region Render Build Guide

        if (flag == FLAG_RENDER) {
            if (world.isRemote) {
                renderBuildGuide(states, world, pos, startPos, FLAG_RENDER);
            }
        }

        //endregion

        //region Activate

        else if (flag == FLAG_FORM) {
            world.setBlockState(pos, DEFeatures.invisECoreBlock.getDefaultState());
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileInvisECoreBlock invis) {
                invis.blockName = Objects.requireNonNull(states.getDefault().getBlock().getRegistryName()).toString();
                TileInvisECoreBlockState invisState = (TileInvisECoreBlockState) invis;
                if (BlockStates.statesEqual(states.getDefault(), states.getDefault().getBlock().getDefaultState())) {
                    invisState.setIsDefault();
                } else {
                    invisState.setMetadata(states.getDefault().getBlock().getMetaFromState(states.getDefault()));
                }
                invis.setController(core);
            }
        }

        //endregion

        //region Deactivate

        else if (flag == FLAG_REVERT) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileInvisECoreBlock tileInvisECoreBlock) {
                tileInvisECoreBlock.revert();
            }
        }

        //endregion
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unused")
    private void renderBuildGuide(BlockStates states, World world, BlockPos pos, BlockPos startPos, int flag) {
        Vec3D corePos = Vec3D.getCenter(startPos.subtract(getCoreOffset(core.tier.value)));
        double dist = Utils.getDistanceAtoB(corePos, Vec3D.getCenter(pos));
        double pDist = Minecraft.getMinecraft().player.getDistance(corePos.x, corePos.y, corePos.z);

        if (GuiEnergyCore.layer != -1) {
            pDist = GuiEnergyCore.layer + 2;
        }

        IBlockState atState = world.getBlockState(pos);
        boolean invalid = !world.isAirBlock(pos) && !states.matches(atState, false);

        if (dist + 2 > pDist && !invalid) {
            return;
        }

        BlockPos translation = new BlockPos(pos.getX() - startPos.getX(), pos.getY() - startPos.getY(), pos.getZ() - startPos.getZ());
        translation = translation.add(getCoreOffset(core.tier.value));

        int alpha = 0xFF000000;
        if (invalid) {
            alpha = (int) (((Math.sin(ClientEventHandler.elapsedTicks / 20.0D) + 1.0D) / 2.0D) * 255.0D) << 24;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(translation.getX(), translation.getY(), translation.getZ());
        if (invalid) {
            GlStateManager.disableDepth();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
            double s = Math.sin(ClientEventHandler.elapsedTicks / 10.0D) * 0.1D;
            GlStateManager.scale(0.8 + s, 0.8 + s, 0.8 + s);
            GlStateManager.translate(0.1 - s, 0.1 - s, 0.1 - s);
        } else {
            GlStateManager.scale(0.8, 0.8, 0.8);
            GlStateManager.translate(0.1, 0.1, 0.1);
        }

        float brightnessX = OpenGlHelper.lastBrightnessX;
        float brightnessY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 150.0f, 150.0f);

        List<BakedQuad> blockQuads = ModelUtils.getModelQuads(states.getDefault());

        int color;
        if (invalid) {
            color = 0x00500000;
        } else {
            color = Minecraft.getMinecraft().getBlockColors().getColor(states.getDefault(), world, pos);
            int r = (int) (((color >> 16) & 0xFF) * 0.8);
            int g = (int) (((color >> 8) & 0xFF) * 0.8);
            int b = (int) ((color & 0xFF) * 0.8);
            color = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
        }
        ModelUtils.renderQuadsARGB(blockQuads, color | alpha);

        if (invalid) {
            GlStateManager.enableDepth();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        }
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessX, brightnessY);
        GlStateManager.popMatrix();
    }

    public boolean checkBlock(BlockStates states, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileInvisECoreBlock invis) {
            if (invis.blockName.equals(Objects.requireNonNull(states.getDefault().getBlock().getRegistryName()).toString())) {
                TileInvisECoreBlockState invisState = (TileInvisECoreBlockState) invis;
                if (invisState.getDefault()) {
                    return BlockStates.statesEqual(states.getDefault(), states.getDefault().getBlock().getDefaultState());
                } else {
                    return states.getDefault().getBlock().getMetaFromState(states.getDefault()) == invisState.getMetadata();
                }
            }
            return false;
        }
        IBlockState state = world.getBlockState(pos);
        return states.matches(state, false);
    }

    @Override
    public BlockPos getCoreOffset(int tier) {
        int offset = tier == 1 ? 0 : tier == 2 || tier == 3 ? -1 : -(tier - 2);
        return new BlockPos(offset, offset, offset);
    }

    public void setBlock(BlockStates states, World world, BlockPos pos) {
        if (states == null)
            return;
        if (!states.equals(coreBlock())) {
            helper.setBlock(states, world, pos);
        }
    }

    public BlockStateMultiblockStorage[] getStructureTiers() {
        return structureTiers;
    }

    private BlockStateMultiblockStorage buildTier1() {
        BlockStateMultiblockStorage storage = new BlockStateMultiblockStorage(1, helper, this);
        BlockStates e = BlockStates.ANY;
        BlockStates X = coreBlock();
        BlockStates R = BlockStates.redstone();
        BlockStates D = BlockStates.draconium();
        BlockStates A = BlockStates.draconic();
        storage.addRow(X);

        return storage;
    }

    @SuppressWarnings("DuplicatedCode")
    private BlockStateMultiblockStorage buildTier2() {
        BlockStateMultiblockStorage storage = new BlockStateMultiblockStorage(3, helper, this);
        BlockStates e = BlockStates.ANY;
        BlockStates X = coreBlock();
        BlockStates R = BlockStates.redstone();
        BlockStates D = BlockStates.draconium();
        BlockStates A = BlockStates.draconic();

        storage.addRow(e, e, e);
        storage.addRow(e, D, e);
        storage.addRow(e, e, e);

        storage.newLayer();
        storage.addRow(e, D, e);
        storage.addRow(D, X, D);
        storage.addRow(e, D, e);

        storage.mirrorHalf();

        return storage;
    }

    @SuppressWarnings("DuplicatedCode")
    private BlockStateMultiblockStorage buildTier3() {
        BlockStateMultiblockStorage storage = new BlockStateMultiblockStorage(3, helper, this);
        BlockStates e = BlockStates.ANY;
        BlockStates X = coreBlock();
        BlockStates R = BlockStates.redstone();
        BlockStates D = BlockStates.draconium();
        BlockStates A = BlockStates.draconic();

        storage.addRow(D, D, D);
        storage.addRow(D, D, D);
        storage.addRow(D, D, D);

        storage.newLayer();
        storage.addRow(D, D, D);
        storage.addRow(D, X, D);
        storage.addRow(D, D, D);

        storage.mirrorHalf();

        return storage;
    }

    @SuppressWarnings("DuplicatedCode")
    private BlockStateMultiblockStorage buildTier4() {
        BlockStateMultiblockStorage storage = new BlockStateMultiblockStorage(5, helper, this);
        BlockStates e = BlockStates.ANY;
        BlockStates X = coreBlock();
        BlockStates R = BlockStates.redstone();
        BlockStates D = BlockStates.draconium();
        BlockStates A = BlockStates.draconic();

        storage.addRow(e, e, e, e, e);
        storage.addRow(e, D, D, D, e);
        storage.addRow(e, D, D, D, e);
        storage.addRow(e, D, D, D, e);
        storage.addRow(e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, D, D, D, e);
        storage.addRow(D, R, R, R, D);
        storage.addRow(D, R, R, R, D);
        storage.addRow(D, R, R, R, D);
        storage.addRow(e, D, D, D, e);

        storage.newLayer();
        storage.addRow(e, D, D, D, e);
        storage.addRow(D, R, R, R, D);
        storage.addRow(D, R, X, R, D);
        storage.addRow(D, R, R, R, D);
        storage.addRow(e, D, D, D, e);

        storage.mirrorHalf();

        return storage;
    }

    @SuppressWarnings("DuplicatedCode")
    private BlockStateMultiblockStorage buildTier5() {
        BlockStateMultiblockStorage storage = new BlockStateMultiblockStorage(7, helper, this);
        BlockStates e = BlockStates.ANY;
        BlockStates X = coreBlock();
        BlockStates R = BlockStates.redstone();
        BlockStates D = BlockStates.draconium();
        BlockStates A = BlockStates.draconic();

        storage.addRow(e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(e, e, D, D, D, e, e);

        storage.newLayer();
        storage.addRow(e, e, D, D, D, e, e);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, D);
        storage.addRow(D, R, R, X, R, R, D);
        storage.addRow(D, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, D, e);
        storage.addRow(e, e, D, D, D, e, e);

        storage.mirrorHalf();

        return storage;
    }

    @SuppressWarnings("DuplicatedCode")
    private BlockStateMultiblockStorage buildTier6() {
        BlockStateMultiblockStorage storage = new BlockStateMultiblockStorage(9, helper, this);
        BlockStates e = BlockStates.ANY;
        BlockStates X = coreBlock();
        BlockStates R = BlockStates.redstone();
        BlockStates D = BlockStates.draconium();
        BlockStates A = BlockStates.draconic();

        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, D, D, D, e, e, e);
        storage.addRow(e, e, e, D, D, D, e, e, e);
        storage.addRow(e, e, e, D, D, D, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, D, D, D, D, D, e, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, e, D, D, D, D, D, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, D, D, D, D, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, D, D, D, D, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, D, D, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, D, D, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, D, D, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, X, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, D, D, e, e, e);

        storage.mirrorHalf();

        return storage;
    }

    @SuppressWarnings("DuplicatedCode")
    private BlockStateMultiblockStorage buildTier7() {
        BlockStateMultiblockStorage storage = new BlockStateMultiblockStorage(11, helper, this);
        BlockStates e = BlockStates.ANY;
        BlockStates X = coreBlock();
        BlockStates R = BlockStates.redstone();
        BlockStates D = BlockStates.draconium();
        BlockStates A = BlockStates.draconic();

        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, D, D, D, D, e, e, e);
        storage.addRow(e, e, e, e, e, e, e, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);

        storage.newLayer();
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(D, R, R, R, R, R, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, X, R, R, R, R, D);
        storage.addRow(D, R, R, R, R, R, R, R, R, R, D);
        storage.addRow(e, D, R, R, R, R, R, R, R, D, e);
        storage.addRow(e, e, D, R, R, R, R, R, D, e, e);
        storage.addRow(e, e, e, D, R, R, R, D, e, e, e);
        storage.addRow(e, e, e, e, D, D, D, e, e, e, e);

        storage.mirrorHalf();

        return storage;
    }

    private BlockStateMultiblockStorage buildTierOMG() {
        BlockStateMultiblockStorage storage = new BlockStateMultiblockStorage(13, helper, this);
        BlockStates e = BlockStates.ANY;
        BlockStates X = coreBlock();
        BlockStates R = BlockStates.redstone();
        BlockStates D = BlockStates.draconium();
        BlockStates A = BlockStates.draconic();

        //region Hard
        if (DEConfig.hardMode) {
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, A, A, A, A, A, A, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, A, A, A, A, A, A, A, A, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, A, A, A, A, A, A, A, A, e, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, e, A, A, A, A, A, A, A, A, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, X, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(A, A, D, D, D, D, D, D, D, D, D, A, A);
            storage.addRow(e, A, A, D, D, D, D, D, D, D, A, A, e);
            storage.addRow(e, e, A, A, D, D, D, D, D, A, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
        } else {
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, e, A, A, A, A, A, A, A, e, e, e);
            storage.addRow(e, e, e, e, e, e, e, e, e, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);

            storage.newLayer();
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, X, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(A, D, D, D, D, D, D, D, D, D, D, D, A);
            storage.addRow(e, A, D, D, D, D, D, D, D, D, D, A, e);
            storage.addRow(e, e, A, D, D, D, D, D, D, D, A, e, e);
            storage.addRow(e, e, e, A, D, D, D, D, D, A, e, e, e);
            storage.addRow(e, e, e, e, A, A, A, A, A, e, e, e, e);
        }
        storage.mirrorHalf();
        return storage;
    }
}
