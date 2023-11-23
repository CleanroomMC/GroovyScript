package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.FluidRarityEntryAccessor;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.FluidRarityRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.base.FluidRarityRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.ApiStatus;

@RegistryDescription
public class Fountain extends VirtualizedRegistry<FluidRarityRegistry.FluidRarityEntry> {

    @RecipeBuilderDescription(example = @Example(".fluid(fluid('astralsorcery.liquidstarlight')).rarity(10000000).minimumAmount(4000000).variance(1000000)"))
    public FountainChanceHelper chanceHelper() {
        return new FountainChanceHelper();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList()::remove);
        restoreFromBackup().forEach(((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList()::add);
    }

    public void afterScriptLoad() {
        // If the rarity list is empty, generating new chunks will cause a NPE. To prevent this, we add a "water" entry that will always have 0mb inside,
        // which causes it to be marked as empty, and thus not be interactable.
        if (((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList().isEmpty()) {
            FluidRarityRegistry.FluidRarityEntry errorBlocker = FluidRarityEntryAccessor.createFluidRarityEntry("water", 1, 0, 0);
            ((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList().add(errorBlocker);
            addScripted(errorBlocker);
        }
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.fountain.add0", type = MethodDescription.Type.ADDITION)
    public void add(FluidRarityRegistry.FluidRarityEntry entry) {
        addScripted(entry);
        ((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList().add(entry);
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.fountain.add1", type = MethodDescription.Type.ADDITION)
    public void add(FluidStack fluid, int rarity, int guaranteedAmt, int addRand) {
        this.add(fluid.getFluid(), rarity, guaranteedAmt, addRand);
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.fountain.add2", type = MethodDescription.Type.ADDITION)
    public void add(Fluid fluid, int rarity, int guaranteedAmt, int addRand) {
        this.add(FluidRarityEntryAccessor.createFluidRarityEntry(fluid, rarity, guaranteedAmt, addRand));
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.fountain.remove0")
    public boolean remove(FluidRarityRegistry.FluidRarityEntry entry) {
        addBackup(entry);
        return ((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList().removeIf(fluidRarityEntry -> fluidRarityEntry == entry);
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

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<FluidRarityRegistry.FluidRarityEntry> streamRecipes() {
        return new SimpleObjectStream<>(((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList())
                .setRemover(this::remove);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList().forEach(this::addBackup);
        ((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList().clear();
    }

    public static class FountainChanceHelper implements IRecipeBuilder<FluidRarityRegistry.FluidRarityEntry> {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private Fluid fluid = null;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int rarity = 0;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int minimumAmount = 0;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int variance = 0;

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

        @RecipeBuilderRegistrationMethod
        public FluidRarityRegistry.FluidRarityEntry register() {
            if (!validate()) return null;
            FluidRarityRegistry.FluidRarityEntry recipe = FluidRarityEntryAccessor.createFluidRarityEntry(fluid, rarity, minimumAmount, variance);
            ModSupport.ASTRAL_SORCERY.get().fountain.add(recipe);
            return recipe;
        }

    }

}
