# Implementation Plan

- [ ] 1. Set up design system foundation


  - Update Tokens.kt to ensure all spacing, elevation, and shape tokens are properly defined
  - Verify Theme.kt uses tokens consistently throughout
  - Create test utilities for design token validation
  - _Requirements: 1.1, 1.2, 1.3, 1.5_
  - _Status: COMPLETED - Tokens.kt, Theme.kt, and Motion.kt are fully implemented_

- [-] 1.1 Add Kotest property testing library





  - Add Kotest dependencies to app/build.gradle.kts
  - Configure Kotest for Android testing
  - _Requirements: Testing Strategy_

- [ ]* 1.2 Write property test for design token completeness


  - **Property: Design tokens exist and have valid values**
  - **Validates: Requirements 1.1, 1.2, 1.3**


- [ ] 2. Create XpButton component


  - Implement XpButton composable with "Claim XP" text
  - Add press animation using MotionSpec.snappySpring()
  - Use design tokens for padding, shape, and colors
  - Add enabled/disabled states
  - _Requirements: 2.3, 2.5_

  - _Status: COMPLETED - XpButton.kt exists_

- [x]* 2.1 Write unit tests for XpButton states

  - Test enabled state renders correctly
  - Test disabled state renders correctly
  - Test press animation triggers
  - _Requirements: 2.3, 2.5_

- [x] 3. Create ProtocolCard component
  - Implement ProtocolCard composable with InteractiveCard base
  - Add category color indicator (4dp x 40dp colored bar)
  - Display protocol title and streak count
  - Show "XP Claimed" badge when completed
  - Show XpButton when incomplete
  - Use design tokens for all spacing and styling
  - _Requirements: 2.1, 2.2, 2.3_
  - _Status: COMPLETED - ProtocolCard.kt exists_

- [ ]* 3.1 Write property test for ProtocolCard completeness
  - **Property 1: Protocol card completeness**
  - **Validates: Requirements 2.1**

- [ ]* 3.2 Write unit tests for ProtocolCard states
  - Test completed state shows "XP Claimed"
  - Test incomplete state shows "Claim XP" button
  - Test category color indicator renders
  - _Requirements: 2.2, 2.3_

- [x] 4. Create QuestRow component
  - Implement QuestRow composable with OutlinedCard base
  - Add checkbox for completion state
  - Display quest title and optional description
  - Use design tokens for spacing and styling
  - _Requirements: 2.4_
  - _Status: COMPLETED - QuestRow.kt exists_

- [ ]* 4.1 Write property test for QuestRow completeness
  - **Property 2: Quest row completeness**
  - **Validates: Requirements 2.4**

- [ ]* 4.2 Write unit tests for QuestRow
  - Test quest with description renders both title and description
  - Test quest without description renders only title
  - Test checkbox state reflects completion
  - _Requirements: 2.4_

- [ ] 5. Create CharacterBlock component
  - Implement CharacterBlock composable with ElevatedCard base
  - Display level and XP progress (e.g., "Level 5" and "100 / 200 XP")
  - Add LinearProgressIndicator for XP progress bar
  - Create RPGStatsRow sub-component for displaying stats
  - Display all five RPG attributes (Strength, Intelligence, Charisma, Wisdom, Discipline)
  - Use design tokens for spacing, shapes, and elevation
  - _Requirements: 3.2, 6.2_

- [ ]* 5.1 Write unit tests for CharacterBlock
  - Test level and XP display correctly
  - Test progress bar shows correct percentage
  - Test all five stats are displayed
  - _Requirements: 3.2, 6.2_

- [ ] 6. Update terminology throughout codebase
  - Search for "Streak" in UI strings and replace with "Protocol"
  - Search for "Task" in UI strings and replace with "Quest"
  - Update section headers in dashboard (already shows "ACTIVE PROTOCOLS" and "QUESTS")
  - Update navigation labels if needed
  - Update button text and labels
  - Do NOT change database field names or data model properties
  - _Requirements: 3.7, 7.4_
  - _Note: Dashboard already uses correct terminology, need to check other screens_

- [ ]* 6.1 Write property test for terminology consistency
  - **Property 5: Terminology consistency**
  - **Validates: Requirements 3.7, 7.4**

