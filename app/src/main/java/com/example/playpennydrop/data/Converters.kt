package com.example.playpennydrop.data

import android.text.TextUtils
import androidx.room.TypeConverter
import com.example.playpennydrop.game.AI
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun toOffsetDateTime(value: String?) = value?.let {
        formatter.parse(it, OffsetDateTime::from)
    }

    @TypeConverter
    fun fromOffsetDateTime(date: OffsetDateTime?) = date?.format(formatter)

    @TypeConverter
    fun fromGameStateToInt(gameState: GameState?) = (gameState ?: GameState.Unknown).ordinal

    @TypeConverter
    fun fromIntToGameState(gameState: Int?) = GameState.values().let { values ->
        if (gameState != null && values.any { it.ordinal == gameState }) {
            GameState.values()[gameState]
        } else {
            GameState.Unknown
        }
    }

    @TypeConverter
    fun toIntList(value: String?) = value?.split(",")?.let {
        it
            .filter { numberString -> !TextUtils.isEmpty(numberString) }
            .map { numberString -> numberString.toInt() }
    } ?: emptyList()

    @TypeConverter
    fun fromListOfIntToString(numbers: List<Int>?) = numbers?.joinToString(",") ?: ""

    @TypeConverter
    fun toAI(aiId: Long?) = AI.basicAI.firstOrNull { it.aiId == aiId }

    @TypeConverter
    fun fromAiToId(ai: AI?) = ai?.aiId
}