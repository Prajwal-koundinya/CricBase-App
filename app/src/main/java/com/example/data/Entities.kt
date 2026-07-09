package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class BattingHand {
    RIGHT, LEFT
}

enum class BowlingStyle {
    RA_FAST, RA_MEDIUM, RA_OFFSPIN, LA_FAST, LA_MEDIUM, LA_SPIN, LEGSPIN;

    fun displayName(): String {
        return when (this) {
            RA_FAST -> "Right-arm fast"
            RA_MEDIUM -> "Right-arm medium"
            RA_OFFSPIN -> "Right-arm off-spin"
            LA_FAST -> "Left-arm fast"
            LA_MEDIUM -> "Left-arm medium"
            LA_SPIN -> "Left-arm spin"
            LEGSPIN -> "Leg-spin"
        }
    }
}

enum class ThemeColor(val hex: String) {
    BLUE_OCEAN("#1E6FD9"),
    EMERALD("#1C8A4B"),
    SUNSET("#E0632E"),
    ROYAL_PURPLE("#7A3FC4"),
    CRIMSON("#A82020"),
    AMBER("#C9871E"),
    SLATE("#4A5568"),
    MIDNIGHT("#1B2A4A");

    fun displayName(): String {
        return name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
    }
}

enum class TossDecision {
    BAT, BOWL
}

enum class MatchStatus {
    SETUP_IN_PROGRESS, INNINGS_1, INNINGS_2, COMPLETED
}

enum class ArenaType {
    GROUND, TURF, BOX_NETS, STREET;

    fun displayName(): String {
        return when (this) {
            GROUND -> "Ground"
            TURF -> "Turf"
            BOX_NETS -> "Box/Nets"
            STREET -> "Street"
        }
    }
}

enum class BallType {
    TENNIS, LEATHER, RUBBER, WIND;

    fun displayName(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

enum class ExtraType {
    NONE, WIDE, NO_BALL, BYE, LEG_BYE
}

enum class DismissalType {
    BOWLED, CAUGHT, LBW, RUN_OUT, STUMPED, HIT_WICKET, OBSTRUCTING, HIT_TWICE;

    fun displayName(): String {
        return name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
    }
}

enum class DismissedEnd {
    STRIKER, NON_STRIKER
}

enum class AwardType {
    POTM, SUPER_HITTER, WICKET_TAKER, CONTAINER, MOST_BOUNDARIES, ECONOMY_KING,
    GAME_CHANGER, BEST_PARTNERSHIP, HIGHEST_SR, MOST_DOT_BALLS, BEST_FIELDER;

    fun displayName(): String {
        return when (this) {
            POTM -> "Player of the Match"
            SUPER_HITTER -> "Super Hitter"
            WICKET_TAKER -> "Wicket Taker"
            CONTAINER -> "Container of the Match"
            MOST_BOUNDARIES -> "Most Boundaries"
            ECONOMY_KING -> "Economy King"
            GAME_CHANGER -> "Game Changer"
            BEST_PARTNERSHIP -> "Best Partnership"
            HIGHEST_SR -> "Highest Strike Rate"
            MOST_DOT_BALLS -> "Most Dot Balls"
            BEST_FIELDER -> "Best Fielder"
        }
    }
}

@Entity(tableName = "players")
data class Player(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val alias: String? = null,
    val battingHand: BattingHand? = null,
    val bowlingStyle: BowlingStyle? = null,
    val themeColor: ThemeColor,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getInitials(): String {
        val useName = if (!alias.isNullOrBlank()) alias else name
        val trimmed = useName.trim()
        if (trimmed.isEmpty()) return "XX"
        val words = trimmed.split(Regex("\\s+"))
        return if (words.size >= 2) {
            val first = words.first().firstOrNull() ?: ' '
            val last = words.last().firstOrNull() ?: ' '
            "$first$last".uppercase()
        } else {
            val single = words.first()
            if (single.length >= 2) {
                single.substring(0, 2).uppercase()
            } else if (single.length == 1) {
                "${single.first()}${single.first()}".uppercase()
            } else {
                "XX"
            }
        }
    }
}

@Entity(tableName = "team_sheets")
data class TeamSheet(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val themeColor: ThemeColor,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "team_sheet_players",
    primaryKeys = ["teamSheetId", "playerId"]
)
data class TeamSheetPlayer(
    val teamSheetId: String,
    val playerId: String,
    val isCaptain: Boolean = false,
    val isWicketKeeper: Boolean = false
)

@Entity(tableName = "matches")
data class Match(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val teamAId: String,
    val teamBId: String,
    val tossWinnerTeamId: String,
    val tossDecision: TossDecision,
    val totalOvers: Int,
    val maxOversPerBowler: Int,
    val gullyRulesEnabled: Boolean,
    val lastManStanding: Boolean,
    val commonPlayerId: String? = null,
    val arena: ArenaType,
    val ballType: BallType,
    val venue: String? = null,
    val city: String? = null,
    val status: MatchStatus = MatchStatus.SETUP_IN_PROGRESS,
    val resultSummary: String? = null,
    val winnerTeamId: String? = null,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

@Entity(tableName = "innings")
data class Innings(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val matchId: String,
    val inningsNumber: Int, // 1 or 2
    val battingTeamId: String,
    val bowlingTeamId: String,
    val targetRuns: Int? = null
)

@Entity(tableName = "overs")
data class Over(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val inningsId: String,
    val overNumber: Int,
    val bowlerId: String
)

@Entity(tableName = "balls")
data class Ball(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val overId: String,
    val sequenceInOver: Int, // includes extra deliveries
    val strikerPlayerId: String,
    val nonStrikerPlayerId: String,
    val runsOffBat: Int,
    val extraType: ExtraType? = null,
    val extraRuns: Int,
    val isLegalDelivery: Boolean, // false for WIDE or NO_BALL
    val isFreeHit: Boolean = false,
    val isWicket: Boolean = false,
    val isUndone: Boolean = false,
    val recordedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "dismissals")
data class Dismissal(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val ballId: String,
    val dismissedPlayerId: String,
    val dismissalType: DismissalType,
    val fielderPlayerId: String? = null, // caught, run out, stumped
    val dismissedEnd: DismissedEnd? = null // for run out
)

@Entity(tableName = "awards")
data class Award(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val matchId: String,
    val playerId: String,
    val awardType: AwardType,
    val headlineStat: String
)

data class PlayerCareerStats(
    val matchesPlayed: Int,
    val runsScored: Int,
    val ballsFaced: Int,
    val average: Double,
    val wicketsTaken: Int,
    val economyRate: Double,
    val strikeRate: Double,
    val recentPerformances: List<String>
)

data class LeaderboardEntry(
    val player: Player,
    val runs: Int,
    val wickets: Int
)

data class CompletedOverInfo(
    val overNumber: Int,
    val bowlerName: String,
    val balls: List<Ball>,
    val runsConceded: Int,
    val wickets: Int
)
