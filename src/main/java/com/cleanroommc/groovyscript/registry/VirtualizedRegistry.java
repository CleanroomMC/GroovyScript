package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Class for controlling the storage of reloadable compat.
 * <p>
 * When reloading, should first remove all added recipes, then add all removed recipes.
 * This should recreate the state prior to any script modifications.
 * If it does not recreate the state prior to any script modifications,
 * the {@link com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription @RegistryDescription} annotation
 * should set the {@link com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription#reloadability reloadability} to
 * {@link com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription.Reloadability#FLAWED Reloadability.FLAWED} or
 * {@link com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription.Reloadability#DISABLED Reloadability.DISABLED}.
 * <p>
 * To document this class, the annotation {@link com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription @RegistryDescription}
 * exists. This annotation <i>must</i> exist for the registry to be documented via GroovyScript.
 * <p>
 * In some situations, more than one type can be used to reload via the creation of
 * a new field using {@link AbstractReloadableStorage}. This field should be private and final.
 * <p>
 * Forge Registries should be handled via {@link ForgeRegistryWrapper}.
 * Basic collections of recipes should be handled via {@link StandardListRegistry}, to reduce boilerplate.
 *
 * @param <R> the recipe type being stored and reloaded
 * @see com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription @RegistryDescription
 * @see NamedRegistry
 * @see AbstractReloadableStorage
 * @see ForgeRegistryWrapper
 * @see StandardListRegistry
 */
public abstract class VirtualizedRegistry<R> extends NamedRegistry implements IScriptReloadable {

    private final AbstractReloadableStorage<R> recipeStorage;

    public VirtualizedRegistry() {
        this(null);
    }

    public VirtualizedRegistry(@Nullable Collection<String> aliases) {
        super(aliases);
        this.recipeStorage = createRecipeStorage();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    public abstract void onReload();

    @Override
    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    public void afterScriptLoad() {
    }

    @GroovyBlacklist
    protected AbstractReloadableStorage<R> createRecipeStorage() {
        return new AbstractReloadableStorage<>();
    }

    @GroovyBlacklist
    public Collection<R> getBackupRecipes() {
        return recipeStorage.getBackupRecipes();
    }

    @GroovyBlacklist
    public Collection<R> getScriptedRecipes() {
        return recipeStorage.getScriptedRecipes();
    }

    @GroovyBlacklist
    public boolean addBackup(R recipe) {
        return recipeStorage.addBackup(recipe);
    }

    @GroovyBlacklist
    public boolean addScripted(R recipe) {
        return recipeStorage.addScripted(recipe);
    }

    @GroovyBlacklist
    protected Collection<R> restoreFromBackup() {
        return recipeStorage.restoreFromBackup();
    }

    @GroovyBlacklist
    protected Collection<R> removeScripted() {
        return recipeStorage.removeScripted();
    }

}
