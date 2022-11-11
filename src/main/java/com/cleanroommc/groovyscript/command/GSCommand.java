package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.event.GsHandEvent;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import com.cleanroommc.groovyscript.network.SCopy;
import com.cleanroommc.groovyscript.network.SReloadJei;
import com.cleanroommc.groovyscript.sandbox.GroovyScriptSandbox;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.command.CommandTreeBase;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GSCommand extends CommandTreeBase {

    public GSCommand() {

        addSubcommand(new SimpleCommand("log", (server, sender, args) -> {
            sender.sendMessage(new TextComponentString(TextFormatting.UNDERLINE + (TextFormatting.GOLD + "Groovy Log"))
                    .setStyle(new Style()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, GroovyLog.get().getLogFilerPath().toString()))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to open GroovyScript log")))));
            sender.sendMessage(new TextComponentString(TextFormatting.UNDERLINE + (TextFormatting.GOLD + "Minecraft Log"))
                    .setStyle(new Style()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, GroovyLog.get().getLogFilerPath().getParent().toString() + "/logs/latest.log"))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to open Minecraft log")))));
        }));

        addSubcommand(new SimpleCommand("reload", (server, sender, args) -> {
            if (FMLCommonHandler.instance().getSide().isServer()) {
                sender.sendMessage(new TextComponentString("Reloading in multiplayer is currently no allowed to avoid desync."));
                return;
            }
            GroovyLog.get().info("========== Reloading Groovy scripts ==========");
            long time = System.currentTimeMillis();
            Throwable throwable = GroovyScript.getSandbox().run(GroovyScriptSandbox.LOADER_POST_INIT);
            time = System.currentTimeMillis() - time;
            sender.sendMessage(new TextComponentString("Reloading Groovy took " + time + "ms"));
            if (throwable == null) {
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully ran scripts"));
                NetworkHandler.sendToPlayer(new SReloadJei(), (EntityPlayerMP) sender);
            } else {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Error executing scripts:"));
                sender.sendMessage(new TextComponentString(TextFormatting.RED + throwable.getMessage()));
                server.commandManager.executeCommand(sender, "/gs log");
            }
        }));

        addSubcommand(new SimpleCommand("hand", (server, sender, args) -> {
            if (sender instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) sender;
                ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
                if (stack.isEmpty()) stack = player.getHeldItem(EnumHand.OFF_HAND);
                BlockPos pos = null;
                IBlockState blockState = null;
                Block block = null;
                if (stack.isEmpty()) {
                    pos = getBlockLookingAt(player);
                    if (pos == null) return;
                    blockState = player.world.getBlockState(pos);
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

                TileEntity tileEntity = pos != null ? player.world.getTileEntity(pos) : null;
                if (tileEntity != null) {
                    GSHandCommand.tileInformation(messages, tileEntity);
                }
                GsHandEvent event = new GsHandEvent(server, player, args, messages, stack, blockState, block, tileEntity);
                MinecraftForge.EVENT_BUS.post(event);
                for (ITextComponent msg : event.messages) {
                    player.sendMessage(msg);
                }
            }
        }));

        addSubcommand(new GSMekanismCommand());
    }

    @Override
    public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            if (sender instanceof EntityPlayerMP && args[0].equals("copy")) {
                NetworkHandler.sendToPlayer(new SCopy(Arrays.copyOfRange(args, 1, args.length)), (EntityPlayerMP) sender);
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
    public String getUsage(@NotNull ICommandSender sender) {
        return "/gs []";
    }

    private static BlockPos getBlockLookingAt(EntityPlayer player) {
        double distance = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d eyes = player.getPositionEyes(0.0F);
        Vec3d look = player.getLook(0.0F);
        Vec3d end = eyes.add(look.x * distance, look.y * distance, look.z * distance);

        RayTraceResult result = player.getEntityWorld().rayTraceBlocks(eyes, end, true);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            return result.getBlockPos();
        }
        return null;
    }
}
