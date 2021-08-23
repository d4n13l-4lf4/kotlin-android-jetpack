package com.example.playpennydrop.types

import androidx.databinding.ObservableBoolean
import com.example.playpennydrop.game.AI

data class NewPlayer(
    var playerName: String = "",
    var isHuman: ObservableBoolean = ObservableBoolean(true),
    val canBeRemoved: Boolean = true,
    val canBeToggled: Boolean = true,
    var isIncluded: ObservableBoolean = ObservableBoolean(!canBeRemoved),
    var selectedAIPosition: Int = -1
) {
    fun selectedAI() = if(!isHuman.get()) {
        AI.basicAI.getOrNull(selectedAIPosition)
    } else {
        null
    }

    fun toPlayer() = Player(
        playerName = if (this.isHuman.get()) {
            this.playerName
        } else {
            (this.selectedAI()?.name ?: "AI")
        },
        isHuman = this.isHuman.get(),
        selectedAI = this.selectedAI()
    )
}
