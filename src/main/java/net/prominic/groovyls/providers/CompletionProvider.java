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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.cleanroommc.groovyscript.api.IDynamicGroovyProperty;
import com.cleanroommc.groovyscript.gameobjects.GameObjectHandlerManager;
import io.github.classgraph.ClassMemberInfo;
import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.compiler.util.DocUtils;
import net.prominic.groovyls.util.URIUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.eclipse.lsp4j.CompletionContext;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.PackageInfo;
import net.prominic.groovyls.compiler.util.GroovyASTUtils;
import net.prominic.groovyls.compiler.util.GroovydocUtils;
import net.prominic.groovyls.util.GroovyLanguageServerUtils;

public class CompletionProvider {

    private final ASTContext astContext;
    private int maxItemCount = 1000;
    private boolean isIncomplete = false;

    public CompletionProvider(ASTContext astContext) {
        this.astContext = astContext;
    }

    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> provideCompletion(
            TextDocumentIdentifier textDocument, Position position, CompletionContext context) {
        URI uri = URIUtils.toUri(textDocument.getUri());
        ASTNode offsetNode = astContext.getVisitor().getNodeAtLineAndColumn(uri, position.getLine(), position.getCharacter());
        if (offsetNode == null) {
            return CompletableFuture.completedFuture(Either.forLeft(Collections.emptyList()));
        }
        ASTNode parentNode = astContext.getVisitor().getParent(offsetNode);

        isIncomplete = false;
        List<CompletionItem> items = new ArrayList<>();

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
        } else if (offsetNode instanceof StaticMethodCallExpression) {
            populateItemsFromStaticMethodCallExpression((StaticMethodCallExpression) offsetNode, position, items);
        }

