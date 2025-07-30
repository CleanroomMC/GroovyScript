package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
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
        return recipeBuilder().researchKey(research).mainInput(centralItem).instability(inst).aspect(aspects).input(input).output(outputResult).register();
    }

    public boolean remove(InfusionRecipe recipe) {
        List<InfusionRecipe> recipes = new ArrayList<>();
        for (IThaumcraftRecipe r : ThaumcraftApi.getCraftingRecipes().values()) {
            if (r instanceof InfusionRecipe infusionRecipe && r.equals(recipe))
                recipes.add(infusionRecipe);
        }
        recipes.forEach(rec -> {
            if (rec.getGroup().isEmpty()) {
                this.addBackup(Pair.of(new ResourceLocation("thaumcraft:" + ((ItemStack) rec.recipeOutput).getItem()), recipe));
            } else {
                this.addBackup(Pair.of(new ResourceLocation(rec.getGroup()), recipe));
            }
            ThaumcraftApi.getCraftingRecipes().values().remove(recipe);
        });
        return !recipes.isEmpty();
    }

    @MethodDescription(example = @Example("item('thaumcraft:crystal_terra')"))
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
            if (r instanceof InfusionRecipe infusionRecipe && infusionRecipe.getRecipeOutput() instanceof ItemStack ro) {
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
            if (recipe.getGroup().isEmpty()) {
                this.addBackup(Pair.of(new ResourceLocation("thaumcraft:" + ((ItemStack) recipe.recipeOutput).getItem()), recipe));
            } else {
                this.addBackup(Pair.of(new ResourceLocation(recipe.getGroup()), recipe));
            }
            ThaumcraftApi.getCraftingRecipes().values().remove(recipe);
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, IThaumcraftRecipe>> streamRecipes() {
        List<Map.Entry<ResourceLocation, IThaumcraftRecipe>> recipes = ThaumcraftApi.getCraftingRecipes().entrySet().stream().filter(x -> x.getValue() instanceof InfusionRecipe).collect(Collectors.toList());
        return new SimpleObjectStream<>(recipes)
                .setRemover(x -> remove((InfusionRecipe) x.getValue()));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        List<Map.Entry<ResourceLocation, IThaumcraftRecipe>> recipes = ThaumcraftApi.getCraftingRecipes().entrySet().stream().filter(x -> x.getValue() instanceof InfusionRecipe).collect(Collectors.toList());
        for (Map.Entry<ResourceLocation, IThaumcraftRecipe> recipe : recipes) {
            addBackup(Pair.of(recipe.getKey(), (InfusionRecipe) recipe.getValue()));
            ThaumcraftApi.getCraftingRecipes().remove(recipe.getKey(), recipe.getValue());
        }
    }

    @Property(property = "input", comp = @Comp(gte = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<InfusionRecipe> {

        @Property(comp = @Comp(not = "null"))
        private IIngredient mainInput;
        @Property
        private String researchKey;
        @Property
        private final AspectList aspects = new AspectList();
        @Property
        private int instability;

        @Override
        public String getRecipeNamePrefix() {
            return "infusion_matrix_recipe";
        }

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
        public RecipeBuilder aspect(AspectStack... aspects) {
            for (AspectStack aspect : aspects) {
                aspect(aspect);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "aspects")
        public RecipeBuilder aspect(Collection<AspectStack> aspects) {
            for (AspectStack aspect : aspects) {
                aspect(aspect);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "aspects")
        public RecipeBuilder aspect(AspectList aspectList) {
            this.aspects.merge(aspectList);
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
        protected int getMaxItemInput() {
            // More than 1 item cannot be placed in each pedestal
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, Integer.MAX_VALUE, 1, 1);
            msg.add(IngredientHelper.isEmpty(mainInput), () -> "Main Input must not be empty");
            // More than 1 item cannot be placed
            validateStackSize(msg, 1, "mainInput", mainInput);
            if (researchKey == null) {
                researchKey = "";
            }
            if (instability < 0) {
                instability = 0;
            }
            validateName();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable InfusionRecipe register() {
            if (!validate()) return null;

            Object[] inputs = this.input.stream().map(IIngredient::toMcIngredient).toArray();
            InfusionRecipe recipe = new InfusionRecipe(researchKey, output.get(0), instability, aspects, mainInput.toMcIngredient(), inputs);
            ModSupport.THAUMCRAFT.get().infusionCrafting.add(super.name, recipe);
            return recipe;
        }
    }
}
