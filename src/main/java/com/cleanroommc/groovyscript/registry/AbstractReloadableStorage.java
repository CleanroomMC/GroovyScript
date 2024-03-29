package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class AbstractReloadableStorage<R> {

    private Collection<R> backup;
    private Collection<R> scripted;

    public AbstractReloadableStorage() {
        initBackup();
        initScripted();
    }

    @GroovyBlacklist
    public Collection<R> getBackupRecipes() {
        return Collections.unmodifiableCollection(backup);
    }

    @GroovyBlacklist
    public Collection<R> getScriptedRecipes() {
        return Collections.unmodifiableCollection(scripted);
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    private void initBackup() {
        this.backup = new ArrayList<>();
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    private void initScripted() {
        this.scripted = new ArrayList<>();
    }

    @GroovyBlacklist
    public boolean addBackup(R recipe) {
        if (this.scripted.stream().anyMatch(r -> compareRecipe(r, recipe))) return false;
        return this.backup.add(recipe);
    }

    @GroovyBlacklist
    public boolean addScripted(R recipe) {
        return this.scripted.add(recipe);
    }

    @GroovyBlacklist
    public Collection<R> restoreFromBackup() {
        Collection<R> backup = this.backup;
        initBackup();
        return backup;
    }

    @GroovyBlacklist
    public Collection<R> removeScripted() {
        Collection<R> scripted = this.scripted;
        initScripted();
        return scripted;
    }

    @GroovyBlacklist
    protected boolean compareRecipe(R recipe, R recipe2) {
        return Objects.equals(recipe, recipe2);
    }

}
