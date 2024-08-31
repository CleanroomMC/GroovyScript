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
package net.prominic.groovyls.providers;

import com.cleanroommc.groovyscript.mapper.ObjectMapper;
import com.cleanroommc.groovyscript.mapper.ObjectMapperManager;
import com.cleanroommc.groovyscript.server.Completions;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodParameterInfo;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.compiler.util.GroovyASTUtils;
import net.prominic.groovyls.compiler.util.GroovyReflectionUtils;
import net.prominic.groovyls.util.CompletionItemFactory;
import net.prominic.groovyls.util.GroovyLSUtils;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CompletionProvider extends DocProvider {

    private static final String[] keywords = {"def", "assert", "if", "for", "else", "while", "switch", "case", "break", "continue",
                                              "return", "transient", "import", "class", "extends", "implements", "enum", "try", "catch",
                                              "finally", "throw", "new", "in", "as", "instanceof", "super", "this", "null", "true", "false",
                                              "void", "byte", "short", "int", "long", "float", "double", "boolean", "private", "public",
                                              "protected", "abstract", "char", "const", "default", "do", "final", "goto", "interface",
                                              "native", "non-sealed", "package", "permits", "record", "sealed", "static", "strictfp",
                                              "synchronized", "threadsafe", "throws", "trait", "var", "yields"};

    public CompletionProvider(URI doc, ASTContext astContext) {
        super(doc, astContext);
    }

    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> provideCompletionFuture(TextDocumentIdentifier textDocument,
                                                                                                   Position position,
                                                                                                   CompletionContext context) {
        return future(Either.forRight(provideCompletion(textDocument, position, context)));
    }

    public CompletionList provideCompletion(TextDocumentIdentifier textDocument, Position position, CompletionContext context) {
        Completions items = new Completions(1000);

        ASTNode offsetNode = astContext.getVisitor().getNodeAtLineAndColumn(doc, position.getLine(), position.getCharacter());
        if (offsetNode == null || populateItemsFromNode(position, offsetNode, items)) {
            populateKeywords(items);
        }
        return new CompletionList(items.reachedLimit(), items);
    }

    private void populateKeywords(Completions items) {
        items.addAll(keywords, s -> {
            var item = new CompletionItem(s);
            item.setKind(CompletionItemKind.Keyword);
            item.setSortText("zzz" + s);
            return item;
        });
    }

    private boolean populateItemsFromNode(Position position, ASTNode offsetNode, Completions items) {
        ASTNode parentNode = astContext.getVisitor().getParent(offsetNode);

        if (offsetNode instanceof PropertyExpression) {
            populateItemsFromPropertyExpression((PropertyExpression) offsetNode, position, items);
        } else if (parentNode instanceof PropertyExpression) {
            populateItemsFromPropertyExpression((PropertyExpression) parentNode, position, items);
        } else if (offsetNode instanceof MethodCallExpression) {
            populateItemsFromMethodCallExpression((MethodCallExpression) offsetNode, position, items);
        } else if (offsetNode instanceof ConstructorCallExpression) {
            populateItemsFromConstructorCallExpression((ConstructorCallExpression) offsetNode, position, items);
        } else if (offsetNode instanceof VariableExpression) {
            populateItemsFromVariableExpression((VariableExpression) offsetNode, position, items);
        } else if (parentNode instanceof MethodCallExpression) {
            populateItemsFromMethodCallExpression((MethodCallExpression) parentNode, position, items);
        } else if (offsetNode instanceof ImportNode) {
            populateItemsFromImportNode((ImportNode) offsetNode, position, items);
        } else if (offsetNode instanceof ClassNode) {
            populateItemsFromClassNode((ClassNode) offsetNode, position, items);
        } else if (offsetNode instanceof MethodNode) {
            populateItemsFromScope(offsetNode, "", items);
        } else if (offsetNode instanceof Statement) {
            populateItemsFromScope(offsetNode, "", items);
        } else if (offsetNode instanceof ClosureExpression) {
            populateItemsFromScope(offsetNode, "", items);
        } else if (offsetNode instanceof StaticMethodCallExpression) {
            populateItemsFromStaticMethodCallExpression((StaticMethodCallExpression) offsetNode, position, items);
        } else if (offsetNode instanceof ConstantExpression) {
            return populateItemsFromConstantExpression((ConstantExpression) offsetNode, parentNode, items);
        }
        return true;
    }

    private boolean populateItemsFromConstantExpression(ConstantExpression node, ASTNode parent, Completions items) {
        if (node.getType().getTypeClass() == String.class) {
            ASTNode parentParent = astContext.getVisitor().getParent(parent);
            if (parentParent instanceof MethodCallExpression expr &&
                    expr.getArguments() instanceof ArgumentListExpression args &&
                    !args.getExpressions().isEmpty()) {
                ObjectMapper<?> goh = GroovyASTUtils.getGohOfNode(expr, astContext);
                if (goh != null && goh.getCompleter() != null) {
                    int index = -1;
                    for (int i = 0; i < args.getExpressions().size(); i++) {
                        if (args.getExpression(i) == node) {
                            index = i;
                            break;
                        }
                    }
                    goh.getCompleter().complete(index, items);
                }
            }
            return false; // don't complete keyword in strings
        }
        return true;
    }

    private void populateItemsFromStaticMethodCallExpression(StaticMethodCallExpression methodCallExpr, Position position,
                                                             Completions items) {
        Set<String> existingNames = new ObjectOpenHashSet<>();
        populateItemsFromGlobalScope(methodCallExpr.getMethod(), existingNames, items);
    }

    private void populateItemsFromPropertyExpression(PropertyExpression propExpr, Position position, Completions items) {
        Range propertyRange = GroovyLSUtils.astNodeToRange(propExpr.getProperty());
        if (propertyRange == null) return;
        String memberName = getMemberName(propExpr.getPropertyAsString(), propertyRange, position);
        populateItemsFromExpression(propExpr.getObjectExpression(), memberName, items);
    }

    private void populateItemsFromMethodCallExpression(MethodCallExpression methodCallExpr, Position position, Completions items) {
        Range methodRange = GroovyLSUtils.astNodeToRange(methodCallExpr.getMethod());
        if (methodRange == null) return;
        String memberName = getMemberName(methodCallExpr.getMethodAsString(), methodRange, position);
        populateItemsFromExpression(methodCallExpr.getObjectExpression(), memberName, items);
    }

    private void populateItemsFromImportNode(ImportNode importNode, Position position, Completions items) {
        Range importRange = GroovyLSUtils.astNodeToRange(importNode);
        if (importRange == null) return;
        // skip the "import " at the beginning
        importRange.setStart(new Position(importRange.getEnd().getLine(),
                                          importRange.getEnd().getCharacter() - importNode.getType().getName().length()));
        String importText = getMemberName(importNode.getType().getName(), importRange, position);

        ModuleNode enclosingModule = getModule();
        if (enclosingModule == null) return;
        String enclosingPackageName = enclosingModule.getPackageName();
        items.addAll(astContext.getVisitor().getClassNodes(), classNode -> {
            String packageName = classNode.getPackageName();
            if (packageName == null || packageName.isEmpty() || packageName.equals(enclosingPackageName)) {
                return null;
            }
            String className = classNode.getName();
            String classNameWithoutPackage = classNode.getNameWithoutPackage();
            if ((!className.startsWith(importText) && !classNameWithoutPackage.startsWith(importText)) ||
                    GroovyLSUtils.hasImport(enclosingModule, className)) {
                return null;
            }
            CompletionItem item = CompletionItemFactory.createCompletion(classNode, classNode.getName(), astContext);
            item.setTextEdit(Either.forLeft(new TextEdit(importRange, classNode.getName())));
            if (classNode.getNameWithoutPackage().startsWith(importText)) {
                item.setSortText(classNode.getNameWithoutPackage());
            }
            return item;
        });

        // scan packages
        items.addAll(astContext.getLanguageServerContext().getScanResult().getPackageInfo(), p -> {
            String packageName = p.getName();
            if (!packageName.startsWith(importText)) return null;
            CompletionItem item = CompletionItemFactory.createCompletion(CompletionItemKind.Module, p.getName());
            item.setTextEdit(Either.forLeft(new TextEdit(importRange, p.getName())));
            return item;
        });

        // scan all classes
        items.addAll(astContext.getLanguageServerContext().getScanResult().getAllClasses(), c -> {
            String packageName = c.getPackageName();
            if (packageName == null || packageName.isEmpty() || packageName.equals(enclosingPackageName)) {
                return null;
            }
            String className = c.getName();
            String classNameWithoutPackage = c.getSimpleName();
            if ((!className.startsWith(importText) && !classNameWithoutPackage.startsWith(importText)) ||
                    !GroovyLSUtils.hasImport(enclosingModule, className)) {
                return null;
            }
            CompletionItem item = CompletionItemFactory.createCompletion(classInfoToCompletionItemKind(c), c.getName());
            item.setTextEdit(Either.forLeft(new TextEdit(importRange, c.getName())));
            if (c.getSimpleName().startsWith(importText)) {
                item.setSortText(c.getSimpleName());
            }
            return item;
        });
    }

    private void populateItemsFromClassNode(ClassNode classNode, Position position, Completions items) {
        ASTNode parentNode = astContext.getVisitor().getParent(classNode);
        if (!(parentNode instanceof ClassNode parentClassNode)) return;
        Range classRange = GroovyLSUtils.astNodeToRange(classNode);
        if (classRange == null) return;
        String className = getMemberName(classNode.getUnresolvedName(), classRange, position);
        if (classNode.equals(parentClassNode.getUnresolvedSuperClass())) {
            populateTypes(classNode, className, new HashSet<>(), true, false, false, items);
        } else if (Arrays.asList(parentClassNode.getUnresolvedInterfaces()).contains(classNode)) {
            populateTypes(classNode, className, new HashSet<>(), false, true, false, items);
        }
    }

    private void populateItemsFromConstructorCallExpression(ConstructorCallExpression constructorCallExpr, Position position,
                                                            Completions items) {
        Range typeRange = GroovyLSUtils.astNodeToRange(constructorCallExpr.getType());
        if (typeRange == null) return;
        String typeName = getMemberName(constructorCallExpr.getType().getNameWithoutPackage(), typeRange, position);
        populateTypes(constructorCallExpr, typeName, new HashSet<>(), true, false, false, items);
    }

    private void populateItemsFromVariableExpression(VariableExpression varExpr, Position position, Completions items) {
        Range varRange = GroovyLSUtils.astNodeToRange(varExpr);
        if (varRange == null) return;
        String memberName = getMemberName(varExpr.getName(), varRange, position);
        populateItemsFromScope(varExpr, memberName, items);
    }

    private void populateItemsFromPropertiesAndFields(List<PropertyNode> properties, List<FieldNode> fields, String memberNamePrefix,
                                                      Set<String> existingNames, Completions items) {
        items.addAll(properties, p -> {
            String name = p.getName();
            if (!name.startsWith(memberNamePrefix) || existingNames.contains(name)) return null;
            existingNames.add(name);
            CompletionItem item = CompletionItemFactory.createCompletion(p, p.getName(), astContext);
            if (!p.isDynamicTyped()) {
                var details = new CompletionItemLabelDetails();
                details.setDetail("  " + p.getType().getNameWithoutPackage());
                item.setLabelDetails(details);
            }
            return item;
        });
        items.addAll(fields, f -> {
            String name = f.getName();
            if (!name.startsWith(memberNamePrefix) || existingNames.contains(name)) return null;
            existingNames.add(name);
            CompletionItem item = CompletionItemFactory.createCompletion(f, f.getName(), astContext);
            if (!f.isDynamicTyped()) {
                var details = new CompletionItemLabelDetails();
                details.setDetail("  " + f.getType().getNameWithoutPackage());
                item.setLabelDetails(details);
            }
            return item;
        });
    }

    private void populateItemsFromMethods(List<MethodNode> methods, String memberNamePrefix, Set<String> existingNames, Completions items) {
        items.addAll(methods, method -> {
            String name = method.getName();
            if (!name.startsWith(memberNamePrefix) || existingNames.contains(name)) return null;
            existingNames.add(name);
            if (method.getDeclaringClass().isResolved() &&
                    (method.getModifiers() & GroovyASTUtils.EXPANSION_MARKER) == 0 &&
                    GroovyReflectionUtils.resolveMethodFromMethodNode(method, astContext) == null) {
                return null;
            }

            CompletionItem item = CompletionItemFactory.createCompletion(method, method.getName(), astContext);
            item.setLabelDetails(getMethodNodeDetails(method));
            return item;
        });
    }

    @NotNull
    private static CompletionItemLabelDetails getMethodNodeDetails(MethodNode method) {
        var detailBuilder = new StringBuilder();
        detailBuilder.append("(");
        var parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            var parameter = parameters[i];
            detailBuilder.append(parameter.isDynamicTyped() ? "?" : parameter.getType().getNameWithoutPackage());
            if (i < parameters.length - 1) {
                detailBuilder.append(",");
            }
        }
        detailBuilder.append(") -> ");
        detailBuilder.append(method.getReturnType().getNameWithoutPackage());

        var details = new CompletionItemLabelDetails();
        details.setDetail(detailBuilder.toString());
        return details;
    }

    @NotNull
    private static CompletionItemLabelDetails getMethodInfoDetails(MethodInfo methodInfo) {
        var detailBuilder = new StringBuilder();
        detailBuilder.append("(");
        MethodParameterInfo[] info = methodInfo.getParameterInfo();
        for (int i = 0; i < info.length; i++) {
            var parameterInfo = info[i];
            detailBuilder.append(parameterInfo.getTypeSignatureOrTypeDescriptor().toStringWithSimpleNames());
            if (i < info.length - 1) {
                detailBuilder.append(",");
            }
        }
        detailBuilder.append(") -> ");
        detailBuilder.append(methodInfo.getTypeSignatureOrTypeDescriptor().getResultType().toStringWithSimpleNames());

        var details = new CompletionItemLabelDetails();
        details.setDetail(detailBuilder.toString());
        return details;
    }


    private void populateItemsFromExpression(Expression leftSide, String memberNamePrefix, Completions items) {
        Set<String> existingNames = new ObjectOpenHashSet<>();

        ClassNode classNode = GroovyASTUtils.getTypeOfNode(leftSide, astContext);
        if (classNode == null) return;
        GroovyASTUtils.fillClassNode(classNode);
        List<PropertyNode> properties = GroovyASTUtils.getPropertiesForLeftSideOfPropertyExpression(classNode, leftSide, astContext);
        List<FieldNode> fields = GroovyASTUtils.getFieldsForLeftSideOfPropertyExpression(classNode, leftSide, astContext);
        populateItemsFromPropertiesAndFields(properties, fields, memberNamePrefix, existingNames, items);

        List<MethodNode> methods = GroovyASTUtils.getMethodsForLeftSideOfPropertyExpression(classNode, leftSide, astContext);
        populateItemsFromMethods(methods, memberNamePrefix, existingNames, items);
    }

    private void populateItemsFromGlobalScope(String memberNamePrefix, Set<String> existingNames, Completions items) {
        items.addAll(astContext.getLanguageServerContext().getSandbox().getBindings().entrySet(), entry -> {
            String name = entry.getKey();
            if (!name.startsWith(memberNamePrefix) || existingNames.contains(name)) return null;
            existingNames.add(name);
            if (entry.getValue() instanceof ObjectMapper<?> goh) {
                for (MethodNode method : goh.getMethodNodes()) {
                    var item = CompletionItemFactory.createCompletion(method, goh.getName(), astContext);
                    item.setLabelDetails(getMethodNodeDetails(method));
                    // TODO
                    items.add(item);
                }
                return null;
            } else if (entry.getValue() instanceof Closure<?> closure) {
                MethodNode method = GroovyASTUtils.methodNodeOfClosure(name, closure);
                var item = CompletionItemFactory.createCompletion(method, name, astContext);
                item.setLabelDetails(getMethodNodeDetails(method));
                return item;
            } else {
                var item = CompletionItemFactory.createCompletion(CompletionItemKind.Variable, name);
                item.setDetail("(global scope)");
                return item;
            }
        });

        ModuleNode enclosingModule = getModule();
        if (enclosingModule == null) return;
        items.addAll(enclosingModule.getStaticStarImports().values(), in -> {
            String name = in.getClassName();
            if (name == null) return null;
            // TODO use meta class?
            ClassInfo info = astContext.getLanguageServerContext().getScanResult().getClassInfo(name);
            if (info == null) return null;
            for (MethodInfo m : info.getMethodInfo()) {
                if (!m.isStatic() || !m.getName().startsWith(memberNamePrefix) || existingNames.contains(m.getName())) continue;
                existingNames.add(m.getName());
                if (GroovyReflectionUtils.resolveMethodFromMethodInfo(m, astContext) == null) continue;
                var item = CompletionItemFactory.createCompletion(CompletionItemKind.Method, m.getName());
                item.setLabelDetails(getMethodInfoDetails(m));
                items.add(item);
            }
            for (FieldInfo m : info.getFieldInfo()) {
                if (!m.isStatic() || !m.getName().startsWith(memberNamePrefix) || existingNames.contains(m.getName())) continue;
                existingNames.add(m.getName());
                var item = CompletionItemFactory.createCompletion(CompletionItemKind.Field, m.getName());
                items.add(item);
            }
            return null;
        });
    }

    private void populateItemsFromVariableScope(VariableScope variableScope, String memberNamePrefix, Set<String> existingNames,
                                                Completions items) {
        //populateItemsFromGameObjects(memberNamePrefix, existingNames, items);
        populateItemsFromGlobalScope(memberNamePrefix, existingNames, items);
        items.addAll(variableScope.getDeclaredVariables().values(), variable -> {
            String variableName = variable.getName();
            if (!variableName.startsWith(memberNamePrefix) || existingNames.contains(variableName)) return null;
            var item = CompletionItemFactory.createCompletion((ASTNode) variable, variable.getName(), astContext);
            if (!variable.isDynamicTyped()) {
                item.setDetail(variable.getType().getName());
            }
            return item;
        });
    }

    private void populateItemsFromScope(ASTNode node, String namePrefix, Completions items) {
        Set<String> existingNames = new ObjectOpenHashSet<>();
        ASTNode current = node;
        ASTNode child = null;
        boolean isInClosure = false;
        int argIndex = -1;
        while (current != null) {
            if (current instanceof ClassNode classNode) {
                populateItemsFromPropertiesAndFields(classNode.getProperties(), classNode.getFields(), namePrefix, existingNames, items);
                populateItemsFromMethods(classNode.getMethods(), namePrefix, existingNames, items);
            } else if (current instanceof MethodNode methodNode) {
                populateItemsFromVariableScope(methodNode.getVariableScope(), namePrefix, existingNames, items);
            } else if (current instanceof BlockStatement block) {
                populateItemsFromVariableScope(block.getVariableScope(), namePrefix, existingNames, items);
            } else if (current instanceof ClosureExpression ce) {
                isInClosure = true;
            } else if (current instanceof ArgumentListExpression ale) {
                if (isInClosure && child instanceof ClosureExpression) {
                    argIndex = ale.getExpressions().indexOf(child);
                }
            } else if (current instanceof MethodCall mce) {
                if (argIndex >= 0) {
                    MethodNode method = GroovyASTUtils.getMethodFromCallExpression(mce, astContext);
                    if (method != null && method.getParameters().length > argIndex) {
                        Parameter parameter = method.getParameters()[argIndex];
                        // we are currently in a method_call(closure) structure
                        // try to find a DelegatesTo
                        for (AnnotationNode ann : parameter.getAnnotations(ClassHelper.makeCached(DelegatesTo.class))) {
                            Expression valueExpr = ann.getMember("value");
                            ClassNode classNode = null;
                            if (valueExpr instanceof ClassExpression classExpr) {
                                classNode = classExpr.getType();
                            } else {
                                valueExpr = ann.getMember("type");
                                if (valueExpr instanceof ConstantExpression ce) {
                                    try {
                                        classNode = ClassHelper.makeCached(Class.forName(ce.getText()));
                                    } catch (ClassNotFoundException ignored) {
                                    }
                                }
                            }
                            if (classNode != null) {
                                populateItemsFromPropertiesAndFields(classNode.getProperties(), classNode.getFields(), namePrefix,
                                                                     existingNames, items);
                                populateItemsFromMethods(classNode.getMethods(), namePrefix, existingNames, items);
                            }
                        }
                    }
                }
            }
            if (current instanceof VariableExpression || current instanceof StaticMethodCallExpression) {
                //populateItemsFromGameObjects(namePrefix, existingNames, items);
                populateItemsFromGlobalScope(namePrefix, existingNames, items);
            }
            child = current;
            current = astContext.getVisitor().getParent(current);
        }
        populateTypes(node, namePrefix, existingNames, items);
    }

    private void populateTypes(ASTNode offsetNode, String namePrefix, Set<String> existingNames, Completions items) {
        populateTypes(offsetNode, namePrefix, existingNames, true, true, true, items);
    }

    private void populateTypes(ASTNode offsetNode, String namePrefix, Set<String> existingNames, boolean includeClasses,
                               boolean includeInterfaces, boolean includeEnums, Completions items) {
        Range addImportRange = GroovyASTUtils.findAddImportRange(offsetNode, astContext);

        ModuleNode enclosingModule = getModule();
        String enclosingPackageName = enclosingModule.getPackageName();
        items.addAll(astContext.getVisitor().getClassNodes(), classNode -> {
            if (!includeEnums && classNode.isEnum()) return null;
            if (!includeInterfaces && classNode.isInterface()) return null;
            if (!includeClasses && (!classNode.isInterface() && !classNode.isEnum())) return null;
            String classNameWithoutPackage = classNode.getNameWithoutPackage();
            String className = classNode.getName();
            if (!classNameWithoutPackage.startsWith(namePrefix) || existingNames.contains(className)) return null;
            existingNames.add(className);
            String packageName = classNode.getPackageName();
            CompletionItem item = CompletionItemFactory.createCompletion(classNode, classNode.getNameWithoutPackage(), astContext);
            item.setDetail(packageName);
            if (packageName != null && !packageName.equals(enclosingPackageName) && !GroovyLSUtils.hasImport(enclosingModule, className)) {
                List<TextEdit> additionalTextEdits = new ArrayList<>();
                TextEdit addImportEdit = createAddImportTextEdit(className, addImportRange);
                additionalTextEdits.add(addImportEdit);
                item.setAdditionalTextEdits(additionalTextEdits);
            }
            return item;
        });

        items.addAll(astContext.getLanguageServerContext().getScanResult().getAllClasses(), classInfo -> {
            if (!includeEnums && classInfo.isEnum()) return null;
            if (!includeInterfaces && classInfo.isInterface()) return null;
            if (!includeClasses && (!classInfo.isInterface() && !classInfo.isEnum())) return null;
            String className = classInfo.getName();
            String classNameWithoutPackage = classInfo.getSimpleName();
            if (!classNameWithoutPackage.startsWith(namePrefix) || existingNames.contains(className)) return null;
            existingNames.add(className);
            String packageName = classInfo.getPackageName();
            CompletionItem item = CompletionItemFactory.createCompletion(classInfoToCompletionItemKind(classInfo),
                                                                         classInfo.getSimpleName());
            item.setDetail(packageName);
            if (packageName != null && !packageName.equals(enclosingPackageName) && !GroovyLSUtils.hasImport(enclosingModule, className)) {
                List<TextEdit> additionalTextEdits = new ArrayList<>();
                TextEdit addImportEdit = createAddImportTextEdit(className, addImportRange);
                additionalTextEdits.add(addImportEdit);
                item.setAdditionalTextEdits(additionalTextEdits);
            }
            return item;
        });
    }

    private String getMemberName(String memberName, Range range, Position position) {
        if (position.getLine() == range.getStart().getLine() && position.getCharacter() > range.getStart().getCharacter()) {
            int length = position.getCharacter() - range.getStart().getCharacter();
            if (length > 0 && length <= memberName.length()) {
                return memberName.substring(0, length).trim();
            }
        }
        return "";
    }

    private CompletionItemKind classInfoToCompletionItemKind(ClassInfo classInfo) {
        if (classInfo.isInterface()) {
            return CompletionItemKind.Interface;
        }
        if (classInfo.isEnum()) {
            return CompletionItemKind.Enum;
        }
        return CompletionItemKind.Class;
    }

    private TextEdit createAddImportTextEdit(String className, Range range) {
        return new TextEdit(range, "import " + className + "\n");
    }
}
