package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES, admonition = {
        @Admonition("groovyscript.wiki.jei.category.note0"),
        @Admonition(value = "groovyscript.wiki.jei.category.note1", type = Admonition.Type.TIP)
})
public class Category extends VirtualizedRegistry<String> {

    private boolean hideAllCategories;

    private final AbstractReloadableStorage<CustomCategory> categoryStorage = new AbstractReloadableStorage<>();
    private final List<String> categoryUidOrder = new ArrayList<>();

    /**
     * Called by {@link JeiPlugin#afterRuntimeAvailable()}
     */
    @GroovyBlacklist
    public void applyChanges(IRecipeRegistry recipeRegistry) {
        if (hideAllCategories) recipeRegistry.getRecipeCategories().stream().map(IRecipeCategory::getUid).forEach(this::addBackup);
        getBackupRecipes().forEach(recipeRegistry::hideRecipeCategory);
    }

    /**
     * Called by {@link JeiPlugin#getCategoryComparator()}
     */
    @GroovyBlacklist
    public List<String> getOrder() {
        return categoryUidOrder;
    }

    @MethodDescription
    public void add(CustomCategory customCategory) {
        categoryStorage.addScripted(customCategory);
    }

    @MethodDescription
    public CustomCategory add(String id,
                              Function<IGuiHelper, ? extends IRecipeCategory<? extends IRecipeWrapper>> category,
                              List<?> catalysts,
                              List<? extends IRecipeWrapper> wrappers) {
        return categoryBuilder()
                .id(id)
                .category(category)
                .catalyst(catalysts)
                .wrapper(wrappers)
                .register();
    }

