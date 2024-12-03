package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.jei.IngredientInfoRecipeAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.plugins.jei.info.IngredientInfoRecipeCategory;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class Description extends VirtualizedRegistry<Pair<List<IIngredient>, List<String>>> {

    /**
     * Called by {@link JeiPlugin#register}
     */
    @GroovyBlacklist
    public void applyAdditions(IModRegistry modRegistry) {
        for (Pair<List<IIngredient>, List<String>> entry : this.getScriptedRecipes()) {
            modRegistry.addIngredientInfo(
                    entry.getLeft().stream().flatMap(x -> Stream.of(x.getMatchingStacks())).collect(Collectors.toList()),
                    // Currently, it is only possible to add VanillaTypes.ITEM. It may be desirable to add the ability to do other types.
                    VanillaTypes.ITEM,
                    entry.getRight().toArray(new String[0]));
        }
    }

    /**
     * Called by {@link JeiPlugin#afterRuntimeAvailable()}
     */
    @GroovyBlacklist
    public void applyRemovals(IRecipeRegistry recipeRegistry) {
        IngredientInfoRecipeCategory category = (IngredientInfoRecipeCategory) recipeRegistry.getRecipeCategory(VanillaRecipeCategoryUid.INFORMATION);
        if (category != null) {
            recipeRegistry.getRecipeWrappers(category).forEach(wrapper -> {
                IngredientInfoRecipeAccessor<?> accessor = (IngredientInfoRecipeAccessor<?>) wrapper;

                // Currently, it is only possible to remove VanillaTypes.ITEM. It may be desirable to add the ability to do other types.
                if (!VanillaTypes.ITEM.equals(accessor.getIngredientType())) return;

                for (Pair<List<IIngredient>, List<String>> entry : this.getBackupRecipes()) {
                    if (entry.getKey()
                            .stream()
                            .anyMatch(x -> accessor.getIngredients().stream().anyMatch(a -> a instanceof ItemStack itemStack && x.test(itemStack)))) {
                        recipeRegistry.hideRecipe(wrapper, VanillaRecipeCategoryUid.INFORMATION);
                    }
                }
            });
        }
    }

    @Override
    public void onReload() {
        restoreFromBackup();
        removeScripted();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(List<IIngredient> target, List<String> description) {
        addScripted(Pair.of(target, description));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(List<IIngredient> target, String... description) {
        addScripted(Pair.of(target, Arrays.asList(description)));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), ['wow', 'this', 'is', 'neat']"))
    public void add(IIngredient target, List<String> description) {
        addScripted(Pair.of(Collections.singletonList(target), description));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:gold_ingot'), 'groovyscript.recipe.fluid_recipe'"))
    public void add(IIngredient target, String... description) {
        addScripted(Pair.of(Collections.singletonList(target), Arrays.asList(description)));
    }

    @MethodDescription(example = @Example(value = "item('thaumcraft:triple_meat_treat')", commented = true))
    public void remove(List<IIngredient> target) {
        addBackup(Pair.of(target, null));
    }

    @MethodDescription
    public void remove(IIngredient... target) {
        addBackup(Pair.of(Arrays.asList(target), null));
    }
}
