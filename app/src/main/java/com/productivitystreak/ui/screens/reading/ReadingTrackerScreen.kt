package com.productivitystreak.ui.screens.reading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.reading.ReadingLog
import com.productivitystreak.ui.state.reading.ReadingTrackerState

@Composable
fun ReadingTrackerScreen(
    state: ReadingTrackerState,
    onAddProgress: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        SummaryCard(state)
        ReadingHistory(logs = state.recentActivity)
        Button(onClick = { onAddProgress(10) }, modifier = Modifier.fillMaxWidth()) {
            Text("Add 10 pages")
        }
    }
}

@Composable
private fun SummaryCard(state: ReadingTrackerState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Current streak: ${state.currentStreakDays} days", style = MaterialTheme.typography.titleMedium)
            Text(text = "Pages read today: ${state.pagesReadToday}/${state.goalPagesPerDay}")
            Text(text = "Progress: ${(state.progressFraction * 100).toInt()}%")
        }
    }
}

@Composable
private fun ReadingHistory(logs: List<ReadingLog>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(logs) { log ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = log.dateLabel, style = MaterialTheme.typography.titleSmall)
                    Text(text = "${log.pages} pages", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
