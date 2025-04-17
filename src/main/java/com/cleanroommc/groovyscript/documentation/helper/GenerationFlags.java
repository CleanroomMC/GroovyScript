package com.cleanroommc.groovyscript.documentation.helper;

public class GenerationFlags {

    public static final boolean GENERATE_EXAMPLES = Boolean.getBoolean("groovyscript.generate_examples");
    public static final boolean GENERATE_WIKI = Boolean.getBoolean("groovyscript.generate_wiki");
    // Kills the game as soon as the wiki or examples are generated.
    public static final boolean GENERATE_AND_CRASH = Boolean.getBoolean("groovyscript.generate_and_crash");
    public static final boolean LOG_MISSING_LANG_KEYS = Boolean.getBoolean("groovyscript.log_missing_lang_keys");
}