- [ ] 7. Integrate CharacterBlock into dashboard
  - Add CharacterBlock to DashboardScreen below greeting/date
  - Wire up level, XP, and stats data from ViewModel
  - Ensure proper spacing with design tokens
  - _Requirements: 3.1, 3.2_

- [ ]* 7.1 Write property test for dashboard protocol list
  - **Property 3: Dashboard protocol list completeness**
  - **Validates: Requirements 3.3**

- [ ]* 7.2 Write property test for dashboard quest list
  - **Property 4: Dashboard quest list completeness**
  - **Validates: Requirements 3.4**

- [ ]* 7.3 Write unit tests for dashboard layout
  - Test greeting and date display at top
  - Test CharacterBlock is present
  - Test section headers are correct
  - _Requirements: 3.1, 3.2_

- [ ] 8. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 9. Update Buddha Chat message components
  - Create BuddhaAvatar composable (circular avatar with Buddha icon)
  - Create UserAvatar composable (circular avatar with user icon)
  - Implement BuddhaMessageBubble composable
  - Align user messages to the right (Arrangement.End)
  - Align Buddha messages to the left (Arrangement.Start)
  - Use primaryContainer background for user messages
  - Use surfaceVariant background for Buddha messages
  - Add rounded corners with different radii for message tails
  - Display timestamp below message content
  - Use design tokens for sizing and spacing
  - _Requirements: 4.1, 4.2, 4.3_

- [ ]* 9.1 Write property test for chat message alignment
  - **Property 6: Chat message alignment and styling**
  - **Validates: Requirements 4.2, 4.3**

- [ ]* 9.2 Write unit tests for message bubbles
  - Test user message aligns right with correct background
  - Test Buddha message aligns left with correct background
  - Test avatars display correctly
  - _Requirements: 4.1, 4.2, 4.3_

- [ ] 10. Update Buddha Chat screen layout
  - Update BuddhaChatScreen to use new message bubble components
  - Ensure input bar uses design tokens for padding and shape
  - Update input bar styling to match design system
  - Use Shapes.medium for input bar corner radius
  - Use Spacing.md for input bar padding
  - _Requirements: 4.1, 4.4, 4.5_

- [ ]* 10.1 Write unit tests for chat screen layout
  - Test header displays correctly
  - Test input bar styling matches design system
  - _Requirements: 4.1, 4.4_

- [ ] 11. Redesign Stats screen
  - Reorder metrics to show most important at top (total XP, current level, active protocols)
  - Group related statistics (protocol stats together, quest stats together, time-based stats together)
  - Use StatCard components from design system
  - Use consistent spacing with design tokens
  - _Requirements: 5.1, 5.2, 5.3_

- [ ]* 11.1 Write unit tests for stats screen
  - Test important metrics appear at top
  - Test related stats are grouped
  - _Requirements: 5.1, 5.2_

- [ ] 12. Redesign Profile screen as character sheet
  - Create character sheet layout with user info at top
  - Display level and total XP prominently
  - Show all five RPG attributes with labels and values
  - Add achievements section
  - Use ElevatedCard for main character info
  - Use StatCard for individual attributes
  - Use design tokens for spacing and elevation
  - _Requirements: 6.1, 6.2, 6.3_

- [ ]* 12.1 Write unit tests for profile screen
  - Test character sheet layout displays correctly
  - Test all five RPG attributes are shown
  - Test level, XP, and achievements display
  - _Requirements: 6.1, 6.2, 6.3_

- [ ] 13. Simplify onboarding flow
  - Reduce onboarding screens to essential information only
  - Update all text to use "Protocol" and "Quest" terminology
  - Use design system components for all UI elements
  - Ensure smooth transition to dashboard on completion
  - _Requirements: 7.1, 7.2, 7.3, 7.4_

- [ ]* 13.1 Write unit tests for onboarding
  - Test onboarding uses correct terminology
  - Test navigation to dashboard works
  - _Requirements: 7.3, 7.4_

- [ ] 14. Implement XP claim animation
  - Create XpClaimAnimation composable
  - Use spring animation with elastic bounce (MotionSpec.elasticBounce())
  - Animate scale from 1.0 to 1.2 and back
  - Add particle effect or glow for visual feedback
  - Trigger haptic feedback on claim
  - Use Duration.medium2 for animation timing
  - _Requirements: 8.1_

