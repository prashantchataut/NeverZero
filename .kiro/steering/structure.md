# Project Structure

## Root Organization

```
app/src/main/java/com/productivitystreak/
├── data/              # Data layer (repositories, DAOs, entities)
├── ui/                # UI layer (screens, components, ViewModels)
├── notifications/     # Background work and notification scheduling
├── debug/             # Debug utilities and crash handling
├── MainActivity.kt
└── NeverZeroApplication.kt
```

## Data Layer (`data/`)

### Local Storage
- `local/`: Room database setup
  - `dao/`: Data Access Objects for each entity
  - `entity/`: Room entities with converters
  - `AppDatabase.kt`: Database singleton
  - `PreferencesManager.kt`: DataStore wrapper

### Repositories
- `repository/`: Business logic and data operations
  - Follow pattern: `XxxRepository.kt` with suspend functions
  - Return `RepositoryResult<T>` sealed class for error handling
  - Expose `Flow<T>` for reactive data streams

### Remote & AI
- `remote/`: API services (Retrofit)
- `gemini/`: Gemini AI client
- `ai/`: Buddha insights and AI models
- `backup/`: Backup/restore functionality

### Models
- `model/`: Domain models (not entities)
  - Separate from database entities for clean architecture
  - Use data classes with immutability

## UI Layer (`ui/`)

### Screens
- `screens/`: Feature-based organization
  - Each feature has its own package (e.g., `home/`, `reading/`, `vocabulary/`)
  - Typically contains: `XxxScreen.kt`, `XxxViewModel.kt`

### Components
- `components/`: Reusable UI components
  - `Buttons.kt`, `Cards.kt`, `Chips.kt`, etc.
  - Composable functions with preview annotations

### State Management
- `state/`: UI state classes
  - Immutable data classes representing screen state
  - Organized by feature (e.g., `home/`, `profile/`)
  - `AppUiState.kt`: Global app state

### Theme & Design
- `theme/`: Material 3 theming
  - `Color.kt`: Color palettes (light/dark)
  - `Type.kt`: Typography with Poppins font
  - `Theme.kt`: Theme composition
  - `Tokens.kt`: Design tokens
  - `Motion.kt`: Animation specifications

### Navigation
- `navigation/`: Compose Navigation setup
  - `NeverZeroApp.kt`: Main navigation graph

### Utilities
- `utils/`: UI helpers
- `icons/`: Custom icon definitions
- `widgets/`: Home screen widgets

## Notifications (`notifications/`)

- WorkManager-based background tasks
- Notification scheduling and delivery
- Pattern: `XxxWorker.kt` + `XxxScheduler.kt`

## Testing Structure

```
app/src/test/          # Unit tests (JVM)
app/src/androidTest/   # Instrumented tests (Android)
```

## Naming Conventions

### Files
- **Screens**: `XxxScreen.kt` (e.g., `HomeScreen.kt`)
- **ViewModels**: `XxxViewModel.kt` (e.g., `HomeViewModel.kt`)
- **Repositories**: `XxxRepository.kt` (e.g., `StreakRepository.kt`)
- **DAOs**: `XxxDao.kt` (e.g., `StreakDao.kt`)
- **Entities**: `XxxEntity.kt` (e.g., `StreakEntity.kt`)
- **Workers**: `XxxWorker.kt` (e.g., `StreakReminderWorker.kt`)

### Composables
- PascalCase for composable functions
- Prefix with feature name for clarity (e.g., `StreakCard`, `VocabularyList`)

### State Classes
- Suffix with `UiState` (e.g., `HomeUiState`, `AddUiState`)
- Use immutable data classes
- Organize in `ui/state/` by feature

## Architecture Patterns

### Repository Pattern
- Single source of truth for data operations
- Repositories injected via Application class
- Use `Flow` for reactive data, `suspend` for one-shot operations

### MVVM
- ViewModels hold UI state as `StateFlow`
- Screens observe state with `collectAsStateWithLifecycle()`
- ViewModels expose functions for user actions

### Dependency Injection
- Manual DI via `NeverZeroApplication`
- Lazy initialization of repositories and services
- ViewModels created via `AppViewModelFactory`

### Error Handling
- `RepositoryResult<T>` sealed class for repository operations
- `UiMessage` for user-facing error messages
- Global exception handler for crash reporting
