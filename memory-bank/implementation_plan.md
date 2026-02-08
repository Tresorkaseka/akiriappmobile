# Final Polish & Animations Implementation Plan

## Goal Description
Enhance the user experience with "premium" feel by adding smooth activity transitions, list item animations, and micro-interactions for touch feedback.

## User Review Required
> [!NOTE]
> This plan introduces standard Material Design-like slide transitions for all activities.

## Proposed Changes

### Global Animations (`res/anim/`)
#### [NEW] `slide_in_right.xml`, `slide_out_left.xml`, `slide_in_left.xml`, `slide_out_right.xml`
- Standard horizontal slide animations for activity navigation.

#### [NEW] `item_animation_fall_down.xml`, `layout_animation_fall_down.xml`
- Vertical fall-down animation for RecyclerView items (slide down + fade in).

### Theme Configuration
#### [MODIFY] `themes.xml`
- Add `android:windowAnimationStyle` to the main AppTheme to apply the slide transitions globally.

### List Animations
#### [MODIFY] `activity_forum.xml`, `activity_course_details.xml` (etc)
- Add `android:layoutAnimation="@anim/layout_animation_fall_down"` to the RecyclerView.

### Micro-Interactions
#### [MODIFY] `item_forum_post.xml`, `item_course.xml`
- Add `android:foreground="?attr/selectableItemBackground"` to root CardViews for ripple effect.
- Ensure `android:clickable="true"` and `android:focusable="true"`.

#### [MODIFY] `ForumPostAdapter.kt`
- Add a programmatic scale animation (bounce) when the Like button is clicked.

## Verification Plan

### Manual Verification
- **Activity Transitions**: Open the app and navigate between Home, Forum, and Settings. Verify screens slide horizontally.
- **List Animations**: Open Forum and Course lists. Verify items cascade down upon loading.
- **Ripple Effect**: Tap on course and forum items. Verify a ripple overlay appears.
- **Like Button**: Tap the heart icon in Forum. Verify it bounces.
