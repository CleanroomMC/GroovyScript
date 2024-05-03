package com.cleanroommc.groovyscript.compat.mods.pneumaticcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import me.desht.pneumaticcraft.common.recipes.AmadronOffer;
import me.desht.pneumaticcraft.common.recipes.AmadronOfferManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Amadron extends VirtualizedRegistry<AmadronOffer> {

    // TODO
    //  compat for PeriodicOffers recipes
    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay') * 3).output(item('minecraft:gold_ingot'))"),
            @Example(".fluidInput(fluid('water') * 50).output(item('minecraft:clay') * 3)"),
            @Example(".fluidInput(fluid('water') * 50).fluidOutput(fluid('lava') * 10)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        AmadronOfferManager.getInstance().getStaticOffers().removeAll(removeScripted());
        AmadronOfferManager.getInstance().getStaticOffers().addAll(restoreFromBackup());
    }

    @Override
    public void afterScriptLoad() {
        AmadronOfferManager.getInstance().recompileOffers();
    }

    public void add(AmadronOffer recipe) {
        AmadronOfferManager.getInstance().getStaticOffers().add(recipe);
        addScripted(recipe);
    }

    public boolean remove(AmadronOffer recipe) {
        addBackup(recipe);
        return AmadronOfferManager.getInstance().getStaticOffers().remove(recipe);
    }

    @MethodDescription(example = @Example("item('minecraft:shears')"))
    public boolean removeByOutput(IIngredient output) {
        return AmadronOfferManager.getInstance().getStaticOffers().removeIf(entry -> {
            if (entry.getOutput() instanceof FluidStack fluid && output.test(fluid) || entry.getOutput() instanceof ItemStack item && output.test(item)) {
                addBackup(entry);
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
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        AmadronOfferManager.getInstance().getStaticOffers().forEach(this::addBackup);
        AmadronOfferManager.getInstance().getStaticOffers().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<AmadronOffer> streamRecipes() {
        return new SimpleObjectStream<>(AmadronOfferManager.getInstance().getStaticOffers()).setRemover(this::remove);
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    @Property(property = "fluidInput", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    @Property(property = "fluidOutput", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<AmadronOffer> {

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

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable AmadronOffer register() {
            if (!validate()) return null;
            Object i = input.isEmpty() ? fluidInput.getOrEmpty(0) : input.get(0);
            Object o = output.isEmpty() ? fluidOutput.getOrEmpty(0) : output.get(0);
            AmadronOffer recipe = new AmadronOffer(i, o);
            ModSupport.PNEUMATICRAFT.get().amadron.add(recipe);
            return recipe;
        }
    }

}
