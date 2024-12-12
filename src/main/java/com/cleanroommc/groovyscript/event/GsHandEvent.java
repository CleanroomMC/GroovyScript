package com.cleanroommc.groovyscript.event;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Called when /gs hand is executed
 */
public class GsHandEvent extends Event {

    /**
     * Server where the command is executed
     */
    public final @NotNull MinecraftServer server;

    /**
     * Player who executes the command
     */
    public final @NotNull EntityPlayer player;

    /**
     * Arguments of the command
     */
    public final @NotNull String[] commandArgs;

    /**
     * A list of messages that will be sent to the player after this event.
     * Add or remove your messages here.
     */
    public final @NotNull List<ITextComponent> messages;

    /**
     * The held item or the item form of the block being looked at.
     */
    public final @NotNull ItemStack stack;

    /**
     * The entity the player is looking at
     */
    public final @Nullable Entity entity;

    /**
     * The block position the player is looking at
     */
    public final @Nullable BlockPos pos;

    /**
     * The block state of the held item or the block state the player is looking at
     */
    public final @Nullable IBlockState blockState;

    /**
     * The block of the held item or the block the player is looking at
     */
    public final @Nullable Block block;

    /**
     * The tile entity the player is looking at
     */
    public final @Nullable TileEntity tileEntity;

    /**
     * If pretty nbt is enabled
     */
    public final boolean prettyNbt;


    public GsHandEvent(
            @NotNull MinecraftServer server,
            @NotNull EntityPlayer player,
            String[] commandArgs,
            @NotNull List<ITextComponent> messages,
            @NotNull ItemStack stack,
            @Nullable Entity entity,
            @Nullable BlockPos pos,
            @Nullable IBlockState blockState,
            @Nullable Block block,
            @Nullable TileEntity tileEntity,
            boolean prettyNbt
    ) {
        this.server = server;
        this.player = player;
        this.commandArgs = commandArgs;
        this.messages = messages;
        this.stack = stack;
        this.entity = entity;
        this.pos = pos;
        this.blockState = blockState;
        this.block = block;
        this.tileEntity = tileEntity;
        this.prettyNbt = prettyNbt;
    }

    public GsHandEvent(InfoParserPackage infoParserPackage) {
        this.server = infoParserPackage.getServer();
        this.player = infoParserPackage.getPlayer();
        this.commandArgs = infoParserPackage.getArgs().toArray(new String[0]);
        this.messages = infoParserPackage.getMessages();
        this.stack = infoParserPackage.getStack();
        this.entity = infoParserPackage.getEntity();
        this.pos = infoParserPackage.getPos();
        this.blockState = infoParserPackage.getBlockState();
        this.block = infoParserPackage.getBlock();
        this.tileEntity = infoParserPackage.getTileEntity();
        this.prettyNbt = infoParserPackage.isPrettyNbt();
    }
}
