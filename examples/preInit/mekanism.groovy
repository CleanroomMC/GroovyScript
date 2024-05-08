
// MODS_LOADED: mekanism
import net.minecraftforge.client.event.TextureStitchEvent

if (!isLoaded('mekanism')) return
println 'mod \'mekanism\' detected, running script'

eventManager.listen(TextureStitchEvent.Pre) { event ->
    event.getMap().registerSprite(resource('placeholdername:blocks/mekanism_infusion_texture'))
}
