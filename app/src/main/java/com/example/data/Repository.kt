package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

class Repository(private val db: AppDatabase) {

    // Player operations
    val allPlayers: Flow<List<Player>> = db.playerDao().getAllPlayers()

    suspend fun getPlayerById(id: String): Player? {
        return db.playerDao().getPlayerById(id)
    }

    suspend fun insertPlayer(player: Player) {
        db.playerDao().insertPlayer(player)
    }

    suspend fun updatePlayer(player: Player) {
        db.playerDao().updatePlayer(player)
    }

    suspend fun deletePlayer(player: Player) {
        db.playerDao().deletePlayer(player)
    }

    // TeamSheet operations
    val allTeamSheets: Flow<List<TeamSheet>> = db.teamSheetDao().getAllTeamSheets()

    suspend fun getTeamSheetById(id: String): TeamSheet? {
        return db.teamSheetDao().getTeamSheetById(id)
    }

    suspend fun insertTeamSheet(teamSheet: TeamSheet, playerIds: List<String>, captains: List<String>, keepers: List<String>) {
        db.teamSheetDao().insertTeamSheet(teamSheet)
        db.teamSheetDao().deletePlayersForTeamSheet(teamSheet.id)
        val joints = playerIds.map { pid ->
            TeamSheetPlayer(
                teamSheetId = teamSheet.id,
                playerId = pid,
                isCaptain = captains.contains(pid),
                isWicketKeeper = keepers.contains(pid)
            )
        }
        db.teamSheetDao().insertTeamSheetPlayers(joints)
    }

    fun getTeamSheetPlayers(teamSheetId: String): Flow<List<Player>> {
        return db.teamSheetDao().getTeamSheetPlayersFlow(teamSheetId).map { joints ->
            joints.mapNotNull { db.playerDao().getPlayerById(it.playerId) }
        }
    }

    suspend fun getTeamSheetPlayersList(teamSheetId: String): List<Player> {
        return db.teamSheetDao().getTeamSheetPlayers(teamSheetId).mapNotNull { db.playerDao().getPlayerById(it.playerId) }
    }

    suspend fun getTeamSheetJoinInfo(teamSheetId: String): List<TeamSheetPlayer> {
        return db.teamSheetDao().getTeamSheetPlayers(teamSheetId)
    }

    suspend fun deleteTeamSheet(teamSheet: TeamSheet) {
        db.teamSheetDao().deletePlayersForTeamSheet(teamSheet.id)
        db.teamSheetDao().deleteTeamSheet(teamSheet)
    }

    // Match operations
    val allMatches: Flow<List<Match>> = db.matchDao().getAllMatches()
    val activeMatchFlow: Flow<Match?> = db.matchDao().getActiveMatchFlow()

    suspend fun getMatchById(id: String): Match? {
        return db.matchDao().getMatchById(id)
    }

    suspend fun getActiveMatch(): Match? {
        return db.matchDao().getActiveMatch()
    }

    suspend fun createMatch(match: Match) {
        db.matchDao().insertMatch(match)
    }

    suspend fun updateMatch(match: Match) {
        db.matchDao().updateMatch(match)
    }

    // Innings
    fun getInningsForMatchFlow(matchId: String): Flow<List<Innings>> {
        return db.inningsDao().getInningsForMatchFlow(matchId)
    }

    suspend fun getInningsForMatch(matchId: String): List<Innings> {
        return db.inningsDao().getInningsForMatch(matchId)
    }

    suspend fun insertInnings(innings: Innings) {
        db.inningsDao().insertInnings(innings)
    }

    // Overs and Balls
    fun getOversForInningsFlow(inningsId: String): Flow<List<Over>> {
        return db.overDao().getOversForInningsFlow(inningsId)
    }

    suspend fun getOversForInnings(inningsId: String): List<Over> {
        return db.overDao().getOversForInnings(inningsId)
    }

    suspend fun insertOver(over: Over) {
        db.overDao().insertOver(over)
    }

    fun getBallsForOversFlow(overIds: List<String>): Flow<List<Ball>> {
        return db.ballDao().getBallsForOversFlow(overIds)
    }

    suspend fun getBallsForOver(overId: String): List<Ball> {
        return db.ballDao().getBallsForOver(overId)
    }

    suspend fun getBallsForOvers(overIds: List<String>): List<Ball> {
        return db.ballDao().getBallsForOvers(overIds)
    }

    suspend fun getDismissalsForBalls(ballIds: List<String>): List<Dismissal> {
        return db.dismissalDao().getDismissalsForBalls(ballIds)
    }

