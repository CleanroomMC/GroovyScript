package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.FluidRarityEntryAccessor;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.FluidRarityRegistryAccessor;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import hellfirepvp.astralsorcery.common.base.FluidRarityRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Level;

import java.util.Collection;

@RegistryDescription
public class Fountain extends StandardListRegistry<FluidRarityRegistry.FluidRarityEntry> {

    @RecipeBuilderDescription(example = @Example(".fluid(fluid('astralsorcery.liquidstarlight')).rarity(10000000).minimumAmount(4000000).variance(1000000)"))
    public FountainChanceHelper chanceHelper() {
        return new FountainChanceHelper();
    }

    @Override
    public Collection<FluidRarityRegistry.FluidRarityEntry> getRecipes() {
        return ((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList();
    }

    @Override
    public void afterScriptLoad() {
        // If the rarity list is empty, generating new chunks will cause a NPE. To prevent this, we add a "water" entry that will always have 0mb inside,
        // which causes it to be marked as empty, and thus not be interactable.
        if (((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList().isEmpty()) {
            FluidRarityRegistry.FluidRarityEntry errorBlocker = FluidRarityEntryAccessor.createFluidRarityEntry("water", 1, 0, 0);
            ((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList().add(errorBlocker);
            addScripted(errorBlocker);
        }
    }

    @Override
    @MethodDescription(description = "groovyscript.wiki.astralsorcery.fountain.add0", type = MethodDescription.Type.ADDITION)
    public boolean add(FluidRarityRegistry.FluidRarityEntry entry) {
        return super.add(entry);
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.fountain.add1", type = MethodDescription.Type.ADDITION)
    public void add(FluidStack fluid, int rarity, int guaranteedAmt, int addRand) {
        this.add(fluid.getFluid(), rarity, guaranteedAmt, addRand);
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.fountain.add2", type = MethodDescription.Type.ADDITION)
    public void add(Fluid fluid, int rarity, int guaranteedAmt, int addRand) {
        this.add(FluidRarityEntryAccessor.createFluidRarityEntry(fluid, rarity, guaranteedAmt, addRand));
    }

    @Override
    @MethodDescription(description = "groovyscript.wiki.astralsorcery.fountain.remove0")
    public boolean remove(FluidRarityRegistry.FluidRarityEntry entry) {
        return super.remove(entry);
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.fountain.remove1",example = @Example("fluid('lava')"))
    public void remove(FluidStack entry) {
        this.remove(entry.getFluid());
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.fountain.remove2")
    public void remove(Fluid entry) {
        ((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList().removeIf(fluidRarityEntry -> {
            if (fluidRarityEntry.fluid != null && fluidRarityEntry.fluid.equals(entry)) {
                addBackup(fluidRarityEntry);
                return true;
            }
            return false;
        });
    }

    public static class FountainChanceHelper implements IRecipeBuilder<FluidRarityRegistry.FluidRarityEntry> {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private Fluid fluid;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int rarity;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int minimumAmount;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int variance;

        @RecipeBuilderMethodDescription
        public FountainChanceHelper fluid(Fluid fluid) {
            this.fluid = fluid;
            return this;
        }

        @RecipeBuilderMethodDescription
        public FountainChanceHelper fluid(FluidStack fluid) {
            return this.fluid(fluid.getFluid());
        }

        @RecipeBuilderMethodDescription
        public FountainChanceHelper rarity(int rarity) {
            this.rarity = rarity;
            return this;
        }

        @RecipeBuilderMethodDescription
        public FountainChanceHelper minimumAmount(int amount) {
            this.minimumAmount = amount;
            return this;
        }

        @RecipeBuilderMethodDescription
        public FountainChanceHelper variance(int variance) {
            this.variance = variance;
            return this;
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg out = GroovyLog.msg("Error adding fluid to Astral Sorcery Fountain").warn();

            if (this.fluid == null) {
                out.add("No fluid specified.").error();
            }
            if (this.rarity < 0) {
                out.add("Rarity cannot be negative, defaulting to 0.");
                this.rarity = 0;
            }
            if (this.minimumAmount < 0) {
                out.add("Minimum amount cannot be negative, defaulting to 0.");
                this.minimumAmount = 0;
            }
            if (this.variance < 0) {
                out.add("Variance cannot be negative, defaulting to 0.");
                this.variance = 0;
            }

            out.postIfNotEmpty();
            return out.getLevel() != Level.ERROR;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public FluidRarityRegistry.FluidRarityEntry register() {
            if (!validate()) return null;
            FluidRarityRegistry.FluidRarityEntry recipe = FluidRarityEntryAccessor.createFluidRarityEntry(fluid, rarity, minimumAmount, variance);
            ModSupport.ASTRAL_SORCERY.get().fountain.add(recipe);
            return recipe;
        }

    }

}
