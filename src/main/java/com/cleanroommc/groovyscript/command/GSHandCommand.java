package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.NbtHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Creates and sends information to the player for the `/gs hand` command
 * Used in {@link GSCommand}
 */
public class GSHandCommand {

    public static void itemInformation(List<ITextComponent> messages, @NotNull ItemStack stack, boolean prettyNbt) {
        String itemPretty = IngredientHelper.asGroovyCode(stack, true, prettyNbt);
        String item = IngredientHelper.asGroovyCode(stack, false, prettyNbt);

        messages.add(new TextComponentString("Item:")
                             .setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setBold(true)));
        messages.add(TextCopyable.string(item, " - " + itemPretty).build());
        GuiScreen.setClipboardString(item);
        String copy = stack.getItem().getTranslationKey(stack);

        messages.add(new TextComponentString("Translation key:")
                             .setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setBold(true)));

        messages.add(TextCopyable.string(copy, " - " + TextFormatting.YELLOW + copy).build());
    }

    public static void blockStateInformation(List<ITextComponent> messages, @NotNull IBlockState state) {
        String copyText = IngredientHelper.asGroovyCode(state, false);
        messages.add(new TextComponentString("Block state:")
                             .setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setBold(true)));
        messages.add(TextCopyable.string(copyText, " - " + IngredientHelper.asGroovyCode(state, true)).build());
    }

    public static void fluidInformation(List<ITextComponent> messages, @NotNull FluidStack stack, boolean prettyNbt) {
        fluidInformation(messages, Collections.singletonList(stack), prettyNbt);
    }

    public static void fluidInformation(List<ITextComponent> messages, @NotNull List<FluidStack> fluidStacks, boolean prettyNbt) {
        if (fluidStacks.isEmpty()) return;
        messages.add(new TextComponentString("Fluids:")
                             .setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setBold(true)));

        for (FluidStack stack : fluidStacks) {
            String copyText = IngredientHelper.asGroovyCode(stack, false, prettyNbt);
            messages.add(TextCopyable.string(copyText, " - " + IngredientHelper.asGroovyCode(stack, true, false)).build());
        }
    }

    public static void oredictInformation(List<ITextComponent> messages, ItemStack stack) {
        String s;
        int[] ids = OreDictionary.getOreIDs(stack);
        if (ids.length > 0) {
            messages.add(new TextComponentString("Ore Dictionaries:")
                                 .setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setBold(true)));
            for (int id : ids) {
                String oreName = OreDictionary.getOreName(id);
                s = IngredientHelper.asGroovyCode(oreName, true);

                messages.add(TextCopyable.string(s, " - \u00A7b" + oreName).build());
            }
        }
    }

    public static void tileInformation(List<ITextComponent> messages, TileEntity tile) {
        NBTTagCompound nbt = tile.serializeNBT();
        String copyText = NbtHelper.toGroovyCode(nbt, false, false);
        String msg = NbtHelper.toGroovyCode(nbt, true, true);
        int trimLocation = StringUtils.ordinalIndexOf(msg, "\n", 8);

        messages.add(new TextComponentString("Tile NBT:")
                             .setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setBold(true)));
        messages.add(TextCopyable.string(copyText, trimLocation == -1 ? msg : msg.substring(0, trimLocation) + "\n\u00A7c(trimmed)").build());
    }
}
