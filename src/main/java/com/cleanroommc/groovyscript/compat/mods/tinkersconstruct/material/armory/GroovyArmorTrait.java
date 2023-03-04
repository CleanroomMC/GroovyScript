package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.armory;

import c4.conarm.lib.armor.ArmorModifications;
import c4.conarm.lib.modifiers.ArmorModifierTrait;
import c4.conarm.lib.traits.IArmorTrait;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.traits.TraitFunctions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import slimeknights.tconstruct.library.modifiers.IToolMod;

import java.util.Arrays;
import java.util.List;

public class GroovyArmorTrait extends ArmorModifierTrait implements IArmorTrait {
    public boolean hidden;
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

    public GroovyArmorTrait(String identifier, int color) {
        this(identifier, color, 0, 0);
    }

    public GroovyArmorTrait(String identifier, int color, int maxLevel, int countPerLevel) {
        super(identifier, color, maxLevel, countPerLevel);
    }

    @Override
    public ArmorModifications getModifications(EntityPlayer entityPlayer, ArmorModifications armorModifications, ItemStack itemStack, DamageSource damageSource, double v, int i) {
        return getModifications != null ? getModifications.handle(entityPlayer, armorModifications, itemStack, damageSource, v, i) : super.getModifications(entityPlayer, armorModifications, itemStack, damageSource, v, i);
    }

    @Override
    public void onItemPickup(ItemStack itemStack, EntityItem entityItem, EntityItemPickupEvent entityItemPickupEvent) {
        if (onItemPickup != null) onItemPickup.handle(itemStack, entityItem, entityItemPickupEvent);
    }

    @Override
    public float onHeal(ItemStack itemStack, EntityPlayer entityPlayer, float v, float v1, LivingHealEvent livingHealEvent) {
        return onHeal != null ? onHeal.handle(itemStack, entityPlayer, v, v1, livingHealEvent) : super.onHeal(itemStack, entityPlayer, v, v1, livingHealEvent);
    }

    @Override
    public float onHurt(ItemStack itemStack, EntityPlayer entityPlayer, DamageSource damageSource, float v, float v1, LivingHurtEvent livingHurtEvent) {
        return onHurt != null ? onHurt.handle(itemStack, entityPlayer, damageSource, v, v1, livingHurtEvent) : super.onHurt(itemStack, entityPlayer, damageSource, v, v1, livingHurtEvent);
    }

    @Override
    public float onDamaged(ItemStack itemStack, EntityPlayer entityPlayer, DamageSource damageSource, float v, float v1, LivingDamageEvent livingDamageEvent) {
        return onDamaged != null ? onDamaged.handle(itemStack, entityPlayer, damageSource, v, v1, livingDamageEvent) : super.onDamaged(itemStack, entityPlayer, damageSource, v, v1, livingDamageEvent);
    }

    @Override
    public void onKnockback(ItemStack itemStack, EntityPlayer entityPlayer, LivingKnockBackEvent livingKnockBackEvent) {
        if (onKnockback != null) onKnockback.handle(itemStack, entityPlayer, livingKnockBackEvent);
    }

    @Override
    public void onFalling(ItemStack itemStack, EntityPlayer entityPlayer, LivingFallEvent livingFallEvent) {
        if (onFalling != null) onFalling.handle(itemStack, entityPlayer, livingFallEvent);
    }

    @Override
    public void onJumping(ItemStack itemStack, EntityPlayer entityPlayer, LivingEvent.LivingJumpEvent livingJumpEvent) {
        if (onJumping != null) onJumping.handle(itemStack, entityPlayer, livingJumpEvent);
    }

    @Override
    public void onAbilityTick(int i, World world, EntityPlayer entityPlayer) {
        if (onAbilityTick != null) onAbilityTick.handle(i, world, entityPlayer);
    }

    @Override
    public void onArmorEquipped(ItemStack itemStack, EntityPlayer entityPlayer, int i) {
        if (onArmorEquipped != null) onArmorEquipped.handle(itemStack, entityPlayer, i);
    }

    @Override
    public void onArmorRemoved(ItemStack itemStack, EntityPlayer entityPlayer, int i) {
        if (onArmorRemoved != null) onArmorRemoved.handle(itemStack, entityPlayer, i);
    }

    @Override
    public int onArmorDamage(ItemStack itemStack, DamageSource damageSource, int i, int i1, EntityPlayer entityPlayer, int i2) {
        return onArmorDamaged != null ? onArmorDamaged.handle(itemStack, damageSource, i, i1, entityPlayer, i2) : super.onArmorDamage(itemStack, damageSource, i, i1, entityPlayer, i2);
    }

    @Override
    public int onArmorHeal(ItemStack itemStack, DamageSource damageSource, int i, int i1, EntityPlayer entityPlayer, int i2) {
        return onArmorHeal != null ? onArmorHeal.handle(itemStack, damageSource, i, i1, entityPlayer, i2) : super.onArmorHeal(itemStack, damageSource, i, i1, entityPlayer, i2);
    }

    @Override
    public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (onUpdate != null) onUpdate.handle(tool, world, entity, itemSlot, isSelected);
    }

    @Override
    public void onArmorTick(ItemStack tool, World world, EntityPlayer player) {
        if (onArmorTick != null) onArmorTick.handle(tool, world, player);
    }

    @Override
    public void onRepair(ItemStack tool, int amount) {
        if (onRepair != null) onRepair.handle(tool, amount);
    }

    @Override
    public String getLocalizedName() {
        return displayName != null ? displayName : super.getLocalizedName();
    }

    @Override
    public String getLocalizedDesc() {
        return description != null ? description : super.getLocalizedDesc();
    }

    @Override
    public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
        return extraInfo != null ? Arrays.asList(extraInfo.handle(tool, modifierTag)) : super.getExtraInfo(tool, modifierTag);
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public boolean canApplyTogether(IToolMod iToolMod) {
        return canApplyTogether != null ? canApplyTogether.handle(iToolMod) : super.canApplyTogether(iToolMod);
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return canApplyTogetherEnchantment != null ? canApplyTogetherEnchantment.handle(enchantment) : super.canApplyTogether(enchantment);
    }
}
