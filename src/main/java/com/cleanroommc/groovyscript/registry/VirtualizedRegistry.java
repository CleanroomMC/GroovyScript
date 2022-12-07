package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.google.common.base.CaseFormat;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public abstract class VirtualizedRegistry<R> {

    protected final List<String> aliases;

    protected Collection<R> backup, scripted;

    public VirtualizedRegistry(String... aliases) { this(true, aliases); }

    public VirtualizedRegistry(Boolean generate, String... aliases) {
        this.aliases = new ArrayList<>();
        if (generate) {
            Collections.addAll(this.aliases, VirtualizedRegistry.generateAliases(this.getClass().getSimpleName()));
        }
        Collections.addAll(this.aliases, aliases);
        initBackup();
        initScripted();
    }

    public static String[] generateAliases(String name) {
        ArrayList<String> aliases = new ArrayList<>();
        aliases.add(name);
        aliases.add(name.toLowerCase(Locale.ROOT));

        if (name.split("[A-Z]").length > 2) {
            aliases.add(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name));
            aliases.add(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name));
        }

        return aliases.toArray(new String[0]);
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
