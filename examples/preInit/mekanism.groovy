
import net.minecraftforge.client.event.TextureStitchEvent

if (!isLoaded('mekanism')) {
    println 'cancelled loading script mekanism'
    return
}

eventManager.listen { TextureStitchEvent.Pre event ->
    event.getMap().registerSprite(resource('groovytest:blocks/example'))
}
