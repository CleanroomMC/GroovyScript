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
package net.prominic.groovyls.compiler.util;

import com.cleanroommc.groovyscript.api.Hidden;
import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.cleanroommc.groovyscript.mapper.AbstractObjectMapper;
import com.cleanroommc.groovyscript.mapper.ObjectMapperManager;
import com.cleanroommc.groovyscript.sandbox.Preprocessor;
import com.cleanroommc.groovyscript.sandbox.expand.IDocumented;
import groovy.lang.*;
import groovy.lang.groovydoc.Groovydoc;
import groovy.lang.groovydoc.GroovydocHolder;
import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.util.GroovyLSUtils;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GroovyASTUtils {

    public static final int EXPANSION_MARKER = 0x01000000;
    public static final int HIDDEN_MARKER = 0x02000000;

    public static <T extends ASTNode> T getEnclosingNodeOfType(ASTNode offsetNode, Class<T> nodeType, ASTContext context) {
        ASTNode current = offsetNode;
        while (current != null) {
            if (nodeType.isInstance(current)) {
                return (T) current;
            }
            current = context.getVisitor().getParent(current);
        }
        return null;
    }

    public static ASTNode getDefinition(ASTNode node, boolean strict, ASTContext context) {
        if (node == null) {
            return null;
        }
        ASTNode parentNode = context.getVisitor().getParent(node);
        if (node instanceof ExpressionStatement statement) {
            node = statement.getExpression();
        }
        if (node instanceof ClassNode) {
            return tryToResolveOriginalClassNode((ClassNode) node, strict, context);
        } else if (node instanceof ConstructorCallExpression callExpression) {
            return GroovyASTUtils.getMethodFromCallExpression(callExpression, context);
        } else if (node instanceof DeclarationExpression declExpression) {
            if (!declExpression.isMultipleAssignmentDeclaration()) {
                ClassNode originType = declExpression.getVariableExpression().getOriginType();
                return tryToResolveOriginalClassNode(originType, strict, context);
            }
        } else if (node instanceof ClassExpression classExpression) {
            return tryToResolveOriginalClassNode(classExpression.getType(), strict, context);
        } else if (node instanceof ImportNode importNode) {
            return tryToResolveOriginalClassNode(importNode.getType(), strict, context);
        } else if (node instanceof MethodNode) {
            return node;
        } else if (node instanceof ConstantExpression && parentNode != null) {
            if (parentNode instanceof MethodCallExpression methodCallExpression) {
                return GroovyASTUtils.getMethodFromCallExpression(methodCallExpression, context);
            } else if (parentNode instanceof PropertyExpression propertyExpression) {
                PropertyNode propNode = GroovyASTUtils.getPropertyFromExpression(propertyExpression, context);
                if (propNode != null) {
                    return propNode;
                }
                return GroovyASTUtils.getFieldFromExpression(propertyExpression, context);
            }
        } else if (node instanceof VariableExpression variableExpression) {
            Variable accessedVariable = variableExpression.getAccessedVariable();
            if (accessedVariable instanceof ASTNode) {
                return (ASTNode) accessedVariable;
            }

            Object binding = context.getLanguageServerContext().getSandbox().getBindings().get(variableExpression.getName());
            if (binding == null) {
                // DynamicVariable is not an ASTNode, so skip it
                return null;
            }

            return new VariableExpression(variableExpression.getName(), new ClassNode(binding.getClass()));
        } else if (node instanceof Variable) {
            return node;
        } else if (node instanceof MethodCallExpression methodCallExpression) {
            return getDefinition(methodCallExpression.getObjectExpression(), strict, context);
        } else if (node instanceof StaticMethodCallExpression staticMethodCallExpression) {
            return GroovyASTUtils.getMethodFromCallExpression(staticMethodCallExpression, context);
        }
        return null;
    }

    public static ASTNode getTypeDefinition(ASTNode node, ASTContext context) {
        ASTNode definitionNode = getDefinition(node, false, context);
        if (definitionNode == null) {
            return null;
        }
        if (definitionNode instanceof MethodNode method) {
            return tryToResolveOriginalClassNode(method.getReturnType(), true, context);
        } else if (definitionNode instanceof Variable variable) {
            return tryToResolveOriginalClassNode(variable.getOriginType(), true, context);
        }
        return null;
    }

    public static List<ASTNode> getReferences(ASTNode node, ASTContext context) {
        ASTNode definitionNode = getDefinition(node, true, context);
        if (definitionNode == null) {
            return Collections.emptyList();
        }
        return context.getVisitor().getNodes().stream().filter(otherNode -> {
            ASTNode otherDefinition = getDefinition(otherNode, false, context);
            return definitionNode.equals(otherDefinition) && node.getLineNumber() != -1 && node.getColumnNumber() != -1;
        }).collect(Collectors.toList());
    }

    private static ClassNode tryToResolveOriginalClassNode(ClassNode node, boolean strict, ASTContext context) {
        for (ClassNode originalNode : context.getVisitor().getClassNodes()) {
            if (originalNode.equals(node)) {
                return originalNode;
            }
        }
        if (strict) {
            return null;
        }
        return node;
    }

    public static PropertyNode getPropertyFromExpression(PropertyExpression node, ASTContext context) {
        ClassNode classNode = getTypeOfNode(node.getObjectExpression(), context);
        if (classNode == null) return null;
        fillClassNode(classNode);
        var prop = classNode.getProperty(node.getProperty().getText());
        var field = classNode.getField(node.getProperty().getText());

        if (prop == null && field != null) {
            prop = new PropertyNode(field, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, null, null);
        }

        return prop;
    }

    public static Object resolveDynamicValue(ASTNode node, ASTContext context) {
        if (node instanceof PropertyExpression propertyExpression) {
            var value = resolveDynamicValue(propertyExpression.getObjectExpression(), context);

            Object result = null;

            if (value != null) {
                try {
                    result = value.getClass().getDeclaredField(propertyExpression.getProperty().getText()).get(value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchFieldException e) {
                    return null;
                }
            }

            return result;
        } else if (node instanceof VariableExpression variableExpression) {
            return context.getLanguageServerContext().getSandbox().getBindings().get(variableExpression.getName());
        }
        return null;
    }

    public static FieldNode getFieldFromExpression(PropertyExpression node, ASTContext context) {
        ClassNode classNode = getTypeOfNode(node.getObjectExpression(), context);
        if (classNode != null) {
            return classNode.getField(node.getProperty().getText());
        }
        return null;
    }

    public static List<FieldNode> getFieldsForLeftSideOfPropertyExpression(ClassNode classNode, Expression expr, ASTContext context) {
        boolean statics = expr instanceof ClassExpression;
        return collectFields(
                classNode,
                new ArrayList<>(),
                node -> statics == node.isStatic() && (node.getModifiers() & HIDDEN_MARKER) == 0);
    }

    public static List<PropertyNode> getPropertiesForLeftSideOfPropertyExpression(ClassNode classNode,
                                                                                  Expression expr,
                                                                                  ASTContext context) {
        boolean statics = expr instanceof ClassExpression;
        return collectProperties(
                classNode,
                new ArrayList<>(),
                node -> statics == node.isStatic() && (node.getModifiers() & HIDDEN_MARKER) == 0);
    }

    public static List<MethodNode> getMethodsForLeftSideOfPropertyExpression(ClassNode classNode, Expression expr, ASTContext context) {
        boolean statics = expr instanceof ClassExpression;
        return collectMethods(
                classNode,
                new ArrayList<>(),
                node -> statics == node.isStatic() && (node.getModifiers() & HIDDEN_MARKER) == 0);
    }

    public static List<FieldNode> collectFields(ClassNode classNode, List<FieldNode> nodes, Predicate<FieldNode> test) {
        for (FieldNode node : classNode.getFields()) {
            if (test.test(node)) nodes.add(node);
        }
        for (ClassNode interfaze : classNode.getInterfaces()) {
            collectFields(interfaze, nodes, test);
        }
        if (classNode.getSuperClass() != null) {
            collectFields(classNode.getSuperClass(), nodes, test);
        }
        return nodes;
    }

    public static List<PropertyNode> collectProperties(ClassNode classNode, List<PropertyNode> nodes, Predicate<PropertyNode> test) {
        for (PropertyNode node : classNode.getProperties()) {
            if (test.test(node)) nodes.add(node);
        }
        for (ClassNode interfaze : classNode.getInterfaces()) {
            collectProperties(interfaze, nodes, test);
        }
        if (classNode.getSuperClass() != null) {
            collectProperties(classNode.getSuperClass(), nodes, test);
        }
        return nodes;
    }

    public static List<MethodNode> collectMethods(ClassNode classNode, List<MethodNode> nodes, Predicate<MethodNode> test) {
        for (MethodNode node : classNode.getMethods()) {
            if (test.test(node)) nodes.add(node);
        }
        for (ClassNode interfaze : classNode.getInterfaces()) {
            collectMethods(interfaze, nodes, test);
        }
        if (classNode.getSuperClass() != null) {
            collectMethods(classNode.getSuperClass(), nodes, test);
        }
        return nodes;
    }

    public static ClassNode getTypeOfNode(ASTNode node, ASTContext context) {
        if (node instanceof BinaryExpression binaryExpr) {
            Expression leftExpr = binaryExpr.getLeftExpression();
            if (binaryExpr.getOperation().getText().equals("[") && leftExpr.getType().isArray()) {
                return leftExpr.getType().getComponentType();
            }
        } else if (node instanceof ClassExpression expression) {
            // This means it's an expression like this: SomeClass.someProp
            return expression.getType();
        } else if (node instanceof ConstructorCallExpression expression) {
            return expression.getType();
        } else if (node instanceof MethodCallExpression expression) {
            AbstractObjectMapper<?> goh = getMapperOfNode(expression, context);
            if (goh != null) {
                return ClassHelper.makeCached(goh.getReturnType());
            }
            MethodNode methodNode = GroovyASTUtils.getMethodFromCallExpression(expression, context);
            if (methodNode != null) {
                return methodNode.getReturnType();
            }
            return expression.getType();
        } else if (node instanceof StaticMethodCallExpression expr) {
            MethodNode methodNode = GroovyASTUtils.getMethodFromCallExpression(expr, context);
            if (methodNode != null) {
                return methodNode.getReturnType();
            }
            return expr.getType();
        } else if (node instanceof PropertyExpression expression) {

            PropertyNode propNode = GroovyASTUtils.getPropertyFromExpression(expression, context);
            if (propNode != null) {
                return getTypeOfNode(propNode, context);
            }
            return expression.getType();
        } else if (node instanceof Variable var) {
            if (var.getName().equals("this")) {
                ClassNode enclosingClass = getEnclosingNodeOfType(node, ClassNode.class, context);
                if (enclosingClass != null) {
                    return enclosingClass;
                }
            } else if (var.isDynamicTyped()) {
                ASTNode defNode = GroovyASTUtils.getDefinition(node, false, context);
                if (defNode instanceof Variable defVar) {
                    if (defVar.hasInitialExpression()) {
                        return getTypeOfNode(defVar.getInitialExpression(), context);
                    } else if (!defVar.isDynamicTyped()) {
                        return defVar.getType();
                    } else {
                        ASTNode declNode = context.getVisitor().getParent(defNode);
                        if (declNode instanceof DeclarationExpression decl) {
                            return getTypeOfNode(decl.getRightExpression(), context);
                        }
                    }
                }
            }
            if (var.getOriginType() != null) {
                return var.getOriginType();
            }
        }
        if (node instanceof Expression expression) {
            return expression.getType();
        }
        return null;
    }

    public static List<MethodNode> getMethodOverloadsFromCallExpression(MethodCall node, ASTContext context) {
        if (node instanceof MethodCallExpression methodCallExpr) {
            List<MethodNode> mn = new ArrayList<>();
            if (methodCallExpr.isImplicitThis()) {
                Object o = context.getLanguageServerContext().getSandbox().getBindings().get(node.getMethodAsString());
                if (o instanceof AbstractObjectMapper<?>goh) {
                    mn.addAll(goh.getMethodNodes());
                } else if (o instanceof Closure<?>closure) {
                    mn.add(methodNodeOfClosure(node.getMethodAsString(), closure));
                }
            }
            ClassNode leftType = getTypeOfNode(methodCallExpr.getObjectExpression(), context);
            if (leftType != null) {
                fillClassNode(leftType);
                mn.addAll(leftType.getMethods(methodCallExpr.getMethod().getText()));
            }
            return mn;
        } else if (node instanceof ConstructorCallExpression constructorCallExpr) {
            ClassNode constructorType = constructorCallExpr.getType();
            if (constructorType != null) {
                fillClassNode(constructorType);
                return constructorType.getDeclaredConstructors()
                        .stream()
                        .map(constructor -> (MethodNode) constructor)
                        .collect(
                                Collectors.toList());
            }
        } else if (node instanceof StaticMethodCallExpression staticMethodCallExpression) {
            var ownerType = staticMethodCallExpression.getOwnerType();
            if (ownerType != null) {
                fillClassNode(ownerType);
                return ownerType.getMethods(staticMethodCallExpression.getMethod());
            }
        }
        return Collections.emptyList();
    }

    public static MethodNode getMethodFromCallExpression(MethodCall node, ASTContext context) {
        return getMethodFromCallExpression(node, context, -1);
    }

    public static MethodNode getMethodFromCallExpression(MethodCall node, ASTContext context, int argIndex) {
        List<MethodNode> possibleMethods = getMethodOverloadsFromCallExpression(node, context);
        if (!possibleMethods.isEmpty() && node.getArguments() instanceof ArgumentListExpression actualArguments) {
            MethodNode foundMethod = possibleMethods.stream().max(new Comparator<MethodNode>() {

                public int compare(MethodNode m1, MethodNode m2) {
                    Parameter[] p1 = m1.getParameters();
                    Parameter[] p2 = m2.getParameters();
                    int m1Value = calculateArgumentsScore(p1, actualArguments, argIndex);
                    int m2Value = calculateArgumentsScore(p2, actualArguments, argIndex);
                    if (m1Value > m2Value) {
                        return 1;
                    } else if (m1Value < m2Value) {
                        return -1;
                    }
                    return 0;
                }
            }).orElse(null);
            return foundMethod;
        }
        return null;
    }

    private static int calculateArgumentsScore(Parameter[] parameters, ArgumentListExpression arguments, int argIndex) {
        int score = 0;
        int paramCount = parameters.length;
        int expressionsCount = arguments.getExpressions().size();
        int argsCount = expressionsCount;
        if (argIndex >= argsCount) {
            argsCount = argIndex + 1;
        }
        int minCount = Math.min(paramCount, argsCount);
        if (minCount == 0 && paramCount == argsCount) {
            score++;
        }
        for (int i = 0; i < minCount; i++) {
            ClassNode argType = (i < expressionsCount) ? arguments.getExpression(i).getType() : null;
            ClassNode paramType = (i < paramCount) ? parameters[i].getType() : null;
            if (argType != null && paramType != null) {
                if (argType.equals(paramType)) {
                    // equal types are preferred
                    score += 1000;
                } else if (argType.isDerivedFrom(paramType)) {
                    // subtypes are nice, but less important
                    score += 100;
                } else {
                    // if a type doesn't match at all, it's not worth much
                    score++;
                }
            } else if (paramType != null) {
                // extra parameters are like a type not matching
                score++;
            }
        }
        return score;
    }

    public static Range findAddImportRange(URI uri, ASTNode offsetNode, ASTContext context) {
        ModuleNode moduleNode = GroovyASTUtils.getEnclosingNodeOfType(offsetNode, ModuleNode.class, context);
        if (moduleNode == null) {
            return new Range(new Position(0, 0), new Position(0, 0));
        }
        ASTNode afterNode = null;
        List<ImportNode> importNodes = moduleNode.getImports();
        if (!importNodes.isEmpty()) {
            // auto added imports don't have a line number,
            // so we need to iterate all imports and see which one is the most bottom one
            for (ImportNode node : importNodes) {
                if (node.getLastLineNumber() < 0) continue;
                if (afterNode == null || node.getLastLineNumber() > afterNode.getLastLineNumber()) {
                    afterNode = node;
                }
            }
        }
        if (afterNode == null) {
            afterNode = moduleNode.getPackage();
        }
        if (afterNode == null) {
            int line = Preprocessor.getImportStartLine(new File(uri));
            Position p = new Position(line, 0);
            return new Range(p, p);
        }
        Range nodeRange = GroovyLSUtils.astNodeToRange(afterNode);
        if (nodeRange == null) {
            return new Range(new Position(0, 0), new Position(0, 0));
        }
        Position position = new Position(nodeRange.getEnd().getLine() + 1, 0);
        return new Range(position, position);
    }

    public static MethodNode methodNodeOfClosure(String name, Closure<?> closure) {
        Class<?> declarer = closure.getThisObject() == null ? (closure.getOwner() == null ? Object.class : closure.getOwner().getClass()) : closure.getThisObject().getClass();
        MethodNode method = new MethodNode(
                name,
                Modifier.PUBLIC,
                ClassHelper.OBJECT_TYPE,
                closure.getParameterTypes() != null
                        ? ArrayUtils.map(
                                closure.getParameterTypes(),
                                c -> new Parameter(ClassHelper.makeCached(c), ""),
                                new Parameter[closure.getParameterTypes().length])
                        : new Parameter[0],
                null,
                null);
        method.setDeclaringClass(ClassHelper.makeCached(declarer));
        return method;
    }

    public static AbstractObjectMapper<?> getMapperOfNode(MethodCallExpression expr, ASTContext context) {
        if (expr.isImplicitThis()) {
            return ObjectMapperManager.getObjectMapper(expr.getMethodAsString());
        }
        ClassNode type = getTypeOfNode(expr.getObjectExpression(), context);
        if (type != null) {
            return ObjectMapperManager.getObjectMapper(type.getTypeClass(), expr.getMethodAsString());
        }
        return null;
    }

    public static void fillClassNode(ClassNode classNode) {
        if (!classNode.isResolved()) return;
        Class<?> clazz;
        try {
            clazz = classNode.getTypeClass();
        } catch (Exception ignored) {
            return;
        }
        MetaClass mc = GroovySystem.getMetaClassRegistry().getMetaClass(clazz);
        if (mc instanceof ExpandoMetaClass emc) {
            for (MetaMethod mm : emc.getExpandoMethods()) {
                if (mm.isPrivate()) continue;
                int m = mm.getModifiers();
                if (mm instanceof Hidden hidden && hidden.isHidden()) m |= HIDDEN_MARKER;
                Parameter[] params = ArrayUtils.map(
                        mm.getNativeParameterTypes(),
                        c -> new Parameter(ClassHelper.makeCached(c), ""),
                        new Parameter[mm.getNativeParameterTypes().length]);
                MethodNode node = new MethodNode(mm.getName(), m, ClassHelper.makeCached(mm.getReturnType()), params, null, null);
                node.setDeclaringClass(classNode);
                if (mm instanceof IDocumented documented && documented.getDocumentation() != null) {
                    node.setNodeMetaData(GroovydocHolder.DOC_COMMENT, new Groovydoc(documented.getDocumentation(), node));
                }
                classNode.addMethod(node);
            }
            for (MetaProperty mp : emc.getExpandoProperties()) {
                int m = mp.getModifiers();
                if (mp instanceof Hidden hidden && hidden.isHidden()) m |= HIDDEN_MARKER;
                FieldNode field = new FieldNode(mp.getName(), m, ClassHelper.makeCached(mp.getType()), classNode.redirect(), null);
                PropertyNode property = makeProperty(classNode, field, m);
                classNode.addProperty(property);
            }
        }
    }

    private static @NotNull PropertyNode makeProperty(ClassNode classNode, FieldNode field, int m) {
        PropertyNode property = new PropertyNode(field, m, null, null);
        property.setDeclaringClass(classNode);
        // remove any previous set fields and properties with the same name
        List<PropertyNode> properties = classNode.getProperties();
        for (int i = 0; i < properties.size(); i++) {
            PropertyNode node = properties.get(i);
            if (node.getName().equals(property.getName())) {
                properties.remove(i--);
            }
        }
        List<FieldNode> fields = classNode.getFields();
        for (int i = 0; i < fields.size(); i++) {
            FieldNode node = fields.get(i);
            if (node.getName().equals(property.getName())) {
                fields.remove(i--);
            }
        }
        return property;
    }
}
