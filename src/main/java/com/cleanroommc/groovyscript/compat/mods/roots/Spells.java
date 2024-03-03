package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.core.mixin.roots.ModifierAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.api.Herb;
import epicsquid.roots.config.SpellConfig;
import epicsquid.roots.modifiers.*;
import epicsquid.roots.properties.Property;
import epicsquid.roots.properties.PropertyTable;
import epicsquid.roots.spell.SpellBase;
import epicsquid.roots.spell.SpellRegistry;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RegistryDescription(
        reloadability = RegistryDescription.Reloadability.FLAWED,
        isFullyDocumented = false // TODO fully document Roots Spells
)
public class Spells extends VirtualizedRegistry<SpellBase> {

    public static SpellWrapper spell(String spell) {
        return new SpellWrapper(spell);
    }

    public static SpellWrapper spell(ResourceLocation spell) {
        return new SpellWrapper(spell);
    }

    public static SpellWrapper spell(SpellBase spell) {
        return new SpellWrapper(spell);
    }

    @RecipeBuilderDescription(example = @Example(".spell(spell('spell_fey_light')).input(item('minecraft:clay'), item('minecraft:diamond'), item('minecraft:gold_ingot'))"))
    public static SpellWrapper.RecipeBuilder recipeBuilder() {
        return new SpellWrapper.RecipeBuilder();
    }

    @RecipeBuilderDescription(example = {
            @Example,
            @Example(".cost(cost('additional_cost'), herb('dewgonia'), 0.25)"),
            @Example(".cost(cost('additional_cost'), herb('spirit_herb'), 0.1).cost(cost('all_cost_multiplier'), null, -0.125)")
    })
    public static CostBuilder costBuilder() {
        return new CostBuilder();
    }

    @Override
    public void onReload() {
        // TODO Roots: I don't have a good idea for how to implement reloading here
    }

    public void disableAll() {
        SpellRegistry.getSpells().forEach(r -> r.setDisabled(true));
    }

    public static class SpellWrapper {

        private final SpellBase spell;

        public SpellWrapper(String name) {
            ResourceLocation rl;
            if (name.contains(":")) rl = new ResourceLocation(name);
            else if (name.startsWith("spell_")) rl = new ResourceLocation("roots", name);
            else rl = new ResourceLocation("roots", "spell_" + name);
            this.spell = SpellRegistry.getSpell(rl);
        }

        public SpellWrapper(ResourceLocation rl) {
            this.spell = SpellRegistry.getSpell(rl);
        }

        public SpellWrapper(SpellBase spell) {
            this.spell = spell;
        }

        public static RecipeBuilder recipe() {
            return new RecipeBuilder();
        }

        public SpellWrapper recipe(IIngredient... input) {
            if (spell == null) {
                GroovyLog.msg("Error modifying Roots Spell recipe").add("No spell specified when recipe change requested.").error().post();
            } else {
                new RecipeBuilder(spell).input(input).register();
            }
            return this;
        }

        public SpellWrapper recipe(Collection<IIngredient> input) {
            if (spell == null) {
                GroovyLog.msg("Error modifying Roots Spell recipe").add("No spell specified when recipe change requested.").error().post();
            } else {
                new RecipeBuilder(spell).input(input).register();
            }
            return this;
        }

        public SpellWrapper setDisabled() {
            this.spell.setDisabled(true);
            return this;
        }

        public SpellWrapper setEnabled() {
            this.spell.setDisabled(false);
            return this;
        }

        public SpellWrapper disableSound() {
            SpellConfig.SpellSoundsCategory.SpellSound sound = new SpellConfig.SpellSoundsCategory.SpellSound();
            sound.enabled = false;
            this.spell.setSound(sound);
            return this;
        }

        public SpellWrapper setSound(boolean enabled) {
            SpellConfig.SpellSoundsCategory.SpellSound sound = new SpellConfig.SpellSoundsCategory.SpellSound();
            sound.enabled = enabled;
            this.spell.setSound(sound);
            return this;
        }

        public SpellWrapper setSound(double volume) {
            SpellConfig.SpellSoundsCategory.SpellSound sound = new SpellConfig.SpellSoundsCategory.SpellSound();
            sound.volume = volume;
            this.spell.setSound(sound);
            return this;
        }

        public SpellWrapper setSound(boolean enabled, double volume) {
            SpellConfig.SpellSoundsCategory.SpellSound sound = new SpellConfig.SpellSoundsCategory.SpellSound();
            sound.enabled = enabled;
            sound.volume = volume;
            this.spell.setSound(sound);
            return this;
        }

        public <T> SpellWrapper set(String propertyName, T value) {
            PropertyTable table = this.spell.getProperties();
            Property<T> prop = table.get(propertyName);
            if (prop == null) GroovyLog.msg("Property {} was undefined for spell {}", propertyName, spell.getName()).error().post();
            else table.set(prop, value);
            return this;
        }