        if (isIncomplete) {
            return CompletableFuture.completedFuture(Either.forRight(new CompletionList(true, items)));
        }
        return CompletableFuture.completedFuture(Either.forLeft(items));
    }

    private void populateItemsFromStaticMethodCallExpression(StaticMethodCallExpression methodCallExpr, Position position, List<CompletionItem> items) {
        Set<String> existingNames = new HashSet<>();

        if (methodCallExpr.getOwnerType().getTypeClass().equals(GameObjectHandlerManager.class) && methodCallExpr.getMethod().equals("getGameObject")) {
            // expression like item()

            var argumentsExpr = methodCallExpr.getArguments();
            if (argumentsExpr instanceof ArgumentListExpression) {
                var firstArgumentExpr = ((ArgumentListExpression) argumentsExpr).getExpression(0);

                if (firstArgumentExpr instanceof ConstantExpression) {
                    var memberNamePrefix = ((ConstantExpression) firstArgumentExpr).getValue().toString();

                    populateItemsFromGameObjects(memberNamePrefix, existingNames, items);
                }
            }
        }

        populateItemsFromGlobalScope(methodCallExpr.getMethod(), existingNames, items);
    }

    private static void populateItemsFromGameObjects(String memberNamePrefix,
                                                     Set<String> existingNames, List<CompletionItem> items) {
        var completionItems = GameObjectHandlerManager.getGameObjectHandlers().stream()
                .filter(handler -> {
                    if (handler.startsWith(memberNamePrefix) && !existingNames.contains(handler)) {
                        existingNames.add(handler);
                        return true;
                    }
                    return false;
                })
                .map(handler -> {
                    var completionItem = new CompletionItem();
                    completionItem.setLabel(handler);
                    completionItem.setKind(CompletionItemKind.Method);
                    return completionItem;
                }).collect(Collectors.toList());

        items.addAll(completionItems);
    }

    private void populateItemsFromPropertyExpression(PropertyExpression propExpr, Position position,
                                                     List<CompletionItem> items) {
        Range propertyRange = GroovyLanguageServerUtils.astNodeToRange(propExpr.getProperty());
        if (propertyRange == null) {
            return;
        }
        String memberName = getMemberName(propExpr.getPropertyAsString(), propertyRange, position);
        populateItemsFromExpression(propExpr.getObjectExpression(), memberName, items);
    }

    private void populateItemsFromMethodCallExpression(MethodCallExpression methodCallExpr, Position position,
                                                       List<CompletionItem> items) {
        Range methodRange = GroovyLanguageServerUtils.astNodeToRange(methodCallExpr.getMethod());
        if (methodRange == null) {
            return;
        }
        String memberName = getMemberName(methodCallExpr.getMethodAsString(), methodRange, position);
        populateItemsFromExpression(methodCallExpr.getObjectExpression(), memberName, items);
    }

    private void populateItemsFromImportNode(ImportNode importNode, Position position, List<CompletionItem> items) {
        Range importRange = GroovyLanguageServerUtils.astNodeToRange(importNode);
        if (importRange == null) {
            return;
        }
        // skip the "import " at the beginning
        importRange.setStart(new Position(importRange.getEnd().getLine(),
                                          importRange.getEnd().getCharacter() - importNode.getType().getName().length()));
        String importText = getMemberName(importNode.getType().getName(), importRange, position);

        ModuleNode enclosingModule = (ModuleNode) GroovyASTUtils.getEnclosingNodeOfType(importNode, ModuleNode.class,
                                                                                        astContext);
        String enclosingPackageName = enclosingModule.getPackageName();
        List<String> importNames = enclosingModule.getImports().stream()
                .map(otherImportNode -> otherImportNode.getClassName()).collect(Collectors.toList());

        List<CompletionItem> localClassItems = astContext.getVisitor().getClassNodes().stream().filter(classNode -> {
            String packageName = classNode.getPackageName();
            if (packageName == null || packageName.length() == 0 || packageName.equals(enclosingPackageName)) {
                return false;
            }
            String className = classNode.getName();
            String classNameWithoutPackage = classNode.getNameWithoutPackage();
            if (!className.startsWith(importText) && !classNameWithoutPackage.startsWith(importText)) {
                return false;
            }
            if (importNames.contains(className)) {
                return false;
            }
            return true;
        }).map(classNode -> {
            CompletionItem item = new CompletionItem();
            item.setLabel(classNode.getName());
            item.setTextEdit(Either.forLeft(new TextEdit(importRange, classNode.getName())));
            item.setKind(GroovyLanguageServerUtils.astNodeToCompletionItemKind(classNode));
            if (classNode.getNameWithoutPackage().startsWith(importText)) {
                item.setSortText(classNode.getNameWithoutPackage());
            }
            String markdownDocs = DocUtils.getMarkdownDescription(classNode, astContext);
            if (markdownDocs != null) {
                item.setDocumentation(new MarkupContent(MarkupKind.MARKDOWN, markdownDocs));
            }
            return item;
        }).collect(Collectors.toList());
        items.addAll(localClassItems);

        List<ClassInfo> classes = astContext.getLanguageServerContext().getScanResult().getAllClasses();
        List<PackageInfo> packages = astContext.getLanguageServerContext().getScanResult().getPackageInfo();

        List<CompletionItem> packageItems = packages.stream().filter(packageInfo -> {
            String packageName = packageInfo.getName();
            if (packageName.startsWith(importText)) {
                return true;
            }
            return false;
        }).map(packageInfo -> {
            CompletionItem item = new CompletionItem();
            item.setLabel(packageInfo.getName());
            item.setTextEdit(Either.forLeft(new TextEdit(importRange, packageInfo.getName())));
            item.setKind(CompletionItemKind.Module);
            return item;
        }).collect(Collectors.toList());
        items.addAll(packageItems);

        List<CompletionItem> classItems = classes.stream().filter(classInfo -> {
            String packageName = classInfo.getPackageName();
            if (packageName == null || packageName.length() == 0 || packageName.equals(enclosingPackageName)) {
                return false;
            }
            String className = classInfo.getName();
            String classNameWithoutPackage = classInfo.getSimpleName();
            if (!className.startsWith(importText) && !classNameWithoutPackage.startsWith(importText)) {
                return false;
            }
            if (importNames.contains(className)) {
                return false;
            }
            return true;
        }).map(classInfo -> {
            CompletionItem item = new CompletionItem();
            item.setLabel(classInfo.getName());
            item.setTextEdit(Either.forLeft(new TextEdit(importRange, classInfo.getName())));
            item.setKind(classInfoToCompletionItemKind(classInfo));
            if (classInfo.getSimpleName().startsWith(importText)) {
                item.setSortText(classInfo.getSimpleName());
            }
            return item;
        }).collect(Collectors.toList());
        items.addAll(classItems);
    }

    private void populateItemsFromClassNode(ClassNode classNode, Position position, List<CompletionItem> items) {
        ASTNode parentNode = astContext.getVisitor().getParent(classNode);
        if (!(parentNode instanceof ClassNode)) {
            return;
        }
        ClassNode parentClassNode = (ClassNode) parentNode;
        Range classRange = GroovyLanguageServerUtils.astNodeToRange(classNode);
        if (classRange == null) {
            return;
        }
        String className = getMemberName(classNode.getUnresolvedName(), classRange, position);
        if (classNode.equals(parentClassNode.getUnresolvedSuperClass())) {
            populateTypes(classNode, className, new HashSet<>(), true, false, false, items);
        } else if (Arrays.asList(parentClassNode.getUnresolvedInterfaces()).contains(classNode)) {
            populateTypes(classNode, className, new HashSet<>(), false, true, false, items);
        }
    }

    private void populateItemsFromConstructorCallExpression(ConstructorCallExpression constructorCallExpr,
                                                            Position position, List<CompletionItem> items) {
        Range typeRange = GroovyLanguageServerUtils.astNodeToRange(constructorCallExpr.getType());
        if (typeRange == null) {
            return;
        }
        String typeName = getMemberName(constructorCallExpr.getType().getNameWithoutPackage(), typeRange, position);
        populateTypes(constructorCallExpr, typeName, new HashSet<>(), true, false, false, items);
    }

    private void populateItemsFromVariableExpression(VariableExpression varExpr, Position position,
                                                     List<CompletionItem> items) {
        Range varRange = GroovyLanguageServerUtils.astNodeToRange(varExpr);
        if (varRange == null) {
            return;
        }
        String memberName = getMemberName(varExpr.getName(), varRange, position);
        populateItemsFromScope(varExpr, memberName, items);
    }

    private void populateItemsFromPropertiesAndFields(List<PropertyNode> properties, List<FieldNode> fields,
                                                      String memberNamePrefix, Set<String> existingNames, List<CompletionItem> items) {
        List<CompletionItem> propItems = properties.stream().filter(property -> {
            String name = property.getName();
            // sometimes, a property and a field will have the same name
            if (name.startsWith(memberNamePrefix) && !existingNames.contains(name)) {
                existingNames.add(name);
                return true;
            }
            return false;
        }).map(property -> {
            CompletionItem item = new CompletionItem();
            item.setLabel(property.getName());
            item.setKind(GroovyLanguageServerUtils.astNodeToCompletionItemKind(property));
            String markdownDocs = DocUtils.getMarkdownDescription(property, astContext);
            ;
            if (markdownDocs != null) {
                item.setDocumentation(new MarkupContent(MarkupKind.MARKDOWN, markdownDocs));
            }
            return item;
        }).collect(Collectors.toList());
        items.addAll(propItems);
        List<CompletionItem> fieldItems = fields.stream().filter(field -> {
            String name = field.getName();
            // sometimes, a property and a field will have the same name
            if (name.startsWith(memberNamePrefix) && !existingNames.contains(name)) {
                existingNames.add(name);
                return true;
            }
            return false;
        }).map(field -> {
            CompletionItem item = new CompletionItem();
            item.setLabel(field.getName());
            item.setKind(GroovyLanguageServerUtils.astNodeToCompletionItemKind(field));
            String markdownDocs = DocUtils.getMarkdownDescription(field, astContext);
            ;
            if (markdownDocs != null) {
                item.setDocumentation(new MarkupContent(MarkupKind.MARKDOWN, markdownDocs));
            }
            return item;
        }).collect(Collectors.toList());
        items.addAll(fieldItems);
    }

    private void populateItemsFromMethods(List<MethodNode> methods, String memberNamePrefix, Set<String> existingNames,
                                          List<CompletionItem> items) {
        List<CompletionItem> methodItems = methods.stream().filter(method -> {
            String methodName = method.getName();
            // overloads can cause duplicates
            if (methodName.startsWith(memberNamePrefix) && !existingNames.contains(methodName)) {
                existingNames.add(methodName);
                return true;
            }
            return false;
        }).map(method -> {
            CompletionItem item = new CompletionItem();
            item.setLabel(method.getName());
            item.setKind(GroovyLanguageServerUtils.astNodeToCompletionItemKind(method));
            String markdownDocs = DocUtils.getMarkdownDescription(method, astContext);
            ;
            if (markdownDocs != null) {
                item.setDocumentation(new MarkupContent(MarkupKind.MARKDOWN, markdownDocs));
            }
            return item;
        }).collect(Collectors.toList());
        items.addAll(methodItems);
    }

    private void populateItemsFromExpression(Expression leftSide, String memberNamePrefix, List<CompletionItem> items) {
        Set<String> existingNames = new HashSet<>();

        List<PropertyNode> properties = GroovyASTUtils.getPropertiesForLeftSideOfPropertyExpression(leftSide, astContext);
        List<FieldNode> fields = GroovyASTUtils.getFieldsForLeftSideOfPropertyExpression(leftSide, astContext);
        populateItemsFromPropertiesAndFields(properties, fields, memberNamePrefix, existingNames, items);

        List<MethodNode> methods = GroovyASTUtils.getMethodsForLeftSideOfPropertyExpression(leftSide, astContext);
        populateItemsFromMethods(methods, memberNamePrefix, existingNames, items);
    }

    private void populateItemsFromGlobalScope(String memberNamePrefix,
                                              Set<String> existingNames, List<CompletionItem> items) {
        astContext.getLanguageServerContext().getSandbox().getBindings().forEach((variableName, value) -> {
            if (!variableName.startsWith(memberNamePrefix) || existingNames.contains(variableName)) {
                return;
            }
            existingNames.add(variableName);

            CompletionItem item = new CompletionItem();
            item.setLabel(variableName);
            item.setKind(CompletionItemKind.Variable);
            items.add(item);
        });

        List<CompletionItem> staticMethodItems = astContext.getLanguageServerContext().getSandbox().getStaticImports().stream()
                .map(staticImport -> astContext.getLanguageServerContext().getScanResult().getClassInfo(staticImport.getName()))
                .flatMap(classInfo -> classInfo.getMethodInfo().stream().filter(ClassMemberInfo::isStatic))
                .filter(methodInfo -> {
                    String methodName = methodInfo.getName();
                    if (methodName.startsWith(memberNamePrefix) && !existingNames.contains(methodName)) {
                        existingNames.add(methodName);
                        return true;
                    }
                    return false;
                })
                .map(methodInfo -> {
                    CompletionItem item = new CompletionItem();
                    item.setLabel(methodInfo.getName());
                    item.setKind(CompletionItemKind.Method);
                    return item;
                }).collect(Collectors.toList());
        items.addAll(staticMethodItems);
    }

    private void populateItemsFromVariableScope(VariableScope variableScope, String memberNamePrefix,
                                                Set<String> existingNames, List<CompletionItem> items) {
        populateItemsFromGameObjects(memberNamePrefix, existingNames, items);
        populateItemsFromGlobalScope(memberNamePrefix, existingNames, items);

        List<CompletionItem> variableItems = variableScope.getDeclaredVariables().values().stream().filter(variable -> {

            String variableName = variable.getName();
            // overloads can cause duplicates
            if (variableName.startsWith(memberNamePrefix) && !existingNames.contains(variableName)) {
                existingNames.add(variableName);
                return true;
            }
            return false;
        }).map(variable -> {
            CompletionItem item = new CompletionItem();
            item.setLabel(variable.getName());
            item.setKind(GroovyLanguageServerUtils.astNodeToCompletionItemKind((ASTNode) variable));
            if (variable instanceof AnnotatedNode) {
                AnnotatedNode annotatedVar = (AnnotatedNode) variable;
                String markdownDocs = DocUtils.getMarkdownDescription(annotatedVar, astContext);
                ;
                if (markdownDocs != null) {
                    item.setDocumentation(new MarkupContent(MarkupKind.MARKDOWN, markdownDocs));
                }
            }
            return item;
        }).collect(Collectors.toList());
        items.addAll(variableItems);
    }

    private void populateItemsFromScope(ASTNode node, String namePrefix, List<CompletionItem> items) {
        Set<String> existingNames = new HashSet<>();
        ASTNode current = node;
        while (current != null) {
            if (current instanceof ClassNode) {
                ClassNode classNode = (ClassNode) current;
                populateItemsFromPropertiesAndFields(classNode.getProperties(), classNode.getFields(), namePrefix,
                                                     existingNames, items);
                populateItemsFromMethods(classNode.getMethods(), namePrefix, existingNames, items);
            } else if (current instanceof MethodNode) {
                MethodNode methodNode = (MethodNode) current;
                populateItemsFromVariableScope(methodNode.getVariableScope(), namePrefix, existingNames, items);
            } else if (current instanceof BlockStatement) {
                BlockStatement block = (BlockStatement) current;
                populateItemsFromVariableScope(block.getVariableScope(), namePrefix, existingNames, items);
            } else if (current instanceof VariableExpression || current instanceof StaticMethodCallExpression) {
                populateItemsFromGameObjects(namePrefix, existingNames, items);
                populateItemsFromGlobalScope(namePrefix, existingNames, items);
            }
            current = astContext.getVisitor().getParent(current);
        }
        populateTypes(node, namePrefix, existingNames, items);
    }

    private void populateTypes(ASTNode offsetNode, String namePrefix, Set<String> existingNames,
                               List<CompletionItem> items) {
        populateTypes(offsetNode, namePrefix, existingNames, true, true, true, items);
    }

    private void populateTypes(ASTNode offsetNode, String namePrefix, Set<String> existingNames, boolean includeClasses,
                               boolean includeInterfaces, boolean includeEnums, List<CompletionItem> items) {
        Range addImportRange = GroovyASTUtils.findAddImportRange(offsetNode, astContext);

        ModuleNode enclosingModule = (ModuleNode) GroovyASTUtils.getEnclosingNodeOfType(offsetNode, ModuleNode.class,
                                                                                        astContext);
        String enclosingPackageName = enclosingModule.getPackageName();
        List<String> importNames = enclosingModule.getImports().stream().map(importNode -> importNode.getClassName())
                .collect(Collectors.toList());

        List<CompletionItem> localClassItems = astContext.getVisitor().getClassNodes().stream().filter(classNode -> {
            if (isIncomplete) {
                return false;
            }
            if (existingNames.size() >= maxItemCount) {
                isIncomplete = true;
                return false;
            }
            String classNameWithoutPackage = classNode.getNameWithoutPackage();
            String className = classNode.getName();
            if (classNameWithoutPackage.startsWith(namePrefix) && !existingNames.contains(className)) {
                existingNames.add(className);
                return true;
            }
            return false;
        }).map(classNode -> {
            String className = classNode.getName();
            String packageName = classNode.getPackageName();
            CompletionItem item = new CompletionItem();
            item.setLabel(classNode.getNameWithoutPackage());
            item.setKind(GroovyLanguageServerUtils.astNodeToCompletionItemKind(classNode));
            item.setDetail(packageName);
            String markdownDocs = DocUtils.getMarkdownDescription(classNode, astContext);
            ;
            if (markdownDocs != null) {
                item.setDocumentation(new MarkupContent(MarkupKind.MARKDOWN, markdownDocs));
            }
            if (packageName != null && !packageName.equals(enclosingPackageName) && !importNames.contains(className)) {
                List<TextEdit> additionalTextEdits = new ArrayList<>();
                TextEdit addImportEdit = createAddImportTextEdit(className, addImportRange);
                additionalTextEdits.add(addImportEdit);
                item.setAdditionalTextEdits(additionalTextEdits);
            }
            return item;
        }).collect(Collectors.toList());
        items.addAll(localClassItems);

        List<ClassInfo> classes = astContext.getLanguageServerContext().getScanResult().getAllClasses();

        List<CompletionItem> classItems = classes.stream().filter(classInfo -> {
            if (isIncomplete) {
                return false;
            }
            if (existingNames.size() >= maxItemCount) {
                isIncomplete = true;
                return false;
            }
            String className = classInfo.getName();
            String classNameWithoutPackage = classInfo.getSimpleName();
            if (classNameWithoutPackage.startsWith(namePrefix) && !existingNames.contains(className)) {
                existingNames.add(className);
                return true;
            }
            return false;
        }).map(classInfo -> {
            String className = classInfo.getName();
            String packageName = classInfo.getPackageName();
            CompletionItem item = new CompletionItem();
            item.setLabel(classInfo.getSimpleName());
            item.setDetail(packageName);
            item.setKind(classInfoToCompletionItemKind(classInfo));
            if (packageName != null && !packageName.equals(enclosingPackageName) && !importNames.contains(className)) {
                List<TextEdit> additionalTextEdits = new ArrayList<>();
                TextEdit addImportEdit = createAddImportTextEdit(className, addImportRange);
                additionalTextEdits.add(addImportEdit);
                item.setAdditionalTextEdits(additionalTextEdits);
            }
            return item;
        }).collect(Collectors.toList());
        items.addAll(classItems);
    }

    private String getMemberName(String memberName, Range range, Position position) {
        if (position.getLine() == range.getStart().getLine()
            && position.getCharacter() > range.getStart().getCharacter()) {
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
        TextEdit edit = new TextEdit();
        StringBuilder builder = new StringBuilder();
        builder.append("import ");
        builder.append(className);
        builder.append("\n");
        edit.setNewText(builder.toString());
        edit.setRange(range);
        return edit;
    }
}