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

        messages.add(new TextComponentString("Item:"));
        messages.add(TextCopyable.string(item, itemPretty).build());
        GuiScreen.setClipboardString(item);
        String copy = stack.getItem().getTranslationKey(stack);
        messages.add(TextCopyable.string(copy, "Translation key: " + TextFormatting.YELLOW + copy).build());
    }

    public static void blockStateInformation(List<ITextComponent> messages, @NotNull IBlockState state) {
        String copyText = IngredientHelper.asGroovyCode(state, false);
        messages.add(new TextComponentString("Block state:"));
        messages.add(TextCopyable.string(copyText, IngredientHelper.asGroovyCode(state, true)).build());
    }

    public static void fluidInformation(List<ITextComponent> messages, @NotNull FluidStack stack) {
        fluidInformation(messages, Collections.singletonList(stack));
    }

    public static void fluidInformation(List<ITextComponent> messages, @NotNull List<FluidStack> fluidStacks) {
        if (fluidStacks.isEmpty()) return;
        messages.add(new TextComponentString("Fluids:")
                             .setStyle(new Style().setColor(TextFormatting.GREEN)));

        for (FluidStack stack : fluidStacks) {
            String s = IngredientHelper.asGroovyCode(stack, true);
            messages.add(TextCopyable.string(s, " - " + stack.getFluid().getName()).build());
        }
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

                messages.add(TextCopyable.string(s, " - \u00A7b" + oreName).build());
            }
        }
    }

    public static void tileInformation(List<ITextComponent> messages, TileEntity tile) {
        NBTTagCompound nbt = tile.serializeNBT();
        String copyText = NbtHelper.toGroovyCode(nbt, false, false);

        messages.add(new TextComponentString("Tile NBT:"));
        messages.add(TextCopyable.string(copyText, NbtHelper.toGroovyCode(nbt, true, true)).build());
    }
}
