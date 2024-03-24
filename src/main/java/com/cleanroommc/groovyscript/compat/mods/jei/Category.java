package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.IRecipeCategory;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES,
                     admonition = @Admonition("groovyscript.wiki.jei.category.note0"))
public class Category extends VirtualizedRegistry<String> {

    private boolean hideAllCategories;

    /**
     * Called by {@link JeiPlugin#onRuntimeAvailable}
     */
    @GroovyBlacklist
    public void applyChanges(IRecipeRegistry recipeRegistry) {
        if (hideAllCategories) recipeRegistry.getRecipeCategories().stream().map(IRecipeCategory::getUid).forEach(this::addBackup);
        getBackupRecipes().forEach(recipeRegistry::hideRecipeCategory);
    }

    @Override
    public void onReload() {
        restoreFromBackup();
        hideAllCategories = false;
    }

    @MethodDescription(description = "groovyscript.wiki.jei.category.hideCategory")
    public void remove(String category) {
        if (category == null || category.isEmpty()) {
            GroovyLog.msg("Error hiding category")
                    .add("category must not be empty")
                    .error()
                    .post();
            return;
        }
        addBackup(category);
    }

    @MethodDescription(example = @Example("'minecraft.fuel'"))
    public void hideCategory(String category) {
        remove(category);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void hideAll() {
        hideAllCategories = true;
    }

}
