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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.common.config.ConfigRecipes;

import java.util.*;
import java.util.stream.Collectors;

@RegistryDescription
public class Crucible extends VirtualizedRegistry<CrucibleRecipe> {

    @RecipeBuilderDescription(example = @Example(".researchKey('UNLOCKALCHEMY@3').catalyst(item('minecraft:rotten_flesh')).output(item('minecraft:gold_ingot')).aspect(aspect('metallum') * 10)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> ThaumcraftApi.getCraftingRecipes().values().remove(recipe));
        restoreFromBackup().forEach(recipe -> {
            if (!ThaumcraftApi.getCraftingRecipes().containsValue(recipe))
                ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(recipe.getRecipeOutput().toString()), recipe);
        });
        ConfigRecipes.compileGroups();
    }

    public void add(CrucibleRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(recipe.getRecipeOutput().getDisplayName()), recipe);
            ConfigRecipes.compileGroups();
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public CrucibleRecipe add(String researchKey, ItemStack result, IIngredient catalyst, AspectList tags) {
        return recipeBuilder().researchKey(researchKey).catalyst(catalyst).aspect(tags).output(result).register();
    }

    public boolean remove(CrucibleRecipe recipe) {
        Iterator<IThaumcraftRecipe> recipeIterator = ThaumcraftApi.getCraftingRecipes().values().iterator();

        Object r;
        do {
            if (!recipeIterator.hasNext()) {
                return false;
            }

            r = recipeIterator.next();
        } while (!(r instanceof CrucibleRecipe) || !((CrucibleRecipe) r).getRecipeOutput().isItemEqual(recipe.getRecipeOutput()));

        recipeIterator.remove();

        addBackup(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:gunpowder')"))
    public void removeByOutput(IIngredient output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Thaumcraft Crucible recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
        }
        Object r;
        List<CrucibleRecipe> recipes = new ArrayList<>();
        for (IThaumcraftRecipe iThaumcraftRecipe : ThaumcraftApi.getCraftingRecipes().values()) {
            r = iThaumcraftRecipe;
            if ((r instanceof CrucibleRecipe crucibleRecipe) && output.test(crucibleRecipe.getRecipeOutput())) {
                recipes.add(crucibleRecipe);
            }
        }
        if (recipes.isEmpty()) {
            GroovyLog.msg("Error removing Thaumcraft Crucible recipe")
                    .add("no recipes found for {}", output)
                    .error()
                    .post();
            return;
        }
        recipes.forEach(recipe -> {
            this.addBackup(recipe);
            ThaumcraftApi.getCraftingRecipes().values().remove(recipe);
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, IThaumcraftRecipe>> streamRecipes() {
        List<Map.Entry<ResourceLocation, IThaumcraftRecipe>> recipes = ThaumcraftApi.getCraftingRecipes().entrySet().stream().filter(x -> x.getValue() instanceof CrucibleRecipe).collect(Collectors.toList());
        return new SimpleObjectStream<>(recipes)
                .setRemover(x -> remove((CrucibleRecipe) x));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        List<Map.Entry<ResourceLocation, IThaumcraftRecipe>> recipes = ThaumcraftApi.getCraftingRecipes().entrySet().stream().filter(x -> x.getValue() instanceof CrucibleRecipe).collect(Collectors.toList());
        for (Map.Entry<ResourceLocation, IThaumcraftRecipe> recipe : recipes) {
            addBackup((CrucibleRecipe) recipe.getValue());
            ThaumcraftApi.getCraftingRecipes().remove(recipe.getKey(), recipe.getValue());
        }
    }

    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CrucibleRecipe> {

        @Property
        private String researchKey;
        @Property(comp = @Comp(gt = 0))
        private final AspectList aspects = new AspectList();
        @Property(comp = @Comp(not = "null"))
        private IIngredient catalyst;

        @RecipeBuilderMethodDescription
        public RecipeBuilder researchKey(String researchKey) {
            this.researchKey = researchKey;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "aspects")
        public RecipeBuilder aspect(AspectStack aspectIn) {
            this.aspects.add(aspectIn.getAspect(), aspectIn.getAmount());
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
        public RecipeBuilder catalyst(IIngredient catalyst) {
            this.catalyst = catalyst;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thaumcraft Crucible recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            msg.add(IngredientHelper.isEmpty(catalyst), () -> "Catalyst must not be empty");
            validateStackSize(msg, 1, "catalyst", catalyst);
            msg.add(aspects.size() == 0, () -> "Aspects must not be empty");
            if (researchKey == null) researchKey = "";
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CrucibleRecipe register() {
            if (!validate()) return null;
            CrucibleRecipe recipe = new CrucibleRecipe(researchKey, this.output.get(0), catalyst.toMcIngredient(), aspects);
            ModSupport.THAUMCRAFT.get().crucible.add(recipe);
            return recipe;
        }
    }
}
