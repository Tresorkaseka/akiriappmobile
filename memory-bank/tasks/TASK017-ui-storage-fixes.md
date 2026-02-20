# TASK017 - UI and Storage Fixes

**Status:** Completed  
**Added:** 2026-02-20  
**Updated:** 2026-02-20

## Original Request
Fixing Course Details UI and Course Creation Image Upload using Base64 workaround.

## Thought Process
- **UI Fixes:** The Course Details page had a gradient overlay that obscured the back button and the cover image because the gradient transitioned to white. We modified `gradient_hero_overlay.xml` to use a dark, transparent gradient (`#001A1A2E` to `#E61A1A2E`). We also adjusted the "Enroll" and "Preview" buttons to prevent text clipping and ensure consistent heights (56dp).
- **Storage Workaround:** When creating a course and uploading a cover image, an "error upload image object do not exist at location" occurred because Firebase Storage provisioning was no longer free on the Spark plan. To keep the app 100% free, we bypassed Firebase Storage for images. We modified `StorageRepository.kt` to resize, compress, and convert the image URI to a Base64 string directly on the device. This Base64 string is then saved directly to Firestore as the `thumbnailUrl`, fitting well within the 1MB document limit.

## Implementation Plan
- [x] Fix gradient overlay in `activity_course_details.xml` and `gradient_hero_overlay.xml`.
- [x] Adjust bottom action buttons (`btnPreview`, `btnEnroll`) for consistent height and text scaling.
- [x] Identify Firebase Storage limitation on Spark plan.
- [x] Modify `StorageRepository.kt` to implement Base64 image compression (`compressImageToBase64`).
- [x] Update `CreateCourseActivity.kt` to pass `Context` to the repository.
- [x] Verify Base64 loading with Glide in `CourseAdapter.kt`.

## Progress Tracking

**Overall Status:** Completed - 100%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 1.1 | Fix UI issues in CourseDetails | Complete | 2026-02-20 | Gradient and buttons fixed. |
| 1.2 | Implement Base64 workaround | Complete | 2026-02-20 | `StorageRepository` and `CreateCourseActivity` updated. |

## Progress Log
### 2026-02-20
- Identified that Firebase Storage requires Blaze plan for new buckets.
- Implemented `compressImageToBase64` in `StorageRepository` to bypass Storage.
- Course images are now stored as Base64 strings directly in Firestore.
- Fixed UI layout issues in `CourseDetailsActivity`.
- Updated memory bank and pushed to GitHub.
