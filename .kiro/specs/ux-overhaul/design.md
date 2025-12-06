# Design Document

## Overview

The UX Overhaul transforms Never Zero from a functional habit tracker into a polished, immersive character progression experience. The redesign centers on three core pillars:

1. **Unified Design System**: Establish consistent design tokens, components, and patterns that eliminate visual inconsistencies
2. **RPG-Centric Experience**: Rebrand "streaks" as "protocols" and "tasks" as "quests" to reinforce the character leveling metaphor
3. **Professional Polish**: Implement smooth animations, refined interactions, and attention to detail that elevate the user experience

The design maintains the existing "Calm Zen RPG" aesthetic with muted blue-grey backgrounds and desaturated accent colors while standardizing all components to use centralized design tokens. The dashboard becomes the character's command center, Buddha Chat becomes a more engaging conversational interface, and all interactions feel responsive and intentional.

## Architecture

### Design System Architecture

The design system follows a token-based architecture where all visual properties flow from centralized definitions:

```
Design Tokens (Tokens.kt)
    ↓
Theme System (Theme.kt, Color.kt, Type.kt, Motion.kt)
    ↓
Base Components (Cards.kt, Buttons.kt, etc.)
    ↓
Feature Components (ProtocolCard, QuestRow, CharacterBlock)
    ↓
Screens (Dashboard, BuddhaChat, Profile, Stats)
```

**Token Categories:**
- Spacing: 4dp grid system (xs through xxxxxl)
- Elevation: 7 levels (level0 through level7)
- Shapes: Material 3 shape system (extraSmall through extraLarge)
- Motion: Duration and easing specifications
- Colors: Semantic color roles mapped to design palette
- Typography: Poppins font family with standardized scale

### Component Hierarchy

**Atomic Components** (lowest level):
- XpButton: Standardized button for claiming XP
- CategoryIndicator: Colored bar showing protocol category
- ProgressBar: XP and level progress visualization

**Molecular Components** (composed of atoms):
- ProtocolCard: Complete protocol display with title, streak, status
- QuestRow: Quest item with checkbox and description
- StatCard: Metric display with icon, value, and trend
- StandardCard: Base card following design system

**Organism Components** (composed of molecules):
- CharacterBlock: Level, XP bar, and RPG stats
- ProtocolList: Collection of ProtocolCards
- QuestList: Collection of QuestRows
- ChatMessageBubble: Message with avatar and alignment

**Template Components** (screen layouts):
- DashboardLayout: Greeting, CharacterBlock, Protocols, Quests
- ChatLayout: Header, message list, input bar
- ProfileLayout: Character sheet with stats and achievements

### State Management

The design maintains the existing MVVM architecture with ViewModels managing UI state:

- **DashboardViewModel**: Manages protocol/quest data, completion states, XP calculations
- **BuddhaViewModel**: Manages chat messages, AI responses, input state
- **ProfileViewModel**: Manages user stats, achievements, character attributes
- **StatsViewModel**: Manages metrics, trends, historical data

State flows from repositories through ViewModels to composables using StateFlow and collectAsStateWithLifecycle().

## Components and Interfaces

### Design Token Interface

```kotlin
// Spacing tokens
object Spacing {
    val xs: Dp = 8.dp
    val sm: Dp = 12.dp
    val md: Dp = 16.dp
    val lg: Dp = 20.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 32.dp
}

// Elevation tokens
object Elevation {
    val level0: Dp = 0.dp
    val level1: Dp = 1.dp
    val level2: Dp = 3.dp
    val level3: Dp = 6.dp
    val level4: Dp = 8.dp
    val level5: Dp = 12.dp
}

// Shape tokens (already exist in Tokens.kt)
object Shapes {
    val extraSmall = RoundedCornerShape(8.dp)
    val small = RoundedCornerShape(12.dp)
    val medium = RoundedCornerShape(16.dp)
    val large = RoundedCornerShape(20.dp)
    val extraLarge = RoundedCornerShape(28.dp)
}
```

