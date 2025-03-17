package com.cleanroommc.groovyscript.compat.mods.betterwithaddons;

import betterwithaddons.block.EriottoMod.BlockNettedScreen;
import betterwithaddons.crafting.manager.CraftingManagerSandNet;
import betterwithaddons.crafting.recipes.NetRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class SandNet extends StandardListRegistry<NetRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:gold_ingot')).sand(2)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay') * 4, item('minecraft:diamond'), item('minecraft:diamond') * 2).sand(5)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<NetRecipe> getRecipes() {
        return CraftingManagerSandNet.getInstance().getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:iron_ingot')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            for (var itemstack : r.getInput()) {
                if (input.test(itemstack)) {
                    return doAddBackup(r);
                }
            }
            return false;
        });
    }

    @MethodDescription(example = {
            @Example("item('minecraft:sand')"), @Example("item('betterwithaddons:iron_sand')")
    })
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> {
            for (var itemstack : r.getOutput()) {
                if (output.test(itemstack)) {
                    return doAddBackup(r);
                }
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<NetRecipe> {

        @Property(comp = @Comp(gte = 0, lte = 8))
        private int sand;

        @RecipeBuilderMethodDescription
        public RecipeBuilder sand(int sand) {
            this.sand = sand;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Addons Sand Net recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, Integer.MAX_VALUE);
            validateFluids(msg);
            msg.add(sand < 0 || sand > 8, "sand must be greater than or equal to 0 and less than or equal to 8, yet it was {}", sand);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable NetRecipe register() {
            if (!validate()) return null;
            NetRecipe recipe = new NetRecipe(BlockNettedScreen.SifterType.SAND, BetterWithAddons.FromIngredient.fromIIngredient(input.get(0)), sand, output.toArray(new ItemStack[0]));
            ModSupport.BETTER_WITH_ADDONS.get().sandNet.add(recipe);
            return recipe;
        }
    }
}
