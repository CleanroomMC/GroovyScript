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

import com.cleanroommc.groovyscript.gameobjects.GameObjectHandlerManager;
import com.cleanroommc.groovyscript.server.Completions;
import groovy.lang.DelegatesTo;
import io.github.classgraph.*;
import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.compiler.util.GroovyASTUtils;
import net.prominic.groovyls.compiler.util.GroovyReflectionUtils;
import net.prominic.groovyls.util.CompletionItemFactory;
import net.prominic.groovyls.util.GroovyLanguageServerUtils;
import net.prominic.groovyls.util.URIUtils;
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
import java.util.stream.Collectors;

public class CompletionProvider {

    private final ASTContext astContext;
    //private int maxItemCount = 1000;
    private boolean isIncomplete = false;

    public CompletionProvider(ASTContext astContext) {
        this.astContext = astContext;
    }

    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> provideCompletion(
            TextDocumentIdentifier textDocument, Position position, CompletionContext context) {
        URI uri = URIUtils.toUri(textDocument.getUri());

        Completions items = new Completions(1000);

        ASTNode offsetNode = astContext.getVisitor().getNodeAtLineAndColumn(uri, position.getLine(), position.getCharacter());
        if (offsetNode != null) {
            populateItemsFromNode(position, offsetNode, items);
        }
        populateKeywords(items);

        return CompletableFuture.completedFuture(items.getResult(this.isIncomplete));
    }

    private void populateKeywords(Completions items) {
        items.addAll(new String[]{"def", "assert", "if", "for", "else", "while", "switch", "case", "break", "continue", "return",
                                  "transient", "import", "class", "extends", "implements", "enum", "try", "catch", "finally", "throw", "new", "in", "as",
                                  "instanceof", "super", "this", "null", "true", "false", "void", "byte", "short", "int", "long", "float", "double", "boolean",
                                  "private", "public", "protected"},
                     s -> {
                         var item = new CompletionItem(s);
                         item.setKind(CompletionItemKind.Keyword);
                         item.setSortText("zzz" + s);
                         return item;
                     });
    }