    fun getDismissalsForBallsFlow(ballIds: List<String>): Flow<List<Dismissal>> {
        return db.dismissalDao().getDismissalsForBallsFlow(ballIds)
    }

    // Record ball transactional
    suspend fun recordBall(ball: Ball, dismissal: Dismissal?) {
        db.ballDao().insertBall(ball)
        if (dismissal != null) {
            db.dismissalDao().insertDismissal(dismissal)
        }
    }

    // Undo last ball
    suspend fun undoLastBall(matchId: String): Boolean {
        // Find current status
        val match = db.matchDao().getMatchById(matchId) ?: return false
        val inningsList = db.inningsDao().getInningsForMatch(matchId)
        val inningsOpt = if (match.status == MatchStatus.INNINGS_2) {
            inningsList.find { it.inningsNumber == 2 }
        } else if (match.status == MatchStatus.INNINGS_1) {
            inningsList.find { it.inningsNumber == 1 }
        } else {
            null
        }
        val currentInnings = inningsOpt ?: return false

        val overs = db.overDao().getOversForInnings(currentInnings.id)
        if (overs.isEmpty()) return false

        val overIds = overs.map { it.id }
        val lastBall = db.ballDao().getLastBallForOvers(overIds) ?: return false

        // Mark it as undone
        val updatedBall = lastBall.copy(isUndone = true)
        db.ballDao().insertBall(updatedBall)

        // Delete associated dismissal if any
        db.dismissalDao().deleteDismissalForBall(lastBall.id)
        return true
    }

    // Redo is optional/soft, we can simply support standard Undone flag
    // For simplicity, we just filter out `isUndone = 1` for everything, which we do.

    // Calculate Awards & Complete Match
    suspend fun completeMatch(matchId: String, resultSummary: String, winnerTeamId: String?): List<Award> {
        val match = db.matchDao().getMatchById(matchId) ?: return emptyList()
        val updatedMatch = match.copy(
            status = MatchStatus.COMPLETED,
            resultSummary = resultSummary,
            winnerTeamId = winnerTeamId,
            completedAt = System.currentTimeMillis()
        )
        db.matchDao().updateMatch(updatedMatch)

        // Generate the awards!
        val awards = calculateAwardsForMatch(matchId)
        db.awardDao().insertAwards(awards)
        return awards
    }

    fun getAwardsForMatchFlow(matchId: String): Flow<List<Award>> {
        return db.awardDao().getAwardsForMatchFlow(matchId)
    }

    suspend fun getAwardsForMatch(matchId: String): List<Award> {
        return db.awardDao().getAwardsForMatch(matchId)
    }

    // Destructive reset
    suspend fun clearAllData() {
        db.playerDao().deleteAllPlayers()
        db.teamSheetDao().deleteAllTeamSheets()
        db.teamSheetDao().deleteAllTeamSheetPlayers()
        db.matchDao().deleteAllMatches()
        // Room tables will cascade or clean up if we clean these top-level tables
    }

