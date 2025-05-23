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
package net.prominic.groovyls.util;

import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.compiler.util.GroovyASTUtils;
import org.codehaus.groovy.ast.*;

public class GroovyNodeToStringUtils {

    private static final String JAVA_OBJECT = "java.lang.Object";

    public static String classToString(ClassNode classNode, ASTContext astContext) {
        StringBuilder builder = new StringBuilder();
        String packageName = classNode.getPackageName();
        if (packageName != null && packageName.length() > 0) {
            builder.append("package ");
            builder.append(packageName);
            builder.append("\n");
        }
        if (!classNode.isSyntheticPublic()) {
            builder.append("public ");
        }
        if (classNode.isAbstract()) {
            builder.append("abstract ");
        }
        if (classNode.isInterface()) {
            builder.append("interface ");
        } else if (classNode.isEnum()) {
            builder.append("enum ");
        } else {
            builder.append("class ");
        }
        builder.append(classNode.getNameWithoutPackage());

        ClassNode superClass = null;
        try {
            superClass = classNode.getSuperClass();
        } catch (NoClassDefFoundError e) {
            // this is fine, we'll just treat it as null
        }
        if (superClass != null && !superClass.getName().equals(JAVA_OBJECT)) {
            builder.append(" extends ");
            builder.append(superClass.getNameWithoutPackage());
        }
        return builder.toString();
    }

    public static String constructorToString(ConstructorNode constructorNode, ASTContext astContext) {
        String builder = constructorNode.getDeclaringClass().getName() + "(" + parametersToString(constructorNode.getParameters(), astContext) + ")";
        return builder;
    }

    public static String methodToString(MethodNode methodNode, ASTContext astContext) {
        if (methodNode instanceof ConstructorNode) {
            return constructorToString((ConstructorNode) methodNode, astContext);
        }
        StringBuilder builder = new StringBuilder();
        if (methodNode.isPublic()) {
            if (!methodNode.isSyntheticPublic()) {
                builder.append("public ");
            }
        } else if (methodNode.isProtected()) {
            builder.append("protected ");
        } else if (methodNode.isPrivate()) {
            builder.append("private ");
        }

        if (methodNode.isStatic()) {
            builder.append("static ");
        }

        if (methodNode.isFinal()) {
            builder.append("final ");
        }
        ClassNode returnType = methodNode.getReturnType();
        builder.append(returnType.getNameWithoutPackage());
        builder.append(" ");
        builder.append(methodNode.getName());
        builder.append("(");
        builder.append(parametersToString(methodNode.getParameters(), astContext));
        builder.append(")");
        return builder.toString();
    }

    public static String parametersToString(Parameter[] params, ASTContext astContext) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            Parameter paramNode = params[i];
            builder.append(variableToString(paramNode, astContext));
        }
        return builder.toString();
    }

    public static String variableToString(Variable variable, ASTContext astContext) {
        StringBuilder builder = new StringBuilder();
        if (variable instanceof FieldNode fieldNode) {
            if (fieldNode.isPublic()) {
                builder.append("public ");
            }
            if (fieldNode.isProtected()) {
                builder.append("protected ");
            }
            if (fieldNode.isPrivate()) {
                builder.append("private ");
            }

            if (fieldNode.isFinal()) {
                builder.append("final ");
            }

            if (fieldNode.isStatic()) {
                builder.append("static ");
            }
        }
        ClassNode varType = null;
        if (variable instanceof ASTNode) {
            varType = GroovyASTUtils.getTypeOfNode((ASTNode) variable, astContext);
        } else {
            varType = variable.getType();
        }
        builder.append(varType.getNameWithoutPackage());
        builder.append(" ");
        builder.append(variable.getName());
        return builder.toString();
    }
}
