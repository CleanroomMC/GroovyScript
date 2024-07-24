package classes

/**
 * A simple example of reloadable compat for a single registry, in this case {@code SimpleConversionRecipe}.<br>
 *
 * To use, call {@link GenericRecipeReloading#onReload()} in an event listener listening to {@code GroovyReloadEvent},
 * and manipulate recipes by calling {@link GenericRecipeReloading#add()} or {@link GenericRecipeReloading#remove()}.<br>
 *
 * Note that {@link GenericRecipeReloading#onReload()} should only be called when GroovyScript is reloading,
 * so you will want to have it called by listening to {@code GroovyReloadEvent} in something like this:<br>
 * <pre>
 * eventManager.listen(com.cleanroommc.groovyscript.event.GroovyReloadEvent) {
 *     GenericRecipeReloading.instance.onReload()
 * }
 * </pre>
 */
class GenericRecipeReloading {

    static def instance = new GenericRecipeReloading()

    def scripted = []
    def backup = []

    void onReload() {
        scripted.each { SimpleConversionRecipe.recipes.remove(it) }
        scripted.clear()
        backup.each { SimpleConversionRecipe.recipes.add(it) }
        backup.clear()
    }

    void add(recipe) {
        scripted << recipe
        SimpleConversionRecipe.recipes.add(recipe)
    }

    void remove(recipe) {
        backup << recipe
        SimpleConversionRecipe.recipes.remove(recipe)
    }
}