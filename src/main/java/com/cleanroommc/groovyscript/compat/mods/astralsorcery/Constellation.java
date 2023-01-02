package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.ConstellationMapEffectRegistryAccessor;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.ConstellationRegistryAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.constellation.*;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.constellation.starmap.ConstellationMapEffectRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Constellation extends VirtualizedRegistry<IConstellation> {

    private final HashMap<IConstellation, ConstellationMapEffectRegistry.MapEffect> constellationMapEffectsAdded = new HashMap<>();
    private final HashMap<IConstellation, ConstellationMapEffectRegistry.MapEffect> constellationMapEffectsRemoved = new HashMap<>();

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(c -> this.remove(c, false));
        restoreFromBackup().forEach(c -> this.add(c, false));

        this.constellationMapEffectsRemoved.forEach((constellation, effect) -> ConstellationMapEffectRegistry.registerMapEffect(constellation, effect.enchantmentEffects, effect.potionEffects));
        this.constellationMapEffectsAdded.forEach((constellation, effect) -> this.removeConstellationMapEffect(constellation, false));
        this.constellationMapEffectsAdded.clear();
        this.constellationMapEffectsRemoved.clear();
    }

    private void add(IConstellation constellation) {
        this.add(constellation, true);
    }

    private void add(IConstellation constellation, boolean addScripted) {
        if (addScripted) this.addScripted(constellation);
        ConstellationRegistry.registerConstellation(constellation);
    }

    private void remove(IConstellation constellation) {
        this.remove(constellation, true);
    }

    private void remove(IConstellation constellation, boolean doBackup) {
        if (ConstellationRegistryAccessor.getConstellationList() == null) return;
        if (doBackup) this.addBackup(constellation);
        this.removeConstellationMapEffect(constellation, doBackup);
        ConstellationRegistryAccessor.getConstellationList().removeIf(registeredConstellation -> constellation.getSimpleName().equals(registeredConstellation.getSimpleName()));
        if (constellation instanceof IMajorConstellation && ConstellationRegistryAccessor.getMajorConstellations() != null)
            ConstellationRegistryAccessor.getMajorConstellations().removeIf(registeredConstellation -> constellation.getSimpleName().equals(registeredConstellation.getSimpleName()));
        if (constellation instanceof IMinorConstellation && ConstellationRegistryAccessor.getMinorConstellations() != null)
            ConstellationRegistryAccessor.getMinorConstellations().removeIf(registeredConstellation -> constellation.getSimpleName().equals(registeredConstellation.getSimpleName()));
        if (constellation instanceof IWeakConstellation && ConstellationRegistryAccessor.getWeakConstellations() != null)
            ConstellationRegistryAccessor.getWeakConstellations().removeIf(registeredConstellation -> constellation.getSimpleName().equals(registeredConstellation.getSimpleName()));
    }

    private void addConstellationMapEffect(IConstellation constellation, List<ConstellationMapEffectRegistry.EnchantmentMapEffect> enchantmentEffectList, List<ConstellationMapEffectRegistry.PotionMapEffect> potionEffectList) {
        this.constellationMapEffectsAdded.put(constellation, ConstellationMapEffectRegistry.registerMapEffect(constellation, enchantmentEffectList, potionEffectList));
    }

    public void removeConstellationMapEffect(IConstellation constellation) {
        this.removeConstellationMapEffect(constellation, true);
    }

    private void removeConstellationMapEffect(IConstellation constellation, boolean doBackup) {
        if (ConstellationMapEffectRegistryAccessor.getEffectRegistry() == null) return;
        if (ConstellationMapEffectRegistryAccessor.getEffectRegistry().containsKey(constellation)) {
            if (doBackup) this.constellationMapEffectsRemoved.put(constellation, ConstellationMapEffectRegistryAccessor.getEffectRegistry().get(constellation));
            ConstellationMapEffectRegistryAccessor.getEffectRegistry().remove(constellation);
        }
    }

    public ConstellationBuilder constellationBuilder() {
        return new ConstellationBuilder();
    }

    public ConstellationMapEffectBuilder constellationMapEffectBuilder() {
        return new ConstellationMapEffectBuilder();
    }

    public static class ConstellationBuilder {

        private String name;
        private Color color = null;
        private ConstellationBuilder.Type type;
        private final ArrayList<Point2PointConnection> connections = new ArrayList<>();
        private final ArrayList<MoonPhase> phases = new ArrayList<>();

        private enum Type {
            MAJOR,
            MINOR,
            WEAK
        }

        public ConstellationBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ConstellationBuilder color(int color) {
            this.color = new Color(color);
            return this;
        }

        public ConstellationBuilder major() {
            this.type = Type.MAJOR;
            return this;
        }

        public ConstellationBuilder minor() {
            this.type = Type.MINOR;
            return this;
        }

        public ConstellationBuilder weak() {
            this.type = Type.WEAK;
            return this;
        }

        public ConstellationBuilder connection(int x1, int y1, int x2, int y2) {
            Point2PointConnection connection = new Point2PointConnection(x1, y1, x2, y2);
            if (!connections.contains(connection)) {
                connections.add(connection);
            }
            return this;
        }

        public ConstellationBuilder phase(MoonPhase moonPhase) {
            this.phases.add(moonPhase);
            return this;
        }

        public ConstellationBuilder phase(MoonPhase... moonPhases) {
            this.phases.addAll(Arrays.asList(moonPhases));
            return this;
        }

        public ConstellationBuilder phase(Collection<MoonPhase> moonPhases) {
            this.phases.addAll(moonPhases);
            return this;
        }

        private boolean validate() {
            ArrayList<String> errors = new ArrayList<>();

            if (this.name == null || this.name.equals(""))
                errors.add("name must be provided");
            if (this.connections.equals(new ArrayList<>()))
                errors.add("connections must not be empty");
            if (this.type.equals(Type.MINOR) && this.phases.size() == 0)
                errors.add("minor constellations require at least one moon phase");

            if (!errors.isEmpty()) {
                GroovyLog.Msg errorOut = GroovyLog.msg("Error adding Astral Sorcery Constellation: ");
                errors.forEach(errorOut::add);
                errorOut.error().post();
                return false;
            }

            if (this.color == null) {
                switch (this.type) {
                    case MAJOR:
                        this.color = new Color(40, 67, 204);
                    case WEAK:
                        this.color = new Color(67, 44, 176);
                    case MINOR:
                        this.color = new Color(93, 25, 127);
                }
            }

            return true;
        }

        public void register() {
            if (!this.validate()) return;
            IConstellation constellation;
            if (type.equals(Type.MAJOR)) constellation = new ConstellationBase.Major(name, color);
            else if (type.equals(Type.MINOR)) constellation = new ConstellationBase.Minor(name, color, phases.toArray(new MoonPhase[0]));
            else if (type.equals(Type.WEAK)) constellation = new ConstellationBase.Weak(name, color);
            else return;
            HashMap<Point, StarLocation> addedStars = new HashMap<>();
            this.connections.forEach(connection -> {
                StarLocation s1, s2;
                if (addedStars.containsKey(connection.p1)) s1 = addedStars.get(connection.p1);
                else {
                    s1 = constellation.addStar(connection.p1.x, connection.p1.y);
                    addedStars.put(connection.p1, s1);
                }
                if (addedStars.containsKey(connection.p2)) s2 = addedStars.get(connection.p2);
                else {
                    s2 = constellation.addStar(connection.p2.x, connection.p2.y);
                    addedStars.put(connection.p2, s2);
                }
                constellation.addConnection(s1, s2);
            });

            ModSupport.ASTRAL_SORCERY.get().constellation.add(constellation);
        }
    }

    public static class ConstellationMapEffectBuilder {

        private IConstellation constellation = null;
        private final List<ConstellationMapEffectRegistry.EnchantmentMapEffect> enchantmentEffect = new ArrayList<>();
        private final List<ConstellationMapEffectRegistry.PotionMapEffect> potionEffect = new ArrayList<>();

        public ConstellationMapEffectBuilder constellation(IConstellation constellation) {
            this.constellation = constellation;
            return this;
        }

        public ConstellationMapEffectBuilder enchantmentEffect(Enchantment ench, int min, int max) {
            this.enchantmentEffect.add(new ConstellationMapEffectRegistry.EnchantmentMapEffect(ench, min, max));
            return this;
        }

        public ConstellationMapEffectBuilder potionEffect(Potion potion, int min, int max) {
            this.potionEffect.add(new ConstellationMapEffectRegistry.PotionMapEffect(potion, min, max));
            return this;
        }

        private boolean validate() {
            ArrayList<String> errors = new ArrayList<>();
            if (constellation == null) errors.add("No constellation provided.");
            if (enchantmentEffect.isEmpty() && potionEffect.isEmpty()) errors.add("Either enchantmentEffect or potionEffect must be provided, neither were found.");
            if (!errors.isEmpty()) {
                GroovyLog.Msg errorOut = GroovyLog.msg("Error adding Astral Sorcery Constellation Map Effect: ");
                errors.forEach(errorOut::add);
                errorOut.error().post();
                return false;
            }
            return true;
        }

        public void register() {
            if (!validate()) return;
            ModSupport.ASTRAL_SORCERY.get().constellation.addConstellationMapEffect(constellation, enchantmentEffect, potionEffect);
        }

    }

    public static class Point2PointConnection {

        public Point p1;
        public Point p2;

        public Point2PointConnection(Point p1, Point p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        public Point2PointConnection(int x1, int y1, int x2, int y2) {
            this.p1 = new Point(x1, y1);
            this.p2 = new Point(x2, y2);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point2PointConnection)) return false;
            if (o == this) return true;
            Point2PointConnection other = (Point2PointConnection) o;
            return ((p1.equals(other.p1) && p2.equals(other.p2)) || (p2.equals(other.p1) && p1.equals(other.p2)));
        }

    }
}
