package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.api.jeiremoval.IJEIRemoval;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.IOperation;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.WrapperOperation;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.OperationHandler;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.collect.ImmutableList;
import epicsquid.roots.init.ModRecipes;
import epicsquid.roots.integration.jei.JEIRootsPlugin;
import epicsquid.roots.integration.jei.shears.RunicShearsSummonEntityWrapper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES
)
public class LifeEssence extends VirtualizedRegistry<Class<? extends EntityLivingBase>> implements IJEIRemoval.Default {

    private static IOperation entityOperation() {
        return new WrapperOperation<>(RunicShearsSummonEntityWrapper.class, wrapper -> {
            var tag = wrapper.recipe.getEssenceStack().getTagCompound();
            if (tag == null) return Collections.emptyList();
            // only real way to access the entity ID here
            return Collections.singletonList(OperationHandler.format("remove", GroovyScriptCodeConverter.formatGenericHandler("entity", tag.getString("id"), true)));
        });
    }

    @Override
    public void onReload() {
        removeScripted().forEach(ModRecipes.getLifeEssenceList()::remove);
        restoreFromBackup().forEach(ModRecipes.getLifeEssenceList()::add);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(Class<? extends EntityLivingBase> clazz) {
        ModRecipes.getLifeEssenceList().add(clazz);
        addScripted(clazz);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(EntityLivingBase entity) {
        add(entity.getClass());
    }

    @MethodDescription(example = @Example("entity('minecraft:wither_skeleton')"), type = MethodDescription.Type.ADDITION)
    public void add(EntityEntry entity) {
        add((Class<? extends EntityLivingBase>) entity.getEntityClass());
    }

    @MethodDescription(example = @Example("entity('minecraft:sheep')"))
    public boolean remove(Class<? extends EntityLivingBase> clazz) {
        if (!ModRecipes.getLifeEssenceList().remove(clazz)) return false;
        addBackup(clazz);
        return true;
    }

    @MethodDescription
    public boolean remove(EntityLivingBase entity) {
        return remove(entity.getClass());
    }

    @MethodDescription
    public boolean remove(EntityEntry entity) {
        return remove((Class<? extends EntityLivingBase>) entity.getEntityClass());
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ModRecipes.getLifeEssenceList().forEach(this::addBackup);
        ModRecipes.getLifeEssenceList().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Class<? extends EntityLivingBase>> streamRecipes() {
        return new SimpleObjectStream<>(ModRecipes.getLifeEssenceList()).setRemover(this::remove);
    }

    @Override
    public @NotNull Collection<String> getCategories() {
        return ImmutableList.of(JEIRootsPlugin.RUNIC_SHEARS_SUMMON_ENTITY);
    }

    @Override
    public @NotNull List<IOperation> getJEIOperations() {
        return ImmutableList.of(entityOperation());
    }

}
