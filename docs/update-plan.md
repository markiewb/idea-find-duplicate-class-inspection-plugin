# Update Plan: Find Duplicates Inspection Plugin

Status: Prepared for execution
Date: 2026-08-25

## Objective

Modernize the plugin APIs and registration without changing its functional behavior:

- Detect classes with the same fully qualified name.
- Offer a diff quick-fix for duplicate files.
- Offer a removal quick-fix for writable local files.
- Remain runnable with IntelliJ IDEA Community Edition.

## Platform Constraint

The target platform is exclusively IntelliJ IDEA Community Edition 2024.3,
platform build `243.*`.

The `since-build` value may be raised to `243`, but must not be raised to a
platform version from 2025 or later. No IntelliJ IDEA Ultimate distribution and
no Ultimate-only dependency may be introduced.

The existing module dependencies remain valid and must be preserved unless the
Community Edition verifier proves a dependency unnecessary:

```xml
<depends>com.intellij.modules.platform</depends>
<depends>com.intellij.modules.lang</depends>
<depends>com.intellij.modules.java</depends>
```

## Execution Order

### 1. Add characterization tests first

Before changing the plugin APIs or registration, add a Gradle-based IntelliJ
plugin test setup targeting IntelliJ IDEA Community Edition 2024.3. The tests
must initially exercise the existing behavior and establish a baseline.

Add the following happy-path tests:

- Two Java files with the same fully qualified class name produce an inspection
  problem.
- The reported problem contains the expected duplicate-file information.
- The `Diff...` quick-fix is offered for a detected duplicate.
- The `Remove local ...` quick-fix is offered when the local file is writable.
- A single class with a unique fully qualified name produces no problem.
- Classes with the same simple name in different packages produce no problem.
- Applying the remove quick-fix deletes the local duplicate and preserves the
  other file.
- The diff quick-fix creates a request for the two expected files. If opening
  the UI cannot be tested reliably, test the fix's request/file selection at
  the non-UI level.

Run these tests against the unchanged implementation before proceeding. If
the old project structure prevents execution, add only the required test/build
infrastructure first; do not mix API modernization into this baseline step.

### 2. Replace inspection registration

Update `resources/META-INF/plugin.xml`:

- Replace the `inspectionToolProvider` extension with direct `localInspection`
  registration.
- Register `FindDuplicatesInspection` as the implementation class.
- Keep the inspection language set to `JAVA`.
- Preserve the display name and default-enabled behavior.
- Configure the inspection category with `groupName="Probable Bugs"`.

Remove `FindDuplicatesInspectionToolProvider.java` after the direct registration
is verified and no longer referenced.

### 3. Remove the deprecated inspection category API

Update `FindDuplicatesInspection.java`:

- Remove the `GroupNames` import.
- Remove the `getGroupDisplayName()` override.
- Keep `getDisplayName()` and `buildVisitor(...)` behavior unchanged.

This removes the `GroupNames.BUGS_GROUP_NAME` usage, which is marked
`@Deprecated(forRemoval = true)` by the IntelliJ Platform.

### 4. Update writable-file handling

Update `FindDuplicatesInspectionVisitor.java`:

- Replace `ReadonlyStatusHandler.ensureFilesWritable(VirtualFile...)`, which is
  deprecated, with the current Collection-based or static project-based API.
- Check the result of the writable-file operation.
- Abort the remove fix when the file remains read-only.
- Keep the deletion inside the appropriate write action and command so undo
  behavior remains intact.

Do not change the duplicate detection or diff behavior in this step.

### 5. Raise the minimum build only to 2024.3

Update `resources/META-INF/plugin.xml` from:

```xml
<idea-version since-build="131"/>
```

to:

```xml
<idea-version since-build="243"/>
```

Do not add an Ultimate product target. Do not set a 2025-or-later build.

### 6. Re-run tests and compatibility checks

Run the complete characterization test suite after every modernization step
and once as the final regression run.

Verify all of the following against IntelliJ IDEA Community Edition 2024.3:

- The plugin builds successfully.
- The plugin loads successfully.
- The inspection appears under `Probable Bugs`.
- Duplicate classes are reported.
- Unique and differently packaged classes are not reported.
- Both quick-fixes are available where applicable.
- The remove quick-fix removes only the intended writable duplicate.
- The diff quick-fix uses the intended two files.
- No compatibility warning remains for `GroupNames`.
- No compatibility warning remains for the deprecated writable-file overload.
- No compatibility warning remains for `inspectionToolProvider`.
- The Plugin Verifier reports compatibility against Community Edition 2024.3.

## Acceptance Criteria

The update is complete only when:

1. The happy-path tests pass before and after the API changes.
2. The plugin is registered through `localInspection`, not
   `inspectionToolProvider`.
3. `GroupNames.BUGS_GROUP_NAME` is no longer referenced.
4. The deprecated varargs writable-file API is no longer referenced.
5. `since-build` is `243` or another build from 2024, never a later build.
6. The plugin runs in IntelliJ IDEA Community Edition 2024.3.
7. No Ultimate dependency or Ultimate-only target was added.
8. The final test and Plugin Verifier results are recorded with the change.

## Audit Record

Record the following when executing this plan:

- Commit or change identifier.
- IntelliJ IDEA Community Edition distribution and exact build used.
- Gradle and Java versions used for the build.
- Test command and result.
- Plugin Verifier command and result.
- List of changed files.
- Any deviations from this plan and their justification.

## Collaboration and Repository Preferences

- Communicate with the maintainer in German.
- Keep project documentation and plugin metadata in English.
- When showing changes, show only the actual diff unless a full file is explicitly requested.
- Do not mention mise in the README; document direct Gradle commands.
- Use sufficiently long timeouts for IntelliJ Platform downloads and show Gradle output with `--console=plain`.
- Use the personal namespace `de.markiewb.idea` for plugin IDs, Java package names, and related implementation references.
- Do not use example namespaces such as `com.example`, and do not suppress Plugin Verifier errors such as `ForbiddenPluginIdPrefix`; fix the namespace instead.
- Use a valid personal namespace for all future plugins and package renames.
- Commit only intended source, test, build, and documentation files; exclude generated and system files.
- Git commits must use `markiewb` as both author and committer.
