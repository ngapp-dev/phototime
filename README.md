📷 Photo time App 📷
=====================

This is the repository for the [Photo time app](https://github.com/ngapp-dev/photo-time)
app. This app is made based on the famous application [Now in Android](https://developer.android.com/series/now-in-android).
It is also a **work in progress** 🚧.

**Photo time** is a half functional with a lot of bugs Android app built entirely with Kotlin and Jetpack Compose. It
follows Android design and development best practices and is intended to be a useful reference
for developers. As it use Now in Android app as basis, it has many common things, but even more differences.
Ultimately, the application will be able to help photography freelancers around the world plan their work,
create their own social network of friends, colleagues and find interesting places for inspiration

The app is currently in development by only one developer [NGApps](https://github.com/ngapp-dev).

# Features

**Photo time** displays content from the demo source, it has hardcoded data, which helps to show UI.
The user can move between screens and explore the functionality.
Full functionality and backend are under development. To follow Offline first principle, the application uses
the [Push-based synchronization](https://developer.android.com/topic/architecture/data-layer/offline-first) method.

## Screenshots
<div style="display:flex;">
<img alt="Calendar screen" src="screenshots/Sign%20in%20screen.jpg" width="30%">
<img alt="Calendar screen dark" src="screenshots/Sign%20in%20screen%20dark.jpg" width="30%">
</div>
<div style="display:flex;">
<img alt="Calendar screen" src="screenshots/Contacts%20screen.png" width="30%">
<img alt="Calendar screen dark" src="screenshots/Contacts%20screen%20dark.png" width="30%">
</div>
<div style="display:flex;">
<img alt="Contacts screen" src="screenshots/Contacts%20screen%20tablet.png" width="30%">
<img alt="Contacts screen dark" src="screenshots/Contacts%20screen%20tablet%20dark.png" width="30%">
</div>
<div style="display:flex;">
<img alt="Location screen" src="screenshots/Location%20screen.png" width="30%">
<img alt="Location screen dark" src="screenshots/Location%20screen%20dark.png" width="30%">
</div>
<div style="display:flex;">
<img alt="Task screen" src="screenshots/Task%20screen.png" width="30%">
<img alt="Task screen dark" src="screenshots/Task%20screen%20dark.png" width="30%">
</div>

# Development Environment

**Photo time** uses the Gradle build system and can be imported directly into Android Studio (make sure you are using the latest stable version available [here](https://developer.android.com/studio)).

Change the run configuration to `app`.

The `demoDebug` and `demoRelease` build variants can be built and run (the `prod` variants use a backend server which is not currently publicly available).

# Architecture

The **Photo time** app follows the
[official architecture guidance](https://developer.android.com/topic/architecture)
and is described in detail in the
[architecture learning journey](docs/ArchitectureLearningJourney.md).

# Modularization

The **Photo time** app has been fully modularized and you can find the detailed guidance and
description of the modularization strategy used in
[modularization learning journey](docs/ModularizationLearningJourney.md).

# Build

The app contains the usual `debug` and `release` build variants.

`app-pt-catalog` is a standalone app that displays the list of components that are stylized for
**Photo time**.

The app also has such flavors as `demo` and `paid`

# UI
The app was designed using [Material 3 guidelines](https://m3.material.io/).

The Screens and UI elements are built entirely using [Jetpack Compose](https://developer.android.com/jetpack/compose).

By default the app works in Light or Dark dome

The app uses adaptive layouts to
[support different screen sizes](https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes).

Find out more about the [UI architecture here](docs/ArchitectureLearningJourney.md#ui-layer).

# Performance

## Benchmarks

Not yet configured

## Baseline profiles

Not yet configured

# License

**Photo time** is distributed under the terms of the Apache License (Version 2.0). See the
[license](LICENSE) for more information.