### ProtocolCard Component

```kotlin
@Composable
fun ProtocolCard(
    protocol: Protocol,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    InteractiveCard(
        onClick = if (!protocol.isCompleted) onClaim else {},
        modifier = modifier,
        shape = Shapes.medium,
        elevation = Elevation.level2
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category color indicator
            CategoryIndicator(color = protocol.categoryColor)
            
            Spacer(Modifier.width(Spacing.md))
            
            // Protocol info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = protocol.title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (protocol.streakCount > 0) {
                    Text(
                        text = "${protocol.streakCount} day streak",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            // Status indicator
            if (protocol.isCompleted) {
                XpClaimedBadge()
            } else {
                XpButton(onClick = onClaim)
            }
        }
    }
}
```

### QuestRow Component

```kotlin
@Composable
fun QuestRow(
    quest: Quest,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onToggle,
        modifier = modifier,
        shape = Shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = quest.isCompleted,
                onCheckedChange = { onToggle() }
            )
            
            Spacer(Modifier.width(Spacing.sm))
            
            Column {
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                quest.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
```

### XpButton Component

```kotlin
@Composable
fun XpButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = MotionSpec.snappySpring()
    )
    
    Surface(
        onClick = onClick,
        modifier = modifier.scale(scale),
        enabled = enabled,
        shape = Shapes.full,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        border = BorderStroke(
            width = Border.thin,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
    ) {
        Text(
            text = "Claim XP",
            modifier = Modifier.padding(
                horizontal = Spacing.md,
                vertical = Spacing.xs
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}
```

### CharacterBlock Component

```kotlin
@Composable
fun CharacterBlock(
    level: Int,
    currentXp: Int,
    xpToNextLevel: Int,
    stats: CharacterStats,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = Shapes.large,
        elevation = Elevation.level3
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Level and XP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Level $level",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$currentXp / $xpToNextLevel XP",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // XP Progress Bar
            LinearProgressIndicator(
                progress = currentXp.toFloat() / xpToNextLevel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(Shapes.full),
                color = MaterialTheme.colorScheme.primary
            )
            
            // RPG Stats
            RPGStatsRow(stats = stats)
        }
    }
}
```

### Buddha Chat Interface

```kotlin
@Composable
fun BuddhaMessageBubble(
    message: String,
    timestamp: String,
    isUser: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            BuddhaAvatar(modifier = Modifier.size(Size.avatarMedium))
            Spacer(Modifier.width(Spacing.sm))
        }
        
        Surface(
            shape = if (isUser) {
                RoundedCornerShape(
                    topStart = Shapes.medium.topStart,
                    topEnd = Shapes.medium.topEnd,
                    bottomStart = Shapes.medium.bottomStart,
                    bottomEnd = 4.dp
                )
            } else {
                RoundedCornerShape(
                    topStart = Shapes.medium.topStart,
                    topEnd = Shapes.medium.topEnd,
                    bottomStart = 4.dp,
                    bottomEnd = Shapes.medium.bottomEnd
                )
            },
            color = if (isUser) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (isUser) {
            Spacer(Modifier.width(Spacing.sm))
            UserAvatar(modifier = Modifier.size(Size.avatarMedium))
        }
    }
}
```

## Data Models

### Protocol Model

```kotlin
data class Protocol(
    val id: String,
    val title: String,
    val categoryColor: Color,
    val streakCount: Int,
    val isCompleted: Boolean,
    val xpValue: Int,
    val lastCompletedDate: LocalDate?
)
```

### Quest Model

```kotlin
data class Quest(
    val id: String,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val xpValue: Int,
    val priority: QuestPriority
)

enum class QuestPriority {
    LOW, MEDIUM, HIGH
}
```

### Character Stats Model

```kotlin
data class CharacterStats(
    val strength: Int,      // Physical protocols (exercise, wellness)
    val intelligence: Int,  // Learning protocols (reading, vocabulary)
    val charisma: Int,      // Social protocols (communication, networking)
    val wisdom: Int,        // Reflection protocols (meditation, journaling)
    val discipline: Int     // Consistency across all protocols
)
```

