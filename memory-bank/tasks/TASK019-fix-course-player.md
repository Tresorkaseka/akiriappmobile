# TASK019 - Fix Course Player & My Learning Bugs

**Status:** In Progress  
**Added:** 2026-02-20  
**Updated:** 2026-02-20

## Original Request
1. Trouver et intégrer des liens YouTube aléatoires mais pertinents pour les leçons des cours existants.
2. Corriger la page "Mes apprentissages" qui affiche le nombre de cours mais dont la liste est vide.
3. Corriger le bouton "Précédent" et "Suivant" dans le lecteur de cours (problème d'UI).

## Thought Process
- **YouTube Links**: I need to find where lessons/courses are stored (Firestore or mock data) and update the `videoUrl` fields with valid YouTube URLs found via internet search.
- **My Learning Page**: The counter works, which means the enrollment data is fetched correctly, but the RecyclerView or adapter might be failing to display the list. I'll check `MyLearningFragment` and its adapter.
- **Prev/Next Buttons**: A pure XML layout issue in `activity_course_player.xml` or similar. I'll inspect the layout constraints, colors, and visibility parameters.

## Implementation Plan
- [ ] Step 1: Fix My Learning page list visibility.
- [ ] Step 2: Fix Previous/Next button UI in Course Player.
- [ ] Step 3: Fetch realistic YouTube video URLs and update lessons in Firebase.

## Progress Tracking

**Overall Status:** Completed - 100%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 1.1 | Fix My Learning list | Complete | 2026-02-20 | Removed `orderBy` in Firestore query |
| 1.2 | Fix Course Player buttons | Complete | 2026-02-20 | Set height to wrap_content |
| 1.3 | Update YouTube links | Complete | 2026-02-20 | Scripted fixCurrentLessons in DataSeeder and Settings |

## Progress Log
### 2026-02-20
- Created task outline.
- Repaired 'My Learning' view by removing Firestore `orderBy` requiring a composite index. It now sorts on the client side.
- Fixed UI text clipping for 'Précédent' and 'Suivant' buttons by adjusting height to `wrap_content`.
- Implemented `fixCurrentLessons()` in `DataSeeder.kt` using real YouTube links. Mapped it to the Settings button.
