package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.base.WellLiquefaction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class Lightwell extends VirtualizedRegistry<WellLiquefaction.LiquefactionEntry> {

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

        private ItemStack catalyst = null;
        private Fluid output = null;
        private float productionMultiplier = 0.0F;
        private float shatterMultiplier = 0.0F;
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

        private boolean validate() {
            GroovyLog.Msg out = GroovyLog.msg("Error adding recipe to Astral Sorcery Lightwell");

            if (this.productionMultiplier < 0.0F) {
                out.add("Production multiplier may not be negative, defaulting to 0.").warn();
                this.productionMultiplier = 0.0F;
            }
            if (this.shatterMultiplier < 0.0F) {
                out.add("Shatter multiplier may not be negative, defaulting to 0.").warn();
                this.shatterMultiplier = 0.0F;
            }
            out.add(this.output == null, "No output specified.").error();
            out.add(this.catalyst == null, "No catalyst specified.").error();

            out.postIfNotEmpty();
            return out.getLevel() != Level.ERROR;
        }

        public void register() {
            if (!validate()) return;
            ModSupport.ASTRAL_SORCERY.get().lightwell.add(catalyst, output, productionMultiplier, shatterMultiplier, color);
        }
    }
}