### Dashboard UI State

```kotlin
data class DashboardUiState(
    val greeting: String,
    val currentDate: String,
    val level: Int,
    val currentXp: Int,
    val xpToNextLevel: Int,
    val stats: CharacterStats,
    val activeProtocols: List<Protocol>,
    val quests: List<Quest>,
    val aiInsight: String?,
    val isLoading: Boolean = false
)
```

### Chat UI State

```kotlin
data class ChatUiState(
    val messages: List<ChatMessage>,
    val inputText: String,
    val isLoading: Boolean,
    val error: String?
)

data class ChatMessage(
    val id: String,
    val content: String,
    val timestamp: String,
    val isUser: Boolean
)
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*


### Property Reflection

After reviewing the prework analysis, several properties can be consolidated or are redundant:

- Properties 3.3 and 3.4 (displaying all protocols and quests) are similar in nature and test the same pattern
- Properties 4.2 and 4.3 (message alignment and styling) both validate message rendering and can be combined
- Properties 9.1 and 9.2 (API key verification and connection) are both initialization checks that can be tested together
- Properties 9.3, 9.4, and 9.5 (error handling, messages, and logging) all relate to error handling and can be consolidated

The consolidated properties provide comprehensive coverage without redundancy.

### Correctness Properties

Property 1: Protocol card completeness
*For any* Protocol object with valid data, rendering a ProtocolCard should produce UI output containing the protocol title, streak count, category color indicator, and completion state indicator
**Validates: Requirements 2.1**

Property 2: Quest row completeness
*For any* Quest object with valid data, rendering a QuestRow should produce UI output containing the quest title, optional description (if present), and completion checkbox
**Validates: Requirements 2.4**

Property 3: Dashboard protocol list completeness
*For any* list of active protocols, the dashboard should render all protocols in the "Active Protocols" section with their completion status
**Validates: Requirements 3.3**

Property 4: Dashboard quest list completeness
*For any* list of quests, the dashboard should render all quests in the "Quests" section
**Validates: Requirements 3.4**

Property 5: Terminology consistency
*For any* dashboard UI text, the rendered output should contain "Protocol" and "Quest" terminology and should not contain "Streak" or "Task" terminology
**Validates: Requirements 3.7, 7.4**

Property 6: Chat message alignment and styling
*For any* list of chat messages, user messages should be aligned right with primary container background, and Buddha messages should be aligned left with surface variant background
**Validates: Requirements 4.2, 4.3**

Property 7: AI initialization and configuration
*For any* application startup, the GeminiClient initialization should verify the API key is configured and attempt connection, handling both success and failure cases appropriately
**Validates: Requirements 9.1, 9.2**

Property 8: AI error handling completeness
*For any* AI endpoint error, the application should handle the error gracefully, display a user-friendly error message, and log diagnostic information including error type, timestamp, and context
**Validates: Requirements 9.3, 9.4, 9.5**

## Error Handling

### Design System Errors

**Missing Token Reference**: If a component attempts to reference a non-existent design token, the application should fail at compile time (Kotlin type safety). All tokens are defined as object properties, ensuring compile-time verification.

**Invalid Token Values**: Design tokens use Kotlin's type system (Dp, Color, etc.) to prevent invalid values. Runtime validation is not needed as the type system enforces correctness.

### Component Rendering Errors

**Null or Invalid Data**: Components should handle null or invalid data gracefully:
- ProtocolCard: If protocol data is invalid, display a placeholder or error state
- QuestRow: If quest data is invalid, skip rendering or show error indicator
- CharacterBlock: If stats are invalid, display default values (level 1, 0 XP)

**Missing Required Fields**: Use Kotlin's non-null types and data class validation to ensure required fields are present at compile time.

### UI State Errors

**Loading States**: All screens should display loading indicators when data is being fetched:
```kotlin
if (uiState.isLoading) {
    LoadingIndicator()
} else {
    Content(uiState)
}
```

**Empty States**: When lists are empty, display appropriate empty state messages:
- No protocols: "Create your first protocol to begin your journey"
- No quests: "All quests completed! Check back tomorrow"
- No messages: "Start a conversation with Buddha"

**Error States**: When operations fail, display user-friendly error messages with retry options:
```kotlin
if (uiState.error != null) {
    ErrorMessage(
        message = uiState.error,
        onRetry = { viewModel.retry() }
    )
}
```

### AI Integration Errors

**API Key Missing**: On app initialization, check for Gemini API key:
```kotlin
val apiKey = BuildConfig.GEMINI_API_KEY
if (apiKey.isBlank()) {
    Log.e("GeminiClient", "API key not configured")
    // Disable AI features gracefully
    return Result.failure(ApiKeyMissingException())
}
```

**Network Errors**: Handle network failures with exponential backoff:
```kotlin
suspend fun fetchAiResponse(prompt: String): Result<String> {
    return try {
        val response = geminiClient.generateContent(prompt)
        Result.success(response)
    } catch (e: IOException) {
        Log.e("AI", "Network error: ${e.message}")
        Result.failure(NetworkException("Unable to reach AI service"))
    } catch (e: Exception) {
        Log.e("AI", "Unexpected error: ${e.message}", e)
        Result.failure(e)
    }
}
```

**Rate Limiting**: Handle API rate limits with appropriate user messaging:
```kotlin
catch (e: ApiException) {
    when (e.statusCode) {
        429 -> Result.failure(RateLimitException("Too many requests. Please try again later."))
        else -> Result.failure(e)
    }
}
```

**Timeout Errors**: Set reasonable timeouts and handle timeout scenarios:
```kotlin
val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()
```

### Animation Errors

**Performance Issues**: If animations cause performance problems, provide fallback to instant transitions:
```kotlin
val animationSpec = if (isLowPerformanceDevice) {
    snap()
} else {
    MotionSpec.quickScale()
}
```

**Interrupted Animations**: Ensure animations can be interrupted and restarted without visual glitches by using Compose's animation APIs which handle interruption automatically.

## Testing Strategy

### Unit Testing

**Component Tests**: Test individual components with various input states:
- ProtocolCard with completed/incomplete states
- QuestRow with/without descriptions
- CharacterBlock with various XP levels
- XpButton enabled/disabled states

**ViewModel Tests**: Test state management and business logic:
- DashboardViewModel: Protocol completion, XP calculation
- BuddhaViewModel: Message handling, AI response processing
- ProfileViewModel: Stat calculations, achievement unlocking

**Repository Tests**: Test data operations:
- Protocol CRUD operations
- Quest management
- XP calculations and level progression

**Example Unit Tests**:
```kotlin
@Test
fun `protocol card displays all required elements`() {
    val protocol = Protocol(
        id = "1",
        title = "Morning Meditation",
        categoryColor = Color.Blue,
        streakCount = 5,
        isCompleted = false,
        xpValue = 10
    )
    
    composeTestRule.setContent {
        ProtocolCard(protocol = protocol, onClaim = {})
    }
    
    composeTestRule.onNodeWithText("Morning Meditation").assertExists()
    composeTestRule.onNodeWithText("5 day streak").assertExists()
    composeTestRule.onNodeWithText("Claim XP").assertExists()
}

