package com.productivitystreak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.productivitystreak.ui.AppViewModel
import com.productivitystreak.ui.AppViewModelFactory
import com.productivitystreak.ui.navigation.NeverZeroApp
import com.productivitystreak.ui.theme.ProductivityStreakTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: AppViewModel by viewModels { AppViewModelFactory(application) }
        setContent {
            val state = viewModel.uiState.collectAsStateWithLifecycle()
            val darkTheme = when (state.value.profileState.theme) {
                com.productivitystreak.ui.state.profile.ProfileTheme.Dark -> true
                com.productivitystreak.ui.state.profile.ProfileTheme.Light -> false
                com.productivitystreak.ui.state.profile.ProfileTheme.Auto -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            ProductivityStreakTheme(darkTheme = darkTheme) {
                Surface {
                    NeverZeroApp(
                        uiState = state.value,
                        onRefreshQuote = viewModel::refreshQuote,
                        onSelectStreak = viewModel::onSelectStreak,
                        onToggleTask = viewModel::onToggleTask,
                        onSimulateTaskCompletion = viewModel::simulateTaskCompletion,
                        onLogReadingProgress = viewModel::onLogReadingProgress,
                        onAddVocabularyWord = viewModel::onAddVocabularyWord,
                        onToggleOnboardingCategory = viewModel::onToggleOnboardingCategory,
                        onCompleteOnboarding = viewModel::onCompleteOnboarding,
                        onDismissOnboarding = viewModel::onDismissOnboarding,
                        onToggleNotifications = viewModel::onToggleNotifications,
                        onChangeReminderFrequency = viewModel::onChangeReminderFrequency,
                        onToggleWeeklySummary = viewModel::onToggleWeeklySummary,
                        onChangeTheme = viewModel::onChangeTheme
                    )
                }
            }
        }
    }
}
