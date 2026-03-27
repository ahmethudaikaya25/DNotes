# DNotes — Jetpack Compose Refactoring: Scrum Plan

> **Strategy:** Each sprint is a focused, shippable unit of work.  
> Each story maps to **one or more atomic Git commits**.  
> Existing Fragment/View code is kept intact until each screen is fully migrated.

---

## Sprint Overview

| Sprint | Theme | Stories |
|--------|-------|---------|
| **0** | Foundation & Tooling | Compose deps, version upgrades, MVI base, theme |
| **1** | Design System | Shared composables, NoteCard, CategoryChip |
| **2** | Home Screen | Sort/Group toolbar, note list |
| **3** | Note Screen | Editor, delete button, category sheet |
| **4** | All Notes Screen | Staggered grid, selection mode |
| **5** | Manage Category | Category grid + add/edit sheets |
| **6** | Storage Upgrade | Room migrations, DataStore preferences |
| **7** | Search Screen | Global note search |
| **8** | Settings + Export/Import | Settings screen, AES-256 export/import |
| **9** | Polish | Animations, transitions, empty states, cleanup |

---

## Sprint 0 — Foundation & Tooling

**Goal:** The project compiles with Compose. No visible UI change yet.

### Story 0.1 — Upgrade Dependencies & Enable Compose ✅ *[DONE]*
**Commits:**
- [x] `chore: upgrade Kotlin to 1.9.22 and AGP plugins`
- [x] `chore: add Compose BOM, Material3, and Activity-Compose dependencies`
- [x] `chore: enable Compose build features and set compiler extension version`

**Acceptance Criteria:**
- Project builds and existing app runs unchanged
- Compose compiler is active (no errors)

---

### Story 0.2 — MVI Base Architecture ✅ *[DONE]*
**Commits:**
- [x] `feat: add UiState, UiIntent, UiEffect base interfaces`
- [x] `feat: add MviViewModel base class with StateFlow + Channel`

**Acceptance Criteria:**
- `MviViewModel` compiles and is ready to be subclassed
- No existing ViewModels are changed

---

### Story 0.3 — Compose Navigation Setup ✅ *[DONE]*
**Commits:**
- [x] `chore: add Compose Navigation and Hilt-Navigation-Compose deps`
- [x] `feat: create AppNavGraph skeleton with placeholder routes`

**Acceptance Criteria:**
- Navigation graph compiles with all screen routes declared
- App still launches via Activity (not nav graph yet)

---

### Story 0.4 — Material 3 Design System ✅ *[DONE]*
**Commits:**
- [x] `feat: create DNotesTheme with Material3 color scheme, typography, shapes`
- [x] `feat: add Plus Jakarta Sans font via Google Fonts`
- [x] `feat: define dark/light color palettes`

**Acceptance Criteria:**
- Theme is defined and can wrap any Composable
- Font loads correctly in Preview

---

## Sprint 1 — Design System Components

**Goal:** All reusable UI building blocks are ready before any screen is migrated.

### Story 1.1 — NoteCard Composable ✅ *[DONE]*
**Commits:**
- [x] `feat: add NoteCard composable with color gradient, title, body preview`
- [x] `feat: add selection state animation to NoteCard`
- [x] `feat: add pin indicator badge to NoteCard`

### Story 1.2 — Category Components ✅ *[DONE]*
**Commits:**
- [x] `feat: add CategoryChip composable (emoji + name + color)`
- [x] `feat: add ColorSelectorRow composable for category color picker`
- [x] `feat: add EmojiPickerField composable`

### Story 1.3 — Scaffold & Navigation Components ✅ *[DONE]*
**Commits:**
- [x] `feat: add DNotesTopBar composable with back button + actions`
- [x] `feat: add DNotesBottomNavigation composable`
- [x] `feat: add BaseScreenScaffold composable wrapper`

### Story 1.4 — Feedback & State Components ✅ *[DONE]*
**Commits:**
- [x] `feat: add LoadingScreen composable`
- [x] `feat: add EmptyStateView composable with illustration`
- [x] `feat: add ConfirmDialog and InfoDialog composables`
- [x] `feat: add BaseModalSheet composable wrapper`

---

## Sprint 2 — Home Screen

**Goal:** Home screen is fully migrated to Compose with Sort By + Group By.

