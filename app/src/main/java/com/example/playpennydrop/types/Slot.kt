package com.example.playpennydrop.types

import com.example.playpennydrop.data.Game

data class Slot(
    val number: Int,
    val canBeFilled: Boolean = true,
    var isFilled: Boolean = number % 2 == 0,
    var lastRolled: Boolean = number % 3 == 2
) {
    companion object {
        fun mapFromGame(game: Game?) = (1..6).map { slotNum ->
            Slot(
                number = slotNum,
                canBeFilled = slotNum != 6,
                isFilled = game?.filledSlots?.contains(slotNum) ?: false,
                lastRolled = game?.lastRoll == slotNum
            )
        }
    }
}

fun List<Slot>.clear() = this.forEach { slot ->
    slot.isFilled = false
    slot.lastRolled = false
}