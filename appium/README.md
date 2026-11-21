# Notes Appium Framework

A small Java, Maven, TestNG, and Appium framework for the Notes Android app. It includes tests for creating a text note and a to-do list.

## Setup

1. Install JDK 11 or newer, Maven, Android SDK platform tools, Appium, and the Appium UiAutomator2 driver. Set `JAVA_HOME` and `ANDROID_HOME`, and add the Android SDK's `platform-tools` to your `PATH`.
2. Start an Android emulator. Run `adb devices` to check that the emulator is connected.
3. Start the Appium server with `appium`.
4. Check `src/test/resources/config.properties`. The local app package and activity are already set. Set `local.udid` to the device ID from `adb devices` if needed.

To install the driver after installing Appium, run `appium driver install uiautomator2`.

## Configuration

Set `execution=local` for the emulator or `execution=cloud` for a remote service. Before each local suite run, the framework installs or updates the APK specified by `local.apkPath` and clears the app's data. **This deletes all existing notes and to-do lists in the emulator app.** The default APK is `output results/updated.apk`; replace it or change the path when using a newer build. If multiple devices are connected, set `local.udid` to the intended emulator ID.

For cloud execution, set `cloud.serverUrl`, `cloud.deviceName`, and any required `cloud.platformVersion` or `cloud.app`. Set `CLOUD_USERNAME` and `CLOUD_ACCESS_KEY` as environment variables if the service uses URL credentials. Provider-specific string capabilities can be added as properties, for example `cloud.capability.vendor\:option=example` (the backslash escapes the colon in a properties key). Provider-specific nested objects or other authentication methods may require changes to `cloudOptions` or the server setup once a provider is chosen.

`noReset=true` retains app data between test sessions within a run; the local suite setup still clears the app once at the start. Set it to `false` to request a clean app state for each session. `waitSeconds` sets the explicit wait for page interactions. Configuration values can also be overridden with Maven system properties, for example `mvn test -Dexecution=local`.

## Add tests

- Put fixed locator values in `src/main/java/com/notes/framework/constants/PageConstants.java`. Declare page elements with `@AndroidFindBy` in the page classes; `BasePage` initializes them using Appium Page Factory.
- Add screen actions using the helpers in `BasePage`.
- Add TestNG test classes under `src/test/java/com/notes/tests` and extend `BaseTest`. Its setup and teardown create and close a driver for each test method. Pass `driver` and `Integer.parseInt(config.required("waitSeconds"))` to page constructors.
- Add assertions in your test methods. The included tests use unique note titles and check the saved row's date against the device date. Date assertions expect the device to use English month names.

Run `mvn clean test` from this project directory to start with fresh results. Allure results are written to `target/allure-results`, and a screenshot is attached when a test fails and a driver is available. Generate the HTML report with `mvn allure:report`, then view `target/site/allure-maven-plugin/index.html` using a local web server. Alternatively, `mvn allure:serve` generates and opens the report. TestNG reports remain available under `target/surefire-reports`. Run `mvn test-compile` to compile the framework without running tests.

For parallel regression, configure `execution=cloud` and a provider that assigns a separate device to each session, then run `mvn clean test -Pregression`. `regression.xml` runs test classes in parallel with up to four threads; methods within each class run sequentially. Set its `thread-count` to the available cloud capacity. The regression profile rejects local execution; use the normal sequential command for one emulator. The cloud provider, credentials, device allocation, and provider-specific capabilities still need to be configured.

## App limitations and test tradeoffs

- **Original app and changes:** The original supplied app had no UI controls for deleting notes, lists, or individual to-do items, and no editor for item text. The modified app adds those controls. The text-note deletion test and supporting page methods are commented out; the to-do item deletion check remains active and requires the updated APK.
- **Title validation:** In the original app, the error icon appeared without a visible message in the tested state, and the icon had no separate locator. The tests read the title field's Android accessibility `error` attribute to verify `Title cannot be empty`. The modified app also displays a separately locatable text message; the existing tests do not yet verify that visible text.
