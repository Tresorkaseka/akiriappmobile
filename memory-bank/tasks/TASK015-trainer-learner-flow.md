# TASK015 - Fixing Trainer and Learner Flows

**Status:** In Progress  
**Added:** 2026-02-19  
**Updated:** 2026-02-19

## Original Request
"on dirait le backend est pas fonctionnel y'a beacoup de donnee dummy chez le formateur et l'apprenant,normalement un apprenant peut suivre un cour poster par un formateur et un cours inclus des module avec des videos description etc bref style udemy openclassroomet le formateur peut voir le nombre de personne inscrit a cours etc bref tu comprends"

The user noticed the backend relies heavily on dummy data. We need to implement a fully functional flow where trainers create courses with modules, learners enroll, and trainers see their stats based on Firebase real data.

## Thought Process
- Identified skills to load: `firebase`, `kotlin-specialist`, `backend-dev-guidelines`.
- Found `CourseDetailsActivity` doesn't load actual lessons for the Program tab.
- Found `CoursePlayerActivity` relies on hardcoded `totalLessons = 5`.
- Found `HomeActivity` and `CoursesActivity` use `loadDummyData()` with mock variables.
- Found missing `LessonRepository`.
- Found that Trainers have no UI to add lessons to the courses they create.
- Need to draft an Implementation Plan to add `ManageCourseActivity` and `LessonRepository`, and connect the existing dummy activities to real Firebase repositories.

## Implementation Plan
- [x] Review existing models and repository methods
- [ ] Draft Implementation Plan and wait for approval
- [ ] Execute changes: Create `LessonRepository`
- [ ] Execute changes: Add `ManageCourse` and `AddLesson` flows for Trainer
- [ ] Execute changes: Refactor `HomeActivity` and `CoursesActivity`
- [ ] Execute changes: Update `CoursePlayerActivity` and `CourseDetailsActivity`

## Progress Tracking

**Overall Status:** In Progress - 20%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 1.1 | Investigate repositories | Complete | 2026-02-19 | Confirmed dummy vs real usage |
| 1.2 | Update Trainer Dashboard | Not Started | 2026-02-19 | Pending plan approval |
| 1.3 | Update Course Creation | Not Started | 2026-02-19 | Pending plan approval |
| 1.4 | Update Course Fetching for Learner | Not Started | 2026-02-19 | Pending plan approval |

## Progress Log
### 2026-02-19
- Initialized Task015.
- Completed investigation of repositories and UI files for dummy data.
- Discovered there is no UI for trainers to add lessons/modules.
- Drafted Implementation Plan to propose the complete Firebase rewiring and addition of a Trainer Manage Course flow.
