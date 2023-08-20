package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.GroovyScript;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.ingredients.GuiIngredient;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class BaseCategory<T extends IRecipeWrapper> implements IRecipeCategory<T> {

    private final String uid;
    private final IDrawable background;

    public static IDrawable rightArrow;
    public static IDrawable slot;

    public BaseCategory(IGuiHelper guiHelper, String uid, int width, int height) {
        this.uid = uid;
        this.background = guiHelper.createBlankDrawable(width, height);
        if (slot == null) {
            rightArrow = guiHelper.drawableBuilder(new ResourceLocation(GroovyScript.ID, "textures/jei/arrow_right.png"), 0, 0, 24, 15)
                    .setTextureSize(24, 15)
                    .build();
            slot = guiHelper.getSlotDrawable();
        }
    }

    @Override
    public @NotNull String getUid() {
        return this.uid;
    }

    @Override
    public @NotNull String getTitle() {
        return I18n.format(GroovyScript.ID + ".jei.category." + this.uid + ".name");
    }

    @Override
    public @NotNull String getModName() {
        return GroovyScript.NAME;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    public static void addItemSlot(IRecipeLayout recipeLayout, int index, boolean input, int x, int y) {
        recipeLayout.getItemStacks().init(index, input, x, y);
    }

    public static void addFluidSlot(IRecipeLayout recipeLayout, int index, boolean input, int x, int y) {
        recipeLayout.getFluidStacks().init(index, input, JeiPlugin.fluidRenderer, x, y, 18, 18, 1, 1);
    }

    protected static void setBackgrounds(IGuiIngredientGroup<?> ingredientGroup, IDrawable drawable) {
        for (IGuiIngredient<?> ingredient : ingredientGroup.getGuiIngredients().values()) {
            ((GuiIngredient<?>) ingredient).setBackground(drawable);
        }
    }
}
