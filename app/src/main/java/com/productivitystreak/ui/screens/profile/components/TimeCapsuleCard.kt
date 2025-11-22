package com.productivitystreak.ui.screens.profile.components

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
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.model.TimeCapsule

@Composable
fun TimeCapsuleCard(
    capsules: List<TimeCapsule>,
    onWriteNew: () -> Unit,
    onReflect: (TimeCapsule) -> Unit
) {
    val now = System.currentTimeMillis()
    val upcoming = capsules.filter { !it.opened && it.deliveryDateMillis > now }.minByOrNull { it.deliveryDateMillis }
    val pendingReflection = capsules.filter { !it.opened && it.deliveryDateMillis <= now }.minByOrNull { it.deliveryDateMillis }

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
            Text(
                text = "Time Capsule Protocol",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Write a serious letter to your future self and revisit whether you kept your promises.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            upcoming?.let {
                Text(
                    text = "Next delivery scheduled",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = it.goalDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            pendingReflection?.let {
                Text(
                    text = "Awaiting reflection",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = it.goalDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Capture reflection",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(onClick = { onReflect(it) })
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Text(
                text = "Write to future self",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable(onClick = onWriteNew)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}
