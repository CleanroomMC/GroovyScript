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

    public void registerFlowerWithMini(String name, Class<? extends SubTileEntity> clazz) {
        registerFlower(name, clazz);
        Class<?>[] subClasses = clazz.getDeclaredClasses();
        int l = subClasses.length;

        for (int i = 0; i < l; i++) {
            Class<?> subClass = subClasses[i];
            if (subClass.getSimpleName().equals("Mini") && SubTileEntity.class.isAssignableFrom(subClass)) {
                BotaniaAPI.registerMiniSubTile(name + "Chibi", (Class<? extends SubTileEntity>) subClass, name);
                BotaniaAPI.registerSubTileSignature((Class<? extends SubTileEntity>) subClass, new BasicSignature(name + "Chibi"));
                BotaniaAPI.addSubTileToCreativeMenu(name);
                break;
            }
        }
    }
}
