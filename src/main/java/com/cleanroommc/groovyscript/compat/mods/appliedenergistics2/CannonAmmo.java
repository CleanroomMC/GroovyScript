package com.cleanroommc.groovyscript.compat.mods.appliedenergistics2;

import appeng.api.AEApi;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.appliedenergistics2.MatterCannonAmmoRegistryAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES
)
public class CannonAmmo extends VirtualizedRegistry<Pair<ItemStack, Double>> {

    public CannonAmmo() {
        super(Alias.generateOfClassAnd(CannonAmmo.class, "Cannon"));
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> ((MatterCannonAmmoRegistryAccessor) AEApi.instance().registries().matterCannon()).getDamageModifiers().entrySet().removeIf(x -> ItemStack.areItemStacksEqual(x.getKey(), pair.getKey())));
        restoreFromBackup().forEach(pair -> ((MatterCannonAmmoRegistryAccessor) AEApi.instance().registries().matterCannon()).getDamageModifiers().put(pair.getKey(), pair.getValue()));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), 10000"))
    public void add(ItemStack item, double value) {
        addScripted(Pair.of(item, value));
        ((MatterCannonAmmoRegistryAccessor) AEApi.instance().registries().matterCannon()).getDamageModifiers().put(item, value);
    }

    @MethodDescription(example = @Example("item('minecraft:gold_nugget')"))
    public void remove(ItemStack item) {
        addBackup(Pair.of(item, ((MatterCannonAmmoRegistryAccessor) AEApi.instance().registries().matterCannon()).getDamageModifiers().get(item)));
        ((MatterCannonAmmoRegistryAccessor) AEApi.instance().registries().matterCannon()).getDamageModifiers().entrySet().removeIf(x -> ItemStack.areItemStacksEqual(x.getKey(), item));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ((MatterCannonAmmoRegistryAccessor) AEApi.instance().registries().matterCannon()).getDamageModifiers().forEach((item, value) -> addBackup(Pair.of(item, value)));
        ((MatterCannonAmmoRegistryAccessor) AEApi.instance().registries().matterCannon()).getDamageModifiers().clear();
    }

}
