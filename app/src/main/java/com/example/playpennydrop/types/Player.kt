package com.example.playpennydrop.types

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.playpennydrop.game.AI

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true) var playerId: Long = 0,
    val playerName: String = "",
    val isHuman: Boolean = true,
    val selectedAI: AI? = null
) {
    @Ignore
    var pennies: Int = defaultPennyCount

    fun addPennies(count: Int = 1) {
        pennies += count
    }

    @Ignore
    var isRolling: Boolean = false

    @Ignore
    var gamePlayerNumber: Int = -1

    fun penniesLeft(subtractPenny: Boolean = false) = (pennies - (if(subtractPenny) 1 else 0)) > 0

    companion object {
        const val defaultPennyCount = 10
    }
}