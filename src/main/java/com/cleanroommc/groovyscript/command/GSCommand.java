package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.event.GsHandEvent;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.Nonnull;
import java.util.ArrayList;
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
                IBlockState blockState = null;
                Block block = null;
                if (stack.isEmpty()) {
                    blockState = getBlockLookingAt(player);
                    if (blockState == null) return;
                    block = blockState.getBlock();
                    stack = new ItemStack(block, 1, block.getMetaFromState(blockState));
                }

                List<ITextComponent> messages = new ArrayList<>();

                if (stack.getItem() instanceof ItemBlock) {
                    block = ((ItemBlock) stack.getItem()).getBlock();
                    blockState = block.getStateFromMeta(stack.getMetadata());
                }

                boolean prettyNbt = false;
                for (String arg : args) {
                    if ("pretty".equals(arg) || "-p".equals(arg)) {
                        prettyNbt = true;
                        break;
                    }
                }

                // add the item and oredict info
                GSHandCommand.itemInformation(messages, stack, prettyNbt);
                GSHandCommand.oredictInformation(messages, stack);

                // if the item holds fluids, add that info
                if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                    IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                    if (handler != null) {
                        FluidStack fluidStack = handler.drain(Integer.MAX_VALUE, false);
                        if (fluidStack != null) GSHandCommand.fluidInformation(messages, fluidStack);
                    }
                }

                if (blockState != null) {
                    // if the block is a fluid, add the fluid's info
                    Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
                    if (fluid != null) {
                        GSHandCommand.fluidInformation(messages, new FluidStack(fluid, 1000));
                    }

                    // add the block's info
                    GSHandCommand.blockInformation(messages, block);
                    GSHandCommand.blockStateInformation(messages, blockState);
                }

                GsHandEvent event = new GsHandEvent(server, player, args, messages, stack, blockState, block);
                MinecraftForge.EVENT_BUS.post(event);
                for (ITextComponent msg : event.messages) {
                    player.sendMessage(msg);
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

    private static IBlockState getBlockLookingAt(EntityPlayer player) {
        double distance = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d eyes = player.getPositionEyes(0.0F);
        Vec3d look = player.getLook(0.0F);
        Vec3d end = eyes.add(look.x * distance, look.y * distance, look.z * distance);

        RayTraceResult result = player.getEntityWorld().rayTraceBlocks(eyes, end, true);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            return player.world.getBlockState(result.getBlockPos());
        }
        return null;
    }
}
