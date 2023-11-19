package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.helper.Alias;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class VirtualizedRegistry<R> implements IScriptReloadable {

    private final List<String> aliases;
    private Collection<R> backup, scripted;

    public VirtualizedRegistry() {
        this(null);
    }

    public VirtualizedRegistry(@Nullable Collection<String> aliases) {
        if (aliases == null) {
            aliases = Alias.generateOfClass(this);
        } else if (aliases.isEmpty()) {
            throw new IllegalArgumentException("VirtualRegistry must have at least one name!");
        }
        List<String> aliases1 = aliases.stream().distinct().collect(Collectors.toList());
        this.aliases = Collections.unmodifiableList(aliases1);
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
        if (this.scripted.stream().anyMatch(r -> compareRecipe(r, recipe))) return;
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

    @GroovyBlacklist
    protected boolean compareRecipe(R recipe, R recipe2) {
        return recipe == recipe2;
    }
}
