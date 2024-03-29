package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.helper.Alias;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class VirtualizedRegistry<R> implements IScriptReloadable {

    protected final String name;
    private final List<String> aliases;
    private final AbstractReloadableStorage<R> recipeStorage = new AbstractReloadableStorage<>();

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
        this.name = aliases1.get(0).toLowerCase(Locale.ROOT);
        this.aliases = Collections.unmodifiableList(aliases1);
    }

    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    public abstract void onReload();

    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    public void afterScriptLoad() {
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
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
    public void addBackup(R recipe) {
        recipeStorage.addBackup(recipe);
    }

    @GroovyBlacklist
    public void addScripted(R recipe) {
        recipeStorage.addScripted(recipe);
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