    private void populateItemsFromNode(Position position, ASTNode offsetNode, Completions items) {
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
            populateItemsFromConstantExpression((ConstantExpression) offsetNode, parentNode, items);
        }
    }

    private void populateItemsFromConstantExpression(ConstantExpression node, ASTNode parent, Completions items) {
        if (node.getType().getName().equals(String.class.getName())) {
            ASTNode parentParent = astContext.getVisitor().getParent(parent);
            if (parentParent instanceof StaticMethodCallExpression expr &&
                expr.getOwnerType().getName().equals(GameObjectHandlerManager.class.getName()) &&
                expr.getMethod().equals("getGameObject") &&
                expr.getArguments() instanceof ArgumentListExpression args && !args.getExpressions().isEmpty() &&
                args.getExpression(0) instanceof ConstantExpression expr1 && expr1.getValue() instanceof String name &&
                GameObjectHandlerManager.hasGameObjectHandler(name)) {
                int index = -1;
                if (args.getExpressions().size() > 1) {
                    for (int i = 1; i < args.getExpressions().size(); i++) {
                        if (args.getExpression(i) == node) {
                            index = i - 1;
                            break;
                        }
                    }
                }
                GameObjectHandlerManager.provideCompletion(name, index, items);
            }
        }
    }

    private void populateItemsFromStaticMethodCallExpression(StaticMethodCallExpression methodCallExpr, Position position, Completions items) {
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
                                                     Set<String> existingNames, Completions items) {
        GameObjectHandlerManager.getGameObjectHandlers().stream()
                .filter(handler -> {
                    if (handler.getName().startsWith(memberNamePrefix) && !existingNames.contains(handler.getName())) {
                        existingNames.add(handler.getName());
                        return true;
                    }
                    return false;
                }).forEach(handler -> {
                    for (Class<?>[] paramTypes : handler.getParamTypes()) {
                        var completionItem = CompletionItemFactory.createCompletion(CompletionItemKind.Method, handler.getName());
                        completionItem.setDetail("(global scope)");
                        StringBuilder builder = new StringBuilder().append('(');
                        for (int i = 0; i < paramTypes.length; i++) {
                            var parameter = paramTypes[i];
                            builder.append(parameter.getSimpleName());
                            if (i < paramTypes.length - 1) {
                                builder.append(",");
                            }
                        }
                        builder.append(") -> ");
                        builder.append(handler.getReturnType().getSimpleName());
                        CompletionItemLabelDetails details = new CompletionItemLabelDetails();
                        details.setDetail(builder.toString());
                        completionItem.setLabelDetails(details);
                        items.add(completionItem);
                    }
                });
    }

    private void populateItemsFromPropertyExpression(PropertyExpression propExpr, Position position,
                                                     Completions items) {
        Range propertyRange = GroovyLanguageServerUtils.astNodeToRange(propExpr.getProperty());
        if (propertyRange == null) {
            return;
        }
        String memberName = getMemberName(propExpr.getPropertyAsString(), propertyRange, position);
        populateItemsFromExpression(propExpr.getObjectExpression(), memberName, items);
    }

    private void populateItemsFromMethodCallExpression(MethodCallExpression methodCallExpr, Position position,
                                                       Completions items) {
        Range methodRange = GroovyLanguageServerUtils.astNodeToRange(methodCallExpr.getMethod());
        if (methodRange == null) {
            return;
        }
        String memberName = getMemberName(methodCallExpr.getMethodAsString(), methodRange, position);
        populateItemsFromExpression(methodCallExpr.getObjectExpression(), memberName, items);
    }

    private void populateItemsFromImportNode(ImportNode importNode, Position position, Completions items) {
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
                .map(ImportNode::getClassName).collect(Collectors.toList());

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
            CompletionItem item = CompletionItemFactory.createCompletion(classNode, classNode.getName(), astContext);
            item.setTextEdit(Either.forLeft(new TextEdit(importRange, classNode.getName())));
            if (classNode.getNameWithoutPackage().startsWith(importText)) {
                item.setSortText(classNode.getNameWithoutPackage());
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
            CompletionItem item = CompletionItemFactory.createCompletion(CompletionItemKind.Module, packageInfo.getName());
            item.setTextEdit(Either.forLeft(new TextEdit(importRange, packageInfo.getName())));
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
            CompletionItem item = CompletionItemFactory.createCompletion(classInfoToCompletionItemKind(classInfo), classInfo.getName());

            item.setTextEdit(Either.forLeft(new TextEdit(importRange, classInfo.getName())));
            if (classInfo.getSimpleName().startsWith(importText)) {
                item.setSortText(classInfo.getSimpleName());
            }
            return item;
        }).collect(Collectors.toList());
        items.addAll(classItems);
    }

    private void populateItemsFromClassNode(ClassNode classNode, Position position, Completions items) {
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
                                                            Position position, Completions items) {
        Range typeRange = GroovyLanguageServerUtils.astNodeToRange(constructorCallExpr.getType());
        if (typeRange == null) {
            return;
        }
        String typeName = getMemberName(constructorCallExpr.getType().getNameWithoutPackage(), typeRange, position);
        populateTypes(constructorCallExpr, typeName, new HashSet<>(), true, false, false, items);
    }

    private void populateItemsFromVariableExpression(VariableExpression varExpr, Position position,
                                                     Completions items) {
        Range varRange = GroovyLanguageServerUtils.astNodeToRange(varExpr);
        if (varRange == null) {
            return;
        }
        String memberName = getMemberName(varExpr.getName(), varRange, position);
        populateItemsFromScope(varExpr, memberName, items);
    }

    private void populateItemsFromPropertiesAndFields(List<PropertyNode> properties, List<FieldNode> fields,
                                                      String memberNamePrefix, Set<String> existingNames, Completions items) {
        List<CompletionItem> propItems = properties.stream().filter(property -> {
            String name = property.getName();
            // sometimes, a property and a field will have the same name
            if (name.startsWith(memberNamePrefix) && !existingNames.contains(name)) {
                existingNames.add(name);
                return true;
            }
            return false;
        }).map(property -> {
            CompletionItem item = CompletionItemFactory.createCompletion(property, property.getName(), astContext);

            if (!property.isDynamicTyped()) {
                item.setDetail(property.getType().getNameWithoutPackage());
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
            CompletionItem item = CompletionItemFactory.createCompletion(field, field.getName(), astContext);

            if (!field.isDynamicTyped()) {
                item.setDetail(field.getType().getNameWithoutPackage());
            }
            return item;
        }).collect(Collectors.toList());
        items.addAll(fieldItems);
    }

    private void populateItemsFromMethods(List<MethodNode> methods, String memberNamePrefix, Set<String> existingNames,
                                          Completions items) {
        List<CompletionItem> methodItems = methods.stream()
                .filter(method -> {
                    String methodName = method.getName();
                    // overloads can cause duplicates
                    if (methodName.startsWith(memberNamePrefix) && !existingNames.contains(methodName)) {
                        existingNames.add(methodName);
                        return !method.getDeclaringClass().isResolved() || GroovyReflectionUtils.resolveMethodFromMethodNode(method, astContext).isPresent();
                    }
                    return false;
                }).map(method -> {
                    CompletionItem item = CompletionItemFactory.createCompletion(method, method.getName(), astContext);

                    var details = getMethodNodeDetails(method);
                    item.setLabelDetails(details);

                    return item;
                }).collect(Collectors.toList());
        items.addAll(methodItems);
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

            var item = CompletionItemFactory.createCompletion(CompletionItemKind.Variable, variableName);

            item.setDetail("(global scope)");

            items.add(item);
        });

        List<CompletionItem> staticMethodItems = astContext.getLanguageServerContext().getSandbox().getStaticImports().stream()
                .map(staticImport -> astContext.getLanguageServerContext().getScanResult().getClassInfo(staticImport.getName()))
                .filter(Objects::nonNull)
                .flatMap(classInfo -> classInfo.getMethodInfo().stream().filter(ClassMemberInfo::isStatic))
                .filter(methodInfo -> {
                    String methodName = methodInfo.getName();
                    if (methodName.startsWith(memberNamePrefix) && !existingNames.contains(methodName)) {
                        existingNames.add(methodName);
                        return GroovyReflectionUtils.resolveMethodFromMethodInfo(methodInfo, astContext).isPresent();
                    }
                    return false;
                })
                .map(methodInfo -> {
                    var item = CompletionItemFactory.createCompletion(CompletionItemKind.Method, methodInfo.getName());

                    var details = getMethodInfoDetails(methodInfo);
                    item.setLabelDetails(details);
                    return item;
                })
                .collect(Collectors.toList());
        items.addAll(staticMethodItems);
    }

    private void populateItemsFromVariableScope(VariableScope variableScope, String memberNamePrefix,
                                                Set<String> existingNames, Completions items) {
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
            var item = CompletionItemFactory.createCompletion((ASTNode) variable, variable.getName(), astContext);

            if (!variable.isDynamicTyped()) {
                item.setDetail(variable.getType().getName());
            }
            return item;
        }).collect(Collectors.toList());
        items.addAll(variableItems);
    }

    private void populateItemsFromScope(ASTNode node, String namePrefix, Completions items) {
        Set<String> existingNames = new HashSet<>();
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
                                populateItemsFromPropertiesAndFields(classNode.getProperties(), classNode.getFields(), namePrefix, existingNames, items);
                                populateItemsFromMethods(classNode.getMethods(), namePrefix, existingNames, items);
                            }
                        }
                    }
                }
            }
            if (current instanceof VariableExpression || current instanceof StaticMethodCallExpression) {
                populateItemsFromGameObjects(namePrefix, existingNames, items);
                populateItemsFromGlobalScope(namePrefix, existingNames, items);
            }
            child = current;
            current = astContext.getVisitor().getParent(current);
        }
        populateTypes(node, namePrefix, existingNames, items);
    }

    private void populateTypes(ASTNode offsetNode, String namePrefix, Set<String> existingNames,
                               Completions items) {
        populateTypes(offsetNode, namePrefix, existingNames, true, true, true, items);
    }

    private void populateTypes(ASTNode offsetNode, String namePrefix, Set<String> existingNames, boolean includeClasses,
                               boolean includeInterfaces, boolean includeEnums, Completions items) {
        Range addImportRange = GroovyASTUtils.findAddImportRange(offsetNode, astContext);

        ModuleNode enclosingModule = (ModuleNode) GroovyASTUtils.getEnclosingNodeOfType(offsetNode, ModuleNode.class,
                                                                                        astContext);
        String enclosingPackageName = enclosingModule.getPackageName();
        List<String> importNames = enclosingModule.getImports().stream().map(importNode -> importNode.getClassName())
                .collect(Collectors.toList());

        List<CompletionItem> localClassItems = astContext.getVisitor().getClassNodes().stream().filter(classNode -> {
            if (items.reachedLimit()) return false;
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
            CompletionItem item = CompletionItemFactory.createCompletion(classNode, classNode.getNameWithoutPackage(), astContext);
            item.setDetail(packageName);
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
            if (items.reachedLimit()) return false;
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
            CompletionItem item = CompletionItemFactory.createCompletion(classInfoToCompletionItemKind(classInfo), classInfo.getSimpleName());
            item.setDetail(packageName);
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