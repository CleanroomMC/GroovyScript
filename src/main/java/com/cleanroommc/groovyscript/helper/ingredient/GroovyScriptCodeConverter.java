package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.core.mixin.CreativeTabsAccessor;
import com.cleanroommc.groovyscript.helper.StyleConstant;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;
import java.util.Map;

public class GroovyScriptCodeConverter {

    public static String formatNumber(int number, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.NUMBER);
        builder.append(number);
        return builder.toString();
    }

    public static String formatString(String target, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.BASE);
        builder.append("'");
        if (colored) builder.append(StyleConstant.STRING);
        builder.append(target);
        if (colored) builder.append(StyleConstant.BASE);
        builder.append("'");
        return builder.toString();
    }

    public static String formatGenericHandler(String handler, String target, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.METHOD);
        builder.append(handler);
        if (colored) builder.append(StyleConstant.BASE);
        builder.append("('");
        if (colored) builder.append(StyleConstant.STRING);
        builder.append(target);
        if (colored) builder.append(StyleConstant.BASE);
        builder.append("')");
        return builder.toString();
    }

    public static String formatMultiple(int amount, boolean colored) {
        if (amount <= 1) return "";
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.BASE);
        builder.append(" * ");
        builder.append(formatNumber(amount, colored));
        return builder.toString();
    }

    public static String formatInstantiation(String clazz, List<String> params, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.NEW);
        builder.append("new ");
        if (colored) builder.append(StyleConstant.CLASS);
        builder.append(clazz);
        if (colored) builder.append(StyleConstant.BASE);
        builder.append("(");
        builder.append(String.join(colored ? StyleConstant.BASE + ", " : ", ", params));
        if (colored) builder.append(StyleConstant.BASE);
        builder.append(")");
        return builder.toString();
    }

    public static String formatResourceLocation(String handler, ResourceLocation resourceLocation, boolean colored) {
        if (resourceLocation == null) return null;
        return formatGenericHandler(handler, resourceLocation.toString(), colored);
    }

    public static String formatForgeRegistryImpl(String handler, IForgeRegistryEntry.Impl<?> impl, boolean colored) {
        return formatResourceLocation(handler, impl.getRegistryName(), colored);
    }

    public static String formatNBTTag(NBTTagCompound tag, boolean colored, boolean prettyNbt) {
        StringBuilder builder = new StringBuilder();
        if (tag != null) {
            if (colored) builder.append(StyleConstant.BASE);
            builder.append(".");
            if (colored) builder.append(StyleConstant.METHOD);
            builder.append("withNbt");
            if (colored) builder.append(StyleConstant.BASE);
            builder.append("(");
            builder.append(NbtHelper.toGroovyCode(tag, prettyNbt, colored));
            if (colored) builder.append(StyleConstant.BASE);
            builder.append(")");
        }
        return builder.toString();
    }

    private static String getSingleItemStack(ItemStack itemStack, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.METHOD);
        builder.append("item");
        if (colored) builder.append(StyleConstant.BASE);
        builder.append("('");
        if (colored) builder.append(StyleConstant.STRING);
        builder.append(itemStack.getItem().getRegistryName());
        // code is more complex than strictly needed here to allow using the wildcard
        if (itemStack.getMetadata() == Short.MAX_VALUE) {
            builder.append(":");
            builder.append("*");
            if (colored) builder.append(StyleConstant.BASE);
            builder.append("'");
        } else if (itemStack.getMetadata() == 0) {
            if (colored) builder.append(StyleConstant.BASE);
            builder.append("'");
        } else {
            if (colored) builder.append(StyleConstant.BASE);
            builder.append("'");
            builder.append(", ");
            builder.append(formatNumber(itemStack.getMetadata(), colored));
            if (colored) builder.append(StyleConstant.BASE);
        }
        builder.append(")");
        return builder.toString();
    }

    public static String asGroovyCode(ItemStack itemStack, boolean colored) {
        return getSingleItemStack(itemStack, colored) + formatMultiple(itemStack.getCount(), colored);
    }

    public static String asGroovyCode(ItemStack itemStack, boolean colored, boolean prettyNbt) {
        return getSingleItemStack(itemStack, colored) + formatNBTTag(itemStack.getTagCompound(), colored, prettyNbt) + formatMultiple(itemStack.getCount(), colored);
    }

    public static String asGroovyCode(FluidStack fluidStack, boolean colored) {
        return formatGenericHandler("fluid", fluidStack.getFluid().getName(), colored) + formatMultiple(fluidStack.amount, colored);
    }

    public static String asGroovyCode(FluidStack fluidStack, boolean colored, boolean prettyNbt) {
        return formatGenericHandler("fluid", fluidStack.getFluid().getName(), colored) + formatNBTTag(fluidStack.tag, colored, prettyNbt) + formatMultiple(fluidStack.amount, colored);
    }

    public static String asGroovyCode(String oreDict, boolean colored) {
        return formatGenericHandler("ore", oreDict, colored);
    }

    public static String asGroovyCode(Biome biome, boolean colored) {
        return formatForgeRegistryImpl("biome", biome, colored);
    }

    public static String asGroovyCode(VillagerRegistry.VillagerProfession profession, boolean colored) {
        return formatForgeRegistryImpl("profession", profession, colored);
    }

    public static String asGroovyCode(VillagerRegistry.VillagerCareer career, boolean colored) {
        return formatGenericHandler("career", career.getName(), colored);
    }

    public static String asGroovyCode(DimensionType dimensionType, boolean colored) {
        return formatGenericHandler("dimension", dimensionType.getName(), colored);
    }

    public static String asGroovyCode(Entity entity, boolean colored) {
        return formatResourceLocation("entity", EntityList.getKey(entity), colored);
    }

    public static String asGroovyCode(EntityEntry entity, boolean colored) {
        return formatForgeRegistryImpl("entity", entity, colored);
    }

    public static String asGroovyCode(CreativeTabs tab, boolean colored) {
        return formatGenericHandler("creativeTab", ((CreativeTabsAccessor) tab).getTabLabel2(), colored);
    }

    public static String asGroovyCode(Enchantment enchantment, boolean colored) {
        return formatForgeRegistryImpl("enchantment", enchantment, colored);
    }

    public static String asGroovyCode(Potion potion, boolean colored) {
        return formatResourceLocation("potion", Potion.REGISTRY.getNameForObject(potion), colored);
    }

    public static String asGroovyCode(SoundEvent sound, boolean colored) {
        return formatForgeRegistryImpl("sound", sound, colored);
    }

    public static String asGroovyCode(PotionEffect potionEffect, boolean colored) {
        StringBuilder builder = new StringBuilder();
        List<String> list = Lists.newArrayList(
                asGroovyCode(potionEffect.getPotion(), colored),
                formatNumber(potionEffect.getDuration(), colored),
                formatNumber(potionEffect.getAmplifier(), colored));
        builder.append(formatInstantiation("PotionEffect", list, colored));
        return builder.toString();
    }

    public static String asGroovyCode(Block state, boolean colored) {
        return formatResourceLocation("block", state.getRegistryName(), colored);
    }

    @SuppressWarnings("all")
    public static String asGroovyCode(IBlockState state, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.METHOD);
        builder.append("blockstate");
        if (colored) builder.append(StyleConstant.BASE);
        builder.append("('");
        if (colored) builder.append(StyleConstant.STRING);
        builder.append(state.getBlock().getRegistryName());
        if (colored) builder.append(StyleConstant.BASE);
        builder.append("'");
        if (!state.getProperties().isEmpty()) {
            for (Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet()) {
                IProperty property = entry.getKey();
                if (colored) builder.append(StyleConstant.BASE);
                builder.append(", ").append("'");
                if (colored) builder.append(StyleConstant.NEW);
                builder.append(property.getName());
                if (colored) builder.append(StyleConstant.BASE);
                builder.append("=");
                if (colored) builder.append(StyleConstant.CLASS);
                builder.append(property.getName(entry.getValue()));
                if (colored) builder.append(StyleConstant.BASE);
                builder.append("'");
            }
        }
        builder.append(")");
        return builder.toString();
    }
}
