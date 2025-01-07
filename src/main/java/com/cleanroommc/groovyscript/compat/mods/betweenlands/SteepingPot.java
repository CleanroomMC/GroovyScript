package com.cleanroommc.groovyscript.compat.mods.betweenlands;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IOreDicts;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thebetweenlands.SteepingPotRecipesAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.recipes.ISteepingPotRecipe;
import thebetweenlands.common.inventory.container.ContainerSilkBundle;
import thebetweenlands.common.recipe.misc.SteepingPotRecipes;
import thebetweenlands.common.registries.FluidRegistry;

import java.util.Collection;
import java.util.List;

/**
 * While {@link SteepingPotRecipes} does implement the interface {@link ISteepingPotRecipe},
 * the place that stores recipes can only be of the type {@link SteepingPotRecipes}.
 */
@RegistryDescription
public class SteepingPot extends StandardListRegistry<SteepingPotRecipes> {

    private final AbstractReloadableStorage<ItemStack> acceptedItemsStorage = new AbstractReloadableStorage<>();

    private static boolean doesIIngredientContainMatch(IIngredient target, Object[] match) {
        for (var o : match) {
            if (o instanceof String s) {
                return target instanceof IOreDicts ore && ore.getOreDicts().contains(s);
            }
            if (o instanceof ItemStack stack) {
                return target.test(stack);
            }
            return false;
        }
        return false;
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        super.onReload();
        var acceptedItems = getAcceptedItems();
        acceptedItems.removeAll(acceptedItemsStorage.removeScripted());
        acceptedItems.addAll(acceptedItemsStorage.restoreFromBackup());
    }

    /**
     * There is currently no support for item output recipes in JEI - they throw an NPE instead of appearing.
     * As such, this functionality and corresponding examples of the RecipeBuilder is commented out.
     */
    @RecipeBuilderDescription(example = {
//            @Example(".input(item('minecraft:clay')).fluidInput(fluid('water')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay')).fluidInput(fluid('lava')).fluidOutput(fluid('water'))"),
//            @Example(".input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:clay')).fluidInput(fluid('lava')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:diamond')).fluidInput(fluid('lava')).fluidOutput(fluid('dye_fluid')).meta(5)"),
            @Example(".input(item('minecraft:emerald')).fluidInput(fluid('lava')).fluidOutput(fluid('water'))"),
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<SteepingPotRecipes> getRecipes() {
        return SteepingPotRecipesAccessor.getRecipes();
    }

    public Collection<ItemStack> getAcceptedItems() {
        return ContainerSilkBundle.acceptedItems;
    }

    @MethodDescription(example = {
            @Example("item('thebetweenlands:items_crushed:13')"), @Example("fluid('clean_water')")
    })
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> (input.test(r.getInputFluidStack()) || doesIIngredientContainMatch(input, r.getInputs())) && doAddBackup(r));
    }

    @MethodDescription(example = {
            @Example(value = "item('thebetweenlands:limestone')", commented = true), @Example("fluid('dye_fluid').withNbt(['type': 14])")
    })
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> (output.test(r.getOutputItem()) || output.test(r.getOutputFluidStack())) && doAddBackup(r));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, priority = 500, example = @Example("item('minecraft:gold_block')"))
    public boolean addAcceptedItem(ItemStack entry) {
        return entry != null && getAcceptedItems().add(entry) && acceptedItemsStorage.addScripted(entry);
    }

    @MethodDescription(priority = 500, example = @Example("item('thebetweenlands:items_crushed:5')"))
    public boolean removeAcceptedItem(ItemStack entry) {
        return entry != null && getAcceptedItems().removeIf(r -> r == entry) && acceptedItemsStorage.addBackup(entry);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllAcceptedItem() {
        var acceptedItems = getAcceptedItems();
        acceptedItems.forEach(acceptedItemsStorage::addBackup);
        acceptedItems.clear();
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 4))
    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    @Property(property = "fluidOutput", comp = @Comp(eq = 1))
//    @Property(property = "output", comp = @Comp(gte = 0, lte = 1))
//    @Property(property = "fluidOutput", comp = @Comp(gte = 0, lte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<SteepingPotRecipes> {

        // Determines the color and state if relevant between two fluids.
        // except one is an enum with 16 values and the other has 11.
        // mods truly are amazing.
        @Property(comp = @Comp(gte = 0, lte = 15))
        private int meta;

        @RecipeBuilderMethodDescription
        public RecipeBuilder meta(int meta) {
            this.meta = meta;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Betweenlands Steeping Pot recipe";
        }

        @Override
        @GroovyBlacklist
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 4, 0, 0);
            validateFluids(msg, 1, 1, 1, 1);
//            validateItems(msg, 1, 4, 0, 1);
//            validateFluids(msg, 1, 1, 0, 1);
//            msg.add(output.isEmpty() && fluidOutput.isEmpty(), "either output or fluidOutput must contain an entry, but both were empty");
//            msg.add(!output.isEmpty() && !fluidOutput.isEmpty(), "either output or fluidOutput must have an entry, yet both had an entry");
            if (!fluidOutput.isEmpty()) {
                var fluid = fluidOutput.get(0).getFluid();
                if (fluid != FluidRegistry.DYE_FLUID && fluid != FluidRegistry.DRINKABLE_BREW) {
                    msg.add(meta != 0, "meta has no effect if the fluid is not of type {} or {}, and meta was {} for the fluidOutput {}", FluidRegistry.DYE_FLUID.getName(), FluidRegistry.DRINKABLE_BREW.getName(), meta, fluid.getName());
                } else {
                    msg.add((meta <= 0 || meta >= 15), "meta must be greater than or equal to 0 and less than or equal to 15 if fluidOutput is of type {} or {}, yet it was {}", FluidRegistry.DYE_FLUID.getName(), FluidRegistry.DRINKABLE_BREW.getName(), meta);
                }
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SteepingPotRecipes register() {
            if (!validate()) return null;

            for (var ingredient : input) {
                for (var stack : ingredient.getMatchingStacks()) {
                    ModSupport.BETWEENLANDS.get().steepingPot.addAcceptedItem(stack);
                }
            }

            SteepingPotRecipes recipe = null;
            List<List<Object>> inputs = IngredientHelper.cartesianProductOres(input);
            for (var objects : inputs) {
                recipe = new SteepingPotRecipes(fluidOutput.get(0), meta, fluidInput.get(0), objects.toArray());
                ModSupport.BETWEENLANDS.get().steepingPot.add(recipe);
            }

//            if (output.isEmpty()) {
//                for (var objects : inputs) {
//                    recipe = new SteepingPotRecipes(fluidOutput.get(0), meta, fluidInput.get(0), objects.toArray());
//                    ModSupport.BETWEENLANDS.get().steepingPot.add(recipe);
//                }
//            } else {
//                for (var objects : inputs) {
//                    recipe = new SteepingPotRecipes(output.get(0), fluidInput.get(0), objects.toArray());
//                    ModSupport.BETWEENLANDS.get().steepingPot.add(recipe);
//                }
//            }
            return recipe;
        }
    }
}
