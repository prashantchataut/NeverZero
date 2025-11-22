package com.productivitystreak.ui.screens.skills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.productivitystreak.data.model.Badge
import com.productivitystreak.data.model.BadgeRequirementType
import com.productivitystreak.data.model.SkillPath
import com.productivitystreak.data.model.SkillPathProgress
import com.productivitystreak.data.model.UserBadge
import com.productivitystreak.ui.components.SkillPathCard
import com.productivitystreak.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillPathsScreen(
    onBack: () -> Unit,
    onPathSelected: (SkillPathProgress) -> Unit,
    paths: List<SkillPathProgress>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Skill Paths",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(bottom = Spacing.xl)
        ) {
            item {
                Text(
                    text = "Master your habits",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = Spacing.sm)
                )
            }

            items(paths) { progress ->
                SkillPathCard(
                    progress = progress,
                    onClick = { onPathSelected(progress) }
                )
            }
        }
    }
}