    /**
     * Called by {@link JeiPlugin#registerCategories(IRecipeCategoryRegistration)}
     */
    @GroovyBlacklist
    public void addCustomRecipeCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        for (CustomCategory scriptedRecipe : categoryStorage.getScriptedRecipes()) {
            IRecipeCategory<?> category = scriptedRecipe.category.apply(guiHelper);
            registry.addRecipeCategories(category);
        }
    }

    /**
     * Called by {@link JeiPlugin#register(IModRegistry)}
     */
    @GroovyBlacklist
    public void applyCustomRecipeCategoryProperties(IModRegistry registry) {
        for (CustomCategory scriptedRecipe : categoryStorage.getScriptedRecipes()) {
            for (Object catalyst : scriptedRecipe.catalysts) {
                registry.addRecipeCatalyst(catalyst, scriptedRecipe.id);
            }
            registry.addRecipes(scriptedRecipe.wrappers, scriptedRecipe.id);
        }
    }

    @RecipeBuilderDescription(example = @Example(value = ".id(classes.GenericRecipeCategory.UID)/*()!*/.category(guiHelper -> new classes.GenericRecipeCategory(guiHelper)).catalyst(item('minecraft:clay')).wrapper(classes.GenericRecipeCategory.getRecipeWrappers())", commented = true, annotations = "groovyscript.wiki.jei.category.annotation"))
    public CategoryBuilder categoryBuilder() {
        return new CategoryBuilder();
    }

    @Override
    public void onReload() {
        restoreFromBackup();
        hideAllCategories = false;
        categoryStorage.removeScripted();
        categoryUidOrder.clear();
    }

    @MethodDescription(type = MethodDescription.Type.VALUE)
    public void setOrder(List<String> categoryUidOrder) {
        this.categoryUidOrder.addAll(categoryUidOrder);
    }

    @MethodDescription(type = MethodDescription.Type.VALUE, example = @Example("'minecraft.crafting', 'jei.information', 'minecraft.smelting', 'groovyscript:burning', 'groovyscript:explosion', 'groovyscript:fluid_recipe', 'groovyscript:piston_push', 'minecraft.anvil'"))
    public void setOrder(String... categoryUidOrder) {
        setOrder(Arrays.asList(categoryUidOrder));
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

    @SuppressWarnings("ClassCanBeRecord")
    public static final class CustomCategory {

        private final String id;
        private final Function<IGuiHelper, ? extends IRecipeCategory<? extends IRecipeWrapper>> category;
        private final List<?> catalysts;
        private final List<? extends IRecipeWrapper> wrappers;

        public CustomCategory(String id,
                              Function<IGuiHelper, ? extends IRecipeCategory<? extends IRecipeWrapper>> category,
                              List<?> catalysts,
                              List<? extends IRecipeWrapper> wrappers) {
            this.id = id;
            this.category = category;
            this.catalysts = catalysts;
            this.wrappers = wrappers;
        }

        public String id() {
            return id;
        }

        public Function<IGuiHelper, ? extends IRecipeCategory<? extends IRecipeWrapper>> category() {
            return category;
        }

        public List<?> catalysts() {
            return catalysts;
        }

        public List<? extends IRecipeWrapper> wrappers() {
            return wrappers;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (CustomCategory) obj;
            return Objects.equals(this.id, that.id) &&
                   Objects.equals(this.category, that.category) &&
                   Objects.equals(this.catalysts, that.catalysts) &&
                   Objects.equals(this.wrappers, that.wrappers);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, category, catalysts, wrappers);
        }

        @Override
        public String toString() {
            return "CustomCategory[" +
                   "id=" + id + ", " +
                   "category=" + category + ", " +
                   "catalysts=" + catalysts + ", " +
                   "wrappers=" + wrappers + ']';
        }
    }

    public static class CategoryBuilder implements IRecipeBuilder<CustomCategory> {

        @Property
        private final List<Object> catalyst = new ArrayList<>();
        @Property
        private final List<IRecipeWrapper> wrapper = new ArrayList<>();
        @Property(comp = @Comp(not = "empty"))
        private String id;
        @Property(comp = @Comp(not = "null"))
        private Function<IGuiHelper, ? extends IRecipeCategory<? extends IRecipeWrapper>> category;

        @RecipeBuilderMethodDescription
        public CategoryBuilder id(String id) {
            this.id = id;
            return this;
        }

        @RecipeBuilderMethodDescription
        public CategoryBuilder category(Function<IGuiHelper, ? extends IRecipeCategory<? extends IRecipeWrapper>> category) {
            this.category = category;
            return this;
        }

        @RecipeBuilderMethodDescription
        public CategoryBuilder catalyst(Object catalyst) {
            this.catalyst.add(catalyst);
            return this;
        }

        @RecipeBuilderMethodDescription
        public CategoryBuilder catalyst(Object... catalysts) {
            for (Object catalyst : catalysts) {
                catalyst(catalyst);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public CategoryBuilder catalyst(Collection<Object> catalysts) {
            for (Object catalyst : catalysts) {
                catalyst(catalyst);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public CategoryBuilder wrapper(IRecipeWrapper wrapper) {
            this.wrapper.add(wrapper);
            return this;
        }

        @RecipeBuilderMethodDescription
        public CategoryBuilder wrapper(IRecipeWrapper... wrappers) {
            for (IRecipeWrapper wrapper : wrappers) {
                wrapper(wrapper);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public CategoryBuilder wrapper(Collection<? extends IRecipeWrapper> wrappers) {
            for (IRecipeWrapper wrapper : wrappers) {
                wrapper(wrapper);
            }
            return this;
        }

        public String getErrorMsg() {
            return "Error creating a custom JEI Category";
        }

        public void validate(GroovyLog.Msg msg) {
            msg.add(wrapper.isEmpty(), "wrapper was empty");
            // you don't actually need any catalysts to make a category.
            msg.add(id.isEmpty(), "id was empty");
            msg.add(category == null, "category was null");
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg(getErrorMsg()).error();
            validate(msg);
            return !msg.postIfNotEmpty();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CustomCategory register() {
            if (!validate()) return null;
            var customCategory = new CustomCategory(id, category, catalyst, wrapper);
            ModSupport.JEI.get().category.add(customCategory);
            return customCategory;
        }
    }

}
