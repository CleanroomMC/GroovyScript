package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.helper.IngredientHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Creates and sends information to the player for the `/gs hand` command
 * Used in {@link GSCommand}
 */
public class GSHandCommand {

    public static void itemInformation(List<ITextComponent> messages, @NotNull ItemStack stack, boolean prettyNbt) {
        String itemNbt = IngredientHelper.asGroovyCode(stack, true, prettyNbt);
        String item = IngredientHelper.asGroovyCode(stack, true);

        messages.add(TextCopyable.string(item, itemNbt).build());
        GuiScreen.setClipboardString(item);
    }

    public static void blockInformation(List<ITextComponent> messages, @NotNull Block block) {
        //TODO when blocks are implemented
//        Block block = item.getBlock();
//        String s = IngredientHelper.toIIngredient(block).asGroovyCode();
//        String formatted = BracketFormatter.formatGSCode(s, false);
//        sender.sendMessage(new TextComponentString(formatted));
    }

    public static void blockStateInformation(List<ITextComponent> messages, @NotNull IBlockState state) {
        //TODO when blockStates are implemented
//        String s = IngredientHelper.toIIngredient(state).asGroovyCode();
//        String formatted = BracketFormatter.formatGSCode(s, !state.getPropertyKeys().isEmpty());
//        sender.sendMessage(new TextComponentString(formatted));
    }

    public static void fluidInformation(List<ITextComponent> messages, @NotNull FluidStack stack) {
        String s = IngredientHelper.asGroovyCode(stack, true);

        messages.add(new TextComponentString("Fluids:")
                .setStyle(new Style().setColor(TextFormatting.GREEN)));
        messages.add(TextCopyable.string(s, " - " + stack.getFluid().getName()).build());
    }

    public static void oredictInformation(List<ITextComponent> messages, ItemStack stack) {
        String s;
        int[] ids = OreDictionary.getOreIDs(stack);
        if (ids.length > 0) {
            messages.add(new TextComponentString("Ore Dictionaries:")
                    .setStyle(new Style().setColor(TextFormatting.GREEN)));
            for (int id : ids) {
                String oreName = OreDictionary.getOreName(id);
                s = IngredientHelper.asGroovyCode(oreName, true);

                messages.add(TextCopyable.string(s, " - " + oreName).build());
            }
        }
    }
}