@Test
fun `completed protocol shows XP claimed badge`() {
    val protocol = Protocol(
        id = "1",
        title = "Reading",
        categoryColor = Color.Cyan,
        streakCount = 10,
        isCompleted = true,
        xpValue = 15
    )
    
    composeTestRule.setContent {
        ProtocolCard(protocol = protocol, onClaim = {})
    }
    
    composeTestRule.onNodeWithText("XP Claimed").assertExists()
    composeTestRule.onNodeWithText("Claim XP").assertDoesNotExist()
}
```

### Property-Based Testing

The design uses **Kotest Property Testing** library for property-based tests. Each property-based test should run a minimum of 100 iterations to ensure comprehensive coverage.

**Property Test 1: Protocol Card Completeness**
```kotlin
@Test
fun `property - protocol card displays all required elements for any valid protocol`() = runTest {
    checkAll(100, Arb.protocol()) { protocol ->
        val rendered = renderToString { ProtocolCard(protocol, onClaim = {}) }
        
        rendered shouldContain protocol.title
        rendered shouldContain "${protocol.streakCount} day streak"
        if (protocol.isCompleted) {
            rendered shouldContain "XP Claimed"
        } else {
            rendered shouldContain "Claim XP"
        }
    }
}
```
**Feature: ux-overhaul, Property 1: Protocol card completeness**

**Property Test 2: Quest Row Completeness**
```kotlin
@Test
fun `property - quest row displays all required elements for any valid quest`() = runTest {
    checkAll(100, Arb.quest()) { quest ->
        val rendered = renderToString { QuestRow(quest, onToggle = {}) }
        
        rendered shouldContain quest.title
        quest.description?.let { rendered shouldContain it }
        // Verify checkbox is present
        rendered shouldContain "checkbox"
    }
}
```
**Feature: ux-overhaul, Property 2: Quest row completeness**

**Property Test 3: Dashboard Protocol List Completeness**
```kotlin
@Test
fun `property - dashboard renders all active protocols`() = runTest {
    checkAll(100, Arb.list(Arb.protocol(), 1..20)) { protocols ->
        val uiState = DashboardUiState(
            greeting = "Good morning",
            currentDate = "Dec 2, 2025",
            level = 5,
            currentXp = 100,
            xpToNextLevel = 200,
            stats = CharacterStats(10, 10, 10, 10, 10),
            activeProtocols = protocols,
            quests = emptyList(),
            aiInsight = null
        )
        
        val rendered = renderToString { DashboardContent(uiState) }
        
        protocols.forEach { protocol ->
            rendered shouldContain protocol.title
        }
    }
}
```
**Feature: ux-overhaul, Property 3: Dashboard protocol list completeness**

**Property Test 4: Dashboard Quest List Completeness**
```kotlin
@Test
fun `property - dashboard renders all quests`() = runTest {
    checkAll(100, Arb.list(Arb.quest(), 1..20)) { quests ->
        val uiState = DashboardUiState(
            greeting = "Good morning",
            currentDate = "Dec 2, 2025",
            level = 5,
            currentXp = 100,
            xpToNextLevel = 200,
            stats = CharacterStats(10, 10, 10, 10, 10),
            activeProtocols = emptyList(),
            quests = quests,
            aiInsight = null
        )
        
        val rendered = renderToString { DashboardContent(uiState) }
        
        quests.forEach { quest ->
            rendered shouldContain quest.title
        }
    }
}
```
**Feature: ux-overhaul, Property 4: Dashboard quest list completeness**

**Property Test 5: Terminology Consistency**
```kotlin
@Test
fun `property - dashboard uses protocol and quest terminology`() = runTest {
    checkAll(100, Arb.dashboardUiState()) { uiState ->
        val rendered = renderToString { DashboardContent(uiState) }
        
        // Should contain new terminology
        rendered shouldContain "Protocol"
        rendered shouldContain "Quest"
        
        // Should NOT contain old terminology
        rendered shouldNotContain "Streak"
        rendered shouldNotContain "Task"
    }
}
```
**Feature: ux-overhaul, Property 5: Terminology consistency**

**Property Test 6: Chat Message Alignment and Styling**
```kotlin
@Test
fun `property - chat messages have correct alignment and styling`() = runTest {
    checkAll(100, Arb.list(Arb.chatMessage(), 1..50)) { messages ->
        val rendered = renderToSemanticTree { 
            ChatMessageList(messages) 
        }
        
        messages.forEach { message ->
            val messageNode = rendered.findNode { it.text == message.content }
            
            if (message.isUser) {
                messageNode.alignment shouldBe Alignment.End
                messageNode.backgroundColor shouldBe MaterialTheme.colorScheme.primaryContainer
            } else {
                messageNode.alignment shouldBe Alignment.Start
                messageNode.backgroundColor shouldBe MaterialTheme.colorScheme.surfaceVariant
            }
        }
    }
}
```
**Feature: ux-overhaul, Property 6: Chat message alignment and styling**

**Property Test 7: AI Initialization and Configuration**
```kotlin
@Test
fun `property - AI initialization verifies configuration`() = runTest {
    checkAll(100, Arb.apiKeyConfig()) { config ->
        val result = GeminiClient.initialize(config)
        
        if (config.apiKey.isBlank()) {
            result.isFailure shouldBe true
            result.exceptionOrNull() shouldBe instanceOf<ApiKeyMissingException>()
        } else {
            // Should attempt connection
            result.isSuccess shouldBe true || result.isFailure
            // Should log initialization attempt
            verifyLogged("GeminiClient initialization")
        }
    }
}
```
**Feature: ux-overhaul, Property 7: AI initialization and configuration**

**Property Test 8: AI Error Handling Completeness**
```kotlin
@Test
fun `property - AI errors are handled with messages and logging`() = runTest {
    checkAll(100, Arb.aiError()) { error ->
        val result = handleAiError(error)
        
        // Should return user-friendly message
        result.errorMessage shouldNotBe null
        result.errorMessage shouldNotContain "Exception"
        result.errorMessage shouldNotContain "Stack trace"
        
        // Should log diagnostic information
        val logEntry = getLastLogEntry()
        logEntry shouldContain error.type
        logEntry shouldContain "timestamp"
        logEntry shouldContain error.context
    }
}
```
**Feature: ux-overhaul, Property 8: AI error handling completeness**

**Test Generators**:
```kotlin
fun Arb.Companion.protocol() = arbitrary {
    Protocol(
        id = Arb.uuid().bind().toString(),
        title = Arb.string(5..50).bind(),
        categoryColor = Arb.color().bind(),
        streakCount = Arb.int(0..365).bind(),
        isCompleted = Arb.bool().bind(),
        xpValue = Arb.int(5..50).bind(),
        lastCompletedDate = Arb.localDate().orNull().bind()
    )
}

