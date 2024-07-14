package classes

/**
 * A simple example of reloadable compat for a single registry, in this case {@code SimpleConversionRecipe}.<br>
 *
 * To use, call {@link GenericRecipeReloading#onReload()} *before* adding or removing any recipes,
 * and manipulate recipes by calling {@link GenericRecipeReloading#add()} or {@link GenericRecipeReloading#remove()}.
 * If you do it *after*, you will immediately undo all manipulations you made, you must do it *before*.<br>
 *
 * Note that {@link GenericRecipeReloading#onReload()} should only be called when GroovyScript is reloading,
 * so you will want to write something like this:<br>
 * {@code if (isReloading()) GenericRecipeReloading.instance.onReload()}
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