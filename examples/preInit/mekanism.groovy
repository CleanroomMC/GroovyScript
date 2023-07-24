
import net.minecraftforge.client.event.TextureStitchEvent

if (!isLoaded('mekanism')) {
    println 'cancelled loading script mekanism'
    return
}

eventManager.listen { TextureStitchEvent.Pre event ->
    event.getMap().registerSprite(resource('placeholdername:blocks/mekanism_infusion_texture'))
}
