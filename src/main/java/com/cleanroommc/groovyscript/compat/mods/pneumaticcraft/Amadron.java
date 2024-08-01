package com.cleanroommc.groovyscript.compat.mods.pneumaticcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.IJEIRemoval;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import me.desht.pneumaticcraft.common.recipes.AmadronOffer;
import me.desht.pneumaticcraft.common.recipes.AmadronOfferManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription(
        admonition = @Admonition(value = "groovyscript.wiki.pneumaticcraft.amadron.note0", type = Admonition.Type.WARNING)
)
public class Amadron extends VirtualizedRegistry<AmadronOffer> implements IJEIRemoval.Default {

    private final AbstractReloadableStorage<AmadronOffer> periodicStorage = new AbstractReloadableStorage<>();

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay') * 3).output(item('minecraft:gold_ingot'))"),
            @Example(".fluidInput(fluid('water') * 50).output(item('minecraft:clay') * 3)"),
            @Example(".fluidInput(fluid('water') * 50).fluidOutput(fluid('lava') * 10).periodic()")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        AmadronOfferManager.getInstance().getStaticOffers().removeAll(removeScripted());
        AmadronOfferManager.getInstance().getStaticOffers().addAll(restoreFromBackup());
        AmadronOfferManager.getInstance().getPeriodicOffers().removeAll(periodicStorage.removeScripted());
        AmadronOfferManager.getInstance().getPeriodicOffers().addAll(periodicStorage.restoreFromBackup());
    }

    @Override
    public void afterScriptLoad() {
        AmadronOfferManager.getInstance().shufflePeriodicOffers();
        AmadronOfferManager.getInstance().recompileOffers();
    }

    public void addStatic(AmadronOffer recipe) {
        AmadronOfferManager.getInstance().getStaticOffers().add(recipe);
        addScripted(recipe);
    }

    public void addPeriodic(AmadronOffer recipe) {
        // PeriodicOffers is a List, but it really ought to be a HashSet to properly work with AmadronOfferManager#shufflePeriodicOffers(),
        // especially since AmadronOffer overrides the hashCode.
        if (AmadronOfferManager.getInstance().getPeriodicOffers().contains(recipe)) return;
        AmadronOfferManager.getInstance().getPeriodicOffers().add(recipe);
        periodicStorage.addScripted(recipe);
    }

    public boolean removeStatic(AmadronOffer recipe) {
        addBackup(recipe);
        return AmadronOfferManager.getInstance().getStaticOffers().remove(recipe);
    }

    public boolean removePeriodic(AmadronOffer recipe) {
        periodicStorage.addBackup(recipe);
        return AmadronOfferManager.getInstance().getPeriodicOffers().remove(recipe);
    }

    public boolean remove(AmadronOffer recipe) {
        if (AmadronOfferManager.getInstance().getStaticOffers().contains(recipe)) return removeStatic(recipe);
        if (AmadronOfferManager.getInstance().getPeriodicOffers().contains(recipe)) return removePeriodic(recipe);
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:emerald')"))
    public boolean removeByOutput(IIngredient output) {
        return AmadronOfferManager.getInstance().getStaticOffers().removeIf(entry -> {
            if (entry.getOutput() instanceof FluidStack fluid && output.test(fluid) || entry.getOutput() instanceof ItemStack item && output.test(item)) {
                addBackup(entry);
                return true;
            }
            return false;
        }) | AmadronOfferManager.getInstance().getPeriodicOffers().removeIf(entry -> {
            if (entry.getOutput() instanceof FluidStack fluid && output.test(fluid) || entry.getOutput() instanceof ItemStack item && output.test(item)) {
                periodicStorage.addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:rotten_flesh')"))
    public boolean removeByInput(IIngredient input) {
        return AmadronOfferManager.getInstance().getStaticOffers().removeIf(entry -> {
            if (entry.getInput() instanceof FluidStack fluid && input.test(fluid) || entry.getInput() instanceof ItemStack item && input.test(item)) {
                addBackup(entry);
                return true;
            }
            return false;
        }) | AmadronOfferManager.getInstance().getPeriodicOffers().removeIf(entry -> {
            if (entry.getInput() instanceof FluidStack fluid && input.test(fluid) || entry.getInput() instanceof ItemStack item && input.test(item)) {
                periodicStorage.addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllStatic() {
        AmadronOfferManager.getInstance().getStaticOffers().forEach(this::addBackup);
        AmadronOfferManager.getInstance().getStaticOffers().clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllPeriodic() {
        AmadronOfferManager.getInstance().getPeriodicOffers().forEach(periodicStorage::addBackup);
        AmadronOfferManager.getInstance().getPeriodicOffers().clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        removeAllStatic();
        removeAllPeriodic();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<AmadronOffer> streamRecipes() {
        List<AmadronOffer> list = new ArrayList<>();
        list.addAll(AmadronOfferManager.getInstance().getStaticOffers());
        list.addAll(AmadronOfferManager.getInstance().getPeriodicOffers());
        return new SimpleObjectStream<>(list).setRemover(this::removeStatic);
    }

    /**
     * @see me.desht.pneumaticcraft.common.thirdparty.jei.ModCategoryUid
     */
    @Override
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList("pneumaticcraft.amadron_trade");
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    @Property(property = "fluidInput", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    @Property(property = "fluidOutput", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<AmadronOffer> {

        @Property
        private boolean periodic;

        @RecipeBuilderMethodDescription
        public RecipeBuilder periodic() {
            this.periodic = !periodic;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder periodic(boolean periodic) {
            this.periodic = periodic;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding PneumaticCraft Amadron recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 1, 0, 1);
            validateFluids(msg, 0, 1, 0, 1);
            msg.add(input.isEmpty() && fluidInput.isEmpty(), "either input or fluidInput must contain an entry, but both were empty");
            msg.add(output.isEmpty() && fluidOutput.isEmpty(), "either output or fluidOutput must contain an entry, but both were empty");
        }

        private static void register(boolean periodic, AmadronOffer recipe) {
            if (periodic) ModSupport.PNEUMATIC_CRAFT.get().amadron.addPeriodic(recipe);
            else ModSupport.PNEUMATIC_CRAFT.get().amadron.addStatic(recipe);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable AmadronOffer register() {
            if (!validate()) return null;
            AmadronOffer recipe = null;
            Object o = fluidOutput.isEmpty() ? output.getOrEmpty(0) : fluidOutput.get(0);
            if (input.isEmpty()) {
                recipe = new AmadronOffer(fluidInput.getOrEmpty(0), o);
                register(periodic, recipe);
            } else {
                for (var stack : input.get(0).getMatchingStacks()) {
                    recipe = new AmadronOffer(stack, o);
                    register(periodic, recipe);
                }
            }
            return recipe;
        }
    }

}
