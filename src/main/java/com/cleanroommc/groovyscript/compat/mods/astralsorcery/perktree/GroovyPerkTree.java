package com.cleanroommc.groovyscript.compat.mods.astralsorcery.perktree;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.PerkTreeAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree.PERK_TREE;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES,
        isFullyDocumented = false // TODO fully document GroovyPerkTree
)
public class GroovyPerkTree extends VirtualizedRegistry<AbstractPerk> {

    private final HashMap<AbstractPerk, ArrayList<ResourceLocation>> scriptedConnections = new HashMap<>();
    private final HashMap<AbstractPerk, ArrayList<ResourceLocation>> removedConnections = new HashMap<>();
    private final HashMap<AbstractPerk, Point> movedPerks = new HashMap<>();

    public GroovyPerkTree() {
        super(Alias.generateOf("PerkTree"));
    }

    public static AttributeModifierPerkBuilder attributePerkBuilder() {
        return new AttributeModifierPerkBuilder();
    }

    public static AttributeModifierPerkBuilder.PerkModifierBuilder effectBuilder() {
        return new AttributeModifierPerkBuilder.PerkModifierBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(perk -> this.remove(perk, false));
        restoreFromBackup().forEach(perk -> this.add(perk, false));

        this.scriptedConnections.forEach((perk, resourceLocations) -> resourceLocations.forEach(location -> this.removeConnection(perk, this.getPerk(location), false)));
        this.removedConnections.forEach((perk, resourceLocations) -> resourceLocations.forEach(location -> this.addConnection(perk, this.getPerk(location), false)));

        this.movedPerks.forEach((perk, point) -> this.movePerk(perk, point.x, point.y, true));

        this.scriptedConnections.clear();
        this.removedConnections.clear();
        movedPerks.clear();
    }

    @MethodDescription(example = @Example("'astralsorcery:mec_inc_ms_2'"))
    public void remove(String perk) {
        this.remove(this.getPerk(perk), true);
    }

    void remove(AbstractPerk perk, boolean doBackup) {
        if (!PERK_TREE.getConnectedPerks(perk).isEmpty()) {
            ArrayList<AbstractPerk> connectedPerks = new ArrayList<>(PERK_TREE.getConnectedPerks(perk));
            connectedPerks.forEach(connectedPerk -> this.removeConnection(perk, connectedPerk, doBackup));
        }
        if (doBackup) this.addBackup(perk);

        ((PerkTreeAccessor) PERK_TREE).setFrozen(false);
        PERK_TREE.removePerk(perk);
        ((PerkTreeAccessor) PERK_TREE).setFrozen(true);
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public AbstractPerk getPerk(String perk) {
        return PERK_TREE.getPerk(new ResourceLocation(perk));
    }


    @MethodDescription(type = MethodDescription.Type.QUERY)
    public AbstractPerk getPerk(ResourceLocation perk) {
        return PERK_TREE.getPerk(perk);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("mods.astralsorcery.perktree.getPerk('astralsorcery:magnet_ats_reach'), 30, 30"))
    public void movePerk(AbstractPerk perk, int x, int y) {
        this.movePerk(perk, x, y, true);
    }

    private void movePerk(AbstractPerk perk, int x, int y, boolean doBackup) {
        if (doBackup) movedPerks.put(perk, new Point(perk.getOffset().x, perk.getOffset().y));
        perk.getOffset().x = x;
        perk.getOffset().y = y;
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public PerkTree.PointConnector add(AbstractPerk perk) {
        return this.add(perk, true);
    }

    private PerkTree.PointConnector add(AbstractPerk perk, boolean addScripted) {
        if (addScripted) this.addScripted(perk);
        ((PerkTreeAccessor) PERK_TREE).setFrozen(false);
        PerkTree.PointConnector pk = PERK_TREE.registerPerk(perk);
        ((PerkTreeAccessor) PERK_TREE).setFrozen(true);
        return pk;
    }

    public PerkTree.PointConnector add(AbstractPerk perk, ArrayList<ResourceLocation> connections) {
        return this.add(perk, connections, true);
    }

    private PerkTree.PointConnector add(AbstractPerk perk, ArrayList<ResourceLocation> connections, boolean addScripted) {
        ((PerkTreeAccessor) PERK_TREE).setFrozen(false);
        PerkTree.PointConnector tree = this.add(perk, addScripted);
        connections.forEach(connection -> this.addConnection(perk, this.getPerk(connection), addScripted));
        if (addScripted) {
            this.scriptedConnections.put(perk, connections);
        }
        ((PerkTreeAccessor) PERK_TREE).setFrozen(true);
        return tree;
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addConnection(String perk1, String perk2) {
        this.addConnection(this.getPerk(perk1), this.getPerk(perk2), true);
    }

    public void addConnection(AbstractPerk perk1, AbstractPerk perk2, boolean addScripted) {
        ((PerkTreeAccessor) PERK_TREE).setFrozen(false);
        PerkTree.PointConnector connector = PERK_TREE.tryGetConnector(perk1);
        if (connector == null) return;

        connector.connect(perk2);
        if (addScripted) {
            if (!this.removedConnections.containsKey(perk1)) {
                this.removedConnections.put(perk1, new ArrayList<>());
            }
            this.removedConnections.get(perk1).add(perk2.getRegistryName());
        }
        ((PerkTreeAccessor) PERK_TREE).setFrozen(true);
    }

    @MethodDescription
    public void removeConnection(String perk1, String perk2) {
        this.removeConnection(this.getPerk(perk1), this.getPerk(perk2), true);
    }

    public void removeConnection(AbstractPerk perk1, AbstractPerk perk2, boolean doBackup) {
        ((PerkTreeAccessor) PERK_TREE).setFrozen(false);
        PerkTree.PointConnector connector = PERK_TREE.tryGetConnector(perk1);
        if (connector != null && connector.disconnect(perk2) && doBackup) {
            if (!this.removedConnections.containsKey(perk1)) {
                this.removedConnections.put(perk1, new ArrayList<>());
            }
            this.removedConnections.get(perk1).add(perk2.getRegistryName());
        }
        ((PerkTreeAccessor) PERK_TREE).setFrozen(true);
    }
}
