package com.cleanroommc.groovyscript.mixin;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CompileUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.kohsuke.groovy.sandbox.AliasClassNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(value = CompileUnit.class, remap = false)
public class CompileUnitMixin {

    @Shadow
    @Final
    private Map<String, ClassNode> classes;

    @Shadow
    @Final
    private Map<String, ClassNode> classesToCompile;

    /**
     * @author brachy84
     */
    @Overwrite
    public void addClass(ClassNode node) {
        String name;
        if (node instanceof AliasClassNode) {
            name = ((AliasClassNode) node).getAliasName();
        } else {
            node = node.redirect();
            name = node.getName();
        }
        ClassNode stored = classes.get(name);
        if (stored != null && stored != node) {
            // we have a duplicate class!
            // One possibility for this is, that we declared a script and a
            // class in the same file and named the class like the file
            SourceUnit nodeSource = node.getModule().getContext();
            SourceUnit storedSource = stored.getModule().getContext();
            String txt = "Invalid duplicate class definition of class " + node.getName() + " : ";
            if (nodeSource == storedSource) {
                // same class in same source
                txt += "The source " + nodeSource.getName() + " contains at least two definitions of the class " + node.getName() + ".\n";
                if (node.isScriptBody() || stored.isScriptBody()) {
                    txt += "One of the classes is an explicit generated class using the class statement, the other is a class generated from" +
                            " the script body based on the file name. Solutions are to change the file name or to change the class name.\n";
                }
            } else {
                txt += "The sources " + nodeSource.getName() + " and " + storedSource.getName() + " each contain a class with the name " + node.getName() + ".\n";
            }
            nodeSource.getErrorCollector().addErrorAndContinue(
                    new SyntaxErrorMessage(new SyntaxException(txt, node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()), nodeSource)
            );
        }
        classes.put(name, node);

        ClassNode cn = classesToCompile.get(name);
        if (cn != null) {
            cn.setRedirect(node);
            classesToCompile.remove(name);
        }
    }
}
