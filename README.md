# IntelliJ IDEA "Find Duplicates" Inspection Plugin

This plugin adds an inspection for Java classes that have the same fully qualified
name. For each detected duplicate, it provides a `Diff...` quick-fix and, when
the local file is writable, a `Remove local ...` quick-fix.

The inspection is available under `Probable Bugs` and is enabled by default.
The plugin targets IntelliJ IDEA Community Edition 2024.3 and later
(platform build `243`).

<img src="https://raw.githubusercontent.com/markiewb/idea-find-duplicate-class-inspection-plugin/master/doc/inspectionpanel.png"/>

## Development

Run the plugin in a development IDE:

```bash
gradle runIde
```

Run the tests:

```bash
gradle test
```

Build and verify the plugin:

```bash
gradle clean test buildPlugin verifyPlugin
```

## Updates

* `1.3.0`: Modernized plugin registration and IntelliJ Platform APIs, added tests and raised the minimum platform build to `243`.
* `1.2.1`: Fixed compatibility warnings from the Plugin Portal.
* `1.2`: Renamed the plugin and related metadata.
* `1.1`: Initial release.

## License
Apache 2.0
