package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.util.text.TextFormatting;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.KnowledgeType;

import javax.annotation.Nullable;

public class Knowledge extends VirtualizedRegistry<KnowledgeType> {

    @Override
    public void onReload() {
        removeScripted().forEach(type -> BotaniaAPI.knowledgeTypes.remove(type.id, type));
        restoreFromBackup().forEach(type -> BotaniaAPI.knowledgeTypes.put(type.id, type));
    }

    public KnowledgeType add(String id, @Nullable TextFormatting formatting, boolean autoUnlock) {
        KnowledgeType type = new KnowledgeType(id, formatting != null ? formatting : TextFormatting.RESET, autoUnlock);
        add(type);
        return type;
    }

    public KnowledgeType add(String id, @Nullable TextFormatting formatting) {
        return add(id, formatting, false);
    }

    public void add(KnowledgeType type) {
        if (type == null) return;
        addScripted(type);
        BotaniaAPI.knowledgeTypes.put(type.id, type);
    }

    public SimpleObjectStream<KnowledgeType> streamKnowledgeTypes() {
        return new SimpleObjectStream<>(BotaniaAPI.knowledgeTypes.values());
    }
}
