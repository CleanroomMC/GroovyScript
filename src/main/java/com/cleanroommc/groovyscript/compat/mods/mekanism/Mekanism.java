package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.IGameObjectHandler;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.gameobjects.GameObjectHandlerManager;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.infuse.InfuseRegistry;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.Nullable;

public class Mekanism extends ModPropertyContainer {

    public final Infusion infusion = new Infusion();

    public final ChemicalInfuser chemicalInfuser = new ChemicalInfuser();
    public final Combiner combiner = new Combiner();
    public final Crusher crusher = new Crusher();
    public final Crystallizer crystallizer = new Crystallizer();
    public final DissolutionChamber dissolutionChamber = new DissolutionChamber();
    public final ElectrolyticSeparator electrolyticSeparator = new ElectrolyticSeparator();
    public final EnrichmentChamber enrichmentChamber = new EnrichmentChamber();
    public final InjectionChamber injectionChamber = new InjectionChamber();
    public final MetallurgicInfuser metallurgicInfuser = new MetallurgicInfuser();
    public final OsmiumCompressor osmiumCompressor = new OsmiumCompressor();
    public final ChemicalOxidizer chemicalOxidizer = new ChemicalOxidizer();
    public final PressurizedReactionChamber pressurizedReactionChamber = new PressurizedReactionChamber();
    public final PurificationChamber purificationChamber = new PurificationChamber();
    public final Sawmill sawmill = new Sawmill();
    public final Smelting smelting = new Smelting();
    public final SolarNeutronActivator solarNeutronActivator = new SolarNeutronActivator();
    public final ThermalEvaporationPlant thermalEvaporationPlant = new ThermalEvaporationPlant();
    public final Washer washer = new Washer();

    public Mekanism() {
        addRegistry(infusion);

        addRegistry(chemicalInfuser);
        addRegistry(combiner);
        addRegistry(crusher);
        addRegistry(crystallizer);
        addRegistry(dissolutionChamber);
        addRegistry(electrolyticSeparator);
        addRegistry(enrichmentChamber);
        addRegistry(injectionChamber);
        addRegistry(metallurgicInfuser);
        addRegistry(osmiumCompressor);
        addRegistry(chemicalOxidizer);
        addRegistry(pressurizedReactionChamber);
        addRegistry(purificationChamber);
        addRegistry(sawmill);
        addRegistry(smelting);
        addRegistry(solarNeutronActivator);
        addRegistry(thermalEvaporationPlant);
        addRegistry(washer);
    }

    @Override
    public void initialize() {
        GameObjectHandlerManager.registerGameObjectHandler("mekanism", "gas", (s, args) -> {
            Gas gas = GasRegistry.getGas(s);
            return gas == null ? Result.error() : Result.some(new GasStack(gas, 1));
        });
        GameObjectHandlerManager.registerGameObjectHandler("mekanism", "infusion", IGameObjectHandler.wrapStringGetter(InfuseRegistry::get, true));
    }

    @Optional.Method(modid = "mekanism")
    public static boolean isGas(IIngredient ingredient) {
        return Loader.isModLoaded("mekanism") && ingredient instanceof GasStack;
    }

    @Optional.Method(modid = "mekanism")
    public static boolean isEmpty(@Nullable GasStack gasStack) {
        return gasStack == null || gasStack.getGas() == null || gasStack.amount <= 0;
    }

    @Optional.Method(modid = "mekanism")
    public static String asGroovyCode(Gas gasStack, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(TextFormatting.DARK_GREEN);
        builder.append("gas");
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append("('");
        if (colored) builder.append(TextFormatting.AQUA);
        builder.append(gasStack.getName());
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append("')");
        return builder.toString();
    }
}
