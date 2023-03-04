package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.armory;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.MaterialRepairIngredient;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.traits.TraitFunctions;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import slimeknights.tconstruct.library.TinkerRegistry;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ArmorTraitBuilder {
    public boolean hidden = false;
    public int color = 0xFFFFFF;
    public int maxLevel = 0;
    public int countPerLevel = 0;
    public final String name;
    public String displayName;
    public String description;
    public TraitFunctions.OnUpdate onUpdate;
    public TraitFunctions.OnRepair onRepair;
    public TraitFunctions.CanApplyTogether canApplyTogether;
    public TraitFunctions.CanApplyTogetherEnchantment canApplyTogetherEnchantment;
    public TraitFunctions.ExtraInfo extraInfo;
    public ArmorTraitFunctions.OnArmorTick onArmorTick;
    public ArmorTraitFunctions.GetModifications getModifications;
    public ArmorTraitFunctions.OnItemPickup onItemPickup;
    public ArmorTraitFunctions.OnHeal onHeal;
    public ArmorTraitFunctions.OnHurt onHurt;
    public ArmorTraitFunctions.OnDamaged onDamaged;
    public ArmorTraitFunctions.OnKnockback onKnockback;
    public ArmorTraitFunctions.OnFalling onFalling;
    public ArmorTraitFunctions.OnJumping onJumping;
    public ArmorTraitFunctions.OnAbilityTick onAbilityTick;
    public ArmorTraitFunctions.OnArmorEquipped onArmorEquipped;
    public ArmorTraitFunctions.OnArmorRemoved onArmorRemoved;
    public ArmorTraitFunctions.OnArmorDamaged onArmorDamaged;
    public ArmorTraitFunctions.OnArmorHeal onArmorHeal;
    public ArmorTraitFunctions.GetAbilityLevel getAbilityLevel;
    protected final List<MaterialRepairIngredient> items = new ArrayList<>();

    public ArmorTraitBuilder(String name) {
        this.name = name;
    }

    public ArmorTraitBuilder addItem(IIngredient ingredient, int amountNeeded, int amountMatched) {
        this.items.add(new MaterialRepairIngredient(ingredient, amountNeeded, amountMatched));
        return this;
    }

    public ArmorTraitBuilder setColor(int color) {
        this.color = color;
        return this;
    }

    public ArmorTraitBuilder setColor(Color color) {
        return setColor(color.getRGB());
    }

    public ArmorTraitBuilder setColor(int r, int g, int b) {
        return setColor(new Color(r, g, b));
    }

    public ArmorTraitBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ArmorTraitBuilder setLocalizedName(String name) {
        return setDisplayName(name);
    }

    public ArmorTraitBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ArmorTraitBuilder setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public ArmorTraitBuilder setHidden() {
        return setHidden(!hidden);
    }

    public ArmorTraitBuilder setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public ArmorTraitBuilder setCountPerLevel(int count) {
        this.countPerLevel = count;
        return this;
    }

    public ArmorTraitBuilder onUpdate(TraitFunctions.OnUpdate func) {
        this.onUpdate = func;
        return this;
    }

    public ArmorTraitBuilder onRepair(TraitFunctions.OnRepair func) {
        this.onRepair = func;
        return this;
    }

    public ArmorTraitBuilder canApplyTogether(TraitFunctions.CanApplyTogether func) {
        this.canApplyTogether = func;
        return this;
    }

    public ArmorTraitBuilder canApplyTogetherEnchantment(TraitFunctions.CanApplyTogetherEnchantment func) {
        this.canApplyTogetherEnchantment = func;
        return this;
    }

    public ArmorTraitBuilder extraInfo(TraitFunctions.ExtraInfo func) {
        this.extraInfo = func;
        return this;
    }

    public ArmorTraitBuilder onArmorTick(ArmorTraitFunctions.OnArmorTick func) {
        this.onArmorTick = func;
        return this;
    }

    public ArmorTraitBuilder getModifications(ArmorTraitFunctions.GetModifications func) {
        this.getModifications = func;
        return this;
    }

    public ArmorTraitBuilder onItemPickup(ArmorTraitFunctions.OnItemPickup func) {
        this.onItemPickup = func;
        return this;
    }

    public ArmorTraitBuilder onHeal(ArmorTraitFunctions.OnHeal func) {
        this.onHeal = func;
        return this;
    }

    public ArmorTraitBuilder onHurt(ArmorTraitFunctions.OnHurt func) {
        this.onHurt = func;
        return this;
    }

    public ArmorTraitBuilder onDamaged(ArmorTraitFunctions.OnDamaged func) {
        this.onDamaged = func;
        return this;
    }

    public ArmorTraitBuilder onKnockback(ArmorTraitFunctions.OnKnockback func) {
        this.onKnockback = func;
        return this;
    }

    public ArmorTraitBuilder onFalling(ArmorTraitFunctions.OnFalling func) {
        this.onFalling = func;
        return this;
    }

    public ArmorTraitBuilder onJumping(ArmorTraitFunctions.OnJumping func) {
        this.onJumping = func;
        return this;
    }

    public ArmorTraitBuilder onAbilityTick(ArmorTraitFunctions.OnAbilityTick func) {
        this.onAbilityTick = func;
        return this;
    }

    public ArmorTraitBuilder onArmorEquipped(ArmorTraitFunctions.OnArmorEquipped func) {
        this.onArmorEquipped = func;
        return this;
    }

    public ArmorTraitBuilder onArmorRemoved(ArmorTraitFunctions.OnArmorRemoved func) {
        this.onArmorRemoved = func;
        return this;
    }

    public ArmorTraitBuilder onArmorDamaged(ArmorTraitFunctions.OnArmorDamaged func) {
        this.onArmorDamaged = func;
        return this;
    }

    public ArmorTraitBuilder onArmorHeal(ArmorTraitFunctions.OnArmorHeal func) {
        this.onArmorHeal = func;
        return this;
    }

    public ArmorTraitBuilder getAbilityLevel(ArmorTraitFunctions.GetAbilityLevel func) {
        this.getAbilityLevel = func;
        return this;
    }

    public GroovyArmorTrait register() {
        GroovyArmorTrait trait = new GroovyArmorTrait(name, color, maxLevel, countPerLevel);
        trait.onHeal = onHeal;
        trait.onHurt = onHurt;
        trait.onDamaged = onDamaged;
        trait.onArmorHeal = onArmorHeal;
        trait.onArmorDamaged = onArmorDamaged;
        trait.onArmorTick = onArmorTick;
        trait.onAbilityTick = onAbilityTick;
        trait.getModifications = getModifications;
        trait.getAbilityLevel = getAbilityLevel;
        trait.onUpdate = onUpdate;
        trait.onRepair = onRepair;
        trait.extraInfo = extraInfo;
        trait.canApplyTogether = canApplyTogether;
        trait.canApplyTogetherEnchantment = canApplyTogetherEnchantment;
        trait.onArmorEquipped = onArmorEquipped;
        trait.onArmorRemoved = onArmorRemoved;
        trait.onFalling = onFalling;
        trait.onJumping = onJumping;
        trait.onItemPickup = onItemPickup;
        trait.displayName = displayName;
        trait.description = description;
        trait.hidden = hidden;

        items.forEach(item -> {
            if (item.ingredient instanceof OreDictIngredient) trait.addItem(((OreDictIngredient) item.ingredient).getOreDict(), item.amountNeeded, item.amountMatched);
            else trait.addItem(item.ingredient.getMatchingStacks()[0], item.amountNeeded, item.amountMatched);
        });

        TinkerRegistry.addTrait(trait);
        return trait;
    }
}
