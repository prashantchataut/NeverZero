package com.productivitystreak.ui.screens.vocabulary

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
import com.productivitystreak.ui.state.vocabulary.VocabularyState
import com.productivitystreak.ui.state.vocabulary.VocabularyWord

@Composable
fun VocabularyScreen(
    state: VocabularyState,
    onAddWord: (String, String, String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        OverviewCard(state)
        VocabularyList(words = state.words)
        Button(onClick = { onAddWord("Momentum", "The force that keeps you going", null) }, modifier = Modifier.fillMaxWidth()) {
            Text("Add sample word")
        }
    }
}

@Composable
private fun OverviewCard(state: VocabularyState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Current streak: ${state.currentStreakDays} days", style = MaterialTheme.typography.titleMedium)
            Text(text = "Words added today: ${state.wordsAddedToday}")
        }
    }
}

@Composable
private fun VocabularyList(words: List<VocabularyWord>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(words) { word ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = word.word, style = MaterialTheme.typography.titleSmall)
                    Text(text = word.definition, style = MaterialTheme.typography.bodyMedium)
                    word.example?.let {
                        Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
