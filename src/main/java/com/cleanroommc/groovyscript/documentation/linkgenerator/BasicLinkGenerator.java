package com.cleanroommc.groovyscript.documentation.linkgenerator;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.Tags;
import com.cleanroommc.groovyscript.documentation.Documentation;

/**
 * Parses a class and converts it into a relative link to a website.
 * By default, converts files to a location on CleanroomMC's Groovyscript repository and uses Groovyscript's version,
 * but can be completely overwritten to link to anywhere.
 * <br>
 * An addon should replace {@link #domain()} and {@link #version()} with a link to their own repo, e.g.
 * <a href="https://github.com/WaitingIdly/GroovyScript/">https://github.com/WaitingIdly/GroovyScript/</a>
 * and the version of the addon.
 */
public class BasicLinkGenerator implements ILinkGenerator {

    public String id() {
        return GroovyScript.ID;
    }

    protected String domain() {
        return "https://github.com/CleanroomMC/GroovyScript/";
    }

    protected String version() {
        return "v" + Tags.VERSION;
    }

    protected String defaultBranch() {
        return "master";
    }

    protected String path() {
        return String.format("blob/%s/src/main/java/", Documentation.USE_DEFAULT_BRANCH ? defaultBranch() : version());
    }

    protected String internalLocation(String location) {
        return location.substring(location.indexOf(".jar!/") + 6);
    }

    protected String trimmedLocation(String location) {
        return LinkGeneratorHooks.END_SEGMENT.matcher(internalLocation(location)).replaceFirst("");
    }

    @Override
    public String convert(String location) {
        return domain() + path() + trimmedLocation(location) + extension();
    }

}
