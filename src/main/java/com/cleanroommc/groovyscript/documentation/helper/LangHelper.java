package com.cleanroommc.groovyscript.documentation.helper;

import com.cleanroommc.groovyscript.api.GroovyLog;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.resources.I18n;

import java.util.Set;

public class LangHelper {

    private static final Set<String> MISSING_LANG_KEYS = new ObjectOpenHashSet<>();
    private static final CharSet PUNCTUATION_CHARACTERS;

    static {
        PUNCTUATION_CHARACTERS = new CharOpenHashSet();
        PUNCTUATION_CHARACTERS.add('.');
        PUNCTUATION_CHARACTERS.add('!');
        PUNCTUATION_CHARACTERS.add('?');
        PUNCTUATION_CHARACTERS.add(',');
        PUNCTUATION_CHARACTERS.add(':');
        PUNCTUATION_CHARACTERS.add(';');
    }

    public static void logAnyMissingKeys() {
        if (MISSING_LANG_KEYS.isEmpty()) return;
        GroovyLog.Msg log = GroovyLog.msg("Missing the following localization keys");
        MISSING_LANG_KEYS.stream().sorted().forEach(log::add);
        log.debug().post();
        MISSING_LANG_KEYS.clear();
    }

    public static String translate(String translateKey, Object... parameters) {
        if (GenerationFlags.LOG_MISSING_LANG_KEYS && !I18n.hasKey(translateKey)) {
            MISSING_LANG_KEYS.add(translateKey);
        }
        return I18n.format(translateKey, parameters);
    }

    public static String ensurePeriod(String string) {
        if (string.isEmpty()) return "";
        return PUNCTUATION_CHARACTERS.contains(string.charAt(string.length() - 1)) ? string : string + ".";
    }
}
