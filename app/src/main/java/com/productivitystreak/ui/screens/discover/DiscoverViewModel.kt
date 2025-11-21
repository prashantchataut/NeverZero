package com.productivitystreak.ui.screens.discover

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.productivitystreak.data.local.PreferencesManager
import com.productivitystreak.data.repository.AssetRepository
import com.productivitystreak.ui.state.discover.DiscoverState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiscoverViewModel(
    private val assetRepository: AssetRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverState())
    val uiState: StateFlow<DiscoverState> = _uiState.asStateFlow()

    init {
        observeAssets()
        // bootstrapStaticState() // If needed
    }

    private fun observeAssets() {
        viewModelScope.launch {
            try {
                assetRepository.observeAssets().collect { assets ->
                    _uiState.update { it.copy(assets = assets) }
                }
            } catch (e: Exception) {
                Log.e("DiscoverViewModel", "Error observing assets", e)
            }
        }
    }

    fun onAssetConsumed(assetId: String) {
        viewModelScope.launch {
            try {
                val asset = assetRepository.getAssetById(assetId) ?: return@launch
                if (asset.xpValue > 0) {
                    preferencesManager.addPoints(asset.xpValue)
                }
            } catch (e: Exception) {
                Log.e("DiscoverViewModel", "Error applying XP for asset consumption $assetId", e)
            }
        }
    }

    fun onAssetTestPassed(assetId: String) {
        viewModelScope.launch {
            try {
                val updated = assetRepository.markCertified(assetId)
                val xp = updated?.xpValue ?: 0
                if (xp > 0) {
                    preferencesManager.addPoints(xp)
                }
            } catch (e: Exception) {
                Log.e("DiscoverViewModel", "Error applying XP for asset test $assetId", e)
            }
        }
    }
}
