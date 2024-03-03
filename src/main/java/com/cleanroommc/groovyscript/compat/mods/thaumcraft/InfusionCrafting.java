package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static thaumcraft.common.config.ConfigRecipes.compileGroups;

@RegistryDescription
public class InfusionCrafting extends VirtualizedRegistry<Pair<ResourceLocation, InfusionRecipe>> {

    @RecipeBuilderDescription(example = @Example(".researchKey('UNLOCKALCHEMY@3').mainInput(item('minecraft:gunpowder')).output(item('minecraft:gold_ingot')).aspect(aspect('terra') * 20).aspect('ignis', 30).input(crystal('aer')).input(crystal('ignis')).input(crystal('aqua')).input(crystal('terra')).input(crystal('ordo')).instability(10)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> ThaumcraftApi.getCraftingRecipes().remove(recipe.getKey(), recipe.getValue()));
        restoreFromBackup().forEach(recipe -> ThaumcraftApi.addInfusionCraftingRecipe(recipe.getKey(), recipe.getValue()));
        compileGroups();
    }

    public void add(ResourceLocation rl, InfusionRecipe recipe) {
        if (recipe != null && recipe.recipeOutput instanceof ItemStack) {
            recipe.setGroup(rl);
            addScripted(Pair.of(rl, recipe));
            ThaumcraftApi.addInfusionCraftingRecipe(rl, recipe);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public InfusionRecipe add(String research, ItemStack outputResult, int inst, Collection<AspectStack> aspects, IIngredient centralItem, IIngredient... input) {
        Object[] inputs = ArrayUtils.map(input, IIngredient::toMcIngredient, new Ingredient[0]);
        InfusionRecipe infusionRecipe = new InfusionRecipe(research, outputResult, inst, Thaumcraft.makeAspectList(aspects), centralItem.toMcIngredient(), inputs);
        add(RecipeName.generateRl("infusion_matrix_recipe"), infusionRecipe);
        return infusionRecipe;
    }

    public boolean remove(InfusionRecipe recipe) {
        List<InfusionRecipe> recipes = new ArrayList<>();
        for (IThaumcraftRecipe r : ThaumcraftApi.getCraftingRecipes().values()) {
            if (r instanceof InfusionRecipe && r.equals(recipe))
                recipes.add((InfusionRecipe) r);
        }
        recipes.forEach(rec -> {
            if ("".equals(rec.getGroup())) {
                this.addBackup(Pair.of(new ResourceLocation("thaumcraft:" + ((ItemStack) rec.recipeOutput).getItem()), recipe));
            } else {
                this.addBackup(Pair.of(new ResourceLocation(rec.getGroup()), recipe));
            }
            ThaumcraftApi.getCraftingRecipes().values().remove(recipe);
        });
        return !recipes.isEmpty();
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('thaumcraft:crystal_terra')"))
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
                    .add("no recipes found for {}", output.toString())
                    .error()
                    .post();
            return;
        }
        recipes.forEach(recipe -> {
            if ("".equals(recipe.getGroup())) {
                this.addBackup(Pair.of(new ResourceLocation("thaumcraft:" + ((ItemStack) recipe.recipeOutput).getItem()), recipe));
            } else {
                this.addBackup(Pair.of(new ResourceLocation(recipe.getGroup()), recipe));
            }
            ThaumcraftApi.getCraftingRecipes().values().remove(recipe);
        });
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, IThaumcraftRecipe>> streamRecipes() {
        List<Map.Entry<ResourceLocation, IThaumcraftRecipe>> recipes = ThaumcraftApi.getCraftingRecipes().entrySet().stream().filter(x -> x.getValue() instanceof InfusionRecipe).collect(Collectors.toList());
        return new SimpleObjectStream<>(recipes)
                .setRemover(x -> remove((InfusionRecipe) x.getValue()));
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        List<Map.Entry<ResourceLocation, IThaumcraftRecipe>> recipes = ThaumcraftApi.getCraftingRecipes().entrySet().stream().filter(x -> x.getValue() instanceof InfusionRecipe).collect(Collectors.toList());
        for (Map.Entry<ResourceLocation, IThaumcraftRecipe> recipe : recipes) {
            addBackup(Pair.of(recipe.getKey(), (InfusionRecipe) recipe.getValue()));
            ThaumcraftApi.getCraftingRecipes().remove(recipe.getKey(), recipe.getValue());
        }
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<InfusionRecipe> {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private IIngredient mainInput;
        @Property
        private String researchKey;
        @Property
        private final AspectList aspects = new AspectList();
        @Property
        private int instability;

        @RecipeBuilderMethodDescription
        public RecipeBuilder mainInput(IIngredient ingredient) {
            this.mainInput = ingredient;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder researchKey(String researchKey) {
            this.researchKey = researchKey;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "aspects")
        public RecipeBuilder aspect(AspectStack aspect) {
            this.aspects.add(aspect.getAspect(), aspect.getAmount());
            return this;
        }

        @RecipeBuilderMethodDescription(field = "aspects")
        public RecipeBuilder aspect(String tag, int amount) {
            Aspect a = Thaumcraft.validateAspect(tag);
            if (a != null) this.aspects.add(a, amount);
            return this;
        }

        @RecipeBuilderMethodDescription
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
            validateItems(msg, 1, 100, 1, 1);
            msg.add(IngredientHelper.isEmpty(mainInput), () -> "Main Input must not be empty");
            if (researchKey == null) {
                researchKey = "";
            }
            if (instability < 0) {
                instability = 0;
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable InfusionRecipe register() {
            if (!validate()) return null;

            Object[] inputs = this.input.stream().map(IIngredient::toMcIngredient).toArray();
            InfusionRecipe recipe = new InfusionRecipe(researchKey, output.get(0), instability, aspects, mainInput.toMcIngredient(), inputs);
            ModSupport.THAUMCRAFT.get().infusionCrafting.add(RecipeName.generateRl("infusion_matrix_recipe"), recipe);
            return recipe;
        }
    }
}
