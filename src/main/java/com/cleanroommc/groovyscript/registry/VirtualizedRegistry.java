package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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
