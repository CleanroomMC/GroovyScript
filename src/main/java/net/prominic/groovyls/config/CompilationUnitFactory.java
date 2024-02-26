////////////////////////////////////////////////////////////////////////////////
// Copyright 2022 Prominic.NET, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License
//
// Author: Prominic.NET, Inc.
// No warranty of merchantability or fitness of any kind.
// Use this software at your own risk.
////////////////////////////////////////////////////////////////////////////////
package net.prominic.groovyls.config;

import com.cleanroommc.groovyscript.GroovyScript;
import net.prominic.groovyls.compiler.ILanguageServerContext;
import net.prominic.groovyls.compiler.control.GroovyLSCompilationUnit;
import net.prominic.groovyls.util.FileContentsTracker;
import org.codehaus.groovy.control.SourceUnit;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CompilationUnitFactory extends CompilationUnitFactoryBase {

    protected static final String FILE_EXTENSION_GROOVY = ".groovy";
    private final ILanguageServerContext languageServerContext;

    protected GroovyLSCompilationUnit compilationUnit;

    public CompilationUnitFactory(ILanguageServerContext languageServerContext) {
        this.languageServerContext = languageServerContext;
    }

    @Override
    public void invalidateCompilationUnit() {
        super.invalidateCompilationUnit();
        compilationUnit = null;
    }

    public GroovyLSCompilationUnit create(Path workspaceRoot, @Nullable URI context) {
        if (config == null) {
            config = getConfiguration();
        }

        if (classLoader == null) {
            classLoader = getClassLoader();
        }

        Set<URI> changedUris = null;
        if (compilationUnit == null) {
            compilationUnit = new GroovyLSCompilationUnit(config, null, classLoader, languageServerContext);
            // we don't care about changed URIs if there's no compilation unit yet
        } else {
            changedUris = languageServerContext.getFileContentsTracker().getChangedURIs();
        }

        var fileContentsTracker = languageServerContext.getFileContentsTracker();

        if (changedUris != null && !changedUris.isEmpty()) {
            compilationUnit.setClassLoader(classLoader);
            final Set<URI> urisToRemove = changedUris;
            List<SourceUnit> sourcesToRemove = new ArrayList<>();
            compilationUnit.iterator().forEachRemaining(sourceUnit -> {
                URI uri = sourceUnit.getSource().getURI();
                if (urisToRemove.contains(uri)) {
                    sourcesToRemove.add(sourceUnit);
                }
            });
            // if an URI has changed, we remove it from the compilation unit so
            // that a new version can be built from the updated source file
            compilationUnit.removeSources(sourcesToRemove);
        }

        if (workspaceRoot != null) {
            addDirectoryToCompilationUnit(workspaceRoot, compilationUnit, fileContentsTracker, changedUris);
        } else {
            final Set<URI> urisToAdd = changedUris;
            fileContentsTracker.getOpenURIs().forEach(uri -> {
                // if we're only tracking changes, skip all files that haven't
                // actually changed
                if (urisToAdd != null && !urisToAdd.contains(uri)) {
                    return;
                }
                String contents = fileContentsTracker.getContents(uri);
                addOpenFileToCompilationUnit(uri, contents, compilationUnit);
            });
        }

        return compilationUnit;
    }

    protected void addDirectoryToCompilationUnit(Path dirPath, GroovyLSCompilationUnit compilationUnit,
                                                 FileContentsTracker fileContentsTracker, Set<URI> changedUris) {
        try {
            if (Files.exists(dirPath)) {
                Files.walk(dirPath).forEach((filePath) -> {
                    if (!filePath.toString().endsWith(FILE_EXTENSION_GROOVY)) {
                        return;
                    }
                    URI fileURI = filePath.toUri();
                    if (!fileContentsTracker.isOpen(fileURI)) {
                        File file = filePath.toFile();
                        if (file.isFile()) {
                            if (changedUris == null || changedUris.contains(fileURI)) {
                                compilationUnit.addSource(file);
                            }
                        }
                    }
                });
            }

        } catch (IOException e) {
            GroovyScript.LOGGER.error("Failed to walk directory for source files: {}", dirPath);
        }
        fileContentsTracker.getOpenURIs().forEach(uri -> {
            Path openPath = Paths.get(uri);
            if (!openPath.normalize().startsWith(dirPath.normalize())) {
                return;
            }
            if (changedUris != null && !changedUris.contains(uri)) {
                return;
            }
            String contents = fileContentsTracker.getContents(uri);
            addOpenFileToCompilationUnit(uri, contents, compilationUnit);
        });
    }
}