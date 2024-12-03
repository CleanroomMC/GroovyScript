package com.cleanroommc.groovyscript.compat.inworldcrafting.jei;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.compat.inworldcrafting.Explosion;
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
import java.util.List;

public class ExplosionRecipeCategory extends BaseCategory<ExplosionRecipeCategory.RecipeWrapper> {

    public static final String UID = GroovyScript.ID + ":explosion";

    private final IDrawable icon;

    public ExplosionRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, UID, 176, 56);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(Blocks.TNT));
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
        minecraft.fontRenderer.drawSplitString(I18n.format("groovyscript.recipe.explosion"), 4, 4, 168, 0x404040);
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

        private final Explosion.ExplosionRecipe explosionRecipe;

        public RecipeWrapper(Explosion.ExplosionRecipe explosionRecipe) {
            this.explosionRecipe = explosionRecipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(Arrays.asList(this.explosionRecipe.getInput().getMatchingStacks())));
            ingredients.setOutput(VanillaTypes.ITEM, this.explosionRecipe.getOutput());
        }

        @Override
        public void drawInfo(@NotNull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            float chance = this.explosionRecipe.getChance();
            if (chance < 1) {
                String chanceS = FluidRecipeCategory.numberFormat.format(chance);
                int w = minecraft.fontRenderer.getStringWidth(chanceS);
                int x = 88 - w / 2;
                int y = 44;
                minecraft.fontRenderer.drawString(chanceS, x, y, 0x404040);
            }
        }

        @Override
        public @NotNull List<String> getTooltipStrings(int mouseX, int mouseY) {
            if (this.explosionRecipe.getChance() < 1 && mouseX >= 74 && mouseX <= 72 + 28 && mouseY >= 25 && mouseY <= 54) {
                return Collections.singletonList(I18n.format("groovyscript.recipe.chance_produce", FluidRecipeCategory.numberFormat.format(this.explosionRecipe.getChance())));
            }
            return Collections.emptyList();
        }
    }
}
