package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.traits;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.MaterialRepairIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import slimeknights.tconstruct.library.TinkerRegistry;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TraitBuilder {
    public boolean hidden = false;
    public int color = 0xFFFFFF;
    public int maxLevel = 0;
    public int countPerLevel = 0;
    public final String name;
    public String displayName;
    public String description;
    public TraitFunctions.OnUpdate onUpdate;
    public TraitFunctions.MiningSpeed miningSpeed;
    public TraitFunctions.BeforeBlockBreak beforeBlockBreak;
    public TraitFunctions.AfterBlockBreak afterBlockBreak;
    public TraitFunctions.BlockHarvestDrops blockHarvestDrops;
    public TraitFunctions.IsCriticalHit isCriticalHit;
    public TraitFunctions.Damage damage;
    public TraitFunctions.OnHit onHit;
    public TraitFunctions.AfterHit afterHit;
    public TraitFunctions.KnockBack knockBack;
    public TraitFunctions.OnBlock onBlock;
    public TraitFunctions.OnToolDamage onToolDamage;
    public TraitFunctions.OnToolHeal onToolHeal;
    public TraitFunctions.OnRepair onRepair;
    public TraitFunctions.OnPlayerHurt onPlayerHurt;
    public TraitFunctions.CanApplyTogether canApplyTogether;
    public TraitFunctions.CanApplyTogetherEnchantment canApplyTogetherEnchantment;
    public TraitFunctions.ExtraInfo extraInfo;
    protected final List<MaterialRepairIngredient> items = new ArrayList<>();

    public TraitBuilder(String name) {
        this.name = name;
    }

    public TraitBuilder addItem(IIngredient ingredient, int amountNeeded, int amountMatched) {
        this.items.add(new MaterialRepairIngredient(ingredient, amountNeeded, amountMatched));
        return this;
    }

    public TraitBuilder setColor(int color) {
        this.color = color;
        return this;
    }

    public TraitBuilder setColor(Color color) {
        return setColor(color.getRGB());
    }

    public TraitBuilder setColor(int r, int g, int b) {
        return setColor(new Color(r, g, b));
    }

    public TraitBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public TraitBuilder setLocalizedName(String name) {
        return setDisplayName(name);
    }

    public TraitBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public TraitBuilder setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public TraitBuilder setHidden() {
        return setHidden(!hidden);
    }

    public TraitBuilder setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public TraitBuilder setCountPerLevel(int count) {
        this.countPerLevel = count;
        return this;
    }

    public TraitBuilder onUpdate(TraitFunctions.OnUpdate func) {
        this.onUpdate = func;
        return this;
    }

    public TraitBuilder miningSpeed(TraitFunctions.MiningSpeed func) {
        this.miningSpeed = func;
        return this;
    }

    public TraitBuilder beforeBlockBreak(TraitFunctions.BeforeBlockBreak func) {
        this.beforeBlockBreak = func;
        return this;
    }

    public TraitBuilder afterBlockBreak(TraitFunctions.AfterBlockBreak func) {
        this.afterBlockBreak = func;
        return this;
    }

    public TraitBuilder blockHarvestDrops(TraitFunctions.BlockHarvestDrops func) {
        this.blockHarvestDrops = func;
        return this;
    }

    public TraitBuilder isCriticalHit(TraitFunctions.IsCriticalHit func) {
        this.isCriticalHit = func;
        return this;
    }

    public TraitBuilder damage(TraitFunctions.Damage func) {
        this.damage = func;
        return this;
    }

    public TraitBuilder onHit(TraitFunctions.OnHit func) {
        this.onHit = func;
        return this;
    }

    public TraitBuilder afterHit(TraitFunctions.AfterHit func) {
        this.afterHit = func;
        return this;
    }

    public TraitBuilder knockback(TraitFunctions.KnockBack func) {
        this.knockBack = func;
        return this;
    }

    public TraitBuilder onBlock(TraitFunctions.OnBlock func) {
        this.onBlock = func;
        return this;
    }

    public TraitBuilder onToolDamage(TraitFunctions.OnToolDamage func) {
        this.onToolDamage = func;
        return this;
    }

    public TraitBuilder onToolHeal(TraitFunctions.OnToolHeal func) {
        this.onToolHeal = func;
        return this;
    }

    public TraitBuilder onPlayerHurt(TraitFunctions.OnPlayerHurt func) {
        this.onPlayerHurt = func;
        return this;
    }

    public TraitBuilder onRepair(TraitFunctions.OnRepair func) {
        this.onRepair = func;
        return this;
    }

    public TraitBuilder canApplyTogether(TraitFunctions.CanApplyTogether func) {
        this.canApplyTogether = func;
        return this;
    }

    public TraitBuilder canApplyTogetherEnchantment(TraitFunctions.CanApplyTogetherEnchantment func) {
        this.canApplyTogetherEnchantment = func;
        return this;
    }

    public TraitBuilder extraInfo(TraitFunctions.ExtraInfo func) {
        this.extraInfo = func;
        return this;
    }

    public GroovyTrait register() {
        GroovyTrait trait = new GroovyTrait(name, color, maxLevel, countPerLevel);
        trait.onUpdate = onUpdate;
        trait.miningSpeed = miningSpeed;
        trait.beforeBlockBreak = beforeBlockBreak;
        trait.afterBlockBreak = afterBlockBreak;
        trait.blockHarvestDrops = blockHarvestDrops;
        trait.isCriticalHit = isCriticalHit;
        trait.damage = damage;
        trait.onHit = onHit;
        trait.afterHit = afterHit;
        trait.knockBack = knockBack;
        trait.onBlock = onBlock;
        trait.onToolDamage = onToolDamage;
        trait.onToolHeal = onToolHeal;
        trait.onPlayerHurt = onPlayerHurt;
        trait.onRepair = onRepair;
        trait.canApplyTogether = canApplyTogether;
        trait.canApplyTogetherEnchantment = canApplyTogetherEnchantment;
        trait.extraInfo = extraInfo;
        trait.hidden = hidden;
        trait.displayName = displayName;
        trait.description = description;

        items.forEach(item -> {
            if (item.ingredient instanceof OreDictIngredient) trait.addItem(((OreDictIngredient) item.ingredient).getOreDict(), item.amountNeeded, item.amountMatched);
            else trait.addItem(item.ingredient.getMatchingStacks()[0], item.amountNeeded, item.amountMatched);
        });

        TinkerRegistry.addTrait(trait);
        return trait;
    }
}
