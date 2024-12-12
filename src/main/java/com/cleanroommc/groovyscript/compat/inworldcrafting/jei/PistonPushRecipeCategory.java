package com.cleanroommc.groovyscript.compat.inworldcrafting.jei;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.compat.inworldcrafting.PistonPush;
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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;

public class PistonPushRecipeCategory extends BaseCategory<PistonPushRecipeCategory.RecipeWrapper> {

    public static final String UID = GroovyScript.ID + ":piston_push";

    private final IDrawable icon;

    public PistonPushRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, UID, 176, 67);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(Blocks.PISTON));
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
        minecraft.fontRenderer.drawSplitString(I18n.format("groovyscript.recipe.piston_push"), 4, 4, 168, 0x404040);
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

        private final PistonPush.PistonPushRecipe pistonPushRecipe;

        public RecipeWrapper(PistonPush.PistonPushRecipe pistonPushRecipe) {
            this.pistonPushRecipe = pistonPushRecipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(Arrays.asList(this.pistonPushRecipe.getInput().getMatchingStacks())));
            ingredients.setOutput(VanillaTypes.ITEM, this.pistonPushRecipe.getOutput());
        }

        @Override
        public void drawInfo(@NotNull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            int y = 46;
            if (this.pistonPushRecipe.getMaxConversionsPerPush() < 64) {
                minecraft.fontRenderer.drawString(I18n.format("groovyscript.recipe.piston_push.max_items", this.pistonPushRecipe.getMaxConversionsPerPush()), 7, y, 0x404040);
            }
            y += 11;
            if (this.pistonPushRecipe.getMinHarvestLevel() >= 0) {
                minecraft.fontRenderer.drawString(I18n.format("groovyscript.recipe.piston_push.min_level", this.pistonPushRecipe.getMinHarvestLevel()), 7, y, 0x404040);
            }
        }
    }
}
