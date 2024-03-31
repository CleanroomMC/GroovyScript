package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.jei.JeiPlugin;
import com.cleanroommc.groovyscript.documentation.Documentation;
import com.cleanroommc.groovyscript.event.GsHandEvent;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import com.cleanroommc.groovyscript.network.SReloadScripts;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
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
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.server.command.CommandTreeBase;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GSCommand extends CommandTreeBase {

    public static void runReload(EntityPlayerMP player, MinecraftServer server) {
        if (server.isDedicatedServer()) {
            player.sendMessage(new TextComponentString("Reloading in multiplayer is currently not allowed to avoid desync."));
            return;
        }
        GroovyLog.get().info("========== Reloading Groovy scripts ==========");
        long time = GroovyScript.runGroovyScriptsInLoader(LoadStage.POST_INIT);
        GroovyScript.postScriptRunResult(player, false, true, false, time);
        NetworkHandler.sendToPlayer(new SReloadScripts(null, false, true), player);
    }

    public GSCommand() {
        addSubcommand(new SimpleCommand("log", (server, sender, args) -> postLogFiles(sender)));

        addSubcommand(new SimpleCommand("reload", (server, sender, args) -> {
            if (sender instanceof EntityPlayerMP) {
                runReload((EntityPlayerMP) sender, server);
            }
        }));

        addSubcommand(new SimpleCommand("check", (server, sender, args) -> {
            if (sender instanceof EntityPlayerMP) {
                sender.sendMessage(new TextComponentString("Checking groovy syntax..."));
                long time = System.currentTimeMillis();
                GroovyScript.getSandbox().checkSyntax();
                time = System.currentTimeMillis() - time;
                sender.sendMessage(new TextComponentString("Checking syntax took " + time + "ms"));
                GroovyScript.postScriptRunResult((EntityPlayerMP) sender, false, false, false, time);
            }
        }));

        addSubcommand(new PackmodeCommand());

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
                        GSHandCommand.fluidInformation(messages, Arrays.stream(handler.getTankProperties())
                                .map(IFluidTankProperties::getContents)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()));
                    }
                }

                if (blockState != null) {
                    // if the block is a fluid, add the fluid's info
                    Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
                    if (fluid != null) {
                        GSHandCommand.fluidInformation(messages, new FluidStack(fluid, 1000));
                    }

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

        addSubcommand(new SimpleCommand("wiki", (server, sender, args) ->
                sender.sendMessage(new TextComponentString("GroovyScript wiki")
                                           .setStyle(new Style()
                                                             .setColor(TextFormatting.GOLD)
                                                             .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to open wiki in browser")))
                                                             .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://cleanroommc.com/groovy-script/"))))
                , "doc", "docs", "documentation"));

        addSubcommand(new SimpleCommand("generateWiki", (server, sender, args) -> {
            Documentation.generateWiki();
            sender.sendMessage(new TextComponentString("Generated a local version of the Groovyscript wiki has been generated to the ")
                                       .appendSibling(getTextForFile("Wiki Folder", Documentation.WIKI.toPath().toString(), new TextComponentString("Click to open the generated GroovyScript wiki folder"))));
        }, "generateDoc", "generateDocs", "generateDocumentation"));

        addSubcommand(new SimpleCommand("generateExamples", (server, sender, args) -> {
            Documentation.generateExamples();
            sender.sendMessage(new TextComponentString("Generated examples for the enabled Groovyscript compat to the ")
                                       .appendSibling(getTextForFile("Examples Folder", Documentation.EXAMPLES.toPath().toString(), new TextComponentString("Click to open the Groovyscript examples folder"))));
        }));

        addSubcommand(new SimpleCommand("creativeTabs", (server, sender, args) -> {
            GroovyLog.get().info("All creative tabs:");
            for (CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY) {
                GroovyLog.get().getWriter().println(" - " + tab.getTabLabel());
            }
            sender.sendMessage(new TextComponentString("Creative tabs has been logged to the ")
                                       .appendSibling(GSCommand.getTextForFile("Groovy Log", GroovyLog.get().getLogFilerPath().toString(), new TextComponentString("Click to open GroovyScript log"))));
        }));

        addSubcommand(new SimpleCommand("deleteScriptCache", (server, sender, args) -> {
            if (GroovyScript.getSandbox().deleteScriptCache()) {
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Deleted groovy script cache"));
            } else {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "An error occurred while deleting groovy script cache"));
            }
        }));

        addSubcommand(new SimpleCommand("runLS", (server, sender, args) -> {
            if (GroovyScript.runLanguageServer()) {
                sender.sendMessage(new TextComponentString("Starting language server"));
            } else {
                sender.sendMessage(new TextComponentString("Language server is already running"));
            }
        }));

        if (ModSupport.MEKANISM.isLoaded()) {
            addSubcommand(new GSMekanismCommand());
        }
        if (ModSupport.JEI.isLoaded()) {
            addSubcommand(JeiPlugin.getJeiCategoriesCommand());
        }
    }

    @Override
    @Nonnull
    public String getName() {
        return "groovyscript";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return Arrays.asList("grs", "GroovyScript", "gs");
    }

    @Override
    @Nonnull
    public String getUsage(@NotNull ICommandSender sender) {
        return "/grs []";
    }

    public static void postLogFiles(ICommandSender sender) {
        sender.sendMessage(getTextForFile("Groovy Log", GroovyLog.get().getLogFilerPath().toString(), new TextComponentString("Click to open GroovyScript log")));
        sender.sendMessage(getTextForFile("Minecraft Log", GroovyLog.get().getLogFilerPath().getParent().toString() + File.separator + "latest.log", new TextComponentString("Click to open Minecraft log")));
    }

    public static ITextComponent getTextForFile(String name, String path, ITextComponent hoverText) {
        return new TextComponentString(TextFormatting.UNDERLINE + (TextFormatting.GOLD + name))
                .setStyle(new Style()
                                  .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path))
                                  .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));
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
