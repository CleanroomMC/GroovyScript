package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class VirtualizedRegistry<R> {

    protected final List<String> aliases;

    protected Collection<R> backup, scripted;

    public VirtualizedRegistry(String name, String... aliases) {
        this.aliases = new ArrayList<>();
        this.aliases.add(name);
        addAlias(aliases);
        initCollections();
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public abstract void onReload();

    @GroovyBlacklist
    @ApiStatus.Internal
    public void afterScriptLoad() {

    }

    public void addAlias(String... aliases) {
        Collections.addAll(this.aliases, aliases);
    }

    public List<String> getAliases() {
        return aliases;
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public Collection<R> getBackupRecipes() {
        return Collections.unmodifiableCollection(backup);
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public Collection<R> getScriptedRecipes() {
        return Collections.unmodifiableCollection(scripted);
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    protected void initCollections() {
        this.backup = new ArrayList<>();
        this.scripted = new ArrayList<>();
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public void addBackup(R recipe) {
        this.backup.add(recipe);
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public void addScripted(R recipe) {
        this.scripted.add(recipe);
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    protected Collection<R> restoreFromBackup() {
        Collection<R> backup = this.backup;
        this.backup = null;
        return backup;
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    protected Collection<R> removeScripted() {
        Collection<R> scripted = this.scripted;
        this.scripted = null;
        return scripted;
    }

}
