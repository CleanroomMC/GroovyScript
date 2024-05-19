package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.device;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.CoolantManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.github.bsideup.jabel.Desugar;
import mezz.jei.api.IGuiHelper;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES,
                     admonition = @Admonition(value = "groovyscript.wiki.thermalexpansion.coolant.note0", type = Admonition.Type.WARNING))
public class Coolant extends VirtualizedRegistry<Coolant.CoolantRecipe> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> {
            CoolantManagerAccessor.getCoolantMap().keySet().removeIf(r -> r.equals(recipe.fluid()));
            CoolantManagerAccessor.getCoolantFactorMap().keySet().removeIf(r -> r.equals(recipe.fluid()));
        });
        restoreFromBackup().forEach(r -> {
            CoolantManagerAccessor.getCoolantMap().put(r.fluid(), r.rf());
            CoolantManagerAccessor.getCoolantFactorMap().put(r.fluid(), r.factor());
        });
    }

    /**
     * This validation of rf is required because of {@link cofh.thermalexpansion.plugins.jei.device.coolant.CoolantWrapper#CoolantWrapper(IGuiHelper, FluidStack, int, int) CoolantWrapper}
     * specifically, if the code {@code energy / (TileDynamoCompression.basePower * 10)} returns 0, JEI cannot display it.
     */
    public void add(CoolantRecipe recipe) {
        if (recipe.rf < 4000) {
            GroovyLog.get().error("cannot create a coolant entry with less than 4000 rf, yet tried to create an entry for fluid {} producing {}", recipe.fluid, recipe.rf);
            return;
        }
        CoolantManagerAccessor.getCoolantMap().put(recipe.fluid(), recipe.rf());
        CoolantManagerAccessor.getCoolantFactorMap().put(recipe.fluid(), recipe.factor());
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("fluid('lava'), 4000, 30"))
    public void add(FluidStack fluid, int rf, int factor) {
        add(new CoolantRecipe(fluid.getFluid().getName(), rf, factor));
    }

    public boolean remove(String fluid) {
        if (CoolantManagerAccessor.getCoolantMap().containsKey(fluid) && CoolantManagerAccessor.getCoolantFactorMap().containsKey(fluid)) {
            addBackup(new CoolantRecipe(fluid, CoolantManagerAccessor.getCoolantMap().removeInt(fluid), CoolantManagerAccessor.getCoolantFactorMap().removeInt(fluid)));
            return true;
        }
        return false;
    }

    public boolean remove(CoolantRecipe recipe) {
        if (CoolantManagerAccessor.getCoolantMap().containsKey(recipe.fluid()) && CoolantManagerAccessor.getCoolantFactorMap().containsKey(recipe.fluid())) {
            addBackup(new CoolantRecipe(recipe.fluid(), CoolantManagerAccessor.getCoolantMap().removeInt(recipe.fluid()), CoolantManagerAccessor.getCoolantFactorMap().removeInt(recipe.fluid())));
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("fluid('cryotheum')"))
    public boolean remove(FluidStack input) {
        return remove(input.getFluid().getName());
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<String> streamRecipes() {
        return new SimpleObjectStream<>(CoolantManagerAccessor.getCoolantMap().keySet()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        CoolantManagerAccessor.getCoolantMap().keySet().stream().filter(CoolantManagerAccessor.getCoolantFactorMap()::containsKey)
                .forEach(x -> addBackup(new CoolantRecipe(x, CoolantManagerAccessor.getCoolantMap().getInt(x), CoolantManagerAccessor.getCoolantFactorMap().getInt(x))));
        CoolantManagerAccessor.getCoolantMap().clear();
        CoolantManagerAccessor.getCoolantFactorMap().clear();
    }

    @Desugar
    public record CoolantRecipe(String fluid, int rf, int factor) {

    }

}
