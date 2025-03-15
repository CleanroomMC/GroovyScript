package com.cleanroommc.groovyscript.compat.mods.betterwithaddons;

import betterwithaddons.tileentity.TileEntityLureTree;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.betterwithaddons.TileEntityLureTreeAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES, admonition = @Admonition(value = "groovyscript.wiki.betterwithaddons.lure_tree.note0", type = Admonition.Type.INFO))
public class LureTree extends StandardListRegistry<TileEntityLureTree.TreeFood> {

    private final AbstractReloadableStorage<Class<? extends Entity>> blacklistStorage = new AbstractReloadableStorage<>();

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        super.onReload();
        getBlacklist().removeAll(blacklistStorage.removeScripted());
        getBlacklist().addAll(blacklistStorage.restoreFromBackup());
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).food(1000)"),
            @Example(".input(item('minecraft:gold_ingot')).food(4)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<TileEntityLureTree.TreeFood> getRecipes() {
        return TileEntityLureTree.getTreeFoods();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public Collection<Class<? extends Entity>> getBlacklist() {
        return TileEntityLureTreeAccessor.getBlacklist();
    }

    @MethodDescription(example = @Example("item('minecraft:rotten_flesh')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> input.test(r.stack) && doAddBackup(r));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean addBlacklist(Class<? extends Entity> entity) {
        return getBlacklist().add(entity) && blacklistStorage.addScripted(entity);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("entity('minecraft:chicken')"))
    public boolean addBlacklist(EntityEntry entity) {
        return addBlacklist(entity.getEntityClass());
    }

    @MethodDescription
    public boolean removeBlacklist(Class<? extends Entity> entity) {
        return getBlacklist().removeIf(r -> entity.equals(r) && blacklistStorage.addBackup(r));
    }

    @MethodDescription
    public boolean removeBlacklist(EntityEntry entity) {
        return removeBlacklist(entity.getEntityClass());
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<TileEntityLureTree.TreeFood> {

        @Property(comp = @Comp(gte = 0))
        private int food;

        @RecipeBuilderMethodDescription
        public RecipeBuilder food(int food) {
            this.food = food;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Addons Lure Tree entry";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            msg.add(food < 0, "food must be greater than or equal to 0, yet it was {}", food);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable TileEntityLureTree.TreeFood register() {
            if (!validate()) return null;
            TileEntityLureTree.TreeFood recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = new TileEntityLureTree.TreeFood(stack, food);
                ModSupport.BETTER_WITH_ADDONS.get().lureTree.add(recipe);
            }
            return recipe;
        }
    }
}
