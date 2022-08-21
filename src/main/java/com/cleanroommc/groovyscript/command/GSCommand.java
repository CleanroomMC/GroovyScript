package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import com.google.common.base.Joiner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class GSCommand extends CommandTreeBase {

    public GSCommand() {
        addSubcommand(new RunScriptsCommand());
        addSubcommand(new SimpleCommand("log", (server, sender, args) -> {
            sender.sendMessage(new TextComponentString(TextFormatting.UNDERLINE + (TextFormatting.GOLD + "Groovy Log"))
                    .setStyle(new Style()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, GroovyLog.LOG.getPath().toString()))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to open GroovyScript log")))));
            sender.sendMessage(new TextComponentString(TextFormatting.UNDERLINE + (TextFormatting.GOLD + "Minecraft Log"))
                    .setStyle(new Style()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, GroovyLog.LOG.getPath().getParent().toString() + "/logs/latest.log"))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to open Minecraft log")))));
        }));
        addSubcommand(new SimpleCommand("hand", (server, sender, args) -> {
            if (sender instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) sender;
                ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
                if (stack.isEmpty()) stack = player.getHeldItem(EnumHand.OFF_HAND);

                if (!stack.isEmpty()) {
                    // add the item and oredict info
                    GSHandCommand.itemInformation(player, stack);
                    GSHandCommand.oredictInformation(player, stack);

                    // if the item is for a block, add the block's info
                    if (stack.getItem() instanceof ItemBlock) {
                        GSHandCommand.blockInformation(player, ((ItemBlock) stack.getItem()).getBlock());
                        GSHandCommand.blockStateInformation(player, ((ItemBlock) stack.getItem()).getBlock().getDefaultState());
                    }

                    // if the item holds fluids, add that info
                    if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                        IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                        if (handler != null) {
                            FluidStack fluidStack = handler.drain(Integer.MAX_VALUE, false);
                            if (fluidStack != null) GSHandCommand.fluidInformation(player, fluidStack);
                        }
                    }
                } else {
                    double distance = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
                    Vec3d eyes = player.getPositionEyes(0.0F);
                    Vec3d look = player.getLook(0.0F);
                    Vec3d end = eyes.add(look.x * distance, look.y * distance, look.z * distance);

                    RayTraceResult result = player.getEntityWorld().rayTraceBlocks(eyes, end, true);
                    if (result != null) {
                        IBlockState state = player.world.getBlockState(result.getBlockPos());
                        Block block = state.getBlock();
                        stack = new ItemStack(block, 1, state.getBlock().getMetaFromState(state));

                        // if the block has an item form, add that info
                        if (!stack.isEmpty()) {
                            GSHandCommand.itemInformation(player, stack);
                            GSHandCommand.oredictInformation(player, stack);
                        }

                        // if the block is a fluid, add the fluid's info
                        Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
                        if (fluid != null) {
                            GSHandCommand.fluidInformation(player, new FluidStack(fluid, 1000));
                        }

                        // add the block's info
                        GSHandCommand.blockInformation(player, block);
                        GSHandCommand.blockStateInformation(player, state);
                    }
                }
            }
        }));
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            if (args[0].equals("copy")) {
                GuiScreen.setClipboardString(Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length)));
                return;
            }
        }
        super.execute(server, sender, args);
    }

    @Override
    @Nonnull
    public String getName() {
        return "groovyscript";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return Arrays.asList("gs", "GroovyScript");
    }

    @Override
    @Nonnull
    public String getUsage(ICommandSender sender) {
        return "/gs []";
    }
}
