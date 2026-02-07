# Akiriapp - Projet Android Kotlin

Structure de projet Android Studio avec Kotlin.

## Prérequis

- **Android Studio** (Hedgehog ou plus récent)
- **JDK 17**
- **Android SDK** (minSdk 24, targetSdk 34)

## Structure du projet

```
akiriapp/
├── app/                          # Module principal de l'application
│   ├── src/main/
│   │   ├── java/com/example/akiriapp/
│   │   │   └── MainActivity.kt   # Point d'entrée
│   │   ├── res/
│   │   │   ├── drawable/         # Images vectorielles
│   │   │   ├── layout/           # Layouts XML
│   │   │   ├── mipmap-*/         # Icônes de l'app
│   │   │   └── values/           # Strings, couleurs, thèmes
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/wrapper/
│   └── gradle-wrapper.properties
├── build.gradle.kts              # Configuration racine
├── settings.gradle.kts
├── gradle.properties
├── gradlew / gradlew.bat         # Scripts Gradle
└── .gitignore
```

## Lancement

1. Ouvrir le projet dans **Android Studio**
2. Mettre à jour `local.properties` avec le chemin de votre SDK Android
3. Synchroniser Gradle (File → Sync Project with Gradle Files)
4. Lancer l'application (Run ▶ ou Shift+F10)

## Build en ligne de commande

```bash
# Windows
.\gradlew.bat assembleDebug

# Linux/macOS
./gradlew assembleDebug
```

> **Note** : Si `gradle-wrapper.jar` est manquant, ouvrez d'abord le projet dans Android Studio pour qu'il soit généré.
