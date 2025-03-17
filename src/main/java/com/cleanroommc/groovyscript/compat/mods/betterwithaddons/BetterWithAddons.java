package com.cleanroommc.groovyscript.compat.mods.betterwithaddons;

import betterwithaddons.util.IngredientSized;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.Loader;

public class BetterWithAddons extends GroovyPropertyContainer {

    public final DryingBox dryingBox = new DryingBox();
    public final FireNet fireNet = new FireNet();
    public final Infuser infuser = new Infuser();
    public final LureTree lureTree = new LureTree();
    public final Packing packing;
    public final Rotting rotting = new Rotting();
    public final SandNet sandNet = new SandNet();
    public final SoakingBox soakingBox = new SoakingBox();
    public final Spindle spindle = new Spindle();
    public final Tatara tatara = new Tatara();
    public final Transmutation transmutation = new Transmutation();
    public final WaterNet waterNet = new WaterNet();

    public BetterWithAddons() {
        // the format of "PackingRecipe" has changed, and we cannot build against both.
        packing = isBetterWithEverything() ? null : new Packing();
    }

    /**
     * This is required for grs to not crash when bwm is not loaded for some reason.
     */
    public static class FromIngredient {

        /**
         * Because Better With Addons checks if the ingredient is an instanceof {@link betterwithaddons.util.IHasSize IHasSize}
         * to determine if the Ingredient has an amount, we have to use their custom Ingredient.
         * <p>
         * If this isn't used, then the recipe may appear correctly in JEI but will actually only consume 1 when done in-game.
         */
        public static Ingredient fromIIngredient(IIngredient ingredient) {
            return new IngredientSized(ingredient.toMcIngredient(), ingredient.getAmount());
        }
    }

    public static boolean isBetterWithEverything() {
        var entry = Loader.instance().getIndexedModList().get("betterwithaddons");
        if (entry == null) return false;
        return entry.getMetadata().authorList.contains("ACGaming"); // TODO identify a better way to do this, if one exists
    }
}
