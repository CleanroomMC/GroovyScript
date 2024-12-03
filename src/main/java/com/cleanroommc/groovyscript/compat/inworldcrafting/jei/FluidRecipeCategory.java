package com.cleanroommc.groovyscript.compat.inworldcrafting.jei;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.inworldcrafting.FluidRecipe;
import com.cleanroommc.groovyscript.compat.inworldcrafting.FluidToFluid;
import com.cleanroommc.groovyscript.compat.inworldcrafting.FluidToItem;
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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FluidRecipeCategory extends BaseCategory<FluidRecipeCategory.RecipeWrapper> {

    public static final String UID = GroovyScript.ID + ":fluid_recipe";
    public static final NumberFormat numberFormat = NumberFormat.getPercentInstance();

    private static final int inputY = 23;
    public static final int outputY = 52;
    public static final int outputX = 105;

    public final IDrawable downRightArrow;
    public final IDrawable icon;

    public FluidRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, UID, 176, 79);

        downRightArrow = guiHelper.drawableBuilder(new ResourceLocation(GroovyScript.ID, "textures/jei/arrow_down_right.png"), 0, 0, 24, 18)
                .setTextureSize(24, 18)
                .build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(Items.WATER_BUCKET));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout recipeLayout, @NotNull RecipeWrapper recipeWrapper, @NotNull IIngredients ingredients) {
        for (int i = 0; i < 9; i++) {
            addItemSlot(recipeLayout, i, true, 7 + 18 * i, inputY);
        }
        addFluidSlot(recipeLayout, 10, true, 53, outputY);
        if (recipeWrapper.recipe.getClass() == FluidToFluid.Recipe.class) {
            addFluidSlot(recipeLayout, 11, false, outputX, outputY);
        } else {
            addItemSlot(recipeLayout, 11, false, outputX, outputY);
        }
        recipeLayout.getItemStacks().set(ingredients);
        recipeLayout.getFluidStacks().set(ingredients);

        setBackgrounds(recipeLayout.getItemStacks(), slot);
        setBackgrounds(recipeLayout.getFluidStacks(), slot);

        recipeLayout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex < 9) {
                float chance = recipeWrapper.recipe.getItemConsumeChance()[slotIndex];
                if (chance < 1.0f) {
                    tooltip.add(1, I18n.format("groovyscript.recipe.chance_consume", numberFormat.format(chance)));
                }
            }
        });
        recipeLayout.getFluidStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == 10 && recipeWrapper.recipe instanceof FluidToItem.Recipe fluidToItemRecipe) {
                float chance = fluidToItemRecipe.getFluidConsumptionChance();
                if (chance < 1.0f) {
                    tooltip.add(1, I18n.format("groovyscript.recipe.chance_consume", numberFormat.format(chance)));
                }
            }
        });
    }

    public static List<List<ItemStack>> getJeiItemIngredients(FluidRecipe fluidRecipe) {
        List<List<ItemStack>> list = new ArrayList<>();
        for (IIngredient ingredient : fluidRecipe.getItemInputs()) {
            list.add(Arrays.asList(ingredient.getMatchingStacks()));
        }
        return list;
    }

    public static void getIngredients(FluidRecipe recipe, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, getJeiItemIngredients(recipe));
        ingredients.setInput(VanillaTypes.FLUID, new FluidStack(recipe.getFluidInput(), 1000));
    }

    @Override
    public void drawExtras(@NotNull Minecraft minecraft) {
        drawLine(minecraft, "groovyscript.recipe.fluid_recipe", 4, 4, 0x404040);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.downRightArrow.draw(minecraft, 23, outputY - 3);
        rightArrow.draw(minecraft, 76, outputY + 1);
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    public static void drawLine(Minecraft minecraft, String langKey, int x, int y, int color, Object... obj) {
        minecraft.fontRenderer.drawSplitString(I18n.format(langKey, obj), x, y, 168, color);
    }

    public static class RecipeWrapper implements IRecipeWrapper {

        private final FluidRecipe recipe;

        public RecipeWrapper(FluidRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(@NotNull IIngredients ingredients) {
            FluidRecipeCategory.getIngredients(this.recipe, ingredients);
            this.recipe.setJeiOutput(ingredients);

        }

        @Override
        public void drawInfo(@NotNull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            IRecipeWrapper.super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);

            float scale = 0.6f;
            float yOff = 9 * scale;
            int y = inputY + 18;
            int x = 7 - 18;
            for (int i = 0, n = this.recipe.getItemInputs().length; i < n; i++) {
                x += 18;
                float chance = this.recipe.getItemConsumeChance()[i];
                if (chance >= 1.0f) continue;
                String chanceS = numberFormat.format(chance);
                float w = minecraft.fontRenderer.getStringWidth(chanceS) * scale;
                float xx = 9 - w / 2.0f;
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + xx, y - 1, 0);
                GlStateManager.translate(xx, yOff, 0);
                GlStateManager.scale(scale, scale, 1);
                GlStateManager.translate(-xx, -yOff, 0);
                minecraft.fontRenderer.drawString(chanceS, 0, 0, 0x404040);
                GlStateManager.popMatrix();
            }
            if (this.recipe instanceof FluidToItem.Recipe fluidToItemRecipe) {
                y = outputY + 18;
                x = 53;
                float chance = fluidToItemRecipe.getFluidConsumptionChance();
                if (chance >= 1.0f) return;
                String chanceS = numberFormat.format(chance);
                float w = minecraft.fontRenderer.getStringWidth(chanceS) * scale;
                float xx = 9 - w / 2.0f;
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + xx, y - 1, 0);
                GlStateManager.translate(xx, yOff, 0);
                GlStateManager.scale(scale, scale, 1);
                GlStateManager.translate(-xx, -yOff, 0);
                minecraft.fontRenderer.drawString(chanceS, 0, 0, 0x404040);
                GlStateManager.popMatrix();
            }
        }
    }
}