### Story 2.1 — Home Screen Scaffold & ViewModel
**Commits:**
- [ ] `feat: add HomeIntent, HomeState, HomeEffect sealed classes`
- [ ] `refactor: rewrite HomeViewModel to extend MviViewModel`
- [ ] `feat: add HomeScreen composable scaffold with top bar`

### Story 2.2 — Note List (Flat, Sorted)
**Commits:**
- [ ] `feat: add flat note list with NoteCard in HomeScreen`
- [ ] `feat: add sort-by toolbar (chips: Date ↕, Modified, Title, Color)`
- [ ] `feat: persist sort selection in DataStore`

### Story 2.3 — Group By Feature
**Commits:**
- [ ] `feat: add group-by toolbar option (Category, Date, Color, Pinned)`
- [ ] `feat: add grouped list rendering with section headers`
- [ ] `feat: add "View All" action in category group header`
- [ ] `feat: persist group-by selection in DataStore`

### Story 2.4 — Wire Home Navigation
**Commits:**
- [ ] `feat: wire HomeScreen → NoteScreen navigation`
- [ ] `feat: wire HomeScreen → AllNotesScreen navigation`
- [ ] `feat: replace HomeFragment with HomeScreen in nav graph`

---

## Sprint 3 — Note Screen

**Goal:** Note editor fully migrated to Compose with delete button and category sheet.

### Story 3.1 — Note Editor Scaffold & ViewModel
**Commits:**
- [ ] `feat: add NoteIntent, NoteState, NoteEffect sealed classes`
- [ ] `refactor: rewrite NoteViewModel to extend MviViewModel`
- [ ] `feat: add NoteScreen composable with title + body fields`

### Story 3.2 — Category Selection Sheet
**Commits:**
- [ ] `feat: add SelectCategorySheet as ModalBottomSheet inside NoteScreen`
- [ ] `feat: wire category selection to note state update`

### Story 3.3 — Delete Button
**Commits:**
- [ ] `feat: add delete button to NoteScreen top bar (edit mode only)`
- [ ] `feat: add delete confirmation dialog in NoteScreen`
- [ ] `feat: wire delete to DeleteNote use case + navigate back`

### Story 3.4 — Auto-save & Formatting Toolbar
**Commits:**
- [ ] `feat: add auto-save indicator to NoteScreen`
- [ ] `feat: add bottom formatting toolbar (placeholder actions)`
- [ ] `feat: replace NoteFragment with NoteScreen in nav graph`

---

## Sprint 4 — All Notes Screen

**Goal:** All Notes screen migrated with staggered grid and selection mode.

### Story 4.1 — AllNotes ViewModel & Scaffold
**Commits:**
- [ ] `feat: add AllNotesIntent, AllNotesState, AllNotesEffect`
- [ ] `refactor: rewrite AllNotesViewModel to extend MviViewModel`
- [ ] `feat: add AllNotesScreen composable scaffold`

### Story 4.2 — Staggered Grid Layout
**Commits:**
- [ ] `feat: add staggered 2-column grid with NoteCard in AllNotesScreen`
- [ ] `feat: add sort chips row to AllNotesScreen`

### Story 4.3 — Selection Mode
**Commits:**
- [ ] `feat: add multi-select mode to AllNotesScreen`
- [ ] `feat: add contextual action bar (Delete Selected, Move Selected)`
- [ ] `feat: add move-to-category ModalBottomSheet in AllNotesScreen`
- [ ] `feat: replace AllNotesFragment with AllNotesScreen in nav graph`

---

## Sprint 5 — Manage Category Screen

**Goal:** Category management fully migrated to Compose.

### Story 5.1 — ManageCategory ViewModel & Grid
**Commits:**
- [ ] `feat: add ManageCategoryIntent, ManageCategoryState, ManageCategoryEffect`
- [ ] `refactor: rewrite ManageCategoryViewModel to extend MviViewModel`
- [ ] `feat: add ManageCategoryScreen with 2-column category card grid`

### Story 5.2 — Add/Edit Category Sheet
**Commits:**
- [ ] `feat: add CategoryBottomSheet as ModalBottomSheet composable`
- [ ] `feat: add CategoryBottomSheetViewModel (MviViewModel)`
- [ ] `feat: wire add + edit flows to CategoryBottomSheet`

