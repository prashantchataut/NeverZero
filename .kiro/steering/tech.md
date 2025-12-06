# Technology Stack

## Build System

- **Gradle**: 8.5.2 with Kotlin DSL
- **Android Gradle Plugin**: 8.5.2
- **Kotlin**: 2.0.21
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

## Core Technologies

### UI Framework
- **Jetpack Compose**: Material 3 design system
- **Compose BOM**: 2024.08.00
- **Navigation**: Compose Navigation 2.8.0
- **Theme**: Custom Material 3 theme with Poppins typography

### Architecture
- **Pattern**: MVVM with Repository pattern
- **State Management**: StateFlow and Compose state
- **Dependency Injection**: Manual DI via Application class
- **Coroutines**: kotlinx-coroutines 1.8.1

### Data Layer
- **Local Database**: Room 2.6.1 with KSP
- **Preferences**: DataStore Preferences 1.1.1
- **Backup**: Custom BackupManager with JSON export

### Networking & AI
- **HTTP Client**: Retrofit 2.11.0 + OkHttp 4.12.0
- **JSON**: Moshi 1.15.1
- **AI**: Google Generative AI SDK 0.8.0 (Gemini)

### Background Work
- **WorkManager**: 2.9.1 for scheduled tasks
- **Notifications**: Custom notification engine with exact alarms

### Testing
- **Unit Tests**: JUnit 4.13.2, Mockito 5.7.0
- **UI Tests**: Espresso 3.6.1, Compose UI Test
- **Coverage**: Kover 0.8.0 (Jacoco)

## Common Commands

### Build & Run
```bash
# Clean build
gradlew clean

# Build debug APK
gradlew assembleDebug

# Build release APK (requires keystore)
gradlew assembleRelease

# Install debug on device
gradlew installDebug

# Run unit tests
gradlew test

# Run instrumented tests
gradlew connectedAndroidTest

# Generate test coverage report
gradlew koverHtmlReport
```

### Code Quality
```bash
# Check dependencies for vulnerabilities
gradlew dependencyCheckAnalyze

# Lint check
gradlew lint
```

## Configuration

### API Keys
- `GEMINI_API_KEY`: Required for AI features (set in `local.properties` or environment)

### Signing
- Release signing requires `keystore/release_new.keystore` and credentials in `keystore.properties`:
  - `RELEASE_STORE_PASSWORD`
  - `RELEASE_KEY_ALIAS`
  - `RELEASE_KEY_PASSWORD`

### Performance
- Gradle daemon with 4GB heap
- Parallel builds enabled
- Build cache enabled
- Core library desugaring for Java 8+ APIs on older Android versions
