package com.example.playpennydrop

import com.example.playpennydrop.game.AI
import com.example.playpennydrop.game.GameHandler
import com.example.playpennydrop.types.Player
import org.junit.Test

class GameHandlerTest {
    @Test
    fun `Test nextPlayer() via pass function`() {
        val testPlayers = listOf(
            Player(1, "Michael", true),
            Player(2, "Daniel", true),
            Player(3, "Juan", true),
            Player(4, "Riverboat", false, selectedAI = AI.basicAI[5])
        )

        val currentPlayer = testPlayers.first { it.playerName == "Daniel" }
        val nextPlayer = testPlayers.first { it.playerName == "Juan" }
        GameHandler.pass(testPlayers, currentPlayer).also { result ->

        }
    }
}