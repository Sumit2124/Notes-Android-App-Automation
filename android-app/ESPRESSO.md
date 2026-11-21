# Espresso tests

The Java instrumentation tests are in `app/src/androidTest/java/com/example/myapp`. Screen classes contain reusable actions, and `NotesEspressoTest` covers note and to-do creation, persistence, editing, checked states, deletion, sorting, and input validation. Each test launches and closes the activity independently and uses unique names.

In Android Studio, open this project, select a running emulator, and run `NotesEspressoTest`. Alternatively, use JDK 21 and run:

```bash
bash gradlew connectedDebugAndroidTest
```

Gradle's HTML report is normally written to `app/build/reports/androidTests/connected/index.html`. The emulator used during development exposed a compatibility error in the project's existing Android Gradle plugin device runner. In that environment, the tests were built and run directly instead:

```bash
bash gradlew assembleDebugAndroidTest
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w com.example.myapp.test/androidx.test.runner.AndroidJUnitRunner
```

The direct instrumentation command prints results to the console; it does not create Gradle's HTML report. The last direct run passed all nine tests. The suite creates test notes on the device; it does not clear existing app data.
