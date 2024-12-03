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
package net.prominic.groovyls.compiler.control;

import groovy.lang.GroovyClassLoader;
import net.prominic.groovyls.compiler.ILanguageServerContext;
import net.prominic.groovyls.compiler.ast.ASTNodeVisitor;
import org.codehaus.groovy.ast.CompileUnit;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.tools.GroovyClass;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.security.CodeSource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GroovyLSCompilationUnit extends CompilationUnit {

    private final ILanguageServerContext languageServerContext;
    private @Nullable ASTNodeVisitor visitor;

    private @Nullable URI previousContext;

    public GroovyLSCompilationUnit(
            CompilerConfiguration config,
            CodeSource security,
            GroovyClassLoader loader,
            ILanguageServerContext languageServerContext) {
        super(config, security, loader);
        this.languageServerContext = languageServerContext;
        this.errorCollector = new LanguageServerErrorCollector(config);
    }

    public void setErrorCollector(LanguageServerErrorCollector errorCollector) {
        this.errorCollector = errorCollector;
    }

    public void removeSources(Collection<SourceUnit> sourceUnitsToRemove) {
        for (SourceUnit sourceUnit : sourceUnitsToRemove) {
            if (sourceUnit.getAST() != null) {
                List<String> sourceUnitClassNames = sourceUnit.getAST()
                        .getClasses()
                        .stream()
                        .map(classNode -> classNode.getName())
                        .collect(
                                Collectors.toList());
                final List<GroovyClass> generatedClasses = getClasses();
                generatedClasses.removeIf(groovyClass -> sourceUnitClassNames.contains(groovyClass.getName()));
            }
            sources.remove(sourceUnit.getName());
        }
        // keep existing modules from other source units
        List<ModuleNode> modules = ast.getModules();
        ast = new CompileUnit(this.classLoader, null, this.configuration);
        for (ModuleNode module : modules) {
            if (!sourceUnitsToRemove.contains(module.getContext())) {
                ast.addModule(module);
            }
        }
        LanguageServerErrorCollector lsErrorCollector = (LanguageServerErrorCollector) errorCollector;
        lsErrorCollector.clear();
    }

    public void removeSource(SourceUnit sourceUnit) {
        removeSources(Collections.singletonList(sourceUnit));
    }

    @Override
    public void compile() throws CompilationFailedException {
        // AST is completely built after the canonicalization phase
        // for code intelligence, we shouldn't need to go further
        // http://groovy-lang.org/metaprogramming.html#_compilation_phases_guide
        try {
            compile(Phases.CANONICALIZATION);
        } catch (CompilationFailedException e) {
            // ignore
        }
    }

    private ASTNodeVisitor visitAST(Set<URI> uris) {
        if (visitor == null || uris.isEmpty()) {
            visitor = new ASTNodeVisitor();
            visitor.visitCompilationUnit(this);
        } else {
            visitor.visitCompilationUnit(this, uris);
        }

        return visitor;
    }

    private ASTNodeVisitor compileAndVisitAST() {
        previousContext = null;

        compile();
        return visitAST(Collections.emptySet());
    }

    private ASTNodeVisitor compileAndVisitAST(@Nullable URI context) {
        if (context == null) {
            return compileAndVisitAST();
        }

        previousContext = context;

        compile();
        return visitAST(Collections.singleton(context));
    }

    public ASTNodeVisitor recompileAndVisitASTIfContextChanged(@Nullable URI context) {
        var isChanged = context == null || languageServerContext.getFileContentsTracker().getChangedURIs().contains(context);

        languageServerContext.getFileContentsTracker().resetChangedFiles();

        if ((previousContext == null || previousContext.equals(context)) && visitor != null && !isChanged) {
            return visitor;
        }

        if (context != null) {
            languageServerContext.getFileContentsTracker().forceChanged(context);
        }

        return compileAndVisitAST(context);
    }
}
