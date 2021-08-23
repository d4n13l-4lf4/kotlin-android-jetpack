package com.example.playpennydrop.game

import com.example.playpennydrop.types.Player
import com.example.playpennydrop.types.Slot

data class AI(
    val aiId: Long = 0,
    val name: String,
    val rollAgain: (slots: List<Slot>) -> Boolean
    ) {
    override fun toString() = name

    companion object {
        @JvmStatic
        val basicAI = listOf(
            AI(1, "TwoFace") { slots -> slots.fullSlots() < 3 || (slots.fullSlots()) == 3 && coinFlipIsHeads()},
            AI(2, "No Go Noah") { slots -> slots.fullSlots() == 0},
            AI(3, "Bail Out Beulah") { slots -> slots.fullSlots() <= 1},
            AI(4, "Fearful Fred") { slots -> slots.fullSlots() <= 2},
            AI(5, "Even Steven") { slots -> slots.fullSlots() <= 3},
            AI(6, "Riverboat Ron") { slots -> slots.fullSlots() <= 4},
            AI(7, "Sammy Sixes") { slots -> slots.fullSlots() <= 5 },
            AI(8, "Random Rachael") { coinFlipIsHeads() }
        )
    }

    fun toPlayer() = Player(
        playerId = aiId,
        playerName = name,
        isHuman = false,
        selectedAI = this
    )
}

fun coinFlipIsHeads() = (Math.random() * 2).toInt() == 0
fun List<Slot>.fullSlots(): Int = this.count { it.canBeFilled && it.isFilled }
