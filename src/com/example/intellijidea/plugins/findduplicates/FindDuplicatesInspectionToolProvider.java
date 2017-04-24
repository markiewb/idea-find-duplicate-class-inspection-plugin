package com.example.intellijidea.plugins.findduplicates;

import com.intellij.codeInspection.InspectionToolProvider;

/**
 * @author markiewb
 */
public class FindDuplicatesInspectionToolProvider implements InspectionToolProvider {
  public Class[] getInspectionClasses() {
    return new Class[]{FindDuplicatesInspection.class};
  }
}
