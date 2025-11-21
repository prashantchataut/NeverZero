package com.productivitystreak.ui.state.skills

import com.productivitystreak.data.model.SkillPathProgress

data class SkillPathsState(
    val pathsProgress: List<SkillPathProgress> = emptyList()
)
