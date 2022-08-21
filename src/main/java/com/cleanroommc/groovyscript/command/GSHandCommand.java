package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.helper.BracketFormatter;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.NotNull;

/**
 * Creates and sends information to the player for the `/gs hand` command
 * Used in {@link GSCommand}
 */
public class GSHandCommand {

    public static void itemInformation(@NotNull EntityPlayer sender, @NotNull ItemStack stack) {
        String s = IngredientHelper.asGroovyCode(stack, true);
        String formatted = BracketFormatter.formatGSCode(s);

        sender.sendMessage(new TextComponentString("Items:").setStyle(new Style().setColor(TextFormatting.GREEN)));
        sender.sendMessage(new TextComponentString(formatted));
        GuiScreen.setClipboardString(s);
    }

    public static void blockInformation(@NotNull EntityPlayer sender, @NotNull Block block) {
        //TODO when blocks are implemented
//        Block block = item.getBlock();
//        String s = IngredientHelper.toIIngredient(block).asGroovyCode();
//        String formatted = BracketFormatter.formatGSCode(s, false);
//        sender.sendMessage(new TextComponentString(formatted));
    }

    public static void blockStateInformation(@NotNull EntityPlayer sender, @NotNull IBlockState state) {
        //TODO when blockStates are implemented
//        String s = IngredientHelper.toIIngredient(state).asGroovyCode();
//        String formatted = BracketFormatter.formatGSCode(s, !state.getPropertyKeys().isEmpty());
//        sender.sendMessage(new TextComponentString(formatted));
    }

    public static void fluidInformation(@NotNull EntityPlayer sender, @NotNull FluidStack stack) {
        String s = IngredientHelper.asGroovyCode(stack, true);
        String formatted = BracketFormatter.formatGSCode(s);

        sender.sendMessage(new TextComponentString("Fluids:")
                .setStyle(new Style().setColor(TextFormatting.GREEN)));
        sender.sendMessage(new TextComponentString(formatted));
    }

    public static void oredictInformation(@NotNull EntityPlayer sender, ItemStack stack) {
        String s;
        String formatted;
        int[] ids = OreDictionary.getOreIDs(stack);
        if (ids.length > 0) {
            sender.sendMessage(new TextComponentString("Ore Dictionaries:")
                    .setStyle(new Style().setColor(TextFormatting.GREEN)));
            for (int id : ids) {
                s = IngredientHelper.asGroovyCode(OreDictionary.getOreName(id), stack.getCount(), true);
                formatted = BracketFormatter.formatGSCode(s);

                sender.sendMessage(new TextComponentString(formatted));
            }
        }
    }
}
