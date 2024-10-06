package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import lykrast.prodigytech.common.recipe.ExplosionFurnaceManager;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class ExplosionFurnace extends StandardListRegistry<ExplosionFurnaceManager.ExplosionFurnaceRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(ore('ingotGold'), item('minecraft:diamond')).craftPerReagent(8).power(160).output(item('minecraft:emerald_block'))"),
            @Example(".input(item('minecraft:stone')).power(160).output(item('minecraft:glowstone'))")
    })
    public ExplosionFurnace.RecipeBuilder recipeBuilder() {
        return new ExplosionFurnace.RecipeBuilder();
    }

    @Override
    public Collection<ExplosionFurnaceManager.ExplosionFurnaceRecipe> getRecipes() {
        return ExplosionFurnaceManager.RECIPES;
    }

    public void addRecipe(ExplosionFurnaceManager.ExplosionFurnaceRecipe x) {
        add(x);
    }

    @MethodDescription(example = @Example("item('prodigytech:ferramic_ingot')"))
    public void removeByOutput(ItemStack output) {
        getRecipes().removeIf(r -> {
            if (!r.getOutput().isItemEqual(output)) return false;
            addBackup(r);
            return true;
        });
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 2))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ExplosionFurnaceManager.ExplosionFurnaceRecipe> {

        @Property(comp = @Comp(gte = 1))
        private int craftPerReagent = 1;

        @Property(comp = @Comp(gte = 1))
        private int power;

        @RecipeBuilderMethodDescription
        public ExplosionFurnace.RecipeBuilder craftPerReagent(int craftPerReagent) {
            this.craftPerReagent = craftPerReagent;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ExplosionFurnace.RecipeBuilder power(int power) {
            this.power = power;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding ProdigyTech Explosion Furnace Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 2, 1, 1);
            validateFluids(msg);
            msg.add(craftPerReagent <= 0, "craftPerReagent should be greater than 0!");
            msg.add(power <= 0, "power should be greater than 0!");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ExplosionFurnaceManager.ExplosionFurnaceRecipe register() {
            if (!validate()) return null;
            ExplosionFurnaceManager.ExplosionFurnaceRecipe recipe = null;
            IIngredient inputItem = input.get(0);
            // We do not do the OreDict check like in other places as it adds far too much code bloat
            if (input.size() == 1) {
                for (ItemStack it : inputItem.getMatchingStacks()) {
                    recipe = new ExplosionFurnaceManager.ExplosionFurnaceRecipe(it, output.get(0), power);
                    ModSupport.PRODIGY_TECH.get().explosionFurnace.addRecipe(recipe);
                }
            } else {
                for (ItemStack inp : inputItem.getMatchingStacks()) {
                    for (ItemStack rea : input.get(1).getMatchingStacks()) {
                        recipe = new ExplosionFurnaceManager.ExplosionFurnaceRecipe(inp, output.get(0), power, rea, craftPerReagent);
                        ModSupport.PRODIGY_TECH.get().explosionFurnace.addRecipe(recipe);
                    }
                }
            }

            return recipe;
        }

    }
}
