# System Patterns - akiriapp

## Architecture
- Standard Android Project structure.
- Language: Kotlin.
- UI Framework: Android ViewBinding (XML layouts).

## Key Patterns
- **Entry Point**: `OnboardingActivity` is the launcher activity.
- **Navigation**:
    - `Onboarding` -> `Login`
    - `Login` <-> `SignUp`
- **Authentication UI**: Material 3 Design with "Deep Blue" theme.
- **Dependencies**: ViewBinding for UI interaction.
