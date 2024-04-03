package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Stores backup and scripted entries, typically for a {@link VirtualizedRegistry<R>}.
 *
 * @param <R> The type being stored, typically a recipe
 */
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

    /**
     * The backup collection stores recipes removed from the game.
     * When adding, it first checks if the recipe to is already a member of the scripted collection via {@link #compareRecipe}.
     *
     * @param recipe the recipe to add to the backup collection
     * @return {@code true} if the backup collection was updated.
     */
    @GroovyBlacklist
    public boolean addBackup(R recipe) {
        if (this.scripted.stream().anyMatch(r -> compareRecipe(r, recipe))) return false;
        return this.backup.add(recipe);
    }

    /**
     * The scripted collection stores recipes that are added to the game.
     * Always adds the recipe.
     *
     * @param recipe the recipe to add to the scripted collection
     * @return {@code true} if the scripted collection was updated.
     */
    @GroovyBlacklist
    public boolean addScripted(R recipe) {
        return this.scripted.add(recipe);
    }

    /**
     * Resets the backup collection. Primarily used in {@link VirtualizedRegistry#onReload()}.
     * By convention, be placed after {@link #removeScripted()}
     *
     * @return the backup collection
     */
    @GroovyBlacklist
    public Collection<R> restoreFromBackup() {
        Collection<R> backup = this.backup;
        initBackup();
        return backup;
    }


    /**
     * Resets the scripted collection. Primarily used in {@link VirtualizedRegistry#onReload()}
     * By convention, be placed after {@link #restoreFromBackup()}
     *
     * @return the scripted collection
     */
    @GroovyBlacklist
    public Collection<R> removeScripted() {
        Collection<R> scripted = this.scripted;
        initScripted();
        return scripted;
    }

    /**
     * Checks if the recipes are equal for the purposes of determining if {@param recipe2} will be added to the backup collection.
     * Should be overridden if custom logic is desired to determine when two recipes should be considered equal.
     *
     * @param recipe  recipe to compare
     * @param recipe2 recipe to compare, will be added to the backup collection if this returns true
     * @return {@code true} if the recipes should be considered equal
     */
    @GroovyBlacklist
    protected boolean compareRecipe(R recipe, R recipe2) {
        return recipe == recipe2;
    }

}
