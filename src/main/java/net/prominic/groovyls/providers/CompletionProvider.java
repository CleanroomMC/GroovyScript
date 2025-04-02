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

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.mapper.AbstractObjectMapper;
import com.cleanroommc.groovyscript.server.CompletionParams;
import com.cleanroommc.groovyscript.server.Completions;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.Script;
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

    private static final String[] keywords = {
            "assert",
            "case",
            "break",
            "continue",
            "transient",
            "extends ",
            "implements ",
            "enum",
            "try ",
            "catch",
            "finally",
            "instanceof ",
            "super",
            "private",
            "public",
            "protected",
            "abstract",
            "const",
            "default",
            "goto",
            "interface",
            "native",
            "non-sealed",
            "package ",
            "permits",
            "record",
            "sealed",
            "static",
            "strictfp",
            "synchronized",
            "threadsafe",
            "throws",
            "trait",
            "var",
            "yields"
    };
    private static final String[] popularKeywords = {
            "def ",
            "else ",
            "return",
            "import ",
            "class",
            "this",
            "null",
            "true",
            "false",
            "void",
            "byte",
            "short",
            "int",
            "long",
            "float",
            "double",
            "boolean",
            "char",
            "throw ",
            "new",
            "in ",
            "as ",
            "final "
    };

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
        items.add(CompletionItemFactory.createKeywordCompletion("if", true, " ($1) $0"));
        items.add(CompletionItemFactory.createKeywordCompletion("for", true, " ($1) $0"));
        items.add(CompletionItemFactory.createKeywordCompletion("while", true, " ($1) $0"));
        items.add(CompletionItemFactory.createKeywordCompletion("do", false, " ($1)"));
        items.add(CompletionItemFactory.createKeywordCompletion("switch", true, " ($1) $0"));
        items.addAll(popularKeywords, s -> CompletionItemFactory.createKeywordCompletion(s, true));
        items.addAll(keywords, s -> CompletionItemFactory.createKeywordCompletion(s, false));
    }

    private boolean populateItemsFromNode(Position position, ASTNode offsetNode, Completions items) {
        ASTNode parentNode = astContext.getVisitor().getParent(offsetNode);
        if (parentNode instanceof DeclarationExpression decl && decl.getLeftExpression() == offsetNode) return false; // dont complete definition names
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
            if (parentParent instanceof MethodCallExpression expr && expr.getArguments() instanceof ArgumentListExpression args && !args.getExpressions().isEmpty()) {
                // TODO completions in file()
                AbstractObjectMapper<?> mapper = GroovyASTUtils.getMapperOfNode(expr, astContext);
                if (mapper != null) {
                    CompletionParams params = CompletionParams.EMPTY;
                    int index = -1;
                    for (int i = 0; i < args.getExpressions().size(); i++) {
                        Expression arg = args.getExpression(i);
                        if (arg instanceof ConstantExpression constArg) {
                            params = CompletionParams.addParam(params, constArg.getValue());
                        } else {
                            params = CompletionParams.addUnparsableParam(params);
                        }
                        if (arg == node) {
                            index = i;
                            break;
                        }
                    }
                    mapper.provideCompletion(index, params, items);
                }
            }
            return false; // don't complete keyword in strings
        }
        return true;
    }

    private void populateItemsFromStaticMethodCallExpression(StaticMethodCallExpression methodCallExpr,
                                                             Position position,
                                                             Completions items) {
        Set<String> existingNames = new ObjectOpenHashSet<>();
        populateItemsFromGlobalScope(methodCallExpr.getMethod(), existingNames, items);
    }

    private void populateItemsFromPropertyExpression(PropertyExpression propExpr, Position position, Completions items) {
        Range propertyRange = GroovyLSUtils.astNodeToRange(propExpr.getProperty());
        if (propertyRange == null) return;
        populateItemsFromExpression(propExpr.getObjectExpression(), items);
    }

    private void populateItemsFromMethodCallExpression(MethodCallExpression methodCallExpr, Position position, Completions items) {
        Range methodRange = GroovyLSUtils.astNodeToRange(methodCallExpr.getMethod());
        if (methodRange == null) return;
        populateItemsFromExpression(methodCallExpr.getObjectExpression(), items);
    }

    private void populateItemsFromImportNode(ImportNode importNode, Position position, Completions items) {
        Range importRange = GroovyLSUtils.astNodeToRange(importNode);
        if (importRange == null) return;
        // skip the "import " at the beginning
        importRange.setStart(
                new Position(
                        importRange.getEnd().getLine(),
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
            if ((!className.startsWith(importText) && !classNameWithoutPackage.startsWith(importText)) || GroovyLSUtils.hasImport(enclosingModule, className)) {
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
            if (!className.startsWith(importText) && !classNameWithoutPackage.startsWith(importText)) {
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
            populateTypes(classNode, className, new ObjectOpenHashSet<>(), true, false, false, items);
        } else if (Arrays.asList(parentClassNode.getUnresolvedInterfaces()).contains(classNode)) {
            populateTypes(classNode, className, new ObjectOpenHashSet<>(), false, true, false, items);
        }
    }

    private void populateItemsFromConstructorCallExpression(ConstructorCallExpression constructorCallExpr,
                                                            Position position,
                                                            Completions items) {
        Range typeRange = GroovyLSUtils.astNodeToRange(constructorCallExpr.getType());
        if (typeRange == null) return;
        String typeName = getMemberName(constructorCallExpr.getType().getNameWithoutPackage(), typeRange, position);
        populateTypes(constructorCallExpr, typeName, new ObjectOpenHashSet<>(), true, false, false, items);
    }

    private void populateItemsFromVariableExpression(VariableExpression varExpr, Position position, Completions items) {
        Range varRange = GroovyLSUtils.astNodeToRange(varExpr);
        if (varRange == null) return;
        String memberName = getMemberName(varExpr.getName(), varRange, position);
        populateItemsFromScope(varExpr, memberName, items);
    }

    private void populateItemsFromPropertiesAndFields(List<PropertyNode> properties,
                                                      List<FieldNode> fields,
                                                      Set<String> existingNames,
                                                      Completions items) {
        items.addAll(properties, p -> {
            String name = p.getName();
            if (!p.isPublic() || existingNames.contains(name)) return null;
            existingNames.add(name);
            if (p.getDeclaringClass().isDerivedFrom(ClassHelper.makeCached(Script.class)) && p.getName().equals("__$stMC")) return null;
            CompletionItem item = CompletionItemFactory.createCompletion(p, p.getName(), astContext);
            if (!p.isDynamicTyped()) {
                var details = new CompletionItemLabelDetails();
                details.setDetail(" -> " + appendType(p.getType(), new StringBuilder(), true));
                item.setLabelDetails(details);
            }
            return item;
        });
        items.addAll(fields, f -> {
            String name = f.getName();
            if (!f.isPublic() || existingNames.contains(name)) return null;
            existingNames.add(name);
            if (f.getDeclaringClass().isDerivedFrom(ClassHelper.makeCached(Script.class)) && f.getName().equals("__$stMC")) return null;
            CompletionItem item = CompletionItemFactory.createCompletion(f, f.getName(), astContext);
            if (!f.isDynamicTyped()) {
                var details = new CompletionItemLabelDetails();
                details.setDetail(" -> " + appendType(f.getType(), new StringBuilder(), true));
                item.setLabelDetails(details);
            }
            return item;
        });
    }

    private void populateItemsFromMethods(List<MethodNode> methods, Set<String> existingNames, Completions items) {
        items.addAll(methods, method -> {
            String name = getDescriptor(method, true, false, false);
            if (!method.isPublic() || existingNames.contains(name)) return null;
            existingNames.add(name);
            if (method.getDeclaringClass().isDerivedFrom(ClassHelper.makeCached(Script.class))) {
                if (method.getName().equals("$getLookup") || method.getName().equals("main")) return null;
            }
            if (method.getDeclaringClass().isResolved() && (method.getModifiers() & GroovyASTUtils.EXPANSION_MARKER) == 0 && GroovyReflectionUtils.resolveMethodFromMethodNode(method, astContext) == null) {
                return null;
            }

            CompletionItem item = CompletionItemFactory.createCompletion(method, method.getName(), astContext);
            item.setLabelDetails(getMethodNodeDetails(method));
            return item;
        });
    }

    public static String getDescriptor(MethodNode node, boolean includeName, boolean includeReturn, boolean display) {
        StringBuilder builder = new StringBuilder();
        if (includeName) builder.append(node.getName());
        builder.append("(");
        var parameters = node.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            boolean last = i == parameters.length - 1;
            if (parameters[i].isDynamicTyped()) {
                builder.append("?");
            } else {
                appendType(parameters[i].getType(), builder, display, last);
            }
            if (!last) builder.append(", ");
        }
        builder.append(")");
        if (!includeReturn) return builder.toString();
        if (node.getReturnType() != ClassHelper.VOID_TYPE) {
            if (display) builder.append(" -> ");
            appendType(node.getReturnType(), builder, display);
        }
        return builder.toString();
    }

    public static StringBuilder appendType(ClassNode type, StringBuilder builder, boolean display) {
        return appendType(type, builder, display, false);
    }

    public static StringBuilder appendType(ClassNode type, StringBuilder builder, boolean display, boolean maybeVarargs) {
        boolean isArray = type.getComponentType() != null;
        if (isArray) type = type.getComponentType();
        if (type.isGenericsPlaceHolder()) {
            // this type is a generic
            GenericsType gt = type.asGenericsType();
            ClassNode bound = null;
            if (gt.getUpperBounds() != null && gt.getUpperBounds().length > 0) {
                bound = gt.getUpperBounds()[0];
            } else if (gt.getLowerBound() != null) {
                bound = gt.getLowerBound();
            }
            if (bound == null || bound.equals(type)) {
                // type has no bound (just T f.e.)
                builder.append(gt.getName());
                if (isArray) {
                    builder.append(maybeVarargs ? "..." : "[]");
                }
                return builder;
            }
            type = bound;
        }
        builder.append(display ? type.getNameWithoutPackage() : type.getName());
        appendGenerics(type, builder, display);
        if (isArray) {
            builder.append(maybeVarargs ? "..." : "[]");
        }
        return builder;
    }

    private static void appendGenerics(ClassNode type, StringBuilder builder, boolean display) {
        GenericsType[] gt = type.getGenericsTypes();
        if (gt == null || gt.length == 0) return;
        builder.append("<");
        for (int i = 0; i < gt.length; i++) {
            GenericsType g = gt[i];
            if (g.isWildcard()) builder.append("?");
            else appendType(g.getType(), builder, display);
            if (i < gt.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(">");
    }

    public static String getDescriptor(MethodInfo node, boolean includeName, boolean includeReturn, boolean display) {
        StringBuilder builder = new StringBuilder();
        if (includeName) builder.append(node.getName());
        builder.append("(");
        var parameters = node.getParameterInfo();
        for (int i = 0; i < parameters.length; i++) {
            boolean last = i == parameters.length - 1;
            appendParameter(parameters[i], builder, display, last);
            if (!last) builder.append(", ");
        }
        builder.append(")");
        if (!includeReturn) return builder.toString();
        var ret = display
                ? node.getTypeSignatureOrTypeDescriptor().getResultType().toStringWithSimpleNames()
                : node.getTypeDescriptor().getResultType().toString();
        if (!ret.equals("void")) {
            if (display) builder.append(" -> ");
            builder.append(ret);
        }
        return builder.toString();
    }

    public static StringBuilder appendParameter(MethodParameterInfo param, StringBuilder builder, boolean display, boolean maybeVarargs) {
        builder.append(
                display ? param.getTypeSignatureOrTypeDescriptor().toStringWithSimpleNames() : param.getTypeDescriptor().toString()); // don't use generic types
        if (maybeVarargs && builder.charAt(builder.length() - 1) == ']' && builder.charAt(builder.length() - 2) == '[') {
            builder.delete(builder.length() - 2, builder.length()).append("...");
        }
        return builder;
    }

    private static @NotNull CompletionItemLabelDetails getMethodNodeDetails(MethodNode method) {
        var details = new CompletionItemLabelDetails();
        details.setDetail(getDescriptor(method, false, true, true));
        return details;
    }

    private static @NotNull CompletionItemLabelDetails getMethodInfoDetails(MethodInfo methodInfo) {
        var details = new CompletionItemLabelDetails();
        details.setDetail(getDescriptor(methodInfo, false, true, true));
        return details;
    }

    private void populateItemsFromExpression(Expression leftSide, Completions items) {
        Set<String> existingNames = new ObjectOpenHashSet<>();

        ClassNode classNode = GroovyASTUtils.getTypeOfNode(leftSide, astContext);
        if (classNode == null) return;
        GroovyASTUtils.fillClassNode(classNode);
        List<PropertyNode> properties = GroovyASTUtils.getPropertiesForLeftSideOfPropertyExpression(classNode, leftSide, astContext);
        List<FieldNode> fields = GroovyASTUtils.getFieldsForLeftSideOfPropertyExpression(classNode, leftSide, astContext);
        populateItemsFromPropertiesAndFields(properties, fields, existingNames, items);

        List<MethodNode> methods = GroovyASTUtils.getMethodsForLeftSideOfPropertyExpression(classNode, leftSide, astContext);
        populateItemsFromMethods(methods, existingNames, items);
    }

    private void populateItemsFromGlobalScope(String memberNamePrefix, Set<String> existingNames, Completions items) {
        items.addAll(astContext.getLanguageServerContext().getSandbox().getBindings().entrySet(), entry -> {
            String name = entry.getKey();
            if (!name.toLowerCase(Locale.ENGLISH).contains(memberNamePrefix) || existingNames.contains(name)) return null;
            existingNames.add(name);
            if (entry.getValue() instanceof AbstractObjectMapper<?>goh) {
                for (MethodNode method : goh.getMethodNodes()) {
                    var item = CompletionItemFactory.createCompletion(method, goh.getName(), astContext);
                    item.setLabelDetails(getMethodNodeDetails(method));
                    // TODO
                    items.add(item);
                }
                return null;
            } else if (entry.getValue() instanceof Closure<?>closure) {
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
                String desc = getDescriptor(m, true, false, false);
                if (!m.isStatic() || !m.isPublic() || !desc.toLowerCase(Locale.ENGLISH).contains(memberNamePrefix) || existingNames.contains(desc)) continue;
                existingNames.add(desc);
                if (GroovyReflectionUtils.resolveMethodFromMethodInfo(m, astContext) == null) continue;
                var item = CompletionItemFactory.createCompletion(CompletionItemKind.Method, m.getName());
                item.setLabelDetails(getMethodInfoDetails(m));
                items.add(item);
            }
            for (FieldInfo m : info.getFieldInfo()) {
                if (!m.isStatic() || !m.getName().toLowerCase(Locale.ENGLISH).contains(memberNamePrefix) || existingNames.contains(m.getName())) continue;
                existingNames.add(m.getName());
                var item = CompletionItemFactory.createCompletion(CompletionItemKind.Field, m.getName());
                items.add(item);
            }
            return null;
        });
    }

    private void populateItemsFromVariableScope(VariableScope variableScope,
                                                String memberNamePrefix,
                                                Set<String> existingNames,
                                                Completions items) {
        populateItemsFromGlobalScope(memberNamePrefix, existingNames, items);
        items.addAll(variableScope.getDeclaredVariables().values(), variable -> {
            String variableName = variable.getName();
            if (!variableName.toLowerCase(Locale.ENGLISH).contains(memberNamePrefix) || existingNames.contains(variableName)) return null;
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
                populateItemsFromPropertiesAndFields(classNode.getProperties(), classNode.getFields(), existingNames, items);
                populateItemsFromMethods(classNode.getMethods(), existingNames, items);
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
                                populateItemsFromPropertiesAndFields(
                                        classNode.getProperties(),
                                        classNode.getFields(),
                                        existingNames,
                                        items);
                                populateItemsFromMethods(classNode.getMethods(), existingNames, items);
                            }
                        }
                    }
                }
            }
            if (current instanceof VariableExpression || current instanceof StaticMethodCallExpression) {
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

    private void populateTypes(ASTNode offsetNode,
                               String namePrefix,
                               Set<String> existingNames,
                               boolean includeClasses,
                               boolean includeInterfaces,
                               boolean includeEnums,
                               Completions items) {
        Range addImportRange = GroovyASTUtils.findAddImportRange(doc, offsetNode, astContext);

        ModuleNode enclosingModule = getModule();
        String enclosingPackageName = enclosingModule.getPackageName();
        List<ClassNode> classNodes = astContext.getVisitor().getClassNodes();
        Set<ClassNode> all = new ObjectOpenHashSet<>();
        all.addAll(classNodes);
        for (Class<?> clz : GroovyScript.getSandbox().getEngine().getAllLoadedScriptClasses()) {
            //if (Script.class.isAssignableFrom(clz)) continue;
            all.add(ClassHelper.makeCached(clz));
        }
        items.addAll(all, classNode -> {
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
            CompletionItem item = CompletionItemFactory.createCompletion(
                    classInfoToCompletionItemKind(classInfo),
                    classInfo.getSimpleName());
            item.setDetail(packageName);
            boolean hasImport = GroovyLSUtils.hasImport(enclosingModule, className);
            // sort imported classes higher
            if (hasImport) item.setSortText("aa" + classInfo.getSimpleName());
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
