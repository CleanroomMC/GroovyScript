package com.cleanroommc.groovyscript.core.mixin.botania;

import com.cleanroommc.groovyscript.compat.vanilla.ShapedCraftingRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.ShapelessCraftingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.api.internal.IGuiLexiconEntry;
import vazkii.botania.common.lexicon.page.PageCraftingRecipe;
import vazkii.botania.common.lexicon.page.PageRecipe;

@Mixin(value = PageCraftingRecipe.class, remap = false)
public abstract class PageCraftingRecipeMixin extends PageRecipe {

    @Shadow
    boolean shapelessRecipe;
    @Shadow
    boolean oreDictRecipe;
    @Shadow
    int ticksElapsed;

    public PageCraftingRecipeMixin(String unlocalizedName) {
        super(unlocalizedName);
    }

    /**
     * @author Turing6
     * @reason Groovyscript's recipe classes are ignored during ingredient render
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public void renderCraftingRecipe(IGuiLexiconEntry gui, IRecipe recipe) {
        if (recipe != null) {
            int x;
            int y;
            int index;
            if (!(recipe instanceof ShapedRecipes) && !(recipe instanceof ShapedOreRecipe) && !(recipe instanceof ShapedCraftingRecipe)) {
                if (recipe instanceof ShapelessRecipes || recipe instanceof ShapelessOreRecipe || recipe instanceof ShapelessCraftingRecipe) {
                    this.shapelessRecipe = true;
                    this.oreDictRecipe = recipe instanceof ShapelessOreRecipe;

                    drawGrid:
                    for (y = 0; y < 3; ++y) {
                        for (x = 0; x < 3; ++x) {
                            index = y * 3 + x;
                            if (index >= recipe.getIngredients().size()) {
                                break drawGrid;
                            }

                            Ingredient input = recipe.getIngredients().get(index);
                            if (input != Ingredient.EMPTY) {
                                ItemStack[] stacks = input.getMatchingStacks();
                                this.renderItemAtGridPos(gui, 1 + x, 1 + y, stacks[this.ticksElapsed / 40 % stacks.length], true);
                            }
                        }
                    }
                }
            } else {
                this.oreDictRecipe = recipe instanceof ShapedOreRecipe;
                int width = oreDictRecipe
                        ? ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, (ShapedOreRecipe) recipe, "width")
                        : recipe instanceof ShapedCraftingRecipe craftingRecipe ? craftingRecipe.getRecipeWidth()
                        : ((ShapedRecipes) recipe).getWidth();
                int height = oreDictRecipe
                        ? ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, (ShapedOreRecipe) recipe, "height")
                        : recipe instanceof ShapedCraftingRecipe shapedCraftingRecipe ? shapedCraftingRecipe.getRecipeHeight()
                        : ((ShapedRecipes) recipe).getHeight();

                for (index = 0; index < height; ++index) {
                    for (x = 0; x < width; ++x) {
                        Ingredient input = recipe.getIngredients().get(index * width + x);
                        ItemStack[] stacks = input.getMatchingStacks();
                        if (stacks.length > 0) {
                            this.renderItemAtGridPos(gui, 1 + x, 1 + index, stacks[this.ticksElapsed / 40 % stacks.length], true);
                        }
                    }
                }
            }

            this.renderItemAtGridPos(gui, 2, 0, recipe.getRecipeOutput(), false);
        }
    }
}
