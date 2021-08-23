package com.example.playpennydrop.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.playpennydrop.types.Player
import java.time.OffsetDateTime

@Dao
abstract class PennyDropDao {
    @Query("SELECT * FROM players WHERE playerName = :playerName")
    abstract fun getPlayer(playerName: String): Player?

    @Insert
    abstract suspend fun insertGame(game: Game): Long

    @Insert
    abstract suspend fun insertPlayer(player: Player): Long

    @Insert
    abstract suspend fun insertPlayers(players: List<Player>): List<Long>

    @Update
    abstract suspend fun updateGame(game: Game)

    @Transaction
    @Query("SELECT * FROM games ORDER BY startTime DESC LIMIT 1")
    abstract fun getCurrentGameWithPlayers(): LiveData<GameWithPlayers>

    @Transaction
    @Query("""
        SELECT * FROM game_statuses
        WHERE gameId = (
            SELECT gameId FROM games
            WHERE endTime IS NULL
            ORDER BY startTime DESC
            LIMIT 1
        )
        ORDER BY gamePlayerNumber
    """)
    abstract fun getCurrentGameStatuses(): LiveData<List<GameStatus>>

    @Query(
        """
            UPDATE games
            SET endTime = :endDate, gameState = :gameState
            WHERE endTime IS NULL
        """
    )
    abstract suspend fun closeOpenGames(
        endDate: OffsetDateTime = OffsetDateTime.now(),
        gameState: GameState = GameState.Cancelled
    )

    @Insert
    abstract suspend fun insertGameStatuses(gameStatus: List<GameStatus>)

    @Transaction
    open suspend fun startGame(players: List<Player>, pennyCount: Int? = null): Long {
        this.closeOpenGames()

        val gameId = this.insertGame(
            Game(
                gameState = GameState.Started,
                currentTurnText = "The game has begun!\n",
                canRoll = true
            )
        )

        val playersId = players.map { player ->
            getPlayer(player.playerName)?.playerId ?: insertPlayer(player)
        }

        this.insertGameStatuses(
            playersId.mapIndexed { index, playerId ->
                GameStatus(
                    gameId,
                    playerId,
                    index,
                    index == 0,
                    pennyCount ?: Player.defaultPennyCount
                )
            }
        )

        return gameId
    }

    @Update
    abstract suspend fun updateGameStatuses(gameStatus: List<GameStatus>)

    @Transaction
    open suspend fun updateGameAndStatuses(
        game: Game,
        statuses: List<GameStatus>
    ) {
        this.updateGame(game)
        this.updateGameStatuses(statuses)
    }

    @Transaction
    @Query(
        """
            SELECT * FROM game_statuses gs
            WHERE gs.gameId IN (
                SELECT gameId FROM games
                WHERE gameState = :finishedGameState
            )
        """
    )
    abstract fun getCompletedGameStatusesWithPlayers(
        finishedGameState: GameState = GameState.Finished
    ): LiveData<List<GameStatusWithPlayer>>
}