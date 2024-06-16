package com.cleanroommc.groovyscript.compat.mods.ic2;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.ic2.classic.*;
import com.cleanroommc.groovyscript.compat.mods.ic2.exp.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.List;

public class IC2 extends GroovyPropertyContainer {

    public final boolean isExp;

    public final Macerator macerator;
    public final Compressor compressor;
    public final Extractor extractor;
    public final Scrapbox scrapbox;
    public final Centrifuge centrifuge = new Centrifuge(); // In IC2C Extras
    public final MetalFormer metalFormer = new MetalFormer(); // In IC2C Extras
    public final OreWasher oreWasher = new OreWasher(); // In IC2C Extras

    // Classic
    public Canner canner;
    public ClassicElectrolyzer classicElectrolyzer;
    public Sawmill sawmill;
    public LiquidFuelGenerator liquidFuelGenerator;
    public RareEarthExtractor rareEarthExtractor;

    // Experimental
    public FluidGenerator semiFluidGenerator;
    public Electrolyzer electrolyzer;
    public Fermenter fermenter;
    public BlastFurnace blastFurnace;
    public BlockCutter blockCutter;
    public FluidCanner fluidCanner;
    public SolidCanner solidCanner;
    public Recycler recycler;
    public LiquidHeatExchanger liquidHeatExchanger;
    public FluidHeater liquidFueledFirebox;

    public IC2() {
        isExp = isExp();

        extractor = isExp ? new Extractor() : new ClassicExtractor();
        macerator = isExp ? new Macerator() : new ClassicMacerator();
        compressor = isExp ? new Compressor() : new ClassicCompressor();
        scrapbox = isExp ? new Scrapbox() : new ClassicScrapbox();

        if (isExp) {
            semiFluidGenerator = new FluidGenerator();
            electrolyzer = new Electrolyzer();
            fermenter = new Fermenter();
            blastFurnace = new BlastFurnace();
            blockCutter = new BlockCutter();
            fluidCanner = new FluidCanner();
            solidCanner = new SolidCanner();
            recycler = new Recycler();
            liquidHeatExchanger = new LiquidHeatExchanger();
            liquidFueledFirebox = new FluidHeater();
        } else {
            canner = new Canner();
            classicElectrolyzer = new ClassicElectrolyzer();
            sawmill = new Sawmill();
            liquidFuelGenerator = new LiquidFuelGenerator();
            rareEarthExtractor = new RareEarthExtractor();
        }
    }

    public static boolean isExp() {
        for (ModContainer container : Loader.instance().getActiveModList()) {
            if ("ic2".equals(container.getModId())) {
                return container.getMetadata().version.contains("ex");
            }
        }

        return false;
    }

    public static boolean isNull(List<ItemStack> list) {
        if (list == null || list.size() <= 0) return true;
        for (ItemStack stack : list) {
            if (stack.isEmpty()) return true;
        }

        return false;
    }
}
