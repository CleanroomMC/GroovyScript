
// Auto generated groovyscript example file
// MODS_LOADED: prodigytech

if (!isLoaded('prodigytech')) return
println 'mod \'prodigytech\' detected, running script'

// groovyscript.wiki.prodigytech.zorra_altar_item.title:
// groovyscript.wiki.prodigytech.zorra_altar_item.description

// Create an item at the location 'placeholdername:prodigy_stick' enchantable in the Zorra Altar
// Note: due to the PT's implementation it is impossible to make other mod's items enchantable
// This merely registers the item, the pre-init script adds the specific enchantments
mods.prodigytech.zorra_altar_item.item('prodigy_stick', 'stick')
    .setMaxStackSize(5)
    .setRarity(EnumRarity.RARE)
    .setCreativeTab(creativeTab('misc'))
    .register()

