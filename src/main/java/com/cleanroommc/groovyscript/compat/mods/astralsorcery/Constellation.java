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
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Constellation extends VirtualizedRegistry<IConstellation> {

    private final HashMap<IConstellation, ConstellationMapEffectRegistry.MapEffect> constellationMapEffectsAdded = new HashMap<>();
    private final HashMap<IConstellation, ConstellationMapEffectRegistry.MapEffect> constellationMapEffectsRemoved = new HashMap<>();

    public Constellation() {
        super("Constellation", "constellation");
    }

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
        private final ArrayList<Tuple<Point,Point>> connections = new ArrayList<>();
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

        public ConstellationBuilder color(String color) {
            this.color = Color.decode(color);
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
            Point s1 = new Point(x1,y1);
            Point s2 = new Point(x2,y2);
            Tuple<Point, Point> line = new Tuple<>(s1,s2);
            Tuple<Point, Point> lineReverse = new Tuple<>(s2,s1);
            if (!connections.contains(line) && !connections.contains(lineReverse)) {
                connections.add(line);
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
            if (this.name == null || this.name.equals("")) {
                GroovyLog.msg("Error adding Astral Sorcery Constellation: ")
                        .add("name must be provided")
                        .error()
                        .post();
                return false;
            }
            if (this.connections.equals(new ArrayList<>())) {
                GroovyLog.msg("Error adding Astral Sorcery Constellation: ")
                        .add("connections must not be empty")
                        .error()
                        .post();
                return false;
            }
            if (this.type.equals(Type.MINOR) && this.phases.size() == 0) {
                GroovyLog.msg("Error adding Astral Sorcery Constellation: ")
                        .add("minor constellations require at least one moon phase")
                        .error()
                        .post();
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
                if (addedStars.containsKey(connection.getFirst())) s1 = addedStars.get(connection.getFirst());
                else {
                    s1 = constellation.addStar(connection.getFirst().x, connection.getFirst().y);
                    addedStars.put(connection.getFirst(), s1);
                }
                if (addedStars.containsKey(connection.getSecond())) s2 = addedStars.get(connection.getSecond());
                else {
                    s2 = constellation.addStar(connection.getSecond().x, connection.getSecond().y);
                    addedStars.put(connection.getSecond(), s2);
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
            if (constellation == null) return false;
            return true;
        }

        public void register() {
            if (!validate()) return;
            ModSupport.ASTRAL_SORCERY.get().constellation.addConstellationMapEffect(constellation, enchantmentEffect, potionEffect);
        }

    }
}
