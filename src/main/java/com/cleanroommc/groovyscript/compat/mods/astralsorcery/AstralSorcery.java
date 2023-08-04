package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.brackets.BracketHandlerManager;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.crystal.CrystalItemStackExpansion;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.perktree.GroovyPerkTree;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.perktree.PerkTreeConfig;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.starlightaltar.StarlightAltar;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.ConstellationRegistryAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class AstralSorcery extends ModPropertyContainer {

    public final StarlightAltar altar = new StarlightAltar();
    public final Lightwell lightwell = new Lightwell();
    public final InfusionAltar infusionAltar = new InfusionAltar();
    public final Grindstone grindstone = new Grindstone();
    public final LightTransmutation lightTransmutation = new LightTransmutation();
    public final ChaliceInteraction chaliceInteraction = new ChaliceInteraction();
    public final GroovyPerkTree perkTree = new GroovyPerkTree();
    public final Constellation constellation = new Constellation();
    public final Research research = new Research();
    public final Fountain fountain = new Fountain();
    public final OreChance mineralisRitualOreChance = OreChance.mineralisRitualRegistry();
    public final OreChance aevitasPerkOreChance = OreChance.aevitasPerkRegistry();
    public final OreChance trashPerkOreChance = OreChance.trashPerkRegistry();
    public final OreChance treasureShrineOreChance = OreChance.treasureShrineRegistry();
    public final PerkTreeConfig perkTreeConfig = new PerkTreeConfig();

    public AstralSorcery() {
        addRegistry(altar);
        addRegistry(lightwell);
        addRegistry(infusionAltar);
        addRegistry(grindstone);
        addRegistry(lightTransmutation);
        addRegistry(chaliceInteraction);
        addRegistry(perkTree);
        addRegistry(constellation);
        addRegistry(research);
        addRegistry(fountain);
        addRegistry(mineralisRitualOreChance);
        addRegistry(aevitasPerkOreChance);
        addRegistry(trashPerkOreChance);
        addRegistry(treasureShrineOreChance);
        addRegistry(perkTreeConfig);
    }

    @Override
    public void initialize() {
        BracketHandlerManager.registerBracketHandler("constellation", s -> {
            for (IConstellation constellation : ConstellationRegistryAccessor.getConstellationList()) {
                if (constellation.getSimpleName().equalsIgnoreCase(s)) {
                    return constellation;
                }
            }
            return null;
        });
        ExpansionHelper.mixinClass(ItemStack.class, CrystalItemStackExpansion.class);
    }

    public static ItemHandle toItemHandle(IIngredient ingredient) {
        if (ingredient instanceof FluidStack) return new ItemHandle(((FluidStack) ingredient).getFluid());
        if (ingredient instanceof OreDictIngredient) return new ItemHandle(((OreDictIngredient) ingredient).getOreDict());
        return new ItemHandle(ingredient.getMatchingStacks());
    }

}
