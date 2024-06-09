package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.ingredients.input.ItemStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.OreDictionaryInputIngredient;

public class TechReborn extends ModPropertyContainer {

    public final AlloySmelter alloySmelter = new AlloySmelter();
    public final AssemblingMachine assemblingMachine = new AssemblingMachine();
    public final BlastFurnace blastFurnace = new BlastFurnace();
    public final Centrifuge centrifuge = new Centrifuge();
    public final ChemicalReactor chemicalReactor = new ChemicalReactor();
    public final Compressor compressor = new Compressor();
    public final DieselGenerator dieselGenerator = new DieselGenerator();
    public final DistillationTower distillationTower = new DistillationTower();
    public final Extractor extractor = new Extractor();
    public final FluidReplicator fluidReplicator = new FluidReplicator();
    public final FusionReactor fusionReactor = new FusionReactor();
    public final GasTurbine gasTurbine = new GasTurbine();
    public final Grinder grinder = new Grinder();
    public final ImplosionCompressor implosionCompressor = new ImplosionCompressor();
    public final IndustrialElectrolyzer industrialElectrolyzer = new IndustrialElectrolyzer();
    public final IndustrialGrinder industrialGrinder = new IndustrialGrinder();
    public final IndustrialSawmill industrialSawmill = new IndustrialSawmill();
    public final PlasmaGenerator plasmaGenerator = new PlasmaGenerator();
    public final PlateBendingMachine plateBendingMachine = new PlateBendingMachine();
    public final RollingMachine rollingMachine = new RollingMachine();
    public final Scrapbox scrapbox = new Scrapbox();
    public final SemiFluidGenerator semiFluidGenerator = new SemiFluidGenerator();
    public final SolidCanningMachine solidCanningMachine = new SolidCanningMachine();
    public final ThermalGenerator thermalGenerator = new ThermalGenerator();
    public final VacuumFreezer vacuumFreezer = new VacuumFreezer();
    public final WireMill wireMill = new WireMill();

    public TechReborn() {
        addRegistry(alloySmelter);
        addRegistry(assemblingMachine);
        addRegistry(blastFurnace);
        addRegistry(centrifuge);
        addRegistry(chemicalReactor);
        addRegistry(compressor);
        addRegistry(dieselGenerator);
        addRegistry(distillationTower);
        addRegistry(extractor);
        addRegistry(fluidReplicator);
        addRegistry(fusionReactor);
        addRegistry(gasTurbine);
        addRegistry(grinder);
        addRegistry(implosionCompressor);
        addRegistry(industrialElectrolyzer);
        addRegistry(industrialGrinder);
        addRegistry(industrialSawmill);
        addRegistry(plasmaGenerator);
        addRegistry(plateBendingMachine);
        addRegistry(rollingMachine);
        addRegistry(scrapbox);
        addRegistry(semiFluidGenerator);
        addRegistry(solidCanningMachine);
        addRegistry(thermalGenerator);
        addRegistry(vacuumFreezer);
        addRegistry(wireMill);
    }

    public static ItemStack getStackFromIIngredient(IIngredient ingredient) {
        if (ingredient instanceof OreDictIngredient oreDictIngredient) {
            if (OreDictionary.doesOreNameExist(oreDictIngredient.getOreDict())) {
                return OreDictionary.getOres(oreDictIngredient.getOreDict()).get(0).copy();
            }
        }
        if (IngredientHelper.isItem(ingredient)) {
            return IngredientHelper.toItemStack(ingredient);
        }
        if (ingredient.getMatchingStacks().length > 0) return ingredient.getMatchingStacks()[0];
        return ItemStack.EMPTY;
    }

    public static InputIngredient<?> toInputIngredient(IIngredient ingredient) {
        if (ingredient instanceof OreDictIngredient ore) return OreDictionaryInputIngredient.of(ore.getOreDict(), ore.getAmount());
        if (ingredient instanceof FluidStack fluid) return FluidStackInputIngredient.of(fluid);
        if (IngredientHelper.isItem(ingredient)) return ItemStackInputIngredient.of(IngredientHelper.toItemStack(ingredient));
        return ItemStackInputIngredient.of(ItemStack.EMPTY);
    }

}
