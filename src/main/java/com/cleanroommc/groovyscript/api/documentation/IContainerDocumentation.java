package com.cleanroommc.groovyscript.api.documentation;

import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.sandbox.LoadStage;

import java.io.File;

/**
 * Implementing this interface on something extending {@link GroovyContainer}
 * will allow overriding the default documentation generation
 * for the container.
 */
public interface IContainerDocumentation {

    /**
     * Generate an example file for the given load stage.
     *
     * @param suggestedFile the default file that the example code is suggested to be generated in.
     * @param stage         the load stage the examples are being generated for.
     * @see com.cleanroommc.groovyscript.documentation.Exporter#generateExamples(File, LoadStage, GroovyContainer)
     */
    void generateExamples(File suggestedFile, LoadStage stage);

    /**
     * Generate pages for the wiki in this method.
     *
     * @param suggestedFolder the default folder that the wiki is suggested to be generated in.
     * @see com.cleanroommc.groovyscript.documentation.Exporter#generateWiki(File, GroovyContainer)
     */
    void generateWiki(File suggestedFolder);
}