        public SpellWrapper setProperty(String propertyName, double value) {
            return this.set(propertyName, value);
        }

        public SpellWrapper setProperty(String propertyName, float value) {
            return this.set(propertyName, value);
        }

        public SpellWrapper setProperty(String propertyName, int value) {
            return this.set(propertyName, value);
        }

        public SpellWrapper setProperty(String propertyName, String value) {
            return this.set(propertyName, value);
        }

        public SpellWrapper setCooldown(int value) {
            return this.set("cooldown", value);
        }

        public SpellWrapper setDamage(float value) {
            return this.set("damage", value);
        }

        public SpellWrapper setCost(Herb herb, double value) {
            this.spell.getCosts().clear();
            this.spell.getCosts().put(herb, value);
            return this;
        }

        public SpellWrapper setSpellCost(Herb herb, double value) {
            return this.setCost(herb, value);
        }

        public SpellWrapper addCost(Herb herb, double value) {
            this.spell.getCosts().put(herb, value);
            return this;
        }

        public SpellWrapper addSpellCost(Herb herb, double value) {
            return this.addCost(herb, value);
        }

        public SpellWrapper clearCost() {
            this.spell.getCosts().clear();
            return this;
        }

        public SpellWrapper clearSpellCost() {
            return this.clearCost();
        }

        public SpellWrapper setModifierCost(Modifier mod, Map<CostType, IModifierCost> costs) {
            ((ModifierAccessor) mod).getCosts().clear();
            ((ModifierAccessor) mod).getCosts().putAll(costs);
            return this;
        }

        public SpellWrapper addModifierCost(Modifier mod, Map<CostType, IModifierCost> costs) {
            ((ModifierAccessor) mod).getCosts().putAll(costs);
            return this;
        }

        public SpellWrapper clearModifierCost(Modifier mod) {
            ((ModifierAccessor) mod).getCosts().clear();
            return this;
        }

        @com.cleanroommc.groovyscript.api.documentation.annotations.Property(property = "name")
        @com.cleanroommc.groovyscript.api.documentation.annotations.Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"),
                                                                                                          @Comp(type = Comp.Type.LTE, value = "5")})
        public static class RecipeBuilder extends AbstractRecipeBuilder<SpellBase.SpellRecipe> {

            @com.cleanroommc.groovyscript.api.documentation.annotations.Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
            private SpellBase spell;

            public RecipeBuilder() {
            }

            public RecipeBuilder(SpellBase spell) {
                this.spell = spell;
            }

            @RecipeBuilderMethodDescription
            public RecipeBuilder spell(SpellBase spell) {
                this.spell = spell;
                return this;
            }

            @Override
            public String getErrorMsg() {
                return "Error creating Roots Spell Recipe";
            }

            @Override
            public void validate(GroovyLog.Msg msg) {
                validateItems(msg, 1, 5, 0, 0);
                validateFluids(msg);
            }

            @Override
            @RecipeBuilderRegistrationMethod
            public @Nullable SpellBase.SpellRecipe register() {
                if (!validate()) return null;
                SpellBase.SpellRecipe recipe = new SpellBase.SpellRecipe(input.stream().map(IIngredient::toMcIngredient).toArray());
                this.spell.setRecipe(recipe);
                return recipe;
            }
        }
    }

    public static class CostBuilder extends AbstractRecipeBuilder<Map<CostType, IModifierCost>> {

        @com.cleanroommc.groovyscript.api.documentation.annotations.Property
        List<IModifierCost> list = new ArrayList<>();

        @RecipeBuilderMethodDescription(field = "list")
        public CostBuilder cost(CostType cost, double value, IModifierCore herb) {
            list.add(new epicsquid.roots.modifiers.Cost(cost, value, herb));
            return this;
        }

        @RecipeBuilderMethodDescription(field = "list")
        public CostBuilder cost(CostType cost, IModifierCore herb, double value) {
            return this.cost(cost, value, herb);
        }

        @RecipeBuilderMethodDescription(field = "list")
        public CostBuilder cost(CostType cost, Herb herb, double value) {
            return this.cost(cost, value, ModifierCores.fromHerb(herb));
        }

        @RecipeBuilderMethodDescription(field = "list")
        public CostBuilder cost(CostType cost, double value) {
            return this.cost(cost, value, null);
        }

        @RecipeBuilderMethodDescription(field = "list")
        public CostBuilder cost(CostType cost) {
            return this.cost(cost, 0.0, null);
        }

        @Override
        public String getErrorMsg() {
            return "Error creating Roots Spell Cost";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Map<CostType, IModifierCost> register() {
            if (!validate()) return null;
            if (list.isEmpty()) return epicsquid.roots.modifiers.Cost.noCost();
            return epicsquid.roots.modifiers.Cost.of(list.toArray(new IModifierCost[0]));
        }
    }
}