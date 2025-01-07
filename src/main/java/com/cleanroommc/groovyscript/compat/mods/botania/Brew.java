package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
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

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES
)
public class Brew extends VirtualizedRegistry<vazkii.botania.api.brew.Brew> {

    @RecipeBuilderDescription(example = @Example(value = ".key('groovy_example_brew').name('Groovy Brew').color(0x00FFFF).cost(100).effect(new PotionEffect(potion('minecraft:strength'), 1800, 3), new PotionEffect(potion('minecraft:speed'), 1800, 2), new PotionEffect(potion('minecraft:weakness'), 3600, 1)).incense(true).bloodPendant(true)", imports = "net.minecraft.potion.PotionEffect"))
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

    @MethodDescription
    public boolean remove(String brew) {
        if (brew == null) return false;
        addBackup(BotaniaAPI.brewMap.get(brew));
        return BotaniaAPI.brewMap.remove(brew) != null;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BotaniaAPI.brewMap.forEach((l, r) -> this.addBackup(r));
        BotaniaAPI.brewMap.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<vazkii.botania.api.brew.Brew> streamBrews() {
        return new SimpleObjectStream<>(BotaniaAPI.brewMap.values());
    }

    public static class BrewBuilder extends AbstractRecipeBuilder<vazkii.botania.api.brew.Brew> {

        @Property(comp = @Comp(not = "null"), priority = 100)
        protected String key;
        @Property(ignoresInheritedMethods = true, priority = 200)
        protected String name;
        @Property(defaultValue = "0xFFFFFF", comp = @Comp(not = "null"))
        protected int color = 0xFFFFFF;
        @Property(comp = @Comp(gte = 1))
        protected int cost;
        @Property(defaultValue = "true", priority = 1100)
        protected boolean canInfuseIncense = true;
        @Property(defaultValue = "true", priority = 1200)
        protected boolean canInfuseBloodPendant = true;
        @Property(comp = @Comp(gte = 1))
        protected final List<PotionEffect> effects = new ArrayList<>();

        @RecipeBuilderMethodDescription
        public BrewBuilder key(String key) {
            this.key = key;
            return this;
        }

        @Override
        @RecipeBuilderMethodDescription
        public BrewBuilder name(String name) {
            this.name = name;
            return this;
        }

        @RecipeBuilderMethodDescription
        public BrewBuilder color(int color) {
            this.color = color;
            return this;
        }

        @RecipeBuilderMethodDescription
        public BrewBuilder cost(int cost) {
            this.cost = cost;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "cost")
        public BrewBuilder mana(int mana) {
            return cost(mana);
        }

        @RecipeBuilderMethodDescription(field = "canInfuseIncense")
        public BrewBuilder incense(boolean incense) {
            this.canInfuseIncense = incense;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "canInfuseIncense")
        public BrewBuilder incense() {
            this.canInfuseIncense = !canInfuseIncense;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "canInfuseBloodPendant")
        public BrewBuilder bloodPendant(boolean bloodPendant) {
            this.canInfuseBloodPendant = bloodPendant;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "canInfuseBloodPendant")
        public BrewBuilder bloodPendant() {
            this.canInfuseBloodPendant = !canInfuseBloodPendant;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "effects")
        public BrewBuilder effect(PotionEffect effect) {
            this.effects.add(effect);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "effects")
        public BrewBuilder effect(PotionEffect... effects) {
            for (PotionEffect effect : effects) {
                effect(effect);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "effects")
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
            validateItems(msg);
            validateFluids(msg);
            msg.add(key == null, "key must be defined");
            msg.add(BotaniaAPI.brewMap.containsKey(key), "must have a unique key for brew, got " + key);
            msg.add(cost < 1, "cost must be at least 1, got " + cost);
            msg.add(effects.size() < 1, "must have at least 1 potion effect, got " + effects.size());
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable vazkii.botania.api.brew.Brew register() {
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
