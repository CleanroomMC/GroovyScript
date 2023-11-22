package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.command.GSCommand;
import com.cleanroommc.groovyscript.command.SimpleCommand;
import com.cleanroommc.groovyscript.compat.inworldcrafting.FluidRecipe;
import com.cleanroommc.groovyscript.compat.inworldcrafting.jei.BurningRecipeCategory;
import com.cleanroommc.groovyscript.compat.inworldcrafting.jei.ExplosionRecipeCategory;
import com.cleanroommc.groovyscript.compat.inworldcrafting.jei.FluidRecipeCategory;
import com.cleanroommc.groovyscript.compat.inworldcrafting.jei.PistonPushRecipeCategory;
import com.cleanroommc.groovyscript.compat.vanilla.ShapedCraftingRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.ShapelessCraftingRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import mezz.jei.Internal;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@GroovyBlacklist
@JEIPlugin
public class JeiPlugin implements IModPlugin {

    public static IJeiHelpers jeiHelpers;
    public static IIngredientRegistry itemRegistry;
    public static IRecipeRegistry recipeRegistry;
    public static IIngredientHelper<ItemStack> ingredientHelper;
    public static IModRegistry modRegistry;
    public static IJeiRuntime jeiRuntime;

    public static IIngredientRenderer<FluidStack> fluidRenderer;

    public static final List<ItemStack> HIDDEN = new ArrayList<>();
    public static final List<FluidStack> HIDDEN_FLUIDS = new ArrayList<>();
    public static final List<String> HIDDEN_CATEGORY = new ArrayList<>();

    public static boolean isLoaded() {
        return jeiRuntime != null;
    }

    public static void reload() {
        HIDDEN.clear();
        HIDDEN_CATEGORY.clear();
    }

    public static void hideItem(ItemStack... stack) {
        Collections.addAll(HIDDEN, stack);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
        fluidRenderer = ingredientRegistry.getIngredientRenderer(VanillaTypes.FLUID);

        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new FluidRecipeCategory(guiHelper));
        registry.addRecipeCategories(new ExplosionRecipeCategory(guiHelper));
        registry.addRecipeCategories(new BurningRecipeCategory(guiHelper));
        registry.addRecipeCategories(new PistonPushRecipeCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        jeiHelpers = registry.getJeiHelpers();
        itemRegistry = registry.getIngredientRegistry();
        ingredientHelper = itemRegistry.getIngredientHelper(VanillaTypes.ITEM);
        modRegistry = registry;

        // jei can't handle custom recipe classes on its own
        registry.handleRecipes(ShapedCraftingRecipe.class, recipe -> new ShapedRecipeWrapper(jeiHelpers, recipe), VanillaRecipeCategoryUid.CRAFTING);
        registry.handleRecipes(ShapelessCraftingRecipe.class, recipe -> new ShapelessRecipeWrapper<>(jeiHelpers, recipe), VanillaRecipeCategoryUid.CRAFTING);

        // register in world crafting recipes
        registry.addRecipeCatalyst(new ItemStack(Items.WATER_BUCKET), FluidRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Items.LAVA_BUCKET), FluidRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Blocks.TNT), ExplosionRecipeCategory.UID);
        //registry.addRecipeCatalyst(new ItemStack(Blocks.FIRE), BurningRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Items.FLINT_AND_STEEL), BurningRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Blocks.PISTON), PistonPushRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Blocks.STICKY_PISTON), PistonPushRecipeCategory.UID);

        List<FluidRecipeCategory.RecipeWrapper> recipeWrappers = new ArrayList<>();
        FluidRecipe.forEach(fluidRecipe -> {
            recipeWrappers.add(new FluidRecipeCategory.RecipeWrapper(fluidRecipe));
        });
        registry.addRecipes(recipeWrappers, FluidRecipeCategory.UID);
        registry.addRecipes(VanillaModule.inWorldCrafting.explosion.getRecipeWrappers(), ExplosionRecipeCategory.UID);
        registry.addRecipes(VanillaModule.inWorldCrafting.burning.getRecipeWrappers(), BurningRecipeCategory.UID);
        registry.addRecipes(VanillaModule.inWorldCrafting.pistonPush.getRecipeWrappers(), PistonPushRecipeCategory.UID);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime iJeiRuntime) {
        recipeRegistry = iJeiRuntime.getRecipeRegistry();
        jeiRuntime = iJeiRuntime;

        if (!HIDDEN.isEmpty()) {
            itemRegistry.removeIngredientsAtRuntime(VanillaTypes.ITEM, ingredientHelper.expandSubtypes(HIDDEN));
        }
        if (!HIDDEN_FLUIDS.isEmpty()) {
            itemRegistry.removeIngredientsAtRuntime(VanillaTypes.FLUID, HIDDEN_FLUIDS);
        }
        for (String category : HIDDEN_CATEGORY) {
            recipeRegistry.hideRecipeCategory(category);
        }
    }

    public static SimpleCommand getJeiCategoriesCommand() {
        return new SimpleCommand("jeiCategories", (server, sender, args) -> {
            GroovyLog.get().info("All JEI Categories:");
            for (IRecipeCategory<?> category : recipeRegistry.getRecipeCategories()) {
                GroovyLog.get().getWriter().println(" - " + category.getUid());
            }
            sender.sendMessage(new TextComponentString("JEI Categories has been logged to the ")
                                       .appendSibling(GSCommand.getTextForFile("Groovy Log", GroovyLog.get().getLogFilerPath(), new TextComponentString("Click to open GroovyScript log"))));
        });
    }
}
