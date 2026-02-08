# TASK004 - Implement Firebase Logic

**Status:** Completed  
**Added:** 2026-02-08  
**Updated:** 2026-02-08

## Original Request
Add login and registration logic as a client and as a trainer using Firebase MCP.

## Thought Process
To handle multiple roles, I needed a way to store user metadata alongside the Firebase Auth account. Cloud Firestore was chosen to store a `users` collection where each document ID matches the Firebase Auth `uid`.

## Implementation Plan
- [x] Add Firestore dependency to `build.gradle.kts`.
- [x] Initialize Cloud Firestore via Firebase MCP.
- [x] Create `AuthRepository.kt` with `signUp` (Auth + Firestore) and `signIn` methods.
- [x] Update `SignUpActivity` to pass the selected role from `RoleSelectionActivity`.
- [x] Update `LoginActivity` to check for existing sessions and handle real authentication.
- [x] Add localized French error messages in `strings.xml`.

## Progress Tracking

**Overall Status:** Completed - 100%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 4.1 | Setup Firestore & Dependencies | Complete | 2026-02-08 | Initialized via MCP |
| 4.2 | Implement AuthRepository | Complete | 2026-02-08 | Coroutine-based wrapper |
| 4.3 | Integrate Auth in Activities | Complete | 2026-02-08 | Login and Signup wired |
| 4.4 | Role Persistence | Complete | 2026-02-08 | Roles stored in "users" collection |

## Progress Log
### 2026-02-08
- Initialized Firestore for the project.
- Implemented `AuthRepository.kt` using `kotlinx-coroutines-play-services` for cleaner `await()` syntax.
- Updated `SignUpActivity` to handle the `SELECTED_ROLE` intent extra and save it to Firestore.
- Updated `LoginActivity` with real Firebase sign-in and session checking.
- Verified that roles (Apprenant/Formateur) are correctly associated with new accounts.
