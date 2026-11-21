# Notes App Automation Assignment

## Overview

I prepared automated checks for the Notes app using Appium and Espresso. Both sets of checks were run on an Android emulator. The project includes the modified app, the automation work, a sample test report, and a screen recording.

I have not used Espresso in my professional experience. I tried to create the Espresso tests with the help of AI.

## What was tested

The checks cover:

- Creating a text note and confirming its content is saved.
- Creating a to-do list with multiple items and confirming the items are saved.
- Marking items as complete and confirming they remain checked after saving.
- Editing a text note and changing a to-do list's name.
- Sorting notes by title.
- Checking that blank titles are rejected and blank to-do items are not added.
- Deleting a to-do item and confirming that it remains deleted after saving.

The Appium checks also verify the displayed date on newly created notes and lists. The Espresso checks include deleting a text note using the updated app.

## Changes to the app

The original app did not provide controls to delete notes, to-do lists, or individual to-do items. It also did not provide a way to edit the text of an individual to-do item. These options were added to the Android app, and an updated app file was created. Changes to items are saved when the list is saved.

The original app showed an error icon for an empty title, but the error message was not visible in the state tested. The updated app displays a message on the screen. The Appium checks verify the error associated with the title field; they do not separately verify that the new visible message is displayed.

The Appium text-note deletion check was added after the app was changed and is currently commented out. The to-do item deletion check remains active and requires the updated app. There is no separate automated check yet for deleting an entire to-do list or editing an individual item's text.

## Running the checks

The Appium checks can run on one local emulator. A separate regression setup is prepared for running groups of checks at the same time on cloud devices. Each concurrent session needs its own device, and the cloud service still needs to be configured. The local emulator run is sequential.

At the start of a local Appium run, the app is installed or updated and its existing data is cleared. This removes any notes and to-do lists already on the emulator.

## Results and included files

The output folder contains the original-source app, the updated app, a sample Allure report, and a screen recording. The original-source app was rebuilt from the supplied unchanged source; it is not the exact original app file that was supplied.

The sample Allure report shows an earlier Appium run with nine passing tests, including the text-note deletion check before it was commented out. It is an example and does not represent the current active suite. The Espresso suite was also run on the emulator, with nine tests passing.
