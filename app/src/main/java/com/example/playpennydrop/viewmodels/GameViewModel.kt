package com.example.playpennydrop.viewmodels

import android.app.Application
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.example.playpennydrop.data.*
import com.example.playpennydrop.game.GameHandler
import com.example.playpennydrop.game.TurnEnd
import com.example.playpennydrop.game.TurnResult
import com.example.playpennydrop.types.Player
import com.example.playpennydrop.types.Slot
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class GameViewModel(application: Application): AndroidViewModel(application) {
    private var clearText = false
    private val repository: PennyDropRepository
    val currentGame = MediatorLiveData<GameWithPlayers>()
    private val currentGameStatuses: LiveData<List<GameStatus>>
    val currentPlayer: LiveData<Player>
    val currentStandingText: LiveData<String>
    val slots: LiveData<List<Slot>>

    val canRoll: LiveData<Boolean>
    val canPass: LiveData<Boolean>

    private val prefs = PreferenceManager.getDefaultSharedPreferences(application)

    init {
        val database = PennyDropDatabase.getDatabase(application, viewModelScope)
        this.repository = PennyDropRepository.getInstance(database.pennyDropDao())

        this.currentGameStatuses = this.repository.getCurrentGameStatuses()
        this.currentGame.addSource(this.repository.getCurrentGameWithPlayers()) {
                gameWithPlayers ->
            updateCurrentGame(gameWithPlayers, this.currentGameStatuses.value)
        }
        this.currentGame.addSource(this.currentGameStatuses) { gameStatuses ->
            updateCurrentGame(this.currentGame.value, gameStatuses)
        }

        this.currentPlayer = Transformations.map(this.currentGame) { gameWithPlayers ->
            gameWithPlayers?.players?.firstOrNull { it.isRolling }
        }
        this.currentStandingText = Transformations.map(this.currentGame) { gameWithPlayers ->
            gameWithPlayers?.players?.let { players ->
                this.generateCurrentStandings(players)
            }
        }
        this.slots = Transformations.map(this.currentGame) { gameWithPlayers ->
            Slot.mapFromGame(gameWithPlayers?.game)
        }
        this.canRoll = Transformations.map(this.currentPlayer) { player ->
            player?.isHuman == true && currentGame.value?.game?.canRoll == true
        }

        this.canPass = Transformations.map(this.currentPlayer) { player ->
            player?.isHuman == true && currentGame.value?.game?.canPass == true
        }
    }

    private fun updateCurrentGame(
        gameWithPlayers: GameWithPlayers?,
        gameStatus: List<GameStatus>?
    ) {
        this.currentGame.value = gameWithPlayers?.updateStatuses(gameStatus)
    }

    suspend fun startGame(playersForNewGame: List<Player>) {
        repository.startGame(
            playersForNewGame,
            prefs?.getInt("pennyCount", Player.defaultPennyCount)
        )
    }

    private fun generateCurrentStandings(
        players: List<Player>,
        headerText: String = "Current Standings:"
    ) = players.sortedBy { it.pennies }.joinToString(separator = "\n", prefix = "$headerText\n") {
        "\t${it.playerName} - ${it.pennies} pennies"
    }

    private fun updateFromGameHandler(result: TurnResult) {
        val game = currentGame.value?.let { currentGameWithPlayers ->
            currentGameWithPlayers.game.copy(
                gameState = if(result.isGameOver) GameState.Finished else GameState.Started,
                lastRoll = result.lastRoll,
                filledSlots = updateFilledSlots(result, currentGameWithPlayers.game.filledSlots),
                currentTurnText = generateTurnText(result),
                canPass = result.canPass,
                canRoll = result.canRoll,
                endTime = if (result.isGameOver) OffsetDateTime.now() else null
            )
        } ?: return

        val statuses = currentGameStatuses.value?.map { status ->
            when(status.playerId) {
                result.previousPlayer?.playerId -> {
                    status.copy(
                        isRolling = false,
                        pennies = status.pennies + (result.coinChangeCount ?: 0)
                    )
                }
                result.currentPlayer?.playerId -> {
                    status.copy(
                        isRolling = !result.isGameOver,
                        pennies = status.pennies + if(!result.playerChanged) {
                            result.coinChangeCount ?: 0
                        } else 0
                    )
                }
                else -> status
            }
        } ?: emptyList()

        viewModelScope.launch {
            repository.updateGameAndStatuses(game, statuses)
            if (result.currentPlayer?.isHuman == false)
                playAITurn()
        }

    }

    private fun updateFilledSlots(
        result: TurnResult,
        filledSlots: List<Int>
    ) = when {
        result.clearSlots -> emptyList()
        result.lastRoll != null && result.lastRoll != 6 -> filledSlots + result.lastRoll
        else -> filledSlots
    }

    private suspend fun playAITurn() {
        delay(if (prefs.getBoolean("fastAI", false)) 100 else 1000)
        val game = currentGame.value?.game
        val players = currentGame.value?.players
        val currentPlayer = currentPlayer.value
        val slots = slots.value

        if (game != null && players != null && currentPlayer != null && slots != null) {
            GameHandler.playAITurn(players, currentPlayer, slots, game.canPass)
                ?.let { result -> updateFromGameHandler(result) }
        }
    }

    private fun generateTurnText(result: TurnResult): String {
        val currentText = if (clearText) "" else currentGame.value?.game?.currentTurnText ?: ""
        clearText = result.turnEnd != null

        val currentPlayerName = result.currentPlayer?.playerName ?: "???"

        return when {
            result.isGameOver -> generateGameOverText()
            result.turnEnd == TurnEnd.Bust -> "Your turn is end"
            result.turnEnd == TurnEnd.Pass -> "Your turn has passed"
            result.lastRoll != null -> "$currentText\n$currentPlayerName rolled a ${result.lastRoll}"
            else -> ""
        }
    }

    private fun generateGameOverText(): String {
        val statuses = this.currentGameStatuses.value
        val players = this.currentGame.value?.players?.map { player ->
            player.apply {
                this.pennies = statuses
                    ?.firstOrNull { it.playerId == playerId }
                    ?.pennies
                    ?: Player.defaultPennyCount
            }
        }
        val winningPLayer = players
            ?.firstOrNull { !it.penniesLeft() || it.isRolling }
            ?.apply { this.pennies = 0 }

        if (players == null || winningPLayer == null) return "N/A"

        return """
            |Game Over!
            |${winningPLayer.playerName} is the winner!
            |${generateCurrentStandings(players, "Final Scores: \n")}
        """.trimMargin()
    }

    fun roll() {
        val game = this.currentGame.value?.game
        val players = this.currentGame.value?.players
        val currentPlayer = this.currentPlayer.value
        val slots = this.slots.value

        if (game != null && players != null && currentPlayer != null && slots != null && game.canRoll) {
            updateFromGameHandler(GameHandler.roll(players, currentPlayer, slots))
        }
    }

    fun pass() {
        val game = this.currentGame.value?.game
        val players = this.currentGame.value?.players
        val currentPlayer = this.currentPlayer.value

        if (game != null && players != null && currentPlayer != null && game.canPass) {
            updateFromGameHandler(GameHandler.pass(players, currentPlayer))
        }
    }
}