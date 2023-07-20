

eventManager.listen { TextureStitchEvent.Pre event ->
    event.getMap().registerSprite(resource('groovytest:blocks/example'))
}
