package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(value = PerkTree.class, remap = false)
public interface PerkTreeAccessor {

    @Accessor
    boolean getFrozen();

    @Accessor
    void setFrozen(boolean x);

    @Accessor
    Map<AbstractPerk, Collection<AbstractPerk>> getDoubleConnections();

    @Accessor
    List<Tuple<AbstractPerk, AbstractPerk>> getConnections();

}
