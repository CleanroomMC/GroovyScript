package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.google.common.base.CaseFormat;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public abstract class VirtualizedRegistry<R> implements IScriptReloadable {

    private final List<String> aliases;
    private Collection<R> backup, scripted;

    public VirtualizedRegistry() {
        this(true, Collections.emptyList(), new String[0]);
    }

    public VirtualizedRegistry(String... aliases) {
        this(true, Collections.emptyList(), aliases);
    }

    public VirtualizedRegistry(Collection<String> aliases) {
        this(true, aliases, new String[0]);
    }

    public VirtualizedRegistry(boolean generate, String... aliases) {
        this(generate, Collections.emptyList(), aliases);
    }

    public VirtualizedRegistry(boolean generate, Collection<String> aliases) {
        this(generate, aliases, new String[0]);
    }

    public VirtualizedRegistry(boolean generate, @NotNull Collection<String> aliases, String... aliases1) {
        List<String> aliases2 = aliases.isEmpty() ? new ArrayList<>() : new ArrayList<>(aliases);
        if (generate) generateAliases(aliases2, getClass().getSimpleName());
        Collections.addAll(aliases2, aliases1);
        aliases2 = aliases2.stream().distinct().collect(Collectors.toList());
        this.aliases = Collections.unmodifiableList(aliases2);
        initBackup();
        initScripted();
    }

    public static Collection<String> generateAliases(String name) {
        return generateAliases(name, CaseFormat.UPPER_CAMEL);
    }


    public static Collection<String> generateAliases(String name, CaseFormat caseFormat) {
        return generateAliases(new ArrayList<>(), caseFormat, name);
    }

    public static Collection<String> generateAliases(Collection<String> aliases, String name) {
        return generateAliases(aliases, CaseFormat.UPPER_CAMEL, name);
    }

    public static Collection<String> generateAliases(Collection<String> aliases, CaseFormat caseFormat, String name) {
        if (caseFormat != CaseFormat.UPPER_CAMEL) {
            name = caseFormat.to(CaseFormat.UPPER_CAMEL, name);
        }
        aliases.add(name);
        aliases.add(name.toLowerCase(Locale.ROOT));
        if (name.split("[A-Z]").length > 2) {
            aliases.add(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name));
            aliases.add(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name));
        }
        return aliases;
    }

    public static <T> void putAll(String name, T object, Map<String, T> map) {
        for (String alias : generateAliases(name)) {
            map.put(alias, object);
        }
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
