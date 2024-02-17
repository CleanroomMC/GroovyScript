package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.util.text.TextFormatting;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.KnowledgeType;

import javax.annotation.Nullable;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES,
        priority = 2000
)
public class Knowledge extends VirtualizedRegistry<KnowledgeType> {

    @Override
    public void onReload() {
        removeScripted().forEach(type -> BotaniaAPI.knowledgeTypes.remove(type.id, type));
        restoreFromBackup().forEach(type -> BotaniaAPI.knowledgeTypes.put(type.id, type));
    }

    @MethodDescription(description = "groovyscript.wiki.botania.knowledge.add0", type = MethodDescription.Type.ADDITION, example = @Example(value = "'newType', TextFormatting.RED, true", imports = "net.minecraft.util.text.TextFormatting", def = "newType"))
    public KnowledgeType add(String id, @Nullable TextFormatting formatting, boolean autoUnlock) {
        KnowledgeType type = new KnowledgeType(id, formatting != null ? formatting : TextFormatting.RESET, autoUnlock);
        add(type);
        return type;
    }

    @MethodDescription(description = "groovyscript.wiki.botania.knowledge.add1", type = MethodDescription.Type.ADDITION)
    public KnowledgeType add(String id, @Nullable TextFormatting formatting) {
        return add(id, formatting, false);
    }

    public void add(KnowledgeType type) {
        if (type == null) return;
        addScripted(type);
        BotaniaAPI.knowledgeTypes.put(type.id, type);
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<KnowledgeType> streamKnowledgeTypes() {
        return new SimpleObjectStream<>(BotaniaAPI.knowledgeTypes.values());
    }
}
