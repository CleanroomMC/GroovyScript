package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.api.infocommand.InfoParserRegistry;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.crystal.CrystalItemStackExpansion;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.perktree.GroovyPerkTree;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.perktree.PerkTreeConfig;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.starlightaltar.StarlightAltar;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.ConstellationRegistryAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class AstralSorcery extends GroovyPropertyContainer {

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

    public static String asGroovyCode(IConstellation constellation, boolean colored) {
        return GroovyScriptCodeConverter.formatGenericHandler("constellation", constellation.getSimpleName(), colored);
    }

    @Override
    public void initialize(GroovyContainer<?> container) {
        container.objectMapperBuilder("constellation", IConstellation.class)
                .parser((s, args) -> {
                    for (IConstellation constellation : ConstellationRegistryAccessor.getConstellationList()) {
                        if (constellation.getSimpleName().equalsIgnoreCase(s)) {
                            return Result.some(constellation);
                        }
                    }
                    return Result.error();
                })
                .completerOfNamed(ConstellationRegistryAccessor::getConstellationList, IConstellation::getSimpleName)
                .docOfType("constellation")
                .register();
        ExpansionHelper.mixinClass(ItemStack.class, CrystalItemStackExpansion.class);

        InfoParserRegistry.addInfoParser(InfoParserConstellation.instance);
    }

    public static ItemHandle toItemHandle(IIngredient ingredient) {
        if (ingredient instanceof FluidStack fluidStack) return new ItemHandle(fluidStack.getFluid());
        if (ingredient instanceof OreDictIngredient oreDictIngredient) return new ItemHandle(oreDictIngredient.getOreDict());
        return new ItemHandle(ingredient.getMatchingStacks());
    }
}
