package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Brew extends VirtualizedRegistry<vazkii.botania.api.brew.Brew> {

    public BrewBuilder brewBuilder() {
        return new BrewBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(brew -> BotaniaAPI.brewMap.remove(brew.getKey()));
        restoreFromBackup().forEach(brew -> BotaniaAPI.brewMap.put(brew.getKey(), brew));
    }

    public void add(vazkii.botania.api.brew.Brew brew) {
        if (brew == null) return;
        addScripted(brew);
        BotaniaAPI.brewMap.put(brew.getKey(), brew);
    }

    public boolean remove(vazkii.botania.api.brew.Brew brew) {
        if (brew == null) return false;
        addBackup(brew);
        return BotaniaAPI.brewMap.remove(brew.getKey()) != null;
    }

    public boolean remove(String brew) {
        if (brew == null) return false;
        addBackup(BotaniaAPI.brewMap.get(brew));
        return BotaniaAPI.brewMap.remove(brew) != null;
    }

    public void removeAll() {
        BotaniaAPI.brewMap.forEach((l, r) -> this.addBackup(r));
        BotaniaAPI.brewMap.clear();
    }

    public SimpleObjectStream<vazkii.botania.api.brew.Brew> streamBrews() {
        return new SimpleObjectStream<>(BotaniaAPI.brewMap.values());
    }

    public static class BrewBuilder extends AbstractRecipeBuilder<vazkii.botania.api.brew.Brew> {

        protected String key;
        protected String name;
        protected int color = 0xFFFFFF;
        protected int cost;
        protected boolean canInfuseIncense = true;
        protected boolean canInfuseBloodPendant = true;
        protected List<PotionEffect> effects = new ArrayList<>();

        public BrewBuilder key(String key) {
            this.key = key;
            return this;
        }

        public BrewBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BrewBuilder color(int color) {
            this.color = color;
            return this;
        }

        public BrewBuilder cost(int cost) {
            this.cost = cost;
            return this;
        }

        public BrewBuilder mana(int mana) {
            return cost(mana);
        }

        public BrewBuilder incense(boolean incense) {
            this.canInfuseIncense = incense;
            return this;
        }

        public BrewBuilder incense() {
            this.canInfuseIncense = !canInfuseIncense;
            return this;
        }

        public BrewBuilder bloodPendant(boolean bloodPendant) {
            this.canInfuseBloodPendant = bloodPendant;
            return this;
        }

        public BrewBuilder bloodPendant() {
            this.canInfuseBloodPendant = !canInfuseBloodPendant;
            return this;
        }

        public BrewBuilder effect(PotionEffect effect) {
            this.effects.add(effect);
            return this;
        }

        public BrewBuilder effect(PotionEffect... effects) {
            for (PotionEffect effect : effects) {
                effect(effect);
            }
            return this;
        }

        public BrewBuilder effect(Collection<PotionEffect> effects) {
            for (PotionEffect effect : effects) {
                effect(effect);
            }
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Botania Brew";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 0, 0);
            validateFluids(msg, 0, 0, 0, 0);
            msg.add(key == null, "key must be defined");
            msg.add(BotaniaAPI.brewMap.containsKey(key), "must have a unique key for brew, got " + key);
            msg.add(cost < 1, "cost must be at least 1, got " + cost);
            msg.add(effects.size() < 1, "must have at least 1 potion effect, got " + effects.size());
        }

        @Nullable
        @Override
        public vazkii.botania.api.brew.Brew register() {
            if (!validate()) return null;
            if (name == null) name = key;
            vazkii.botania.api.brew.Brew brew = new vazkii.botania.api.brew.Brew(key, name, color, cost, effects.toArray(new PotionEffect[0]));
            if (!canInfuseBloodPendant) brew.setNotBloodPendantInfusable();
            if (!canInfuseIncense) brew.setNotIncenseInfusable();
            ModSupport.BOTANIA.get().brew.add(brew);
            return brew;
        }

    }

}
