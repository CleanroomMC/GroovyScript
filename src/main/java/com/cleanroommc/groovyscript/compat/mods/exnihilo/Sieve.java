package com.cleanroommc.groovyscript.compat.mods.exnihilo;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.exnihilo.recipe.SieveRecipe;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import exnihilocreatio.registries.manager.ExNihiloRegistryManager;
import exnihilocreatio.registries.types.Siftable;
import exnihilocreatio.util.ItemInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

public class Sieve extends VirtualizedRegistry<SieveRecipe> {


    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(recipe -> {
                NonNullList<Siftable > backup = NonNullList.create();
                backup.addAll(ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().get(recipe.input));
                if (backup.size() > 1) {
                    backup.removeIf(siftable -> siftable.getDrop().getItemStack().isItemEqualIgnoreDurability(recipe.output)
                                                && siftable.getChance() == recipe.chance
                                                && siftable.getMeshLevel() == recipe.meshlevel);
                    ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().remove(recipe.input);
                    ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().put(recipe.input, backup);
                    return;
                }
                ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().remove(recipe.input);
        });
        restoreFromBackup().forEach(recipe -> {
            if (ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().get(recipe.input) != null) {
                NonNullList<Siftable> backup = NonNullList.create();
                backup.addAll(ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().get(recipe.input));
                backup.add(new Siftable(new ItemInfo(recipe.output), recipe.chance, recipe.meshlevel));
                ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().remove(recipe.input);
                ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().put(recipe.input, backup);
                return;
            }
            NonNullList<Siftable> siftable = NonNullList.create();
            siftable.add(new Siftable(new ItemInfo(recipe.output), recipe.chance, recipe.meshlevel));
        ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().put(recipe.input, siftable);
        });
    }

    public void removeByInput(IIngredient input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Ex Nihilo sieve recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        if (ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().get(input.toMcIngredient()) == null) {
            GroovyLog.msg("Error removing Ex Nihilo sieve recipe")
                    .add(String.format("can't find recipe for %s", IngredientHelper.toItemStack(input).getDisplayName()))
                    .error()
                    .post();
            return;
        }
        NonNullList<Siftable> backup = NonNullList.create();
        backup.addAll(ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().get(input.toMcIngredient()));
        backup.forEach(siftable -> addBackup(new SieveRecipe(input.toMcIngredient(), siftable.getDrop().getItemStack(), siftable.getChance(), siftable.getMeshLevel())));
        ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().remove(input.toMcIngredient());
    }

    public void removeByInputAndOutput(IIngredient input, ItemStack output) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Ex Nihilo sieve recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Ex Nihilo sieve recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        NonNullList<Siftable> backup = NonNullList.create();
        backup.addAll(ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().get(input.toMcIngredient()));
        if (backup.size() == 0) {
            GroovyLog.msg("Error removing Ex Nihilo sieve recipe")
                    .add(String.format("couldn't find a recipe for input %s and output %s", IngredientHelper.toItemStack(input).getDisplayName(), output.getDisplayName()))
                    .error()
                    .post();
            return;
        }
        backup.forEach(siftable -> {
            if (siftable.getDrop().getItemStack().isItemEqualIgnoreDurability(output)) {
                addBackup(new SieveRecipe(input.toMcIngredient(), output, siftable.getChance(), siftable.getMeshLevel()));
                backup.remove(siftable);
            }
        });
        ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().remove(input.toMcIngredient());
        ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().put(input.toMcIngredient(), backup);
    }

    public void removeAll() {
        ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().keySet().forEach(ingredient -> {
           NonNullList<Siftable> siftableList = NonNullList.create();
           siftableList.addAll(ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().get(ingredient));
            siftableList.forEach(siftable -> addBackup(new SieveRecipe(ingredient, siftable.getDrop().getItemStack(), siftable.getChance(), siftable.getMeshLevel())));
        });
        ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().clear();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<SieveRecipe> {

        private IIngredient input;
        private ItemStack output;
        private float chance;
        private int meshlevel;

        public RecipeBuilder input(IIngredient ingredient) {
            this.input = ingredient;
            return this;
        }

        public RecipeBuilder output(ItemStack item) {
            this.output = item;
            return this;
        }

        public RecipeBuilder chance(float chance) {
            this.chance = chance;
            return this;
        }

        public RecipeBuilder meshlevel(int meshlevel) {
            this.meshlevel = meshlevel;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding ExNihilo sieve recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(IngredientHelper.isEmpty(input), "Input must not be empty");
            msg.add(IngredientHelper.isEmpty(output), "Output must not be empty");
            msg.add(meshlevel > 4 || meshlevel < 1, "Meshlevel must be a Number between 1 (String) and 4 (Diamond)");
        }

        @Nullable
        @Override
        public SieveRecipe register() {
            if (!validate()) return null;
            SieveRecipe recipe = new SieveRecipe(input.toMcIngredient(), this.output, this.chance, this.meshlevel);
            if (ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().containsKey(recipe.input)) {
                NonNullList<Siftable> backup = NonNullList.create();
                backup.addAll(ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().get(recipe.input));
                backup.add(new Siftable(new ItemInfo(recipe.output), recipe.chance, recipe.meshlevel));
                ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().remove(recipe.input);
                ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().put(recipe.input, backup);
            } else {
                NonNullList<Siftable> siftableList = NonNullList.create();
                siftableList.add(new Siftable(new ItemInfo(recipe.output), recipe.chance, recipe.meshlevel));
                ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().put(recipe.input, siftableList);
            }
            addScripted(recipe);
            return recipe;
        }
    }
}
