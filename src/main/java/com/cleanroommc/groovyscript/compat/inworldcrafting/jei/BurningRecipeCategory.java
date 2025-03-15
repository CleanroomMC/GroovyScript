package com.cleanroommc.groovyscript.compat.inworldcrafting.jei;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.compat.inworldcrafting.Burning;
import com.cleanroommc.groovyscript.compat.mods.jei.BaseCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;

public class BurningRecipeCategory extends BaseCategory<BurningRecipeCategory.RecipeWrapper> {

    public static final String UID = GroovyScript.ID + ":burning";

    private final IDrawable icon;

    public BurningRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, UID, 176, 56);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(Items.BLAZE_POWDER));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout recipeLayout, @NotNull RecipeWrapper recipeWrapper, @NotNull IIngredients ingredients) {
        addItemSlot(recipeLayout, 0, true, 53, 25);
        addItemSlot(recipeLayout, 1, false, 105, 25);

        recipeLayout.getItemStacks().set(ingredients);
        setBackgrounds(recipeLayout.getItemStacks(), slot);
    }

    @Override
    public void drawExtras(@NotNull Minecraft minecraft) {
        minecraft.fontRenderer.drawSplitString(I18n.format("groovyscript.recipe.burning"), 4, 4, 168, 0x404040);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        rightArrow.draw(minecraft, 76, 26);
        float tntScale = 0.5f;
        GlStateManager.pushMatrix();
        GlStateManager.translate(80, 26, 0);
        GlStateManager.translate(8, 8, 0);
        GlStateManager.scale(tntScale, tntScale, 1);
        GlStateManager.translate(-8, -8, 0);
        this.icon.draw(minecraft);
        GlStateManager.popMatrix();
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    public static class RecipeWrapper implements IRecipeWrapper {

        private final Burning.BurningRecipe burningRecipe;

        public RecipeWrapper(Burning.BurningRecipe burningRecipe) {
            this.burningRecipe = burningRecipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(Arrays.asList(this.burningRecipe.getInput().getMatchingStacks())));
            ingredients.setOutput(VanillaTypes.ITEM, this.burningRecipe.getOutput());
        }

        @Override
        public void drawInfo(@NotNull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            int ticks = this.burningRecipe.getTicks();
            String ticksS = ticks + " ticks";
            int w = minecraft.fontRenderer.getStringWidth(ticksS);
            int x = 88 - w / 2;
            int y = 44;
            minecraft.fontRenderer.drawString(ticksS, x, y, 0x404040);
        }
    }
}
