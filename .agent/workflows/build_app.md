---
description: Build and Run Akiriapp
---

# Build and Run Akiriapp

Follow these steps to build and run the application.

## Prerequisites

- **JDK 17**: The project requires JDK 17. It is configured in `gradle.properties` to use the Android Studio bundled JDK:
  ```properties
  org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr
  ```
- **Android Studio**: Recommended for device emulation.

## Build Debug APK

To build the debug APK:

// turbo
1. Run the assembleDebug task:
   ```powershell
   .\gradlew.bat assembleDebug
   ```

## Run on Emulator

1. Ensure an emulator is running or a device is connected.
2. Install the APK:
   ```powershell
   .\gradlew.bat installDebug
   ```