### Story 5.3 — Delete Category
**Commits:**
- [ ] `feat: add swipe-to-delete on category cards with undo snackbar`
- [ ] `feat: replace ManageCategoryFragment with ManageCategoryScreen in nav graph`

---

## Sprint 6 — Storage Upgrade

**Goal:** Room migrations applied, DataStore integrated for preferences.

### Story 6.1 — Room Migrations
**Commits:**
- [ ] `feat: add Room migration 1→2 (index + foreign key enforcement)`
- [ ] `feat: add Room migration 2→3 (is_pinned, created_at, updated_at fields)`
- [ ] `feat: add Room migration 3→4 (category sort_order field)`
- [ ] `feat: update AppDatabase to version 4 with all migrations`

### Story 6.2 — DataStore Preferences
**Commits:**
- [ ] `feat: add UserPreferences DataStore with dark mode, sort, group-by keys`
- [ ] `feat: add UserPreferencesRepository`
- [ ] `feat: wire sort/group-by selections to DataStore in HomeViewModel`
- [ ] `feat: inject DataStore into Hilt module`

---

## Sprint 7 — Search Screen

**Goal:** Global note search across title + body.

### Story 7.1 — Search Screen
**Commits:**
- [ ] `feat: add SearchScreen composable with search bar`
- [ ] `feat: add SearchViewModel (MviViewModel) with real-time query`
- [ ] `feat: add SearchNotes use case (Room LIKE query)`
- [ ] `feat: add SearchNoteDao query method`
- [ ] `feat: wire SearchScreen → NoteScreen navigation`
- [ ] `feat: add search icon to HomeScreen top bar`

---

## Sprint 8 — Settings + Export/Import

**Goal:** Settings screen, full export/import with AES-256-GCM encryption.

### Story 8.1 — Settings Screen
**Commits:**
- [ ] `feat: add SettingsScreen composable`
- [ ] `feat: add dark mode toggle wired to DataStore`
- [ ] `feat: add settings gear icon to HomeScreen top bar`

### Story 8.2 — Export Feature
**Commits:**
- [ ] `feat: add NoteExporter — serializes DB to JSON`
- [ ] `feat: add ExportEncryption — AES-256-GCM with PBKDF2 key derivation`
- [ ] `feat: add ExportScreen composable (password toggle + file picker)`
- [ ] `feat: add ExportUseCase + wire to ExportScreen`

### Story 8.3 — Import Feature
**Commits:**
- [ ] `feat: add NoteImporter — parses JSON and inserts with merge strategy`
- [ ] `feat: add ImportScreen composable (file picker + strategy selector)`
- [ ] `feat: add ImportUseCase + wire to ImportScreen`
- [ ] `feat: add import conflict resolution dialog`

---

## Sprint 9 — Polish & Cleanup

**Goal:** Animations, transitions, final cleanup, remove all legacy Fragment code.

### Story 9.1 — Animations
**Commits:**
- [ ] `feat: add SharedTransitionLayout for NoteCard → NoteScreen transition`
- [ ] `feat: add slide + fade screen transitions in nav graph`
- [ ] `feat: add scale animation on NoteCard selection`

### Story 9.2 — Empty States & Loading
**Commits:**
- [ ] `feat: add Lottie dependency and empty state animations`
- [ ] `feat: add shimmer loading placeholders for note lists`

### Story 9.3 — Cleanup
**Commits:**
- [ ] `refactor: remove all legacy Fragment/DataBinding/ViewBinding files`
- [ ] `refactor: remove DataBinding and ViewBinding from build features`
- [ ] `refactor: remove legacy RecyclerView adapters and BaseListAdapter`
- [ ] `chore: remove unused dependencies (FlexboxLayout, legacy nav SafeArgs, etc.)`
- [ ] `chore: update README and docs for Compose architecture`

---

## Commit Convention

```
<type>(<scope>): <short description>

Types: feat | fix | refactor | chore | docs | test
Scope: home | note | allnotes | category | nav | theme | db | export | search
```

**Examples:**
```
feat(home): add sort-by toolbar with DataStore persistence
refactor(note): rewrite NoteViewModel as MviViewModel
chore(deps): add Compose BOM 2024.09.03 and Material3
fix(db): add missing index on NoteEntity.category_id
```

---

## Current Status

> ✅ **Sprint 1 — COMPLETE**
> 🟡 **Sprint 2, Story 2.1 — Home Screen ViewModel & State — Next**