- [ ]* 14.1 Write unit tests for XP claim animation
  - Test animation triggers on XP claim
  - Test animation uses correct timing
  - _Requirements: 8.1_

- [ ] 15. Add navigation transitions
  - Implement shared axis transitions for screen navigation
  - Use MotionSpec.slideIn() for entering screens
  - Use MotionSpec.slideOut() for exiting screens
  - Apply transitions to all navigation routes
  - Use Duration.medium2 for transition timing
  - _Requirements: 8.2_

- [ ]* 15.1 Write unit tests for navigation transitions
  - Test transitions apply to all routes
  - Test transition timing is consistent
  - _Requirements: 8.2_

- [ ] 16. Add success feedback animations
  - Create SuccessFeedback composable
  - Animate checkmark appearance with scale and fade
  - Use MotionSpec.bounce() for playful feedback
  - Add subtle color pulse effect
  - Trigger on protocol completion and quest completion
  - _Requirements: 8.3_

- [ ]* 16.1 Write unit tests for success feedback
  - Test feedback triggers on completion
  - Test animation timing is correct
  - _Requirements: 8.3_

- [ ] 17. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 18. Verify AI configuration
  - Check that GEMINI_API_KEY is set in local.properties or environment
  - Add initialization check in NeverZeroApplication
  - Log warning if API key is missing
  - Disable AI features gracefully if key is not configured
  - _Requirements: 9.1_
  - _Note: GeminiClient already has basic API key checking_

- [ ]* 18.1 Write property test for AI initialization
  - **Property 7: AI initialization and configuration**
  - **Validates: Requirements 9.1, 9.2**

- [ ] 19. Improve GeminiClient initialization
  - Add connection test in GeminiClient.init()
  - Verify successful connection to Gemini API
  - Handle connection failures gracefully
  - Log initialization success/failure
  - _Requirements: 9.2_

- [ ]* 19.1 Write unit tests for GeminiClient
  - Test initialization with valid API key
  - Test initialization with missing API key
  - Test connection success handling
  - Test connection failure handling
  - _Requirements: 9.2_

- [ ] 20. Improve AI error handling
  - Wrap all AI endpoint calls in try-catch blocks
  - Handle IOException for network errors
  - Handle ApiException for API errors (rate limiting, etc.)
  - Handle timeout errors with appropriate messages
  - Log all errors with diagnostic information (error type, timestamp, context)
  - Return user-friendly error messages (no stack traces or technical details)
  - _Requirements: 9.3, 9.4, 9.5_

- [ ]* 20.1 Write property test for AI error handling
  - **Property 8: AI error handling completeness**
  - **Validates: Requirements 9.3, 9.4, 9.5**

- [ ]* 20.2 Write unit tests for error scenarios
  - Test network error handling
  - Test rate limit error handling
  - Test timeout error handling
  - Test error logging includes required fields
  - Test user-facing messages are friendly
  - _Requirements: 9.3, 9.4, 9.5_

- [ ] 21. Test AI endpoints
  - Test Buddha Chat message sending and receiving
  - Test daily insight generation
  - Test word of the day feature
  - Verify all endpoints handle errors gracefully
  - Check error logs for diagnostic information
  - _Requirements: 9.3, 9.5_

- [ ]* 21.1 Write integration tests for AI endpoints
  - Test end-to-end Buddha Chat flow
  - Test daily insight generation
  - Test error handling in real scenarios
  - _Requirements: 9.3_

- [ ] 22. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ]* 23. Performance optimization
  - Profile animation performance (target 60fps)
  - Optimize LazyColumn rendering for large lists
  - Add remember() for expensive calculations
  - Use derivedStateOf for computed values
  - Test with large data sets (100+ protocols)
  - Monitor memory usage in chat interface

- [ ]* 23.1 Write performance tests
  - Test animation frame rates
  - Test memory usage with large data sets
  - Test scroll performance

- [ ]* 24. Accessibility improvements
  - Add content descriptions to all interactive elements
  - Verify color contrast meets WCAG AA standards
  - Ensure all touch targets meet 48dp minimum
  - Test with TalkBack screen reader
  - Add semantic labels for screen reader navigation

- [x]* 24.1 Write accessibility tests




  - Test content descriptions are present
  - Test touch target sizes
  - Test color contrast ratios
