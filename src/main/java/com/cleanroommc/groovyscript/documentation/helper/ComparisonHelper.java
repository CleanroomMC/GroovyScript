package com.cleanroommc.groovyscript.documentation.helper;

import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.documentation.Builder;
import com.cleanroommc.groovyscript.documentation.helper.descriptor.DescriptorHelper;
import com.cleanroommc.groovyscript.documentation.helper.descriptor.MethodAnnotation;
import com.google.common.collect.ComparisonChain;

import java.lang.reflect.Method;

/**
 * A helper class for comparing various documentation elements against each other.
 *
 */
public final class ComparisonHelper {

    private ComparisonHelper() {}

    public static int iNamed(INamed left, INamed right) {
        return ComparisonChain.start()
                .compare(left.getClass().getAnnotation(RegistryDescription.class).priority(), right.getClass().getAnnotation(RegistryDescription.class).priority())
                .compare(left.getName(), right.getName())
                .result();
    }

    public static int method(MethodAnnotation<MethodDescription> left, MethodAnnotation<MethodDescription> right) {
        return ComparisonHelper.comparePriorityAndMethod(left.annotation().priority(), right.annotation().priority(), left.method(), right.method());
    }

    public static int recipeBuilder(MethodAnnotation<RecipeBuilderDescription> left, MethodAnnotation<RecipeBuilderDescription> right) {
        return ComparisonHelper.comparePriorityAndMethod(left.annotation().priority(), right.annotation().priority(), left.method(), right.method());
    }

    public static int example(Example left, Example right) {
        var leftValue = left.value();
        var rightValue = right.value();
        return ComparisonChain.start()
                .compare(left.priority(), right.priority())
                .compareFalseFirst(left.commented(), right.commented())
                .compare(leftValue.length(), rightValue.length())
                .compare(leftValue, rightValue)
                .result();
    }

    public static int recipeBuilderMethod(MethodAnnotation<RecipeBuilderMethodDescription> left, MethodAnnotation<RecipeBuilderMethodDescription> right) {
        String leftSignature = DescriptorHelper.shortSignature(left.method());
        String rightSignature = DescriptorHelper.shortSignature(right.method());
        String leftPart = leftSignature.substring(0, leftSignature.indexOf("("));
        String rightPart = rightSignature.substring(0, rightSignature.indexOf("("));
        return ComparisonChain.start()
                .compare(left.annotation().priority(), right.annotation().priority())
                .compare(leftPart.length(), rightPart.length())
                .compare(leftPart, rightPart, String::compareToIgnoreCase)
                .compare(leftSignature.length(), rightSignature.length())
                .compare(leftSignature, rightSignature, String::compareToIgnoreCase)
                .result();
    }

    /**
     * In order to ensure the filtering process filters out the correct entries,
     * we sort the stream before filtering.
     * The way we sort is different from the usual {@link #recipeBuilderRegistration},
     * and targets hierarchy and tries to skip returns of Object.
     */
    public static int recipeBuilderRegistrationHierarchy(MethodAnnotation<RecipeBuilderRegistrationMethod> left, MethodAnnotation<RecipeBuilderRegistrationMethod> right) {
        return ComparisonChain.start()
                .compare(left.annotation().hierarchy(), right.annotation().hierarchy())
                // Specifically de-prioritize Object classes
                .compareFalseFirst(left.method().getReturnType() == Object.class, right.method().getReturnType() == Object.class)
                .result();
    }

    public static int recipeBuilderRegistration(MethodAnnotation<RecipeBuilderRegistrationMethod> left, MethodAnnotation<RecipeBuilderRegistrationMethod> right) {
        var leftName = left.method().getName();
        var rightName = right.method().getName();
        return ComparisonChain.start()
                .compare(left.annotation().priority(), right.annotation().priority())
                .compare(leftName.length(), rightName.length())
                .compare(leftName, rightName, String::compareToIgnoreCase)
                .result();
    }

    public static int field(Builder.FieldDocumentation left, Builder.FieldDocumentation right) {
        var leftName = left.getField().getName();
        var rightName = right.getField().getName();
        return ComparisonChain.start()
                .compare(left.priority(), right.priority())
                .compare(leftName.length(), rightName.length())
                .compare(leftName, rightName, String::compareToIgnoreCase)
                .result();
    }

    public static int property(Property left, Property right) {
        return ComparisonChain.start()
                .compare(left.hierarchy(), right.hierarchy())
                .result();
    }

    @SuppressWarnings("deprecation")
    public static int comp(Comp left, Comp right) {
        return ComparisonChain.start()
                .compare(left.type(), right.type())
                .result();
    }

    private static int comparePriorityAndMethod(int leftPriority, int rightPriority, Method leftMethod, Method rightName) {
        return ComparisonChain.start()
                .compare(leftPriority, rightPriority)
                .compare(leftMethod.getName(), rightName.getName(), String::compareToIgnoreCase)
                .compare(DescriptorHelper.simpleParameters(leftMethod), DescriptorHelper.simpleParameters(rightName), String::compareToIgnoreCase)
                .result();

    }
}
