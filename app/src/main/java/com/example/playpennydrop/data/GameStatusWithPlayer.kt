package com.example.playpennydrop.data

import androidx.room.Embedded
import androidx.room.Relation
import com.example.playpennydrop.types.Player

data class GameStatusWithPlayer(
    @Embedded val gameStatus: GameStatus,
    @Relation(
        parentColumn = "playerId",
        entityColumn = "playerId"
    )
    val player: Player
)
