package com.cleanroommc.groovyscript.core.mixin.pyrotech;

import com.codetaylor.mc.pyrotech.library.spi.plugin.jei.JEIRecipeWrapperTimed;
import com.codetaylor.mc.pyrotech.library.spi.recipe.IRecipeTimed;
import com.codetaylor.mc.pyrotech.modules.tech.refractory.plugin.jei.wrapper.JEIRecipeWrapperRefractoryBurn;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = JEIRecipeWrapperRefractoryBurn.class, remap = false)
public abstract class JEIRecipeWrapperRefractoryBurnMixin extends JEIRecipeWrapperTimed {

    public JEIRecipeWrapperRefractoryBurnMixin(IRecipeTimed recipe) {
        super(recipe);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getItemFromBlock(Lnet/minecraft/block/Block;)Lnet/minecraft/item/Item;"), remap = false)
    private Item getBlockItemProperly(Block blockIn) {
        Item item = Item.getItemFromBlock(blockIn);
        if (item == Items.AIR) item = ForgeRegistries.ITEMS.getValue(blockIn.getRegistryName());
        if (item == null) item = Items.AIR;
        return item;
    }
}
