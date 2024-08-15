package classes

import classes.SimpleConversionRecipe
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IGuiIngredient
import mezz.jei.api.gui.IGuiIngredientGroup
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeWrapper
import mezz.jei.gui.ingredients.GuiIngredient
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n

import java.util.stream.Collectors

/**
 * An example of a IRecipeCategory from JEI, used in {@code /postInit/jei.groovy}.
 * Will only appear if {@code SimpleConversionRecipe.recipes} contains recipes.
 */
class GenericRecipeCategory implements IRecipeCategory<RecipeWrapper> {

    static final String UID = "${getPackId()}:generic"

    private final def icon

    private final def uid
    private final def background

    static def rightArrow
    static def slot

    GenericRecipeCategory(guiHelper) {
        this(guiHelper, UID, 176, 67)
    }

    GenericRecipeCategory(guiHelper, uid, width, height) {
        this.uid = uid
        this.background = guiHelper.createBlankDrawable(width, height)
        if (slot == null) {
            rightArrow = guiHelper.drawableBuilder(resource('groovyscript:textures/jei/arrow_right.png'), 0, 0, 24, 15)
                .setTextureSize(24, 15)
                .build()
            slot = guiHelper.getSlotDrawable()
        }
        this.icon = guiHelper.createDrawableIngredient(item('minecraft:clay'))
    }

    static def getRecipeWrappers() {
        SimpleConversionRecipe.recipes.collect RecipeWrapper.&new
    }

    void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
        addItemSlot(recipeLayout, 0, true, 53, 25)
        addItemSlot(recipeLayout, 1, false, 105, 25)

        recipeLayout.getItemStacks().set(ingredients)
        setBackgrounds(recipeLayout.getItemStacks(), slot)
    }

    void drawExtras(Minecraft minecraft) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        rightArrow.draw(minecraft, 76, 26)
    }

    IDrawable getIcon() {
        icon
    }

    String getUid() {
        this.uid
    }

    String getTitle() {
        I18n.format("jei.category.${this.uid}.name")
    }

    String getModName() {
        getPackName()
    }

    IDrawable getBackground() {
        this.background
    }

    private static void addItemSlot(IRecipeLayout recipeLayout, int index, boolean input, int x, int y) {
        recipeLayout.getItemStacks().init(index, input, x, y)
    }

    private static void setBackgrounds(IGuiIngredientGroup<?> ingredientGroup, IDrawable drawable) {
        for (IGuiIngredient<?> ingredient : ingredientGroup.getGuiIngredients().values()) {
            ((GuiIngredient<?>) ingredient).setBackground(drawable)
        }
    }

    static class RecipeWrapper implements IRecipeWrapper {

        private final SimpleConversionRecipe recipe

        RecipeWrapper(SimpleConversionRecipe recipe) {
            this.recipe = recipe
        }

        void getIngredients(IIngredients ingredients) {
            ingredients.setInput(VanillaTypes.ITEM, this.recipe.getInput())
            ingredients.setOutput(VanillaTypes.ITEM, this.recipe.getOutput())
        }
    }
}
