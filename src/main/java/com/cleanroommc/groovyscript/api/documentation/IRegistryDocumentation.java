package com.cleanroommc.groovyscript.api.documentation;

import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.documentation.helper.ContainerHolder;
import com.cleanroommc.groovyscript.documentation.helper.LinkIndex;
import com.cleanroommc.groovyscript.documentation.helper.MarkdownSection;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * Implementing this interface on a registry will allow
 * customizing the wiki and example generation that occurs.
 * This annotation has no effect unless one of its default methods is implemented.
 * <p>
 * The default registry, {@link com.cleanroommc.groovyscript.documentation.Registry Registry},
 * uses this annotation with the goal of consolidating logic.
 *
 * @see com.cleanroommc.groovyscript.documentation.Registry Registry
 */
public interface IRegistryDocumentation extends INamed {

    /**
     * Generate the various files for the wiki - these are almost always
     * markdown files inside the {@code suggestedFolder},
     * and the file linked by adding it to {@code linkIndex}.
     * <p>
     * In most situations simply adding it to the default section via {@link LinkIndex#add(String)}
     * suffices, but for certain reasons a separate section may be desired.
     * If so, the section must be created via {@link LinkIndex#register(String, MarkdownSection)}
     * and can then be added to directly {@link LinkIndex#add(String, String)}.
     *
     * @param container       the container the registry is part of
     * @param suggestedFolder the suggested folder for the wiki files to be generated within.
     *                        This is only a suggestion, and can be ignored
     * @param linkIndex      each category will generate a header, description, and some number of entries for the index page
     * @see MarkdownSection
     * @see LinkIndex
     * @see com.cleanroommc.groovyscript.documentation.Exporter#writeNormalWikiFile
     */
    default void generateWiki(ContainerHolder container, File suggestedFolder, LinkIndex linkIndex) {}

    /**
     * Return a String that will be added to the example groovy file
     * depending on the {@code loadStage}.
     *
     * @param container the container the registry is part of
     * @param loadStage the target load stage, which controls which file the output is printed to
     * @param imports   a list of classes that must be imported. this should only contain the class name,
     *                  and entries should not be removed
     * @return the text of the examples, with an empty string indicating no examples were generated.
     * If no examples were generated among all the suppliers, the relevant file will not be created.
     */
    default @NotNull String generateExamples(ContainerHolder container, LoadStage loadStage, List<String> imports) {
        return "";
    }

    /**
     * Allows skipping creating the default wiki {@link com.cleanroommc.groovyscript.documentation.Registry Registry}.
     * This could be used to either disable documentation of a class, regardless of annotations,
     * replace the existing documentation by implementing {@link #generateWiki(ContainerHolder, File, LinkIndex)},
     * or to provide both the default generation and custom generation.
     *
     * @param container the container the registry is part of
     * @return if the normal wiki creation used by {@link com.cleanroommc.groovyscript.documentation.Registry Registry}
     * should occur, regardless of if {@link #generateWiki(ContainerHolder, File, LinkIndex)} is implemented
     */
    default boolean skipDefaultWiki(ContainerHolder container) {
        return false;
    }

    /**
     * Allows skipping using the default example {@link com.cleanroommc.groovyscript.documentation.Registry Registry}.
     * This could be used to either disable example generation of a class, regardless of annotations,
     * replace the existing examples by implementing {@link #generateExamples(ContainerHolder, LoadStage, List)},
     * or to provide both the default generation and custom generation.
     *
     * @param container the container the registry is part of
     * @return if the normal example creation used by {@link com.cleanroommc.groovyscript.documentation.Registry Registry}
     * should occur, regardless of if {@link #generateExamples(ContainerHolder, LoadStage, List)} is implemented
     */
    default boolean skipDefaultExamples(ContainerHolder container) {
        return false;
    }

    /**
     * Priority of the registry, relative to other registries of the same container.
     * Priorities sort entries such that lowest is first, then by the natural order of {@link INamed#getName()}.
     *
     * @return the registry priority, default {@code 100}
     * @see com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription#priority()
     */
    default int priority() {
        return 100;
    }
}
