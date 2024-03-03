package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.core.mixin.roots.RitualBaseAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.properties.Property;
import epicsquid.roots.properties.PropertyTable;
import epicsquid.roots.ritual.RitualBase;
import epicsquid.roots.ritual.RitualRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription(
        reloadability = RegistryDescription.Reloadability.FLAWED,
        isFullyDocumented = false // TODO fully document Roots Rituals
)
public class Rituals extends VirtualizedRegistry<RitualBase> {

    public static RitualWrapper ritual(String ritual) {
        return ritual(RitualRegistry.getRitual(ritual));
    }

    public static RitualWrapper ritual(RitualBase ritual) {
        return new RitualWrapper(ritual);
    }

    @RecipeBuilderDescription(example = @Example(".ritual(ritual('ritual_healing_aura')).input(item('minecraft:clay'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'))"))
    public static RitualWrapper.RecipeBuilder recipeBuilder() {
        return new RitualWrapper.RecipeBuilder();
    }

    @Override
    public void onReload() {
        // TODO Roots: I don't have a good idea for how to implement reloading here
    }

    public void disableAll() {
        RitualRegistry.getRituals().forEach(r -> {
            ((RitualBaseAccessor) r).setDisabled(true);
            r.setRecipe(RitualBase.RitualRecipe.EMPTY);
        });
    }

    public static class RitualWrapper {

        private final RitualBase ritual;

        public RitualWrapper(String name) {
            this.ritual = RitualRegistry.getRitual(name);
        }

        public RitualWrapper(RitualBase ritual) {
            this.ritual = ritual;
        }

        public RitualWrapper recipe(IIngredient... input) {
            if (ritual == null) {
                GroovyLog.msg("Error modifying Roots Ritual recipe").add("No ritual specified when recipe change requested.").error().post();
            } else {
                new RecipeBuilder(ritual).input(input).register();
            }
            return this;
        }

        public RitualWrapper recipe(Collection<IIngredient> input) {
            if (ritual == null) {
                GroovyLog.msg("Error modifying Roots Ritual recipe").add("No ritual specified when recipe change requested.").error().post();
            } else {
                new RecipeBuilder(ritual).input(input).register();
            }
            return this;
        }

        public RitualWrapper setDisabled() {
            ((RitualBaseAccessor) this.ritual).setDisabled(true);
            return this;
        }

        public RitualWrapper setEnabled() {
            ((RitualBaseAccessor) this.ritual).setDisabled(false);
            return this;
        }

        public <T> RitualWrapper set(String propertyName, T value) {
            PropertyTable table = this.ritual.getProperties();
            Property<T> prop = table.get(propertyName);
            if (prop == null) GroovyLog.msg("Property {} was undefined for ritual {}", propertyName, ritual).error().post();
            else table.set(prop, value);
            return this;
        }

        public RitualWrapper setProperty(String propertyName, double value) {
            return this.set(propertyName, value);
        }

        public RitualWrapper setProperty(String propertyName, float value) {
            return this.set(propertyName, value);
        }

        public RitualWrapper setProperty(String propertyName, int value) {
            return this.set(propertyName, value);
        }

        public RitualWrapper setProperty(String propertyName, String value) {
            return this.set(propertyName, value);
        }

        public RitualWrapper setDuration(int value) {
            return this.set("duration", value);
        }

        @com.cleanroommc.groovyscript.api.documentation.annotations.Property(property = "name")
        @com.cleanroommc.groovyscript.api.documentation.annotations.Property(property = "input", valid = @Comp("5"))
        public static class RecipeBuilder extends AbstractRecipeBuilder<RitualBase.RitualRecipe> {

            @com.cleanroommc.groovyscript.api.documentation.annotations.Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
            private RitualBase ritual;

            public RecipeBuilder() {
            }

            public RecipeBuilder(RitualBase ritual) {
                this.ritual = ritual;
            }

            @RecipeBuilderMethodDescription
            public RecipeBuilder ritual(RitualBase ritual) {
                this.ritual = ritual;
                return this;
            }

            @Override
            public String getErrorMsg() {
                return "Error creating Roots Ritual Recipe";
            }

            @Override
            public void validate(GroovyLog.Msg msg) {
                validateItems(msg, 5, 5, 0, 0);
                validateFluids(msg);
                msg.add(ritual == null, "ritual must be defined");
            }

            @Override
            @RecipeBuilderRegistrationMethod
            public @Nullable RitualBase.RitualRecipe register() {
                if (!validate()) return null;
                RitualBase.RitualRecipe recipe = new RitualBase.RitualRecipe(ritual, input.stream().map(IIngredient::toMcIngredient).toArray());
                this.ritual.setRecipe(recipe);
                return recipe;
            }
        }
    }
}