    // Award calculations
    private suspend fun calculateAwardsForMatch(matchId: String): List<Award> {
        val match = db.matchDao().getMatchById(matchId) ?: return emptyList()
        val inningsList = db.inningsDao().getInningsForMatch(matchId)
        
        // Let's load ALL balls and dismissals for this match
        val allOvers = mutableListOf<Over>()
        for (inn in inningsList) {
            allOvers.addAll(db.overDao().getOversForInnings(inn.id))
        }
        val overIds = allOvers.map { it.id }
        if (overIds.isEmpty()) return emptyList()

        val balls = db.ballDao().getBallsForOvers(overIds).filter { !it.isUndone }
        val ballIds = balls.map { it.id }
        val dismissals = db.dismissalDao().getDismissalsForBalls(ballIds)

        // Group stats by player ID
        class PlayerStats(val id: String, val name: String) {
            var runsScored = 0
            var ballsFaced = 0
            var sixes = 0
            var fours = 0
            var wicketsTaken = 0
            var legalBallsBowled = 0
            var runsConceded = 0
            var catches = 0
            var stumpings = 0
            var dotsBowled = 0
            var matchesPlayed = 1 // in this match
        }

        // We need all player names
        val allMatchPlayerIds = (balls.map { it.strikerPlayerId } + 
                                 balls.map { it.nonStrikerPlayerId } + 
                                 allOvers.map { it.bowlerId } + 
                                 dismissals.map { it.dismissedPlayerId } + 
                                 dismissals.mapNotNull { it.fielderPlayerId }).distinct()
        
        val playerStatsMap = allMatchPlayerIds.associateWith { pid ->
            val name = db.playerDao().getPlayerById(pid)?.name ?: "Unknown Player"
            PlayerStats(pid, name)
        }

        // Batting stats
        for (ball in balls) {
            val strikerStats = playerStatsMap[ball.strikerPlayerId] ?: continue
            strikerStats.runsScored += ball.runsOffBat
            if (ball.extraType != ExtraType.WIDE) {
                strikerStats.ballsFaced += 1
            }
            if (ball.runsOffBat == 6) {
                strikerStats.sixes += 1
                strikerStats.fours += 1 // boundaries count both 4 and 6? Wait, usually boundaries is 4s+6s
            } else if (ball.runsOffBat == 4) {
                strikerStats.fours += 1
            }
        }

        // Bowling stats
        // To compute bowling stats properly, we link balls to their overs
        val overToBowlerMap = allOvers.associate { it.id to it.bowlerId }
        for (ball in balls) {
            val bowlerId = overToBowlerMap[ball.overId] ?: continue
            val bowlerStats = playerStatsMap[bowlerId] ?: continue
            
            if (ball.isLegalDelivery) {
                bowlerStats.legalBallsBowled += 1
                val totalRunsFromBall = ball.runsOffBat + (if (ball.extraType == ExtraType.BYE || ball.extraType == ExtraType.LEG_BYE) 0 else ball.extraRuns)
                if (totalRunsFromBall == 0 && !ball.isWicket) {
                    bowlerStats.dotsBowled += 1
                }
            }
            
            // Runs conceded: off bat + wide/nb penalties and runs run off wide/nb. Byes/Legbyes do not count against bowler.
            var runsForBowler = ball.runsOffBat
            if (ball.extraType == ExtraType.WIDE || ball.extraType == ExtraType.NO_BALL) {
                runsForBowler += ball.extraRuns
            }
            bowlerStats.runsConceded += runsForBowler
        }

        // Dismissals / Fielding stats
        for (dismissal in dismissals) {
            // Who is the bowler? Find the ball, find the over, find the bowler
            val ball = balls.find { it.id == dismissal.ballId } ?: continue
            val bowlerId = overToBowlerMap[ball.overId] ?: continue
            
            if (dismissal.dismissalType != DismissalType.RUN_OUT && dismissal.dismissalType != DismissalType.OBSTRUCTING) {
                // Bowler's wicket!
                playerStatsMap[bowlerId]?.wicketsTaken?.let { playerStatsMap[bowlerId]!!.wicketsTaken = it + 1 }
            }

            val fielderId = dismissal.fielderPlayerId
            if (fielderId != null) {
                val fielderStats = playerStatsMap[fielderId]
                if (fielderStats != null) {
                    if (dismissal.dismissalType == DismissalType.CAUGHT) {
                        fielderStats.catches += 1
                    } else if (dismissal.dismissalType == DismissalType.STUMPED) {
                        fielderStats.stumpings += 1
                    }
                }
            }
        }

        val awards = mutableListOf<Award>()

        // 1. POTM (Player of the Match)
        // impact = runsScored + (battingSR / 5) + (wicketsTaken * 20) - (economyRate * 2) + (catchesOrStumpings * 10)
        var bestPotmId: String? = null
        var bestPotmScore = -999.0
        var potmHeadline = ""

        for ((pid, stats) in playerStatsMap) {
            val sr = if (stats.ballsFaced > 0) (stats.runsScored.toDouble() / stats.ballsFaced) * 100.0 else 0.0
            val oversBowled = stats.legalBallsBowled / 6.0
            val econ = if (stats.legalBallsBowled > 0) (stats.runsConceded.toDouble() / oversBowled) else 0.0
            
            val potmScore = stats.runsScored + (sr / 5.0) + (stats.wicketsTaken * 20.0) - (econ * 2.0) + ((stats.catches + stats.stumpings) * 10.0)
            if (potmScore > bestPotmScore) {
                bestPotmScore = potmScore
                bestPotmId = pid
                
                val battingPart = "${stats.runsScored} (${stats.ballsFaced})"
                val bowlingPart = "${stats.wicketsTaken}/${stats.runsConceded}"
                potmHeadline = "$battingPart · $bowlingPart"
            }
        }

        if (bestPotmId != null) {
            awards.add(Award(
                matchId = matchId,
                playerId = bestPotmId,
                awardType = AwardType.POTM,
                headlineStat = potmHeadline
            ))
        }

        // 2. Super Hitter (Most sixes, ties broken by strike rate)
        val sortedForSixes = playerStatsMap.values.filter { it.sixes > 0 }.sortedWith(
            compareByDescending<PlayerStats> { it.sixes }.thenByDescending { if (it.ballsFaced > 0) (it.runsScored.toDouble() / it.ballsFaced) else 0.0 }
        )
        if (sortedForSixes.isNotEmpty()) {
            val winner = sortedForSixes.first()
            val sr = if (winner.ballsFaced > 0) ((winner.runsScored.toDouble() / winner.ballsFaced) * 100).toInt() else 0
            awards.add(Award(
                matchId = matchId,
                playerId = winner.id,
                awardType = AwardType.SUPER_HITTER,
                headlineStat = "${winner.sixes} sixes"
            ))
        }

        // 3. Wicket Taker (Most wickets, ties broken by economy)
        val sortedForWickets = playerStatsMap.values.filter { it.wicketsTaken > 0 }.sortedWith(
            compareByDescending<PlayerStats> { it.wicketsTaken }.thenBy { 
                val overs = it.legalBallsBowled / 6.0
                if (it.legalBallsBowled > 0) (it.runsConceded.toDouble() / overs) else 99.0
            }
        )
        if (sortedForWickets.isNotEmpty()) {
            val winner = sortedForWickets.first()
            awards.add(Award(
                matchId = matchId,
                playerId = winner.id,
                awardType = AwardType.WICKET_TAKER,
                headlineStat = "${winner.wicketsTaken}/${winner.runsConceded}"
            ))
        }

        // 4. Container of the Match (Best economy rate with >= 1 over bowled)
        val sortedForEcon = playerStatsMap.values.filter { it.legalBallsBowled >= 6 }.sortedBy { 
            val overs = it.legalBallsBowled / 6.0
            it.runsConceded.toDouble() / overs
        }
        if (sortedForEcon.isNotEmpty()) {
            val winner = sortedForEcon.first()
            val overs = winner.legalBallsBowled / 6
            val ballsRem = winner.legalBallsBowled % 6
            val ovStr = "$overs.${ballsRem}"
            val econ = String.format("%.2f", winner.runsConceded.toDouble() / (winner.legalBallsBowled / 6.0))
            awards.add(Award(
                matchId = matchId,
                playerId = winner.id,
                awardType = AwardType.CONTAINER,
                headlineStat = "Econ $econ"
            ))
        }

        // 5. Most Boundaries (Most 4s+6s)
        val sortedForBounds = playerStatsMap.values.filter { it.fours > 0 }.sortedByDescending { it.fours }
        if (sortedForBounds.isNotEmpty()) {
            val winner = sortedForBounds.first()
            awards.add(Award(
                matchId = matchId,
                playerId = winner.id,
                awardType = AwardType.MOST_BOUNDARIES,
                headlineStat = "${winner.fours} boundaries"
            ))
        }

        // 6. Most Dot Balls (Most dot balls bowled)
        val sortedForDots = playerStatsMap.values.filter { it.dotsBowled > 0 }.sortedByDescending { it.dotsBowled }
        if (sortedForDots.isNotEmpty()) {
            val winner = sortedForDots.first()
            awards.add(Award(
                matchId = matchId,
                playerId = winner.id,
                awardType = AwardType.MOST_DOT_BALLS,
                headlineStat = "${winner.dotsBowled} dots"
            ))
        }

        return awards
    }

