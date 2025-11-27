
// Auto generated groovyscript example file
// MODS_LOADED: mekanism

import net.minecraftforge.client.event.TextureStitchEvent

log 'mod \'mekanism\' detected, running script'

eventManager.listen { TextureStitchEvent.Pre event ->
    event.getMap().registerSprite(resource('groovyscriptdev:blocks/mekanism_infusion_texture'))
}
