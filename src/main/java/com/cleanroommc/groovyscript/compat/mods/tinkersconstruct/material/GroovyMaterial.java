package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.ItemsIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.traits.ITrait;

import java.util.Map;
import java.util.function.BiFunction;

public class GroovyMaterial extends Material {

    public boolean hidden = false;
    public IIngredient representativeItem;
    public IIngredient shard;
    public FluidStack fluidStack;
    public String displayName;
    public BiFunction<Material, String, String> localizer;
    protected final Map<String, String> traits;

    public GroovyMaterial(String identifier, int color, Map<String, String> traits) {
        super(identifier, color);
        this.traits = traits;
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) setRenderInfo(color);
    }

    public GroovyMaterial addItem(IIngredient item, int amountNeeded, int amountMatched) {
        if (item instanceof OreDictIngredient) addItem(((OreDictIngredient) item).getOreDict(), amountNeeded, amountMatched * 144);
        else addItem(item.getMatchingStacks()[0], amountNeeded, amountMatched * 144);
        return this;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public ItemStack getRepresentativeItem() {
        return representativeItem.getMatchingStacks()[0];
    }

    @Override
    public ItemStack getShard() {
        return shard != null ? shard.getMatchingStacks()[0] : ItemStack.EMPTY;
    }

    @Override
    public void setShard(@NotNull ItemStack stack) {
        this.shard = new ItemsIngredient(stack);
    }

    @Override
    public boolean hasFluid() {
        return fluidStack != null;
    }

    @Override
    public Fluid getFluid() {
        return fluidStack != null ? fluidStack.getFluid() : null;
    }

    @Override
    public Material setFluid(Fluid fluid) {
        fluidStack = new FluidStack(fluid, 1);
        return this;
    }

    @Override
    public String getLocalizedName() {
        if (displayName != null) return displayName;
        return super.getLocalizedName();
    }

    @Override
    public String getLocalizedItemName(String itemName) {
        if (localizer != null) return localizer.apply(this, itemName);
        return super.getLocalizedItemName(itemName);
    }

    public void registerTraits() {
        for (Map.Entry<String, String> pair : traits.entrySet()) {
            ITrait trait = TinkerRegistry.getTrait(pair.getValue());
            if (trait != null) addTrait(trait, !pair.getKey().equals(pair.getValue()) ? pair.getKey() : null);
        }
    }
}
