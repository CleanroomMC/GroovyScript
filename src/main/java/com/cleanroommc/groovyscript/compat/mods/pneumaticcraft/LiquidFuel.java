package com.cleanroommc.groovyscript.compat.mods.pneumaticcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import me.desht.pneumaticcraft.common.PneumaticCraftAPIHandler;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class LiquidFuel extends VirtualizedRegistry<Pair<String, Integer>> {

    @RecipeBuilderDescription(example = @Example(".fluidInput(fluid('water')).pressure(100_000_000)"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> PneumaticCraftAPIHandler.getInstance().liquidFuels.remove(recipe.getKey()));
        restoreFromBackup().forEach(recipe -> PneumaticCraftAPIHandler.getInstance().liquidFuels.put(recipe.getKey(), recipe.getValue()));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(String fluid, int pressure) {
        PneumaticCraftAPIHandler.getInstance().liquidFuels.put(fluid, pressure);
        addScripted(Pair.of(fluid, pressure));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(FluidStack fluid, int pressure) {
        add(fluid.getFluid().getName(), pressure);
    }

    @MethodDescription
    public boolean remove(String fluid) {
        Integer pressure = PneumaticCraftAPIHandler.getInstance().liquidFuels.get(fluid);
        if (pressure == null) return false;
        addBackup(Pair.of(fluid, pressure));
        return PneumaticCraftAPIHandler.getInstance().liquidFuels.remove(fluid, pressure);
    }

    @MethodDescription(example = @Example("fluid('lava')"))
    public boolean remove(FluidStack fluid) {
        return remove(fluid.getFluid().getName());
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        PneumaticCraftAPIHandler.getInstance().liquidFuels.forEach((key, value) -> addBackup(Pair.of(key, value)));
        PneumaticCraftAPIHandler.getInstance().liquidFuels.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<String, Integer>> streamRecipes() {
        return new SimpleObjectStream<>(PneumaticCraftAPIHandler.getInstance().liquidFuels.entrySet())
                .setRemover(x -> remove(x.getKey()));
    }

    @Property(property = "fluidInput", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<Pair<FluidStack, Integer>> {

        @Property
        private int pressure;

        @RecipeBuilderMethodDescription
        public RecipeBuilder pressure(int pressure) {
            this.pressure = pressure;
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
            msg.add(pressure <= 0, "pressure must be greater than 0, yet it was {}", pressure);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Pair<FluidStack, Integer> register() {
            if (!validate()) return null;
            ModSupport.PNEUMATIC_CRAFT.get().liquidFuel.add(fluidInput.get(0), pressure);
            return Pair.of(fluidInput.get(0), pressure);
        }
    }

}
