// MODS_LOADED: prodigytech

import lykrast.prodigytech.common.item.IZorrasteelEquipment
import lykrast.prodigytech.common.recipe.ZorraAltarManager

if (!isLoaded('prodigytech')) return
println 'mod \'prodigytech\' detected, running script'

// Create an item at the location 'placeholdername:prodigy_stick' enchantable in the Zorra Altar
// Note: due to the PT's implementation it is difficult to make other mod's items enchantable
// This merely registers the item, the pre-init script adds the specific enchantments
class ProdigyStick extends Item implements IZorrasteelEquipment {
    static registry = mods.prodigytech.zorra_altar.createRegistry('stick')

    ZorraAltarManager getManager() {
        return registry
    }
}

content.registerItem('prodigy_stick', new ProdigyStick())

