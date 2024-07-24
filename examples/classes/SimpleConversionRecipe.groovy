package classes

/**
 * A simple recipe and recipe holder to demonstrate both classes in GroovyScript and custom reloading compat.
 */
class SimpleConversionRecipe {
    static final List<SimpleConversionRecipe> recipes = []

    ItemStack input
    ItemStack output

    SimpleConversionRecipe(input, output) {
        this.input = input
        this.output = output
    }

}