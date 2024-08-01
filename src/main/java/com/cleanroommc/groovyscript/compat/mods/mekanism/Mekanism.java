package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.api.infocommand.InfoParserRegistry;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.infuse.InfuseObject;
import mekanism.api.infuse.InfuseRegistry;
import mekanism.api.infuse.InfuseType;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.Nullable;

public class Mekanism extends GroovyPropertyContainer {

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

    // TODO The Rotary Condensentrator does not have compat currently. This should be added.


    @Optional.Method(modid = "mekanism")
    public static String asGroovyCode(Gas gasStack, boolean colored) {
        return GroovyScriptCodeConverter.formatGenericHandler("gas", gasStack.getName(), colored);
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
    public static String getSingleGasStack(GasStack gasStack, boolean colored) {
        return asGroovyCode(gasStack.getGas(), colored);
    }

    @Optional.Method(modid = "mekanism")
    public static String asGroovyCode(GasStack gasStack, boolean colored) {
        return getSingleGasStack(gasStack, colored) + GroovyScriptCodeConverter.formatMultiple(gasStack.amount, colored);
    }

    @Optional.Method(modid = "mekanism")
    public static String asGroovyCode(InfuseType infuseType, boolean colored) {
        return GroovyScriptCodeConverter.formatGenericHandler("infusionType", infuseType.unlocalizedName, colored);
    }

    @Optional.Method(modid = "mekanism")
    public static String asGroovyCode(InfuseObject infuseObject, boolean colored) {
        return asGroovyCode(infuseObject.type, colored) + GroovyScriptCodeConverter.formatMultiple(infuseObject.stored, colored);
    }

    @Override
    public void initialize(GroovyContainer<?> container) {
        container.objectMapperBuilder("gas", GasStack.class)
                .parser((s, args) -> {
                    Gas gas = GasRegistry.getGas(s);
                    return gas == null ? Result.error() : Result.some(new GasStack(gas, 1));
                })
                .completerOfNamed(GasRegistry::getRegisteredGasses, Gas::getName)
                .docOfType("gas stack")
                .register();
        container.objectMapperBuilder("infusionType", InfuseType.class)
                .parser(IObjectParser.wrapStringGetter(InfuseRegistry::get, true))
                .completerOfNames(InfuseRegistry.getInfuseMap()::keySet)
                .docOfType("infusion type")
                .register();

        InfoParserRegistry.addInfoParser(InfoParserGas.instance);
        InfoParserRegistry.addInfoParser(InfoParserInfusion.instance);
    }

}
