package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.WellLiquefactionAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.base.WellLiquefaction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Map;

@RegistryDescription
public class Lightwell extends VirtualizedRegistry<WellLiquefaction.LiquefactionEntry> {

    private static Map<ItemStack, WellLiquefaction.LiquefactionEntry> getRegistry() {
        if (WellLiquefactionAccessor.getRegisteredLiquefactions() == null) {
            throw new IllegalStateException("Astral Sorcery Lightwell getRegisteredLiquefactions() is not yet initialized!");
        }
        return WellLiquefactionAccessor.getRegisteredLiquefactions();
    }

    @RecipeBuilderDescription(example = {
            @Example(".catalyst(item('minecraft:stone')).output(fluid('astralsorcery.liquidstarlight')).productionMultiplier(1.0F).shatterMultiplier(15.0F).catalystColor(16725260)"),
            @Example(".catalyst(item('minecraft:obsidian')).output(fluid('astralsorcery.liquidstarlight')).productionMultiplier(1.0F).shatterMultiplier(15.0F)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(r -> getRegistry().remove(r.catalyst));
        restoreFromBackup().forEach(r -> getRegistry().put(r.catalyst, r));
    }

    public void add(WellLiquefaction.LiquefactionEntry recipe) {
        getRegistry().put(recipe.catalyst, recipe);
        addScripted(recipe);
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.lightwell.add0", type = MethodDescription.Type.ADDITION)
    public void add(ItemStack catalyst, Fluid output, float productionMultiplier, float shatterMultiplier, @Nullable Color color) {
        add(new WellLiquefaction.LiquefactionEntry(catalyst, output, productionMultiplier, shatterMultiplier, color));
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.lightwell.add1", type = MethodDescription.Type.ADDITION)
    public void add(ItemStack catalyst, Fluid output, float productionMultiplier, float shatterMultiplier) {
        add(new WellLiquefaction.LiquefactionEntry(catalyst, output, productionMultiplier, shatterMultiplier, null));
    }

    public boolean remove(WellLiquefaction.LiquefactionEntry recipe) {
        addBackup(recipe);
        return getRegistry().remove(recipe.catalyst) != null;
    }

    @MethodDescription(example = @Example("item('minecraft:ice')"))
    public void removeByCatalyst(ItemStack catalyst) {
        WellLiquefaction.getRegisteredLiquefactions().forEach(le -> {
            if (le.catalyst.isItemEqual(catalyst)) {
                addBackup(getRegistry().remove(le.catalyst));
            }
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:packed_ice')"))
    public void removeByInput(ItemStack input) {
        removeByCatalyst(input);
    }

    @MethodDescription(example = @Example("fluid('lava')"))
    public void removeByOutput(FluidStack fluid) {
        getRegistry().entrySet().removeIf(entry -> {
            if (entry.getValue().producing.equals(fluid.getFluid())) {
                addBackup(entry.getValue());
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<WellLiquefaction.LiquefactionEntry> streamRecipes() {
        return new SimpleObjectStream<>(WellLiquefaction.getRegisteredLiquefactions())
                .setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        getRegistry().values().forEach(this::addBackup);
        getRegistry().clear();
    }

    public static class RecipeBuilder implements IRecipeBuilder<WellLiquefaction.LiquefactionEntry> {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private ItemStack catalyst = null;
        @Property(ignoresInheritedMethods = true, valid = @Comp(value = "null", type = Comp.Type.NOT))
        private Fluid output = null;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private float productionMultiplier = 0.0F;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private float shatterMultiplier = 0.0F;
        @Property
        private Color color = null;

        @RecipeBuilderMethodDescription
        public RecipeBuilder catalyst(ItemStack catalyst) {
            this.catalyst = catalyst;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder output(FluidStack output) {
            this.output = output.getFluid();
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder productionMultiplier(float productionMultiplier) {
            this.productionMultiplier = productionMultiplier;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder shatterMultiplier(float shatterMultiplier) {
            this.shatterMultiplier = shatterMultiplier;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "color")
        public RecipeBuilder catalystColor(Color color) {
            this.color = color;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "color")
        public RecipeBuilder catalystColor(int rgb) {
            this.color = new Color(rgb);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "color")
        public RecipeBuilder catalystColor(int r, int g, int b) {
            this.color = new Color(r, g, b);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "color")
        public RecipeBuilder catalystColor(int r, int g, int b, int a) {
            this.color = new Color(r, g, b, a);
            return this;
        }

        public boolean validate() {
            GroovyLog.Msg out = GroovyLog.msg("Error adding recipe to Astral Sorcery Lightwell");

            if (this.productionMultiplier < 0.0F) {
                out.add("Production multiplier may not be negative, defaulting to 0.").warn();
                this.productionMultiplier = 0.0F;
            }
            if (this.shatterMultiplier < 0.0F) {
                out.add("Shatter multiplier may not be negative, defaulting to 0.").warn();
                this.shatterMultiplier = 0.0F;
            }
            if (this.output == null) out.add("No output specified.").error();
            if (this.catalyst == null) out.add("No catalyst specified.").error();

            out.postIfNotEmpty();
            return out.getLevel() != Level.ERROR;
        }

        @RecipeBuilderRegistrationMethod
        public WellLiquefaction.LiquefactionEntry register() {
            if (!validate()) return null;
            WellLiquefaction.LiquefactionEntry recipe = new WellLiquefaction.LiquefactionEntry(catalyst, output, productionMultiplier, shatterMultiplier, color);
            ModSupport.ASTRAL_SORCERY.get().lightwell.add(recipe);
            return recipe;
        }
    }
}
