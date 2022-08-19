package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.CustomEnchanterRecipe;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.enchanter.EnchanterRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Enchanter extends VirtualizedRegistry<EnchanterRecipe> {

    public Enchanter() {
        super("Enchanter", "enchanter");
    }

    public void add(EnchanterRecipe recipe) {
        MachineRecipeRegistry.instance.registerRecipe(recipe);
        addScripted(recipe);
    }

    public void add(Enchantment enchantment, IIngredient input) {
        recipeBuilder()
                .enchantment(enchantment)
                .input(input)
                .buildAndRegister();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void remove(Enchantment enchantment) {
        if (enchantment == null) {
            GroovyLog.LOG.error("Can't remove EnderIO Enchanter recipe for null enchantment!");
            return;
        }
        List<EnchanterRecipe> recipes = new ArrayList<>();
        for (IMachineRecipe recipe : MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.ENCHANTER).values()) {
            if (recipe instanceof EnchanterRecipe && enchantment == ((EnchanterRecipe) recipe).getEnchantment()) {
                recipes.add((EnchanterRecipe) recipe);
            }
        }
        if (recipes.isEmpty()) {
            GroovyLog.LOG.error("Can't find EnderIO Enchanter recipe for " + enchantment.getName() + " enchantment!");
        } else {
            for (EnchanterRecipe recipe : recipes) {
                MachineRecipeRegistry.instance.removeRecipe(recipe);
                addBackup(recipe);
            }
        }
    }

    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(MachineRecipeRegistry.instance::removeRecipe);
        restoreFromBackup().forEach(MachineRecipeRegistry.instance::registerRecipe);
    }

    public static class RecipeBuilder implements IRecipeBuilder<EnchanterRecipe> {

        private Enchantment enchantment;
        private IIngredient input;
        private int amount = 0;
        private double costMultiplier = 1;
        private IIngredient lapis = new OreDictIngredient("gemLapis");
        private IIngredient book = IngredientHelper.toIIngredient(new ItemStack(Items.WRITABLE_BOOK));

        public RecipeBuilder enchantment(Enchantment enchantment) {
            this.enchantment = enchantment;
            return this;
        }

        public RecipeBuilder input(IIngredient ingredient) {
            this.input = ingredient;
            return this;
        }

        public RecipeBuilder amountPerLevel(int amount) {
            this.amount = amount;
            return this;
        }

        public RecipeBuilder xpCostMultiplier(double costMultiplier) {
            this.costMultiplier = costMultiplier;
            return this;
        }

        public RecipeBuilder customBook(IIngredient book) {
            this.book = book;
            return this;
        }

        public RecipeBuilder customLapis(IIngredient lapis) {
            this.lapis = lapis;
            return this;
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = new GroovyLog.Msg("Error adding EnderIO Enchanter recipe").error();
            msg.add(enchantment == null, () -> "enchantment must not be null");
            msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
            msg.add(IngredientHelper.isEmpty(book), () -> "custom book must not be empty");
            msg.add(IngredientHelper.isEmpty(lapis), () -> "custom lapis must not be empty");
            if (amount <= 0 && input != null) amount = input.getAmount();

            if (msg.hasSubMessages()) {
                GroovyLog.LOG.log(msg);
                return false;
            }
            return true;
        }

        @Override
        public @Nullable EnchanterRecipe buildAndRegister() {
            if (!validate()) return null;
            EnchanterRecipe recipe = new CustomEnchanterRecipe(input, amount, enchantment, costMultiplier, lapis, book);
            ModSupport.ENDER_IO.get().enchanter.add(recipe);
            return recipe;
        }
    }
}
