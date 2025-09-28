package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.util.StackIngredient;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import net.minecraft.item.crafting.Ingredient;

public class BetterWithMods extends GroovyPropertyContainer {

    public final AnvilCrafting anvilCrafting = new AnvilCrafting();
    public final Cauldron cauldron = new Cauldron();
    public final Crucible crucible = new Crucible();
    public final Kiln kiln = new Kiln();
    public final MillStone millStone = new MillStone();
    public final Saw saw = new Saw();
    public final Turntable turntable = new Turntable();
    public final Heat heat = new Heat();
    public final Hopper hopper = new Hopper();
    public final HopperFilters hopperFilters = new HopperFilters();

    public static class Helper {

        /**
         * Because Better With Mods checks if the ingredient is an instanceof {@link StackIngredient}
         * to determine if the Ingredient has an amount, we have to use their custom Ingredient.
         * <p>
         * If this isn't used, then the recipe may appear correctly in JEI but will actually only consume 1 when done in-game.
         */
        public static Ingredient fromIIngredient(IIngredient ingredient) {
            return StackIngredient.fromIngredient(ingredient.getAmount(), ingredient.toMcIngredient());
        }
    }
}
