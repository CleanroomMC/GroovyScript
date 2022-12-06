package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static thaumcraft.common.config.ConfigRecipes.compileGroups;

public class InfusionCrafting extends VirtualizedRegistry<InfusionRecipe> {

    public InfusionCrafting() {
        super("InfusionCrafting", "infusion_crafting");
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> ThaumcraftApi.getCraftingRecipes().values().remove(recipe));
        restoreFromBackup().forEach(recipe -> {
            if (!ThaumcraftApi.getCraftingRecipes().values().contains(recipe))
                ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(recipe.getRecipeOutput().toString()), recipe);
        });
        compileGroups();
    }

    public void add(InfusionRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(recipe.getRecipeOutput().toString()), recipe);
        }
    }

    public InfusionRecipe add(String research, ItemStack outputResult, int inst, Collection<AspectStack> aspects, IIngredient centralItem, IIngredient... input) {
        Object[] inputs = ArrayUtils.map(input, IIngredient::toMcIngredient, new Ingredient[0]);
        InfusionRecipe infusionRecipe = new InfusionRecipe(research, outputResult, inst, Thaumcraft.makeAspectList(aspects), centralItem.toMcIngredient(), inputs);
        add(infusionRecipe);
        return infusionRecipe;
    }

    public void removeByOutput(IIngredient output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Thaumcraft Infusion Crafting recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        List<InfusionRecipe> recipes = new ArrayList<>();
        for (IThaumcraftRecipe r : ThaumcraftApi.getCraftingRecipes().values()) {
            if (r instanceof InfusionRecipe && ((InfusionRecipe) r).getRecipeOutput() instanceof ItemStack) {
                ItemStack ro = (ItemStack) ((InfusionRecipe) r).getRecipeOutput();
                if (output.test(ro)) {
                    recipes.add((InfusionRecipe) r);
                }
            }
        }
        if (recipes.isEmpty()) {
            GroovyLog.msg("Error removing Thaumcraft Infusion Crafting recipe")
                    .add("no recipes found for %s", output.toString())
                    .error()
                    .post();
            return;
        }
        recipes.forEach(recipe -> {
            this.addBackup(recipe);
            ThaumcraftApi.getCraftingRecipes().values().remove(recipe);
        });
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<InfusionRecipe> {

        private IIngredient mainInput;
        private String researchKey;
        private final AspectList aspects = new AspectList();
        private int instability;

        public RecipeBuilder mainInput(IIngredient ingredient) {
            this.mainInput = ingredient;
            return this;
        }

        public RecipeBuilder researchKey(String researchKey) {
            this.researchKey = researchKey;
            return this;
        }

        public RecipeBuilder aspect(AspectStack aspect) {
            this.aspects.add(aspect.getAspect(), aspect.getAmount());
            return this;
        }

        public RecipeBuilder instability(int instability) {
            this.instability = instability;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thaumcraft Infusion Crafting recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 20, 1, 1);
            msg.add(IngredientHelper.isEmpty(mainInput), () -> "Main Input must not be empty");
            if (researchKey == null) {
                researchKey = "";
            }
            if (instability < 0) {
                instability = 0;
            }
        }

        @Override
        public @Nullable InfusionRecipe register() {
            if (!validate()) return null;

            Object[] inputs = this.input.stream().map(IIngredient::toMcIngredient).toArray();
            InfusionRecipe recipe = new InfusionRecipe(researchKey, output.get(0), instability, aspects, mainInput.toMcIngredient(), inputs);
            ModSupport.THAUMCRAFT.get().infusionCrafting.add(recipe);
            return recipe;
        }
    }
}
