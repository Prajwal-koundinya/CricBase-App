package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY createdAt DESC")
    fun getAllPlayers(): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getPlayerById(id: String): Player?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player)

    @Update
    suspend fun updatePlayer(player: Player)

    @Delete
    suspend fun deletePlayer(player: Player)

    @Query("DELETE FROM players")
    suspend fun deleteAllPlayers()
}

@Dao
interface TeamSheetDao {
    @Query("SELECT * FROM team_sheets ORDER BY createdAt DESC")
    fun getAllTeamSheets(): Flow<List<TeamSheet>>

    @Query("SELECT * FROM team_sheets WHERE id = :id")
    suspend fun getTeamSheetById(id: String): TeamSheet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamSheet(teamSheet: TeamSheet)

    @Update
    suspend fun updateTeamSheet(teamSheet: TeamSheet)

    @Delete
    suspend fun deleteTeamSheet(teamSheet: TeamSheet)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamSheetPlayers(players: List<TeamSheetPlayer>)

    @Query("DELETE FROM team_sheet_players WHERE teamSheetId = :teamSheetId")
    suspend fun deletePlayersForTeamSheet(teamSheetId: String)

    @Query("SELECT * FROM team_sheet_players WHERE teamSheetId = :teamSheetId")
    fun getTeamSheetPlayersFlow(teamSheetId: String): Flow<List<TeamSheetPlayer>>

    @Query("SELECT * FROM team_sheet_players WHERE teamSheetId = :teamSheetId")
    suspend fun getTeamSheetPlayers(teamSheetId: String): List<TeamSheetPlayer>

    @Query("DELETE FROM team_sheets")
    suspend fun deleteAllTeamSheets()

    @Query("DELETE FROM team_sheet_players")
    suspend fun deleteAllTeamSheetPlayers()
}

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches ORDER BY startedAt DESC")
    fun getAllMatches(): Flow<List<Match>>

    @Query("SELECT * FROM matches WHERE id = :id")
    suspend fun getMatchById(id: String): Match?

    @Query("SELECT * FROM matches WHERE status != 'COMPLETED' LIMIT 1")
    fun getActiveMatchFlow(): Flow<Match?>

    @Query("SELECT * FROM matches WHERE status != 'COMPLETED' LIMIT 1")
    suspend fun getActiveMatch(): Match?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: Match)

    @Update
    suspend fun updateMatch(match: Match)

    @Delete
    suspend fun deleteMatch(match: Match)

    @Query("DELETE FROM matches")
    suspend fun deleteAllMatches()
}

@Dao
interface InningsDao {
    @Query("SELECT * FROM innings WHERE matchId = :matchId")
    fun getInningsForMatchFlow(matchId: String): Flow<List<Innings>>

    @Query("SELECT * FROM innings WHERE matchId = :matchId")
    suspend fun getInningsForMatch(matchId: String): List<Innings>

    @Query("SELECT * FROM innings WHERE id = :id")
    suspend fun getInningsById(id: String): Innings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInnings(innings: Innings)

    @Update
    suspend fun updateInnings(innings: Innings)
}

@Dao
interface OverDao {
    @Query("SELECT * FROM overs WHERE inningsId = :inningsId ORDER BY overNumber ASC")
    fun getOversForInningsFlow(inningsId: String): Flow<List<Over>>

    @Query("SELECT * FROM overs WHERE inningsId = :inningsId ORDER BY overNumber ASC")
    suspend fun getOversForInnings(inningsId: String): List<Over>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOver(over: Over)
}

@Dao
interface BallDao {
    @Query("SELECT * FROM balls WHERE overId = :overId AND isUndone = 0 ORDER BY sequenceInOver ASC")
    fun getBallsForOverFlow(overId: String): Flow<List<Ball>>

    @Query("SELECT * FROM balls WHERE overId = :overId AND isUndone = 0 ORDER BY sequenceInOver ASC")
    suspend fun getBallsForOver(overId: String): List<Ball>

    @Query("SELECT * FROM balls WHERE overId IN (:overIds) AND isUndone = 0 ORDER BY recordedAt ASC")
    fun getBallsForOversFlow(overIds: List<String>): Flow<List<Ball>>

    @Query("SELECT * FROM balls WHERE overId IN (:overIds) AND isUndone = 0 ORDER BY recordedAt ASC")
    suspend fun getBallsForOvers(overIds: List<String>): List<Ball>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBall(ball: Ball)

    @Update
    suspend fun updateBall(ball: Ball)

    @Query("SELECT * FROM balls WHERE overId IN (:overIds) ORDER BY recordedAt DESC LIMIT 1")
    suspend fun getLastBallForOvers(overIds: List<String>): Ball?
}

@Dao
interface DismissalDao {
    @Query("SELECT * FROM dismissals WHERE ballId = :ballId")
    suspend fun getDismissalForBall(ballId: String): Dismissal?

    @Query("SELECT * FROM dismissals WHERE ballId IN (:ballIds)")
    suspend fun getDismissalsForBalls(ballIds: List<String>): List<Dismissal>

    @Query("SELECT * FROM dismissals WHERE ballId IN (:ballIds)")
    fun getDismissalsForBallsFlow(ballIds: List<String>): Flow<List<Dismissal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDismissal(dismissal: Dismissal)

    @Query("DELETE FROM dismissals WHERE ballId = :ballId")
    suspend fun deleteDismissalForBall(ballId: String)
}

@Dao
interface AwardDao {
    @Query("SELECT * FROM awards WHERE matchId = :matchId")
    fun getAwardsForMatchFlow(matchId: String): Flow<List<Award>>

    @Query("SELECT * FROM awards WHERE matchId = :matchId")
    suspend fun getAwardsForMatch(matchId: String): List<Award>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAwards(awards: List<Award>)
}
