package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.base.WellLiquefaction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class Lightwell extends VirtualizedRegistry<WellLiquefaction.LiquefactionEntry> {

    public Lightwell() {
        super("Lightwell", "lightwell");
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(this::remove);
        restoreFromBackup().forEach(this::add);
    }

    public void add(WellLiquefaction.LiquefactionEntry recipe) {
        WellLiquefaction.registerLiquefaction(recipe.catalyst, recipe.producing, recipe.productionMultiplier, recipe.shatterMultiplier, recipe.catalystColor);
    }

    public void add(ItemStack catalyst, Fluid output, float productionMultiplier, float shatterMultiplier, @Nullable Color color) {
        WellLiquefaction.registerLiquefaction(catalyst, output, productionMultiplier, shatterMultiplier, color);
        addScripted(new WellLiquefaction.LiquefactionEntry(catalyst, output, productionMultiplier, shatterMultiplier, color));
    }

    public void remove(WellLiquefaction.LiquefactionEntry recipe) {
        WellLiquefaction.tryRemoveLiquefaction(recipe.catalyst, recipe.producing);
    }

    public void remove(ItemStack catalyst, Fluid fluid) {
        addBackup(WellLiquefaction.tryRemoveLiquefaction(catalyst, fluid));
    }

    public void removeByOutput(FluidStack fluid) {
        List<WellLiquefaction.LiquefactionEntry> list = WellLiquefaction.getRegisteredLiquefactions();
        list.forEach(le -> {
            if (le.producing.equals(fluid.getFluid()))
                addBackup(WellLiquefaction.tryRemoveLiquefaction(le.catalyst, fluid.getFluid()));
        });
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder {

        private ItemStack catalyst;
        private Fluid output;
        private float productionMultiplier;
        private float shatterMultiplier;
        private Color color = null;

        public RecipeBuilder catalyst(ItemStack catalyst) {
            this.catalyst = catalyst;
            return this;
        }

        public RecipeBuilder output(FluidStack output) {
            this.output = output.getFluid();
            return this;
        }

        public RecipeBuilder productionMultiplier(float productionMultiplier) {
            this.productionMultiplier = productionMultiplier;
            return this;
        }

        public RecipeBuilder shatterMultiplier(float shatterMultiplier) {
            this.shatterMultiplier = shatterMultiplier;
            return this;
        }

        public RecipeBuilder catalystColor(int rgb) {
            this.color = new Color(rgb);
            return this;
        }

        public void register() {
            ModSupport.ASTRAL_SORCERY.get().lightwell.add(catalyst, output, productionMultiplier, shatterMultiplier, color);
        }
    }
}
