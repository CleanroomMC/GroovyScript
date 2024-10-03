package com.cleanroommc.groovyscript.compat.mods.botania;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.api.subtile.signature.BasicSignature;

public class Flowers {

    public void registerFlower(String name, Class<? extends SubTileEntity> clazz) {
        BotaniaAPI.registerSubTile(name, clazz);
        BotaniaAPI.registerSubTileSignature(clazz, new BasicSignature(name));
        BotaniaAPI.addSubTileToCreativeMenu(name);
    }

    public <T extends SubTileEntity> void registerFlowerWithMini(String name, Class<T> clazz, Class<? extends T> miniClazz) {
        registerFlower(name, clazz);
        BotaniaAPI.registerMiniSubTile(name + "Chibi", miniClazz, name);
        BotaniaAPI.registerSubTileSignature(miniClazz, new BasicSignature(name + "Chibi"));
        BotaniaAPI.addSubTileToCreativeMenu(name);
    }

}
