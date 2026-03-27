# DNotes — Jetpack Compose Refactoring Plan

> **Version:** 1.0 | **Date:** 2026-03-27
> **Scope:** Full architectural + UI refactoring from View-based (DataBinding + Fragments) to Jetpack Compose with MVI

---

## Table of Contents

1. [All Screens and Their Duties](#1-all-screens-and-their-duties)
2. [All Screen Flows](#2-all-screen-flows)
3. [All Bottom Sheets](#3-all-bottom-sheets)
4. [All Dialogs](#4-all-dialogs)
5. [All Business Logics](#5-all-business-logics)
6. [Current Project Technologies](#6-current-project-technologies)
7. [Conversion to MVI Architecture](#7-conversion-to-mvi-architecture)
8. [Base Classes for Jetpack Compose](#8-base-classes-for-jetpack-compose)
9. [Making Screens More Beautiful](#9-making-screens-more-beautiful)
10. [Better Screen Designs](#10-better-screen-designs)
11. [Better Screen Flows](#11-better-screen-flows)
12. [Better Storage Technology](#12-better-storage-technology)
13. [Local Export/Import with Password](#13-local-exportimport-with-password)

---

## 1. All Screens and Their Duties

### 1.1 Home Screen (`HomeFragment` + `HomeViewModel`)

**Duty:** The main entry screen and landing page of the app. Shows **all notes sorted by their added date** by default. Users can change the display using **Sort By** and **Group By** options to reorganize the list however they prefer.

**Default Behavior:**
- All notes are shown in a flat list, **sorted by added date (newest first)**
- No grouping is applied by default

**Sort By Options:**
- Date Added (newest first) ← default
- Date Added (oldest first)
- Last Modified
- Title (A → Z)
- Title (Z → A)
- Color

**Group By Options:**
- None (flat list) ← default
- Category (groups notes by their category — current behavior)
- Date (groups by Today / This Week / This Month / Older)
- Color
- Pinned / Unpinned

**UI Elements:**
- **Sort & Group toolbar** (chips or dropdown menus at the top of the list)
- Note list / grid that re-renders based on current sort + group selection
- Group headers when a Group By option is active
- "View All" button per group header (visible when grouped by category)
- FAB (floating action button) in `MainActivity` for creating a new note
- Bottom navigation bar (Home, Categories, Notifications tabs)

**Business Logic:**
- Fetches all notes (across all categories) via `FetchHomeData` use case
- If data is empty → sets `HomeUIState.Error` (no-data-found exception)
- Applies the selected **Sort By** comparator to the note list
- Applies the selected **Group By** grouping — splits the flat list into labeled sections
- Sort + Group selections are **persisted** in DataStore (restored on next launch)
- On note card click → emits `HomeUIEvent.OnNoteClicked` → navigates to `NoteFragment` (edit mode)
- On "View All" click (category group mode) → emits `HomeUIEvent.OnViewAllClicked` → navigates to `AllNotesFragment` for that category

---

### 1.2 All Notes Screen (`AllNotesFragment` + `AllNotesViewModel`)

**Duty:** Shows all notes belonging to a single category in a flexible grid (`FlexboxLayoutManager`). Supports multi-select, delete, and move-to-category operations.

> ⚠️ **This screen will be revised based on the Group By feature added to the Home Screen.**  
> When the user is in a grouped-by-category view on Home and taps "View All", this screen shows the notes for that specific category. If future Group By modes (e.g. date, color) are added to Home, this screen may also need to support filtering by those dimensions, or a new generic "Filtered Notes" screen will be introduced.

**UI Elements:**
- `FlexboxLayout` grid of note cards
- Contextual multi-select action menu (Delete Selected, Move Selected)
- Per-note long-press context menu (Delete, Edit, Select, Move)
- Empty state handling (navigates back if no notes found)

**Business Logic:**
- Receives `categoryId` as navigation argument
- Loads notes via `GetNotesByCategoryId` use case
- If no notes found → `AllNotesState.Error` → auto-navigate back to Home
- **Single tap on note (normal mode):** navigates to `NoteFragment` (edit)
- **Single tap on note (select mode):** toggles selection state
- **Long press:** enables selection mode, selects tapped note
- **Delete Selected:** calls `DeleteNote` use case → reloads notes
- **Delete Single:** calls `DeleteNote` use case → reloads notes
- **Move Single / Move Selected:** opens `SelectCategoryFragment` bottom sheet → on category chosen → calls `UpdateNotes` use case → reloads notes
- Selection mode cleared after any batch action

---

### 1.3 Note Screen (`NoteFragment` + `NoteViewModel`)

**Duty:** Create or edit a single note — title, body, and category. Contains an embedded bottom sheet (inside the fragment layout) for category selection. Also includes a **delete button** to remove the note directly from the editor.

**UI Elements:**
- Title `EditText`
- Body `EditText` (multi-line)
- Category chip/badge (tappable to expand bottom sheet)
- Embedded `SelectCategoryFragment` in a `BottomSheetBehavior` container
- **Delete button** — visible only in edit mode (hidden when creating a new note)
- Back-press interception

**Business Logic:**
- Receives optional `NoteItem: BaseNoteUIModel?` argument
- **Insert Mode** (null arg): fetches default category via `GetDefaultCategory` → creates a new blank note
- **Edit Mode** (non-null arg): pre-fills form with existing note data
- **Back Button:** calls `saveAndGoBackStack()` → tries to save via `UpsertNote`
  - On success → `popBackStack`
  - On failure → shows `OptionDialog` (Yes = exit without save, No = stay & restore last success state)
- **Delete Button** *(edit mode only)*: tapping shows a **confirmation dialog** ("Are you sure you want to delete this note?") → on confirm calls `DeleteNote` use case → `popBackStack`
  - Delete button is **hidden** when in insert mode (no note exists yet)
- **Category bottom sheet (expand):** hides keyboard, disables text editing
- **Category bottom sheet (collapse):** re-enables text editing
- **Category selected:** updates note's category → collapses bottom sheet
- **onPause:** calls `saveAccordingToLastUIEvent()` (saves unless user already pressed Back or Delete)
- State tracking: keeps `lastSuccessState` to restore on error

---

### 1.4 Manage Category Screen (`ManageCategoryFragment` + `ManageCategoryViewModel`)

**Duty:** Lists all categories. Allows adding, editing, and deleting categories. It is a BottomNav tab destination.

**UI Elements:**
- Vertical `RecyclerView` of category list items
- Swipe-left gesture → delete with undo snackbar
- Tap → opens `CategoryBottomSheet` in **Edit** mode
- "Add Category" button/FAB → opens `CategoryBottomSheet` in **Add** mode

**Business Logic:**
- Loads categories on init via `GetCategories` use case
- **Delete:** calls `DeleteCategory` use case → shows undo snackbar
  - Undo: calls `UndoCategory` → reloads
- **Upsert:** after `CategoryBottomSheet` completes successfully → calls `onCategoryUpserted()` → reloads list
- Error states show for 1000ms then retry automatically

---

### 1.5 Notifications Screen (`NotificationsFragment`)

**Duty:** Placeholder / future screen. Currently displayed as a BottomNav tab but has no meaningful implementation.

---

## 2. All Screen Flows

```
MainActivity (BottomNav: Home | Categories | Notifications)
│
├── [Tab: Home] → HomeFragment
│       │
│       ├── Tap Note Card → NoteFragment (edit mode)
│       │       └── Back / Save → HomeFragment
│       │       └── Category Bottom Sheet (embedded) ← SelectCategoryFragment
│       │       └── Error Dialog → OptionDialog
│       │
│       └── "View All" → AllNotesFragment (categoryId)
│               │
│               ├── Tap Note → NoteFragment (edit mode)
│               │       └── Back / Save → AllNotesFragment
│               │
│               ├── Long Press / Select → multi-select mode
│               │       ├── Delete Selected → reload
│               │       └── Move Selected → SelectCategoryFragment (dialog) → reload
│               │
│               └── Per-item menu → Delete / Edit / Select / Move
│
├── [Tab: Categories] → ManageCategoryFragment
│       │
│       ├── Tap Category → CategoryBottomSheet (edit mode)
│       │       └── Save → reload list
│       │       └── Cancel → dismiss
│       │
│       ├── Swipe Left → Delete → Snackbar (Undo)
│       └── Add Button → CategoryBottomSheet (add mode)
│               └── Save → reload list
│
└── [Tab: Notifications] → NotificationsFragment (placeholder)
```

**FAB (global, visible in Home tab only):**
→ Navigate to `NoteFragment` (insert mode, null argument)

---

## 3. All Bottom Sheets

### 3.1 `CategoryBottomSheet` (Modal, `BottomSheetDialogFragment`)

- **Purpose:** Add or edit a category
- **Fields:** Emoji picker, category name, description, color selector (horizontal list)
- **Modes:** `CategoryShowType.Add` or `CategoryShowType.Edit`
- **Events:** `Upserted`, `Canceled`, `Dismissed`, `ColorSelected`, `ShowEmojiDialog`, `DismissedEmojiDialog`
- **Triggered from:** `ManageCategoryFragment`

---

### 3.2 `SelectCategoryFragment` (Snipped / Modal, `BottomSheetDialogFragment`)

- **Purpose:** Choose a category for a note (on create/edit) or for moving notes
- **Fields:** List of categories with selection highlight
- **Events:** `OnCategorySelected`, `Dismiss`
- **Triggered from:**
  - `NoteFragment` (embedded inside layout via `BottomSheetBehavior`)
  - `AllNotesFragment` (via `showBottomSheet` for move operation)

---

### 3.3 `ShowMessageBottomSheet` (Generic, `BottomSheetDialogFragment`)

- **Purpose:** Generic informational bottom sheet (title, message, action button)
- **Events:** `Cancel`
- **Status:** Partially implemented (`handleArgs` and `handleUIState` are TODO)
- **Fields:** `ShowMessageUIModel`, `ButtonStyle`

---

## 4. All Dialogs

### 4.1 `ErrorDialog` (Navigation Dialog, `DialogFragment`)

- **Purpose:** Reusable dialog for errors and option prompts
- **Types via `DialogFragmentState`:**

| Type | Usage |
|---|---|
| `InformativeDialog` | title + message (info only, dismiss) |
| `OptionDialog` | title + message + Yes / No buttons with lambdas |
| `OneButtonDialog` | title + message + single OK button with lambda |

- **Triggered from:** `NoteFragment` (when save fails before back-navigation)

---

## 5. All Business Logics

### 5.1 Use Cases (Domain Layer)

| Use Case | Package | Purpose |
|---|---|---|
| `FetchHomeData` | `add_or_update_category.domain` | Fetch all categories + notes combined for home screen |
| `UpsertCategory` | `add_or_update_category.domain` | Insert or update a category |
| `DeleteCategory` | `add_or_update_category.domain` | Delete a category; moves its notes to the default category |
| `UndoCategory` | `manage_category.domain` | Undo last category deletion |
| `GetCategories` | `manage_category.domain` | Fetch all categories list |
| `InsertCategory` | `manage_category.domain` | Insert a new category |
| `CreateDefaultCategory` | `manage_category.domain` | Create the initial default category on first launch |
| `UpsertNote` | `note.domain` | Insert or update a note |
| `GetDefaultCategory` | `note.domain` | Get the default category when creating a new note |
| `GetNotesByCategoryId` | `all_notes.domain` | Fetch all notes for a given category ID |
| `DeleteNote` | `all_notes.domain` | Delete one or more notes |
| `UpdateNotes` | `all_notes.domain` | Update a batch of notes (used for moving category) |

### 5.2 Domain Rules

- A **default category** always exists (created on first app launch via `CreateDefaultCategory`)
- Notes are **not deleted** when their category is deleted — they are moved to the default category
- A category that is the **default** should not be deletable (or notes are auto-migrated)
- Empty title/body notes **can** be saved (no validation in current code)
- Note save is attempted on every **`onPause`** (auto-save behavior)
- Category name must have a **valid emoji** (validated via `isEmoji` extension function)

### 5.3 Data Layer

- `CategoryRepository` + `NoteRepository` are injected via Hilt
- `CategoryDao` + `NoteDao` handle all Room queries
- `AppDatabase` — Room DB version 1, entities: `CategoryEntity`, `NoteEntity`
- `CategoryEntity.toUIModel()` → returns `CategoryUIModel`
- `NoteEntity.toUIModel(category)` → returns `BasicNoteUIModel`

---

## 6. Current Project Technologies

| Technology | Version | Role |
|---|---|---|
| **Kotlin** | Latest | Primary language |
| **Android SDK** | compileSdk 35 / minSdk 24 / targetSdk 35 | Android platform |
| **Fragments** | AndroidX | UI containers |
| **DataBinding** | AndroidX | Two-way UI ↔ ViewModel binding |
| **ViewBinding** | AndroidX | View access without boilerplate |
| **Navigation Component** | 2.8.0 | Screen navigation + SafeArgs |
| **ViewModel + StateFlow** | lifecycle-ktx 2.6.1 | State management |
| **Kotlin Coroutines + Flow** | — | Async + reactive data streams |
| **MutableStateFlow / SharedFlow** | — | UIState + UIEvent emission |
| **Room** | 2.5.1 | Local SQLite ORM |
| **Hilt** | 2.44 | Dependency injection |
| **Material Components** | 1.9.0 | UI widgets (BottomSheet, Snackbar, FAB, etc.) |
| **FlexboxLayout** | — | Grid layout for AllNotes screen |
| **Emoji (vanniktech)** | 0.16.0 | Emoji picker for category icons |
| **Timber** | 5.0.1 | Structured logging |
| **LeakCanary** | 2.11 | Memory leak detection (debug only) |
| **ConstraintLayout** | 2.1.4 | Complex multi-view layouts |

### Current Architecture Pattern

- **MVVM-ish** with an event-bus flavour
- `BaseViewModel<UE, US>` exposes `uiState: StateFlow<US?>` + `uiEvent: SharedFlow<UE>`
- Fragments observe both flows and delegate to `handleUIState()` / `handleUIEvent()`
- Not strict MVI — intents are direct ViewModel function calls, not a single intent channel

---

## 7. Conversion to MVI Architecture

### What Is MVI?

**Model-View-Intent** is a unidirectional data flow pattern:

| Layer | Role |
|---|---|
| **Intent** | User actions / side effects — single sealed class per screen |
| **State** | Single immutable snapshot of the entire screen — `data class` |
| **Reducer** | Pure function: `(State, Intent) → State` |

### Why MVI for DNotes?

- Current code has partial MVI (state + event) but mixes concerns — ViewModels mutate state from many places
- Compose is **state-driven** — a single `UiState` per composable is the natural fit
- Easier to test, easier to reproduce bugs, clearer data flow direction

### 7.1 Proposed MVI Contracts

```kotlin
// Screen data state
data class ScreenUiState(
    val isLoading: Boolean = false,
    val data: ScreenData? = null,
    val error: String? = null,
)

// User intents / actions
sealed class ScreenIntent {
    object LoadData : ScreenIntent()
    data class DeleteNote(val id: Int) : ScreenIntent()
    data class SelectNote(val note: NoteUiModel) : ScreenIntent()
}

// One-shot side effects (navigate, show snackbar, etc.)
sealed class ScreenEffect {
    object NavigateBack : ScreenEffect()
    data class ShowSnackbar(val message: String) : ScreenEffect()
    data class NavigateToNote(val noteId: Int?) : ScreenEffect()
}
```

### 7.2 Proposed `MviViewModel` Base

```kotlin
abstract class MviViewModel<I : UiIntent, S : UiState, E : UiEffect>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = Channel<E>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    abstract fun processIntent(intent: I)

    protected fun updateState(reducer: S.() -> S) {
        _state.update { it.reducer() }
    }

    protected fun emitEffect(effect: E) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
```

### 7.3 Intent Mapping per Screen

| Screen | Key Intents |
|---|---|
| **Home** | `LoadHomeData`, `ClickNote(note)`, `ClickViewAll(categoryId)` |
| **AllNotes** | `LoadNotes(categoryId)`, `ClickNote(note)`, `LongPressNote(note)`, `DeleteSelected`, `MoveSelected(category)`, `DeleteSingle(note)`, `MoveSingle(note)`, `CancelSelection` |
| **Note** | `LoadNote(noteId?)`, `UpdateTitle(text)`, `UpdateBody(text)`, `SelectCategory(category)`, `ClickBack`, `ExpandCategorySheet`, `CollapseCategorySheet` |
| **ManageCategory** | `LoadCategories`, `ClickCategory(cat)`, `DeleteCategory(cat)`, `UndoDelete`, `ClickAddCategory`, `SaveCategory(cat)` |
| **CategorySheet** | `SetEmoji(emoji)`, `SelectColor(color)`, `UpdateName(text)`, `UpdateDescription(text)`, `ClickSave`, `ClickCancel` |
| **SelectCategory** | `LoadCategories`, `SelectCategory(cat)`, `Dismiss` |

### 7.4 Side Effects (one-shot, not part of state)

- Navigate to a screen
- Show Snackbar / Toast
- Dismiss bottom sheet / dialog
- Trigger confirmation dialog

---

## 8. Base Classes for Jetpack Compose

> Unlike the Fragment-based hierarchy, Compose uses **composition over inheritance**.  
> The equivalents are: base composable functions, shared state-effect collectors, and reusable scaffold wrappers.

### 8.1 `MviScreen` — Universal Composable Wrapper

```kotlin
@Composable
fun <S : UiState, E : UiEffect> MviScreen(
    viewModel: MviViewModel<*, S, E>,
    onEffect: (E) -> Unit,
    content: @Composable (state: S) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { onEffect(it) }
    }

    content(state)
}
```

### 8.2 `BaseScreenScaffold` — Shared Layout Wrapper

```kotlin
@Composable
fun BaseScreenScaffold(
    title: String = "",
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            if (title.isNotEmpty())
                DNotesTopBar(title, showBackButton, onBackClick)
        },
        floatingActionButton = floatingActionButton,
        content = content
    )
}
```

### 8.3 `BaseModalSheet` — Shared Bottom Sheet Wrapper

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseModalSheet(
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        content = content
    )
}
```

### 8.4 Shared UI Components (Reusable Composables)

| Composable | Purpose |
|---|---|
| `NoteCard` | Renders a single note (color, title, body preview, selection state) |
| `CategoryChip` | Category badge — emoji + name + background color |
| `ColorSelectorRow` | Horizontal color picker row |
| `EmojiPickerField` | Tappable field that opens the emoji picker dialog |
| `DNotesTopBar` | Custom `TopAppBar` with back button + title |
| `DNotesBottomNavigation` | Bottom navigation bar with 3 tabs |
| `LoadingScreen` | Full-screen loading indicator (shimmer or circular) |
| `ErrorScreen` | Full-screen error view with retry button |
| `EmptyStateView` | Empty state illustration + descriptive message |
| `ConfirmDialog` | Yes / No confirmation dialog |
| `InfoDialog` | Single-button informational dialog |

---

## 9. Making Screens More Beautiful

### 9.1 Current Visual Issues

- Generic Material 2 design — no custom theme beyond basic colors
- Note cards have flat styling with minimal visual hierarchy
- Category colors are used but not prominently showcased
- No transition animations between screens or state changes
- Typography is the default system font
- Bottom navigation is plain Material style with no personality

### 9.2 Proposed Design System

| Token | Value |
|---|---|
| **Font Family** | [Plus Jakarta Sans](https://fonts.google.com/specimen/Plus+Jakarta+Sans) or Nunito |
| **Corner Radius (cards)** | `20dp` |
| **Corner Radius (chips)** | `50dp` (fully rounded) |
| **Elevation** | `4dp` on cards, `0dp` on flat containers |
| **Spacing grid** | Multiples of `8dp` |
| **Theming** | Material 3 + Dynamic Color (Android 12+) with static fallback |

### 9.3 Note Cards

- **Colored background** using the category color (dark variant)
- **Subtle gradient** from `darkColor` → `lightColor` (top → bottom)
- Title in **bold**, body preview in 2-line ellipsis with lighter weight
- **Selection mode:** animated checkbox overlay + scale animation on select
- **Pinned indicator:** small pin icon in top-right corner

### 9.4 Home Screen

- **Pinterest-style staggered grid** or **grouped category rows** with large emoji headers
- Category header with large emoji in a **circular colored container**
- "View All" replaced by a **pill badge** showing note count
- **Pinned notes section** at the top before categories

### 9.5 Animations

- **Shared Element Transition** (`SharedTransitionLayout`) from note card → note editor
- **Slide-in** for AllNotes screen
- **Scale + fade** for dialog / bottom sheet appearance
- **Ripple effects** on all interactive elements
- **Lottie animations** for empty states and success confirmations

---

## 10. Better Screen Designs

### 10.1 Home Screen Wireframe

```
┌──────────────────────────────────┐
│  🗒 DNotes          [Search] [⚙] │
├──────────────────────────────────┤
│  Good morning — 24 notes total   │
│  across 5 categories             │
├──────────────────────────────────┤
│ ┌────────────────────────────┐   │
│ │ 📚 Work  ·  12 notes   [→] │   │
│ │ ┌──────┐ ┌──────┐ ┌──────┐│   │
│ │ │ Note │ │ Note │ │ +10  ││   │
│ │ └──────┘ └──────┘ └──────┘│   │
│ └────────────────────────────┘   │
│                                  │
│ ┌────────────────────────────┐   │
│ │ 🏠 Personal · 6 notes  [→] │   │
│ └────────────────────────────┘   │
└──────────────────────────────────┘
              [+] FAB
```

**New elements:**
- Global **search bar** at top
- Time-based greeting text
- Total note count summary
- Pinned notes section before categories
- "Quick add" shortcut

---

### 10.2 Note Editor Wireframe

```
┌──────────────────────────────────┐
│ ←  [📁 Work]          [📌] [💾] │
├──────────────────────────────────┤
│                                  │
│  Note title here...              │
│  ────────────────────────────    │
│                                  │
│  Start writing your note...      │
│                                  │
│                                  │
│                                  │
├──────────────────────────────────┤
│ [Aa] [📷] [☑] [🎤] [📎]  [···] │
└──────────────────────────────────┘
```

**New elements:**
- Inline **formatting toolbar** at bottom
- Category pill inline in the top bar
- Character / word count display
- Auto-save indicator ("Saved ✓" / "Saving…")
- Pin toggle in top bar

---

### 10.3 AllNotes Screen Redesign

- **Staggered 2-column grid** (variable height cards — like Google Keep)
- Sort options row: Newest / Oldest / A–Z / Color
- Filter chip: Pinned / Unpinned
- Swipe-to-delete directly on the grid card

---

### 10.4 Manage Category Screen Redesign

- **2-column card grid** with large emoji, note count badge, and color background
- Drag-to-reorder categories
- Swipe-left → delete with animated confirmation
- Category card shows a row of 3 recent note previews

---

## 11. Better Screen Flows

### 11.1 Current Pain Points

1. `NoteFragment` embeds `SelectCategoryFragment` via `BottomSheetBehavior` — tightly coupled, complex lifecycle
2. `CategoryBottomSheet` uses `activityViewModels` shared ViewModel — fragile across lifecycle events
3. No **Search** flow
4. No **Settings** flow
5. Pressing Back on `AllNotesFragment` in some edge cases skips expected destinations

### 11.2 Proposed Navigation Graph (Compose Navigation)

```
Root Nav Graph
├── HomeScreen  ←  start destination
│   ├── → NoteScreen(noteId = null)          [FAB / create new]
│   ├── → NoteScreen(noteId = Int)            [tap existing note]
│   └── → AllNotesScreen(categoryId = Int)   [View All]
│
├── AllNotesScreen(categoryId)
│   └── → NoteScreen(noteId = Int)
│
├── ManageCategoryScreen
│   (Category add/edit shown as ModalBottomSheet — no navigation)
│
├── SearchScreen  ← NEW
│   └── → NoteScreen(noteId = Int)
│
├── SettingsScreen  ← NEW
│   ├── → ExportScreen  ← NEW
│   └── → ImportScreen  ← NEW
│
└── NotificationsScreen  (placeholder → future)
```

### 11.3 Removed Complexity

| Current (complex) | Compose replacement |
|---|---|
| `activityViewModels` for bottom sheet communication | Local `viewModels()` + `ModalBottomSheet` |
| `BottomSheetBehavior` in NoteFragment layout | `ModalBottomSheet` inside Compose |
| `FragmentManager.beginTransaction()` for snipped sheets | Compose handles composition natively |
| SafeArgs Parcelable passing | Type-safe Compose Navigation routes via `@Serializable` |

### 11.4 New Flows Added

| Flow | Entry Point | Description |
|---|---|---|
| **Search** | Top bar search icon | Real-time note search across title + body |
| **Settings** | Top bar gear icon | Theme, backup, export/import, preferences |
| **Pin Note** | Note editor pin icon | Marks note as pinned; shows pinned section on Home |
| **Sort / Filter** | AllNotes top bar | Sort by date, title, color; filter by pinned |

---

## 12. Better Storage Technology

### 12.1 Current Stack Issues

| Issue | Detail |
|---|---|
| No `@ForeignKey` enforcement | Category deletion doesn't cascade at DB level |
| No Room migrations defined | DB is version 1 with no migration strategy |
| No indexing on `category_id` | Potential query performance issue |
| No preference storage | No DataStore or SharedPreferences in use |

### 12.2 Recommended Storage: Room + DataStore

| Data | Storage | Reason |
|---|---|---|
| Notes + Categories | **Room** (keep) | Relational, queryable, well-tested |
| User preferences (theme, sort order, default category) | **Jetpack DataStore** | Type-safe, coroutine-native, replaces SharedPreferences |
| App settings (onboarding shown, export password hash) | **Jetpack DataStore** | Same as above |

### 12.3 Migration Plan

#### Migration 1 → 2: Add Indices + Foreign Key Enforcement

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add index on note.category_id
        db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_NoteEntity_category_id
            ON NoteEntity(category_id)
        """)
        // Recreate NoteEntity with FOREIGN KEY constraint
        db.execSQL("""
            CREATE TABLE NoteEntity_new (
                id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title       TEXT NOT NULL,
                details     TEXT NOT NULL,
                category_id INTEGER NOT NULL,
                FOREIGN KEY (category_id) REFERENCES CategoryEntity(id)
                    ON DELETE SET DEFAULT
            )
        """)
        db.execSQL("INSERT INTO NoteEntity_new SELECT * FROM NoteEntity")
        db.execSQL("DROP TABLE NoteEntity")
        db.execSQL("ALTER TABLE NoteEntity_new RENAME TO NoteEntity")
    }
}
```

#### Migration 2 → 3: Add New Note Fields

```kotlin
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE NoteEntity ADD COLUMN is_pinned INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE NoteEntity ADD COLUMN is_password_protected INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE NoteEntity ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE NoteEntity ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0")
    }
}
```

#### Migration 3 → 4: Add Category Sort Order

```kotlin
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE CategoryEntity ADD COLUMN sort_order INTEGER NOT NULL DEFAULT 0")
    }
}
```

#### Updated `AppDatabase`

```kotlin
@Database(
    entities = [CategoryEntity::class, NoteEntity::class],
    version = 4,
    exportSchema = true  // required for migration testing
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun noteDao(): NoteDao

    companion object {
        val MIGRATIONS = arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
    }
}
```

### 12.4 DataStore Integration

```kotlin
// Preference keys
val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
val DEFAULT_CATEGORY_ID_KEY = intPreferencesKey("default_category_id")
val SORT_ORDER_KEY = stringPreferencesKey("sort_order")

// DataStore instance
val Context.userPreferences by preferencesDataStore("user_prefs")

// Data model
data class UserPreferences(
    val isDarkMode: Boolean = false,
    val defaultCategoryId: Int = -1,
    val sortOrder: SortOrder = SortOrder.NEWEST,
)
```

---

## 13. Local Export/Import with Password

### 13.1 Feature Overview

Allow users to export their entire database (notes + categories) to a local file and import it back later, optionally protected by a user-defined password.

### 13.2 Export File Format — Encrypted JSON

```json
{
  "version": 1,
  "exported_at": "2026-03-27T18:00:00Z",
  "categories": [
    {
      "id": 1,
      "name": "Work",
      "emoji": "📚",
      "color_id": 4,
      "is_default": true
    }
  ],
  "notes": [
    {
      "id": 1,
      "title": "Meeting notes",
      "details": "Discussed Q2 roadmap...",
      "category_id": 1
    }
  ]
}
```

- **File extension:** `.dnotes` (encrypted) or `.json` (plain)
- **Unencrypted:** file is Base64-encoded and clearly labeled as unprotected
- **Encrypted:** AES-256-GCM with PBKDF2-derived key

### 13.3 Encryption Implementation

**Algorithm:** AES-256-GCM  
**Key Derivation:** PBKDF2WithHmacSHA256 (100 000 iterations)  
**File Layout:** `[salt: 16 bytes] + [IV/nonce: 12 bytes] + [ciphertext]`

```kotlin
object ExportEncryption {

    fun encrypt(data: ByteArray, password: String): ByteArray {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val iv   = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val key  = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))
        }
        val encrypted = cipher.doFinal(data)
        return salt + iv + encrypted
    }

    fun decrypt(data: ByteArray, password: String): ByteArray {
        val salt       = data.copyOfRange(0, 16)
        val iv         = data.copyOfRange(16, 28)
        val ciphertext = data.copyOfRange(28, data.size)
        val key = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        }
        return cipher.doFinal(ciphertext)
    }

    private fun deriveKey(password: String, salt: ByteArray): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, 100_000, 256)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }
}
```

### 13.4 Export Flow

```
1. User → Settings → Export Notes
2. Optional: enter password (or skip for unprotected)
3. App serializes CategoryEntity + NoteEntity → JSON string
4. JSON bytes encrypted (if password) → saved to file
5. Android Storage Access Framework (SAF) file picker
   → Save to Downloads / Share to Drive, email, etc.
6. Success toast: "Exported X notes, Y categories"
```

### 13.5 Import Flow

```
1. User → Settings → Import Notes
2. SAF file picker → select .dnotes / .json file
3. App reads file bytes
4. If encrypted → prompt for password
5. Decrypt → parse JSON
6. Show conflict resolution dialog (see 13.6)
7. Insert / merge data into Room
8. Success dialog: "Imported X notes, Y categories"
```

### 13.6 Import Conflict Resolution

```kotlin
sealed class ImportStrategy {
    /** Wipe existing DB and replace with imported data */
    object ReplaceAll : ImportStrategy()

    /** Skip categories/notes that already exist (by name/title) */
    object MergeKeepExisting : ImportStrategy()

    /** Overwrite existing records with imported data */
    object MergeOverwrite : ImportStrategy()
}
```

The strategy is selected via a **radio-button dialog** shown before import begins.

### 13.7 Export / Import UI Screens

#### Export Screen

```
┌──────────────────────────────────┐
│ ← Export Notes                   │
├──────────────────────────────────┤
│                                  │
│  [  ] Protect with password      │
│                                  │
│  Password: ___________________   │
│  Confirm:  ___________________   │
│                                  │
│  📦 24 notes · 5 categories      │
│     will be exported             │
│                                  │
│        [ Export File ]           │
└──────────────────────────────────┘
```

#### Import Screen

```
┌──────────────────────────────────┐
│ ← Import Notes                   │
├──────────────────────────────────┤
│                                  │
│        [ Select File ]           │
│                                  │
│  File: notes_backup.dnotes       │
│  Password: ___________________   │
│                                  │
│  Merge strategy:                 │
│  ◉ Replace all                   │
│  ○ Merge — keep existing         │
│  ○ Merge — overwrite             │
│                                  │
│        [ Import ]                │
└──────────────────────────────────┘
```

### 13.8 Permissions

```xml
<!-- AndroidManifest.xml -->
<!-- SAF handles file access on API 29+; read permission only needed below -->
<uses-permission
    android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="28" />
```

---

## Summary: Recommended Technology Stack for Compose Version

| Category | Current | Recommended |
|---|---|---|
| **UI Framework** | Fragments + DataBinding | Jetpack Compose |
| **Navigation** | Navigation Component + SafeArgs | Compose Navigation + `@Serializable` type-safe routes |
| **State Management** | StateFlow + SharedFlow (partial MVI) | Full MVI — StateFlow + Channel for effects |
| **DI** | Hilt 2.44 | Hilt (update to latest) |
| **Database** | Room 2.5.1 (no migrations) | Room latest + migrations + ForeignKeys + indices |
| **Preferences** | None | Jetpack DataStore |
| **Async** | Coroutines + Flow | Coroutines + Flow (keep) |
| **Animations** | None | Compose Animations + Shared Element Transitions |
| **Theming** | Material 2 static colors | Material 3 + Dynamic Color (API 31+) |
| **Logging** | Timber | Timber (keep) |
| **Emoji** | vanniktech 0.16.0 | vanniktech (keep or migrate to Compose-native) |
| **Export / Import** | None | Custom AES-256-GCM JSON encryption |
| **Architecture** | MVVM-ish | Full MVI |

---

## Suggested Implementation Order

- [ ] **Step 1:** Add Compose + Material 3 dependencies to `build.gradle`
- [ ] **Step 2:** Create MVI base classes (`MviViewModel`, contracts)
- [ ] **Step 3:** Create shared design system (theme, typography, colors)
- [ ] **Step 4:** Build shared composable components (`NoteCard`, `CategoryChip`, etc.)
- [ ] **Step 5:** Migrate **HomeScreen** (least complex, high impact)
- [ ] **Step 6:** Migrate **AllNotesScreen**
- [ ] **Step 7:** Migrate **NoteScreen** (most complex — bottom sheet + auto-save)
- [ ] **Step 8:** Migrate **ManageCategoryScreen** + `CategoryBottomSheet`
- [ ] **Step 9:** Add Room migrations (1→2→3→4)
- [ ] **Step 10:** Add DataStore preferences
- [ ] **Step 11:** Add Search screen
- [ ] **Step 12:** Add Settings screen
- [ ] **Step 13:** Add Export / Import with password
- [ ] **Step 14:** Polish animations + Shared Element Transitions
- [ ] **Step 15:** Final QA + Play Store release
