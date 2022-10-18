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
        initBackup();
        initScripted();
    }

    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    public abstract void onReload();

    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    public void afterScriptLoad() {

    }

    public void addAlias(String... aliases) {
        Collections.addAll(this.aliases, aliases);
    }

    public List<String> getAliases() {
        return aliases;
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
    protected void initBackup() {
        this.backup = new ArrayList<>();
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    protected void initScripted() {
        this.scripted = new ArrayList<>();
    }

    @GroovyBlacklist
    public void addBackup(R recipe) {
        this.backup.add(recipe);
    }

    @GroovyBlacklist
    public void addScripted(R recipe) {
        this.scripted.add(recipe);
    }

    @GroovyBlacklist
    protected Collection<R> restoreFromBackup() {
        Collection<R> backup = this.backup;
        initBackup();
        return backup;
    }

    @GroovyBlacklist
    protected Collection<R> removeScripted() {
        Collection<R> scripted = this.scripted;
        initScripted();
        return scripted;
    }

}
