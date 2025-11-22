package com.productivitystreak.ui.screens.profile.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.settings.SettingsState

@Composable
fun BackupSettingsCard(
    settingsState: SettingsState,
    onCreateBackup: () -> Unit,
    onRestoreBackup: () -> Unit,
    onSettingsRestoreFileSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    val restoreLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                onSettingsRestoreFileSelected(uri)
            }
        }
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Backups", style = MaterialTheme.typography.titleMedium)
            Text(
                text = settingsState.lastBackupTime?.let { "Last backup: $it" } ?: "No backups yet",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Create backup",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(enabled = !settingsState.isBackupInProgress) { onCreateBackup() }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
                Text(
                    text = "Restore from file",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(enabled = !settingsState.isRestoreInProgress) {
                            restoreLauncher.launch(arrayOf("application/json", "*/*"))
                            onRestoreBackup()
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}
