package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.BewitchmentAPI;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class AthameLoot extends VirtualizedRegistry<Map.Entry<Predicate<EntityLivingBase>, Collection<ItemStack>>> {

    public static Map<Predicate<EntityLivingBase>, Collection<ItemStack>> getRegistry() {
        return BewitchmentAPI.ATHAME_LOOT;
    }

    @Override
    public void onReload() {
        var recipes = getRegistry();
        removeScripted().forEach(x -> recipes.remove(x.getKey(), x.getValue()));
        restoreFromBackup().forEach(x -> recipes.put(x.getKey(), x.getValue()));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("entity('minecraft:pig'), item('minecraft:gold_ingot')"))
    public boolean add(EntityEntry entity, ItemStack stack) {
        return add(e -> entity.getEntityClass().isInstance(e), stack);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("entity('minecraft:cow'), item('minecraft:clay') * 5, item('minecraft:iron_sword')"))
    public boolean add(EntityEntry entity, ItemStack... stacks) {
        return add(e -> entity.getEntityClass().isInstance(e), stacks);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean add(EntityEntry entity, Collection<ItemStack> stacks) {
        return add(e -> entity.getEntityClass().isInstance(e), stacks);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean add(Class<? extends EntityLivingBase> entity, ItemStack stack) {
        return add(entity::isInstance, stack);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean add(Class<? extends EntityLivingBase> entity, ItemStack... stacks) {
        return add(entity::isInstance, stacks);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean add(Class<? extends EntityLivingBase> entity, Collection<ItemStack> stacks) {
        return add(entity::isInstance, stacks);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean add(Predicate<EntityLivingBase> predicate, ItemStack stack) {
        return add(predicate, ImmutableList.of(stack));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean add(Predicate<EntityLivingBase> predicate, ItemStack... stacks) {
        return add(predicate, ImmutableList.copyOf(stacks));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean add(Predicate<EntityLivingBase> predicate, Collection<ItemStack> stacks) {
        getRegistry().put(predicate, stacks);
        doAddScripted(Pair.of(predicate, stacks));
        return true;
    }

    @MethodDescription(example = @Example("item('bewitchment:spectral_dust')"))
    public boolean removeByOutput(IIngredient output) {
        return getRegistry().entrySet().removeIf(recipe -> recipe.getValue().stream().anyMatch(output) && doAddBackup(recipe));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        getRegistry().entrySet().removeIf(this::doAddBackup);
    }
}
