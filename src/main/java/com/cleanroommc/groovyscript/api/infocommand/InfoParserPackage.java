package com.cleanroommc.groovyscript.api.infocommand;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created when /gs info is executed
 */
public class InfoParserPackage {

    /**
     * Server where the command is executed
     */
    private final @NotNull MinecraftServer server;
    /**
     * Player who executes the command
     */
    private final @NotNull EntityPlayer player;
    /**
     * Arguments of the command
     */
    private final @NotNull List<String> args;
    /**
     * A list of messages that will be sent to the player after this event.
     * Add or remove your messages here.
     */
    private final @NotNull List<ITextComponent> messages;
    /**
     * If pretty nbt is enabled
     */
    private final boolean prettyNbt;
    /**
     * The held item or the item form of the block being looked at.
     */
    private @NotNull ItemStack stack;
    /**
     * The entity the player is looking at
     */
    private @Nullable Entity entity;
    /**
     * The block position the player is looking at
     */
    private @Nullable BlockPos pos;
    /**
     * The block state of the held item or the block state the player is looking at
     */
    private @Nullable IBlockState blockState;
    /**
     * The block of the held item or the block the player is looking at
     */
    private @Nullable Block block;
    /**
     * The tile entity the player is looking at
     */
    private @Nullable TileEntity tileEntity;

    public InfoParserPackage(
            @NotNull MinecraftServer server,
            @NotNull EntityPlayer player,
            @NotNull List<String> args,
            @NotNull List<ITextComponent> messages,
            boolean prettyNbt
    ) {
        this.server = server;
        this.player = player;
        this.args = args;
        this.messages = messages;
        this.stack = ItemStack.EMPTY;
        this.prettyNbt = prettyNbt;
    }

    public @NotNull MinecraftServer getServer() {
        return server;
    }

    public @NotNull EntityPlayer getPlayer() {
        return player;
    }

    public @NotNull List<String> getArgs() {
        return args;
    }

    public @NotNull List<ITextComponent> getMessages() {
        return messages;
    }

    public @NotNull ItemStack getStack() {
        return stack;
    }

    public void setStack(@NotNull ItemStack stack) {
        this.stack = stack;
    }

    public boolean isPrettyNbt() {
        return prettyNbt;
    }

    public @Nullable Entity getEntity() {
        return entity;
    }

    public void setEntity(@Nullable Entity entity) {
        this.entity = entity;
    }

    public @Nullable BlockPos getPos() {
        return pos;
    }

    public void setPos(@Nullable BlockPos pos) {
        this.pos = pos;
    }

    public @Nullable IBlockState getBlockState() {
        return blockState;
    }

    public void setBlockState(@Nullable IBlockState blockState) {
        this.blockState = blockState;
    }

    public @Nullable Block getBlock() {
        return block;
    }

    public void setBlock(@Nullable Block block) {
        this.block = block;
    }

    public @Nullable TileEntity getTileEntity() {
        return tileEntity;
    }

    public void setTileEntity(@Nullable TileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    public void copyFromPos(BlockPos pos) {
        if (pos == null) return;
        this.pos = pos;
        this.blockState = player.world.getBlockState(pos);
        this.block = blockState.getBlock();
        this.tileEntity = player.world.getTileEntity(pos);

        this.stack = block.getPickBlock(blockState, Minecraft.getMinecraft().objectMouseOver, player.world, pos, player);
        if (this.stack.isEmpty()) this.stack = new ItemStack(block, 1, block.getMetaFromState(blockState));
    }

    public void parse() {
        parse(false);
    }

    public void parse(boolean enabled) {
        InfoParserRegistry.getInfoParsers().forEach(x -> x.parse(this, enabled));
    }
}