fun Arb.Companion.quest() = arbitrary {
    Quest(
        id = Arb.uuid().bind().toString(),
        title = Arb.string(5..50).bind(),
        description = Arb.string(10..100).orNull().bind(),
        isCompleted = Arb.bool().bind(),
        xpValue = Arb.int(5..25).bind(),
        priority = Arb.enum<QuestPriority>().bind()
    )
}

fun Arb.Companion.chatMessage() = arbitrary {
    ChatMessage(
        id = Arb.uuid().bind().toString(),
        content = Arb.string(10..200).bind(),
        timestamp = Arb.instant().bind().toString(),
        isUser = Arb.bool().bind()
    )
}
```

### Integration Testing

**Screen Navigation Tests**: Verify navigation flows work correctly:
- Dashboard → Buddha Chat → Dashboard
- Dashboard → Profile → Stats → Dashboard
- Onboarding → Dashboard

**AI Integration Tests**: Test end-to-end AI functionality:
- Send message to Buddha Chat, receive response
- Generate daily insight on dashboard
- Handle AI service unavailability

**Data Persistence Tests**: Verify data survives app restarts:
- Complete protocol, restart app, verify completion persists
- Earn XP, restart app, verify XP and level persist

### Visual Regression Testing

Use screenshot testing to catch unintended visual changes:
- Capture screenshots of all major screens
- Compare against baseline images
- Flag differences for manual review

### Performance Testing

**Animation Performance**: Ensure animations run at 60fps:
- Monitor frame times during XP claim animation
- Test with large protocol/quest lists
- Verify smooth scrolling

**Memory Usage**: Monitor memory consumption:
- Test with large data sets (100+ protocols)
- Verify no memory leaks in chat interface
- Check image loading and caching

## Implementation Notes

### Migration Strategy

The UX overhaul should be implemented incrementally to minimize risk:

**Phase 1: Design System Foundation**
1. Update Tokens.kt with any missing tokens
2. Refactor Theme.kt to use tokens consistently
3. Create base components (XpButton, StandardCard)

**Phase 2: Component Library**
1. Implement ProtocolCard
2. Implement QuestRow
3. Implement CharacterBlock
4. Update existing components to use design system

**Phase 3: Dashboard Redesign**
1. Create new dashboard layout
2. Integrate ProtocolCard and QuestRow
3. Add CharacterBlock
4. Update terminology throughout

**Phase 4: Buddha Chat Polish**
1. Redesign message bubbles
2. Add avatar/header
3. Improve input bar styling
4. Test message alignment

**Phase 5: Secondary Screens**
1. Redesign Stats screen
2. Redesign Profile as character sheet
3. Simplify Onboarding
4. Update any remaining screens

**Phase 6: Motion and Polish**
1. Implement XP claim animation
2. Add navigation transitions
3. Add success feedback animations
4. Performance optimization

**Phase 7: AI Investigation and Fixes**
1. Verify API key configuration
2. Test GeminiClient initialization
3. Fix any AI endpoint issues
4. Improve error handling and logging

### Backward Compatibility

**Data Migration**: Existing user data should work without migration:
- Protocol/Quest data models remain unchanged
- Only UI presentation changes
- No database schema changes required

**Terminology**: Update UI strings while maintaining data model field names:
- Database: Keep "streak" field names
- UI: Display as "Protocol"
- API: No changes needed

### Accessibility

**Color Contrast**: Ensure all text meets WCAG AA standards:
- TextPrimary on Background: 4.5:1 minimum
- TextSecondary on Surface: 4.5:1 minimum
- Primary on PrimaryContainer: 4.5:1 minimum

**Touch Targets**: All interactive elements meet minimum size:
- Buttons: 48dp minimum (TouchTarget.minimum)
- Checkboxes: 48dp minimum
- Cards: Full width, 56dp minimum height

**Screen Reader Support**: Add content descriptions:
- ProtocolCard: "Protocol: {title}, {streakCount} day streak, {status}"
- QuestRow: "Quest: {title}, {completed/incomplete}"
- XpButton: "Claim {xp} experience points"

**Keyboard Navigation**: Ensure all interactive elements are keyboard accessible (handled by Compose automatically).

### Performance Considerations

**Lazy Loading**: Use LazyColumn for protocol and quest lists:
```kotlin
LazyColumn {
    items(protocols) { protocol ->
        ProtocolCard(protocol, onClaim = { ... })
    }
}
```

**State Hoisting**: Keep state in ViewModels, not composables:
- Prevents unnecessary recompositions
- Survives configuration changes
- Easier to test

**Remember and Derivation**: Use remember and derivedStateOf appropriately:
```kotlin
val sortedProtocols = remember(protocols) {
    protocols.sortedBy { it.title }
}
```

**Animation Performance**: Use hardware acceleration:
- Compose animations use hardware acceleration by default
- Avoid complex calculations in animation blocks
- Use graphicsLayer for transforms

### Security Considerations

**API Key Protection**: Never expose API keys in logs or UI:
```kotlin
// Good
Log.d("AI", "Initializing client")

// Bad
Log.d("AI", "API Key: $apiKey")
```

**Input Validation**: Validate all user input:
- Protocol titles: Max length, no special characters
- Quest descriptions: Max length
- Chat messages: Max length, sanitize before sending to AI

**Error Messages**: Don't expose internal details:
```kotlin
// Good
"Unable to connect to AI service. Please try again."

// Bad
"API call failed: 401 Unauthorized at endpoint /v1/generate"
```
