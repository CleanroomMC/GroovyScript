package com.cleanroommc.groovyscript.compat.mods.lightningcraft;

import com.cleanroommc.groovyscript.GroovyScriptConfig;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import sblectric.lightningcraft.api.recipes.LightningInfusionRecipe;
import sblectric.lightningcraft.recipes.LightningInfusionRecipes;

import java.util.Collection;

@RegistryDescription
public class Infusion extends StandardListRegistry<LightningInfusionRecipe> {

    @Override
    public Collection<LightningInfusionRecipe> getRecipes() {
        return LightningInfusionRecipes.instance().getRecipeList();
    }

    @MethodDescription(example = @Example("item('minecraft:diamond')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @RecipeBuilderDescription(example = {
        @Example(".centerItem(item('minecraft:clay')).input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:iron_ingot'), item('minecraft:iron_ingot')).output(item('minecraft:nether_star')).le(500)"),
        @Example(".centerItem(item('minecraft:clay')).input(item('minecraft:gold_ingot'), item('minecraft:potion').withNbt(['Potion': 'minecraft:leaping'])).output(item('minecraft:diamond_block')).le(200)"),
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Property(property = "input", comp = @Comp(gte = 0, lte = 4))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<LightningInfusionRecipe> {

        @Property()
        private IIngredient centerItem = null;

        @Property(comp = @Comp(gte = 0), defaultValue = "-1")
        private int le = -1;

        @Property(defaultValue = "determined automatically based on the input items")
        private boolean nbtSensitive = false;

        private boolean nbtSensitiveChanged = false;

        @RecipeBuilderMethodDescription
        public RecipeBuilder le(int le) {
            this.le = le;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "le")
        public RecipeBuilder cost(int le) {
            this.le = le;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder nbtSensitive(boolean nbtSensitive) {
            this.nbtSensitive = nbtSensitive;
            nbtSensitiveChanged = true;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder centerItem(IIngredient centerItem) {
            this.centerItem = centerItem;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding LightningCraft Infusion Table recipe";
        }

        @Override
        protected int getMaxItemInput() {
            // recipes with more than 1 item in some slot don't get recognized
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 4, 1, 1);
            validateFluids(msg);
            msg.add(centerItem == null, "Center item must not be null");
            msg.add(centerItem != null && centerItem.getMatchingStacks().length == 0, "Center item must not have a matching item");
            msg.add(le < 0, "LE cost must be positive");
            for (IIngredient it : this.input) {
                msg.add(it == null || it.getMatchingStacks().length == 0, "All inputs must have a matching item");
            }
            if (GroovyScriptConfig.compat.checkInputStackCounts && centerItem != null) {
                msg.add(centerItem.getAmount() > 1, "Expected stack size of 1 for {}, got {}", centerItem, centerItem.getAmount());
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable LightningInfusionRecipe register() {
            if (!validate()) return null;

            ItemStack centerItem = this.centerItem.getMatchingStacks()[0];
            ItemStack[] inputs = input.stream().map(i -> i.getMatchingStacks()[0]).toArray(ItemStack[]::new);

            // check if any input items have NBT to enable NBT sensitive mode automatically
            if (!nbtSensitiveChanged) {
                if (centerItem.hasTagCompound()) {
                    nbtSensitive = true;
                } else {
                    for (ItemStack i : inputs) {
                        if (i.hasTagCompound()) {
                            nbtSensitive = true;
                            break;
                        }
                    }
                }
            }

            LightningInfusionRecipe recipe = new LightningInfusionRecipe(output.get(0), le, centerItem, (Object[]) inputs);
            if (nbtSensitive) recipe.setNBTSensitive();
            ModSupport.LIGHTNINGCRAFT.get().infusion.add(recipe);
            return recipe;
        }
    }

}
