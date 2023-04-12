package com.cleanroommc.groovyscript.core.mixin.extendedcrafting;

import com.blakebr0.extendedcrafting.config.ModConfig;
import com.blakebr0.extendedcrafting.item.ItemRecipeMaker;
import com.blakebr0.extendedcrafting.lib.IExtendedTable;
import com.blakebr0.extendedcrafting.tile.TileEnderCrafter;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.google.common.base.Joiner;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = ItemRecipeMaker.class, remap = false)
public abstract class ItemRecipeMakerMixin {

    @Shadow
    protected abstract boolean isShapeless(ItemStack stack);

    @Inject(method = "setClipboard", at = @At("HEAD"), cancellable = true)
    public void setClipboard(IExtendedTable table, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (Desktop.isDesktopSupported()) {
            boolean ender = table instanceof TileEnderCrafter;
            StringBuilder string = (new StringBuilder("mods.extendedcrafting.")).append(ender ? "EnderCrafting" : "TableCrafting");
            String inputs;
            if (isShapeless(stack)) {
                inputs = groovyscript$makeItemArrayShapeless(table);
                string.append(".addShapeless(");
            } else {
                inputs = groovyscript$makeItemArrayShaped(table, !ender);
                string.append(".addShaped(");
            }
            if (inputs == null) {
                cir.setReturnValue(false);
                return;
            }
            if (!ender) string.append("0, ");
            string.append("OUTPUT, [").append(inputs).append("])");
            StringSelection stringSelection = new StringSelection(string.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            cir.setReturnValue(true);
            return;
        }
        cir.setReturnValue(false);
    }

    public String groovyscript$makeItemArrayShapeless(IExtendedTable table) {
        StringBuilder builder = new StringBuilder();
        boolean isEmpty = true;
        for (ItemStack stack : table.getMatrix()) {
            if (!stack.isEmpty()) {
                builder.append(groovyscript$makeItem(stack))
                        .append(", ");
                isEmpty = false;
            }
        }
        if (isEmpty) return null;
        return builder.delete(builder.length() - 2, builder.length()).toString();
    }

    public String groovyscript$makeItemArrayShaped(IExtendedTable table, boolean removeEmpties) {
        List<List<String>> matrix = new ArrayList<>();
        int row = 0;
        int column = 0;
        boolean rowEmpty = true, allEmpty = true;
        matrix.add(new ArrayList<>());
        for (ItemStack stack : table.getMatrix()) {
            String s = stack.isEmpty() ? null : groovyscript$makeItem(stack);
            rowEmpty &= s == null;
            allEmpty &= rowEmpty;
            matrix.get(row).add(s);
            if (++column == table.getLineSize()) {
                if (rowEmpty && removeEmpties) {
                    matrix.remove(row);
                    row--;
                }
                row++;
                column = 0;
                rowEmpty = true;
                matrix.add(new ArrayList<>());
            }
        }
        matrix.remove(row);
        if (allEmpty || matrix.isEmpty()) return null;
        if (removeEmpties) {
            // remove empty columns
            for (int col = 0; col < matrix.get(0).size(); col++) {
                boolean isEmpty = true;
                for (row = 0; row < matrix.size(); row++) {
                    if (matrix.get(row).get(col) != null) {
                        isEmpty = false;
                        break;
                    }
                }
                if (isEmpty) {
                    for (row = 0; row < matrix.size(); row++) {
                        matrix.get(row).remove(col);
                    }
                    col--;
                }
            }
        }
        return '\n' + Joiner.on(", \n").join(matrix) + '\n';
    }

    private static String groovyscript$makeItem(ItemStack stack) {
        if (ModConfig.confRMOredict) {
            int[] oreIds = OreDictionary.getOreIDs(stack);
            if (oreIds.length > 0) {
                return IngredientHelper.asGroovyCode(OreDictionary.getOreName(oreIds[0]), false);
            }
        }
        if (ModConfig.confRMNBT) {
            return IngredientHelper.asGroovyCode(stack, false, false);
        }
        return IngredientHelper.asGroovyCode(stack, false);
    }
}
