package com.cleanroommc.groovyscript.compat.inworldcrafting.jei;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.compat.inworldcrafting.FluidRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@GroovyBlacklist
public class InWorldCraftingJeiPlugin {

    public static void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new FluidRecipeCategory(guiHelper));
        registry.addRecipeCategories(new ExplosionRecipeCategory(guiHelper));
        registry.addRecipeCategories(new BurningRecipeCategory(guiHelper));
        registry.addRecipeCategories(new PistonPushRecipeCategory(guiHelper));
    }

    public static void register(IModRegistry registry) {
        // add catalyst items
        registry.addRecipeCatalyst(new ItemStack(Items.WATER_BUCKET), FluidRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Items.LAVA_BUCKET), FluidRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Blocks.TNT), ExplosionRecipeCategory.UID);
        //registry.addRecipeCatalyst(new ItemStack(Blocks.FIRE), BurningRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Items.FLINT_AND_STEEL), BurningRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Blocks.PISTON), PistonPushRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Blocks.STICKY_PISTON), PistonPushRecipeCategory.UID);

        // add recipe wrappers
        List<FluidRecipeCategory.RecipeWrapper> recipeWrappers = new ArrayList<>();
        FluidRecipe.forEach(fluidRecipe -> recipeWrappers.add(new FluidRecipeCategory.RecipeWrapper(fluidRecipe)));
        registry.addRecipes(recipeWrappers, FluidRecipeCategory.UID);
        registry.addRecipes(VanillaModule.inWorldCrafting.explosion.getRecipeWrappers(), ExplosionRecipeCategory.UID);
        registry.addRecipes(VanillaModule.inWorldCrafting.burning.getRecipeWrappers(), BurningRecipeCategory.UID);
        registry.addRecipes(VanillaModule.inWorldCrafting.pistonPush.getRecipeWrappers(), PistonPushRecipeCategory.UID);
    }

}
