package com.cleanroommc.groovyscript.compat.mods.exnihilo;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import exnihilocreatio.registries.manager.ExNihiloRegistryManager;
import exnihilocreatio.registries.types.Siftable;
import exnihilocreatio.util.ItemInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Sieve extends VirtualizedRegistry<Pair<Ingredient, Siftable>> {

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(recipe -> ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().get(recipe.getKey()).removeIf(siftable -> recipe.getValue() == siftable));
        restoreFromBackup().forEach(recipe -> ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().computeIfAbsent(recipe.getKey(), key -> new ArrayList<>()).add(recipe.getValue()));
    }

    public void removeByInput(IIngredient input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Ex Nihilo sieve recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        ItemStack[] matchingInput = input.getMatchingStacks();
        boolean successful = ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().entrySet().removeIf(entry -> {
            for (ItemStack stack : matchingInput) {
                if (entry.getKey().test(stack)) {
                    for (Siftable siftable : entry.getValue()) {
                        addBackup(Pair.of(entry.getKey(), siftable));
                    }
                    return true;
                }
            }
            return false;
        });
        if (!successful) {
            GroovyLog.msg("Error removing Ex Nihilo sieve recipe")
                    .add(String.format("can't find recipe for %s", IngredientHelper.toItemStack(input).getDisplayName()))
                    .error()
                    .post();
        }
    }

    public void removeByInputAndOutput(IIngredient input, IIngredient output) {
        if (GroovyLog.msg("Error removing Ex Nihilo sieve recipe")
                .add(IngredientHelper.isEmpty(input), "input must not be empty")
                .add(IngredientHelper.isEmpty(output), "output must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }

        ItemStack[] matchingInput = input.getMatchingStacks();
        ItemStack[] matchingOutput = output.getMatchingStacks();
        AtomicBoolean successful = new AtomicBoolean();
        ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().entrySet().removeIf(entry -> {
            for (ItemStack stack : matchingInput) {
                if (entry.getKey().test(stack)) {
                    entry.getValue().removeIf(siftable -> {
                        for (ItemStack out : matchingOutput) {
                            if (siftable.getDrop().equals(out)) {
                                addBackup(Pair.of(entry.getKey(), siftable));
                                successful.set(true);
                                return true;
                            }
                        }
                        return false;
                    });
                }
            }
            return entry.getValue().isEmpty();
        });
        if (!successful.get()) {
            GroovyLog.msg("Error removing Ex Nihilo sieve recipe")
                    .add("couldn't find a recipe for input {} and output {}", input, output)
                    .error()
                    .post();
        }
    }

    public void removeAll() {
        ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().entrySet().removeIf(entry -> {
            for (Siftable siftable : entry.getValue()) {
                addBackup(Pair.of(entry.getKey(), siftable));
            }
            return true;
        });
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<Pair<Ingredient, Siftable>> {

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

        public RecipeBuilder meshLevel(int meshlevel) {
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
            this.chance = (float) Math.max(0.000001, this.chance);
        }

        @Nullable
        @Override
        public Pair<Ingredient, Siftable> register() {
            if (!validate()) return null;
            Pair<Ingredient, Siftable> recipe = Pair.of(this.input.toMcIngredient(), new Siftable(new ItemInfo(this.output), this.chance, this.meshlevel));
            ExNihiloRegistryManager.SIEVE_REGISTRY.getRegistry().computeIfAbsent(recipe.getKey(), key -> new ArrayList<>()).add(recipe.getValue());
            addScripted(recipe);
            return recipe;
        }
    }
}
