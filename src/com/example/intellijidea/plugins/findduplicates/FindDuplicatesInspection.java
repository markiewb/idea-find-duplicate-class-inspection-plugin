package com.example.intellijidea.plugins.findduplicates;

import com.intellij.codeInspection.*;
import org.jetbrains.annotations.*;

/**
 * Example of an inspection, which finds a class with the same fully qualified name. The files can be diffed or deleted.
 * @author markiewb
 */
public class FindDuplicatesInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public String getDisplayName() {
        return "Duplicate classes in classpath (same fully qualified name)";
    }

    @NotNull
    @Override
    public FindDuplicatesInspectionVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new FindDuplicatesInspectionVisitor(holder);
    }
}
