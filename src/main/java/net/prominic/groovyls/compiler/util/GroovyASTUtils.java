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

import com.cleanroommc.groovyscript.api.IDynamicGroovyProperty;
import com.cleanroommc.groovyscript.gameobjects.GameObjectHandlerManager;
import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.util.ClassGraphUtils;
import net.prominic.groovyls.util.GroovyLanguageServerUtils;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GroovyASTUtils {

    public static ASTNode getEnclosingNodeOfType(ASTNode offsetNode, Class<? extends ASTNode> nodeType,
                                                 ASTContext context) {
        ASTNode current = offsetNode;
        while (current != null) {
            if (nodeType.isInstance(current)) {
                return current;
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
        if (node instanceof ExpressionStatement) {
            ExpressionStatement statement = (ExpressionStatement) node;
            node = statement.getExpression();
        }
        if (node instanceof ClassNode) {
            return tryToResolveOriginalClassNode((ClassNode) node, strict, context);
        } else if (node instanceof ConstructorCallExpression) {
            ConstructorCallExpression callExpression = (ConstructorCallExpression) node;
            return GroovyASTUtils.getMethodFromCallExpression(callExpression, context);
        } else if (node instanceof DeclarationExpression) {
            DeclarationExpression declExpression = (DeclarationExpression) node;
            if (!declExpression.isMultipleAssignmentDeclaration()) {
                ClassNode originType = declExpression.getVariableExpression().getOriginType();
                return tryToResolveOriginalClassNode(originType, strict, context);
            }
        } else if (node instanceof ClassExpression) {
            ClassExpression classExpression = (ClassExpression) node;
            return tryToResolveOriginalClassNode(classExpression.getType(), strict, context);
        } else if (node instanceof ImportNode) {
            ImportNode importNode = (ImportNode) node;
            return tryToResolveOriginalClassNode(importNode.getType(), strict, context);
        } else if (node instanceof MethodNode) {
            return node;
        } else if (node instanceof ConstantExpression && parentNode != null) {
            if (parentNode instanceof MethodCallExpression) {
                MethodCallExpression methodCallExpression = (MethodCallExpression) parentNode;
                return GroovyASTUtils.getMethodFromCallExpression(methodCallExpression, context);
            } else if (parentNode instanceof PropertyExpression) {
                PropertyExpression propertyExpression = (PropertyExpression) parentNode;
                PropertyNode propNode = GroovyASTUtils.getPropertyFromExpression(propertyExpression, context);
                if (propNode != null) {
                    return propNode;
                }
                return GroovyASTUtils.getFieldFromExpression(propertyExpression, context);
            }
        } else if (node instanceof VariableExpression) {
            VariableExpression variableExpression = (VariableExpression) node;
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
            var gameObjectName = getAccessedGameObjectName(staticMethodCallExpression);

            if (gameObjectName != null) {
                var gameObjectReturnType = GameObjectHandlerManager.getReturnTypeOf(gameObjectName);

                if (gameObjectReturnType == null) {
                    return null;
                }

                var methodNode = new MethodNode(gameObjectName, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                                                ClassHelper.makeCached(gameObjectReturnType),
                                                new Parameter[]{
                                                        new Parameter(ClassHelper.makeCached(String.class), "mainArg"),
                                                        new Parameter(ClassHelper.makeCached(Object[].class), "args")
                                                },
                                                null,
                                                null
                );

                methodNode.setDeclaringClass(ClassHelper.makeCached(GameObjectHandlerManager.class));

                return methodNode;
            }

            return GroovyASTUtils.getMethodFromCallExpression(staticMethodCallExpression, context);
        }
        return null;
    }

    private static @Nullable String getAccessedGameObjectName(StaticMethodCallExpression staticMethodCallExpression) {
        return staticMethodCallExpression.getOwnerType().equals(ClassHelper.makeCached(GameObjectHandlerManager.class)) &&
               staticMethodCallExpression.getMethod().equals("getGameObject") &&
               staticMethodCallExpression.getArguments() instanceof ArgumentListExpression argumentListExpression &&
               !argumentListExpression.getExpressions().isEmpty() &&
               argumentListExpression.getExpression(0) instanceof ConstantExpression objectNameConstantExpression &&
               GameObjectHandlerManager.hasGameObjectHandler(objectNameConstantExpression.getText()) ?
               objectNameConstantExpression.getText() :
               null;
    }

    public static ASTNode getTypeDefinition(ASTNode node, ASTContext context) {
        ASTNode definitionNode = getDefinition(node, false, context);
        if (definitionNode == null) {
            return null;
        }
        if (definitionNode instanceof MethodNode) {
            MethodNode method = (MethodNode) definitionNode;
            return tryToResolveOriginalClassNode(method.getReturnType(), true, context);
        } else if (definitionNode instanceof Variable) {
            Variable variable = (Variable) definitionNode;
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
        if (classNode != null && classNode.implementsInterface(new ClassNode(IDynamicGroovyProperty.class))) {
            var value = resolveDynamicValue(node, context);

            if (value != null) {
                return new PropertyNode(node.getProperty().getText(), Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                                        new ClassNode(value.getClass()),
                                        classNode,
                                        null, null, null);
            }
        }
        if (classNode != null) {
            var prop = classNode.getProperty(node.getProperty().getText());
            var field = classNode.getField(node.getProperty().getText());

            if (prop == null && field != null) {
                prop = new PropertyNode(field, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, null, null);
            }

            return prop;
        }
        return null;
    }

    public static Object resolveDynamicValue(ASTNode node, ASTContext context) {
        if (node instanceof PropertyExpression propertyExpression) {
            var value = resolveDynamicValue(propertyExpression.getObjectExpression(), context);

            Object result = null;
            if (value instanceof IDynamicGroovyProperty dynamicValue) {
                result = dynamicValue.getProperty(propertyExpression.getProperty().getText());
            }

            if (value != null && result == null) {
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

    public static List<FieldNode> getFieldsForLeftSideOfPropertyExpression(Expression node, ASTContext context) {
        ClassNode classNode = getTypeOfNode(node, context);

        if (classNode != null && node instanceof VariableExpression) {
            var binding = context.getLanguageServerContext().getSandbox().getBindings().get(((VariableExpression) node).getName());
            var classInfo = ClassGraphUtils.resolveAllowedClassInfo(classNode, context);

            if (classInfo != null && binding != null && (classInfo.loadClass().equals(IDynamicGroovyProperty.class) || classInfo.implementsInterface(IDynamicGroovyProperty.class))) {
                final ClassNode finalClassNode = classNode;
                return ((IDynamicGroovyProperty) binding).getProperties().entrySet().stream()
                        .filter(entry -> entry.getValue() != null)
                        .map(entry -> new FieldNode(entry.getKey(), Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                                                    new ClassNode(entry.getValue().getClass()),
                                                    finalClassNode,
                                                    new ConstantExpression(entry.getValue())))
                        .collect(Collectors.toList());
            }
        }

        if (classNode != null) {
            boolean statics = node instanceof ClassExpression;
            return classNode.getFields().stream().filter(fieldNode -> {
                return statics ? fieldNode.isStatic() : !fieldNode.isStatic();
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static List<PropertyNode> getPropertiesForLeftSideOfPropertyExpression(Expression node,
                                                                                  ASTContext context) {
        ClassNode classNode = getTypeOfNode(node, context);
        if (classNode != null) {
            boolean statics = node instanceof ClassExpression;
            return classNode.getProperties().stream().filter(propNode -> {
                return statics ? propNode.isStatic() : !propNode.isStatic();
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static List<MethodNode> getMethodsForLeftSideOfPropertyExpression(Expression node,
                                                                             ASTContext context) {
        ClassNode classNode = getTypeOfNode(node, context);
        if (classNode != null) {
            boolean statics = node instanceof ClassExpression;
            return classNode.getMethods().stream().filter(methodNode -> {
                return statics ? methodNode.isStatic() : !methodNode.isStatic();
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static ClassNode getTypeOfNode(ASTNode node, ASTContext context) {
        if (node instanceof BinaryExpression) {
            BinaryExpression binaryExpr = (BinaryExpression) node;
            Expression leftExpr = binaryExpr.getLeftExpression();
            if (binaryExpr.getOperation().getText().equals("[") && leftExpr.getType().isArray()) {
                return leftExpr.getType().getComponentType();
            }
        } else if (node instanceof ClassExpression) {
            ClassExpression expression = (ClassExpression) node;
            // This means it's an expression like this: SomeClass.someProp
            return expression.getType();
        } else if (node instanceof ConstructorCallExpression) {
            ConstructorCallExpression expression = (ConstructorCallExpression) node;
            return expression.getType();
        } else if (node instanceof MethodCallExpression) {
            MethodCallExpression expression = (MethodCallExpression) node;
            MethodNode methodNode = GroovyASTUtils.getMethodFromCallExpression(expression, context);
            if (methodNode != null) {
                return methodNode.getReturnType();
            }
            return expression.getType();
        } else if (node instanceof StaticMethodCallExpression expr) {
            var gameObjectName = getAccessedGameObjectName(expr);
            if (gameObjectName != null) {
                return ClassHelper.makeCached(GameObjectHandlerManager.getReturnTypeOf(gameObjectName));
            }

            MethodNode methodNode = GroovyASTUtils.getMethodFromCallExpression(expr, context);
            if (methodNode != null) {
                return methodNode.getReturnType();
            }
            return expr.getType();
        } else if (node instanceof PropertyExpression) {
            PropertyExpression expression = (PropertyExpression) node;

            PropertyNode propNode = GroovyASTUtils.getPropertyFromExpression(expression, context);
            if (propNode != null) {
                return getTypeOfNode(propNode, context);
            }
            return expression.getType();
        } else if (node instanceof Variable) {
            Variable var = (Variable) node;
            if (var.getName().equals("this")) {
                ClassNode enclosingClass = (ClassNode) getEnclosingNodeOfType(node, ClassNode.class, context);
                if (enclosingClass != null) {
                    return enclosingClass;
                }
            } else if (var.isDynamicTyped()) {
                ASTNode defNode = GroovyASTUtils.getDefinition(node, false, context);
                if (defNode instanceof Variable) {
                    Variable defVar = (Variable) defNode;
                    if (defVar.hasInitialExpression()) {
                        return getTypeOfNode(defVar.getInitialExpression(), context);
                    } else if (!defVar.isDynamicTyped()) {
                        return defVar.getType();
                    } else {
                        ASTNode declNode = context.getVisitor().getParent(defNode);
                        if (declNode instanceof DeclarationExpression) {
                            DeclarationExpression decl = (DeclarationExpression) declNode;
                            return getTypeOfNode(decl.getRightExpression(), context);
                        }
                    }
                }
            }
            if (var.getOriginType() != null) {
                return var.getOriginType();
            }
        }
        if (node instanceof Expression) {
            Expression expression = (Expression) node;
            return expression.getType();
        }
        return null;
    }

    public static List<MethodNode> getMethodOverloadsFromCallExpression(MethodCall node, ASTContext context) {
        if (node instanceof MethodCallExpression) {
            MethodCallExpression methodCallExpr = (MethodCallExpression) node;
            ClassNode leftType = getTypeOfNode(methodCallExpr.getObjectExpression(), context);
            if (leftType != null) {
                return leftType.getMethods(methodCallExpr.getMethod().getText());
            }
        } else if (node instanceof ConstructorCallExpression) {
            ConstructorCallExpression constructorCallExpr = (ConstructorCallExpression) node;
            ClassNode constructorType = constructorCallExpr.getType();
            if (constructorType != null) {
                return constructorType.getDeclaredConstructors().stream().map(constructor -> (MethodNode) constructor)
                        .collect(Collectors.toList());
            }
        } else if (node instanceof StaticMethodCallExpression staticMethodCallExpression) {
            var ownerType = staticMethodCallExpression.getOwnerType();
            if (ownerType != null) {
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
        if (!possibleMethods.isEmpty() && node.getArguments() instanceof ArgumentListExpression) {
            ArgumentListExpression actualArguments = (ArgumentListExpression) node.getArguments();
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

    public static Range findAddImportRange(ASTNode offsetNode, ASTContext context) {
        ModuleNode moduleNode = (ModuleNode) GroovyASTUtils.getEnclosingNodeOfType(offsetNode, ModuleNode.class,
                                                                                   context);
        if (moduleNode == null) {
            return new Range(new Position(0, 0), new Position(0, 0));
        }
        ASTNode afterNode = null;
        if (afterNode == null) {
            List<ImportNode> importNodes = moduleNode.getImports();
            if (importNodes.size() > 0) {
                afterNode = importNodes.get(importNodes.size() - 1);
            }
        }
        if (afterNode == null) {
            afterNode = moduleNode.getPackage();
        }
        if (afterNode == null) {
            return new Range(new Position(0, 0), new Position(0, 0));
        }
        Range nodeRange = GroovyLanguageServerUtils.astNodeToRange(afterNode);
        if (nodeRange == null) {
            return new Range(new Position(0, 0), new Position(0, 0));
        }
        Position position = new Position(nodeRange.getEnd().getLine() + 1, 0);
        return new Range(position, position);
    }
}