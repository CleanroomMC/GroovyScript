package com.cleanroommc.groovyscript.compat.mods.astralsorcery.perktree;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;

public class AttributeModifierPerkBuilder {

    ResourceLocation name;
    Point point = new Point();
    ArrayList<PerkModifierBuilder> modifiers = new ArrayList<>();
    ArrayList<ResourceLocation> connections = new ArrayList<>();


    public AttributeModifierPerkBuilder name(String name) {
        return this.name(new ResourceLocation(GroovyScript.ID, name));
    }

    public AttributeModifierPerkBuilder name(ResourceLocation name) {
        this.name = name;
        return this;
    }

    public AttributeModifierPerkBuilder point(int x, int y) {
        this.point.setLocation(x, y);
        return this;
    }

    public AttributeModifierPerkBuilder point(Point point) {
        this.point.setLocation(point);
        return this;
    }

    public AttributeModifierPerkBuilder modifier(PerkModifierBuilder modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public AttributeModifierPerkBuilder modifier(float modifier, PerkAttributeModifier.Mode mode, String type) {
        this.modifiers.add(new PerkModifierBuilder().modifier(modifier).mode(mode).type(type));
        return this;
    }

    public AttributeModifierPerkBuilder modifier(float modifier, int mode, String type) {
        this.modifiers.add(new PerkModifierBuilder().modifier(modifier).mode(mode).type(type));
        return this;
    }

    public AttributeModifierPerkBuilder connection(ResourceLocation connection) {
        this.connections.add(connection);
        return this;
    }

    public AttributeModifierPerkBuilder connection(String connection) {
        this.connections.add(new ResourceLocation(connection));
        return this;
    }

    public @Nullable PerkTree.PointConnector register() {
        AttributeModifierPerk perk = new AttributeModifierPerk(name, point.x, point.y);
        for (PerkModifierBuilder mod : modifiers) {
            perk.addModifier(mod.modifier, mod.mode, mod.type);
        }

        return ModSupport.ASTRAL_SORCERY.get().perkTree.add(perk, connections);
    }

    public static class PerkModifierBuilder {
        float modifier;
        PerkAttributeModifier.Mode mode;
        String type;

        public PerkModifierBuilder modifier(float modifier) {
            this.modifier = modifier;
            return this;
        }

        public PerkModifierBuilder mode(PerkAttributeModifier.Mode mode) {
            this.mode = mode;
            return this;
        }

        public PerkModifierBuilder mode(int mode) {
            switch (mode) {
                case 0:
                    this.mode = PerkAttributeModifier.Mode.ADDITION;
                    break;
                case 1:
                    this.mode = PerkAttributeModifier.Mode.ADDED_MULTIPLY;
                    break;
                case 2:
                    this.mode = PerkAttributeModifier.Mode.STACKING_MULTIPLY;
                    break;
            }
            return this;
        }

        public PerkModifierBuilder type(String type) {
            this.type = type;
            return this;
        }

        public PerkModifierBuilder addition() {
            this.mode = PerkAttributeModifier.Mode.ADDITION;
            return this;
        }

        public PerkModifierBuilder multiply() {
            this.mode = PerkAttributeModifier.Mode.ADDED_MULTIPLY;
            return this;
        }

        public PerkModifierBuilder multipyCompounding() {
            this.mode = PerkAttributeModifier.Mode.STACKING_MULTIPLY;
            return this;
        }
    }

}