    suspend fun getPlayerCareerStats(playerId: String): PlayerCareerStats {
        val completedMatches = db.matchDao().getAllMatches().first().filter { it.status == MatchStatus.COMPLETED }
        
        var matchesPlayed = 0
        var totalRunsScored = 0
        var totalBallsFaced = 0
        var totalWicketsTaken = 0
        var totalLegalBallsBowled = 0
        var totalRunsConceded = 0
        val performances = mutableListOf<String>()

        for (match in completedMatches) {
            val teamAPlayers = db.teamSheetDao().getTeamSheetPlayers(match.teamAId).map { it.playerId }
            val teamBPlayers = db.teamSheetDao().getTeamSheetPlayers(match.teamBId).map { it.playerId }
            
            if (!teamAPlayers.contains(playerId) && !teamBPlayers.contains(playerId)) {
                continue
            }
            
            matchesPlayed++

            val inningsList = db.inningsDao().getInningsForMatch(match.id)
            val inningsIds = inningsList.map { it.id }
            
            val oversList = mutableListOf<Over>()
            for (innId in inningsIds) {
                oversList.addAll(db.overDao().getOversForInnings(innId))
            }
            val overIds = oversList.map { it.id }
            
            if (overIds.isEmpty()) continue

            val balls = db.ballDao().getBallsForOvers(overIds).filter { !it.isUndone }
            val ballIds = balls.map { it.id }
            val dismissals = if (ballIds.isNotEmpty()) db.dismissalDao().getDismissalsForBalls(ballIds) else emptyList()

            var matchRuns = 0
            var matchBalls = 0
            var matchWickets = 0
            var matchRunsConceded = 0
            var matchLegalBallsBowled = 0

            val playerBattingBalls = balls.filter { it.strikerPlayerId == playerId }
            matchRuns = playerBattingBalls.sumOf { it.runsOffBat }
            matchBalls = playerBattingBalls.count { it.extraType != ExtraType.WIDE }

            totalRunsScored += matchRuns
            totalBallsFaced += matchBalls

            val playerOvers = oversList.filter { it.bowlerId == playerId }
            val playerOverIds = playerOvers.map { it.id }
            if (playerOverIds.isNotEmpty()) {
                val playerBowlingBalls = balls.filter { it.overId in playerOverIds }
                matchLegalBallsBowled = playerBowlingBalls.count { it.isLegalDelivery }
                matchRunsConceded = playerBowlingBalls.sumOf { b ->
                    var r = b.runsOffBat
                    if (b.extraType == ExtraType.WIDE || b.extraType == ExtraType.NO_BALL) {
                        r += b.extraRuns
                    }
                    r
                }
                
                val bowlerBallIds = playerBowlingBalls.map { it.id }
                matchWickets = dismissals.count { d ->
                    d.ballId in bowlerBallIds && d.dismissalType != DismissalType.RUN_OUT && d.dismissalType != DismissalType.OBSTRUCTING
                }

                totalLegalBallsBowled += matchLegalBallsBowled
                totalRunsConceded += matchRunsConceded
                totalWicketsTaken += matchWickets
            }

            val wasDismissed = dismissals.any { it.dismissedPlayerId == playerId }

            val opponentTeamName = if (teamAPlayers.contains(playerId)) {
                db.teamSheetDao().getTeamSheetById(match.teamBId)?.name ?: "Opponent"
            } else {
                db.teamSheetDao().getTeamSheetById(match.teamAId)?.name ?: "Opponent"
            }

            val batSummary = if (matchBalls > 0) {
                "$matchRuns($matchBalls)${if (!wasDismissed) "*" else ""}"
            } else null

            val bowlSummary = if (matchLegalBallsBowled > 0) {
                val oversBowled = "${matchLegalBallsBowled / 6}.${matchLegalBallsBowled % 6}"
                "$matchWickets/$matchRunsConceded ($oversBowled ov)"
            } else null

            val perfParts = listOfNotNull(batSummary, bowlSummary)
            if (perfParts.isNotEmpty()) {
                performances.add("${perfParts.joinToString(" · ")} vs $opponentTeamName")
            }
        }

        val average = if (matchesPlayed > 0) {
            totalRunsScored.toDouble() / matchesPlayed
        } else 0.0

        val economyRate = if (totalLegalBallsBowled > 0) {
            val totalOversBowled = totalLegalBallsBowled / 6.0
            totalRunsConceded.toDouble() / totalOversBowled
        } else 0.0

        val strikeRate = if (totalBallsFaced > 0) {
            (totalRunsScored.toDouble() / totalBallsFaced) * 100.0
        } else 0.0

        return PlayerCareerStats(
            matchesPlayed = matchesPlayed,
            runsScored = totalRunsScored,
            ballsFaced = totalBallsFaced,
            average = average,
            wicketsTaken = totalWicketsTaken,
            economyRate = economyRate,
            strikeRate = strikeRate,
            recentPerformances = performances.take(5)
        )
    }

    suspend fun getSquadLeaderboard(): List<LeaderboardEntry> {
        val players = db.playerDao().getAllPlayers().first()
        val entries = mutableListOf<LeaderboardEntry>()
        for (player in players) {
            val stats = getPlayerCareerStats(player.id)
            entries.add(LeaderboardEntry(player, stats.runsScored, stats.wicketsTaken))
        }
        return entries.sortedByDescending { it.runs }
    }
}
