package com.cleanroommc.groovyscript.compat.mods.pneumaticcraft;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import me.desht.pneumaticcraft.api.recipe.ItemIngredient;
import net.minecraft.item.ItemStack;

public class PneumaticCraft extends GroovyPropertyContainer {

    public final Amadron amadron = new Amadron();
    public final AssemblyController assemblyController = new AssemblyController();
    public final Explosion explosion = new Explosion();
    public final HeatFrameCooling heatFrameCooling = new HeatFrameCooling();
    public final LiquidFuel liquidFuel = new LiquidFuel();
    public final PlasticMixer plasticMixer = new PlasticMixer();
    public final PressureChamber pressureChamber = new PressureChamber();
    public final Refinery refinery = new Refinery();
    public final ThermopneumaticProcessingPlant thermopneumaticProcessingPlant = new ThermopneumaticProcessingPlant();
    public final XpFluid xpFluid = new XpFluid();

    public static ItemIngredient toItemIngredient(IIngredient ingredient) {
        if (ingredient instanceof OreDictIngredient oreDictIngredient) return new ItemIngredient(oreDictIngredient.getOreDict(), ingredient.getAmount());
        if (IngredientHelper.isItem(ingredient)) return new ItemIngredient(IngredientHelper.toItemStack(ingredient).copy());
        return new ItemIngredient(ItemStack.EMPTY);
    }

}
