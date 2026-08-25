package de.markiewb.idea.findduplicates;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class FindDuplicatesInspectionTest extends LightPlatformCodeInsightFixture4TestCase {
    @BeforeEach
    public void setUpFixture() throws Exception {
        setUp();
    }

    @AfterEach
    public void tearDownFixture() throws Exception {
        tearDown();
    }

    @Override
    protected String getTestDataPath() {
        return "";
    }

    @Test
    public void testDuplicateClassIsReportedWithBothFixes() {
        myFixture.addFileToProject("src/one/Foo.java", "package duplicate; <warning descr=\"Found: /src/src/two/Foo.java\">public class <error descr=\"Duplicate class found in the file '/src/src/two/Foo.java'\">Foo</error> {}</warning>");
        myFixture.addFileToProject("src/two/Foo.java", "package duplicate; public class Foo {}");

        LocalInspectionTool inspection = new FindDuplicatesInspection();
        myFixture.enableInspections(inspection);
        myFixture.configureFromExistingVirtualFile(myFixture.findFileInTempDir("src/one/Foo.java"));

        myFixture.testHighlighting();
        List<IntentionAction> fixes = myFixture.getAllQuickFixes();
        org.junit.jupiter.api.Assertions.assertTrue(fixes.stream().anyMatch(fix -> "Diff...".equals(fix.getText())));
        org.junit.jupiter.api.Assertions.assertTrue(fixes.stream().anyMatch(fix -> fix.getText().startsWith("Remove local '")));
    }

    @Test
    public void testUniqueClassIsNotReported() {
        myFixture.configureByText("Unique.java", "package sample; class Unique {}");
        myFixture.enableInspections(new FindDuplicatesInspection());
        myFixture.testHighlighting();
    }

    @Test
    public void testSameSimpleNameInDifferentPackagesIsNotReported() {
        myFixture.addFileToProject("src/one/Foo.java", "package one; public class Foo {}");
        myFixture.addFileToProject("src/two/Foo.java", "package two; public class Foo {}");
        myFixture.configureByText("Use.java", "class Use {}");
        myFixture.enableInspections(new FindDuplicatesInspection());
        myFixture.testHighlighting();
    }

    @Test
    public void testRemoveFixDeletesOnlyLocalDuplicate() {
        addDuplicateFiles();
        myFixture.enableInspections(new FindDuplicatesInspection());
        myFixture.configureFromExistingVirtualFile(myFixture.findFileInTempDir("src/one/Foo.java"));

        IntentionAction removeFix = myFixture.getAllQuickFixes().stream()
                .filter(fix -> fix.getText().startsWith("Remove local '"))
                .findFirst().orElseThrow();
        myFixture.launchAction(removeFix);

        org.junit.jupiter.api.Assertions.assertNull(myFixture.findFileInTempDir("src/one/Foo.java"));
        org.junit.jupiter.api.Assertions.assertNotNull(myFixture.findFileInTempDir("src/two/Foo.java"));
    }

    private void addDuplicateFiles() {
        myFixture.addFileToProject("src/one/Foo.java", "package duplicate; public class Foo {}");
        myFixture.addFileToProject("src/two/Foo.java", "package duplicate; public class Foo {}");
    }
}
