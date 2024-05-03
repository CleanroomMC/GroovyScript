package com.cleanroommc.groovyscript.compat.mods.pneumaticcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import me.desht.pneumaticcraft.common.PneumaticCraftAPIHandler;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class XpFluid extends VirtualizedRegistry<Pair<Fluid, Integer>> {

    @RecipeBuilderDescription(example = @Example(".fluidInput(fluid('lava')).ratio(5)"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(recipe -> PneumaticCraftAPIHandler.getInstance().liquidXPs.remove(recipe.getKey()));
        restoreFromBackup().forEach(recipe -> PneumaticCraftAPIHandler.getInstance().liquidXPs.put(recipe.getKey(), recipe.getValue()));
    }

    @Override
    @GroovyBlacklist
    public void afterScriptLoad() {
        PneumaticCraftAPIHandler.getInstance().availableLiquidXPs.clear();
        PneumaticCraftAPIHandler.getInstance().availableLiquidXPs.addAll(PneumaticCraftAPIHandler.getInstance().liquidXPs.keySet());
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(Fluid fluid, int ratio) {
        PneumaticCraftAPIHandler.getInstance().liquidXPs.put(fluid, ratio);
        addScripted(Pair.of(fluid, ratio));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(FluidStack fluid, int ratio) {
        add(fluid.getFluid(), ratio);
    }

    @MethodDescription
    public boolean remove(Fluid fluid) {
        Integer ratio = PneumaticCraftAPIHandler.getInstance().liquidXPs.get(fluid);
        if (ratio == null) return false;
        addBackup(Pair.of(fluid, ratio));
        return PneumaticCraftAPIHandler.getInstance().liquidXPs.remove(fluid, ratio);
    }

    @MethodDescription(example = @Example(value = "fluid('xpjuice')", commented = true))
    public boolean remove(FluidStack fluid) {
        return remove(fluid.getFluid());
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        PneumaticCraftAPIHandler.getInstance().liquidXPs.forEach((key, value) -> addBackup(Pair.of(key, value)));
        PneumaticCraftAPIHandler.getInstance().liquidXPs.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<Fluid, Integer>> streamRecipes() {
        return new SimpleObjectStream<>(PneumaticCraftAPIHandler.getInstance().liquidXPs.entrySet())
                .setRemover(x -> remove(x.getKey()));
    }

    @Property(property = "fluidInput", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<Pair<FluidStack, Integer>> {

        @Property
        private int ratio;

        @RecipeBuilderMethodDescription
        public RecipeBuilder ratio(int ratio) {
            this.ratio = ratio;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding PneumaticCraft Liquid Fuel entry";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 0, 0);
            msg.add(ratio <= 0, "ratio must be greater than 0, yet it was {}", ratio);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Pair<FluidStack, Integer> register() {
            if (!validate()) return null;
            ModSupport.PNEUMATICRAFT.get().xpFluid.add(fluidInput.get(0), ratio);
            return Pair.of(fluidInput.get(0), ratio);
        }
    }

}
