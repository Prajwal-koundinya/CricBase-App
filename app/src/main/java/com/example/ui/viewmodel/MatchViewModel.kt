package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.GullyCrixApplication
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class BatterScorecard(
    val playerId: String,
    val name: String,
    val runs: Int,
    val balls: Int,
    val fours: Int,
    val sixes: Int,
    val strikeRate: Double,
    val dismissalText: String,
    val isOut: Boolean,
    val isNotOutAtCrease: Boolean = false
)

data class BowlerScorecard(
    val playerId: String,
    val name: String,
    val overs: Double, // e.g. 2.1
    val runs: Int,
    val wickets: Int,
    val economy: Double
)

data class PartnershipInfo(
    val batterAName: String,
    val batterBName: String,
    val batterARuns: Int,
    val batterBRuns: Int,
    val totalRuns: Int,
    val totalBalls: Int
)

data class LiveMatchState(
    val match: Match,
    val currentInnings: Innings?,
    val teamAName: String,
    val teamBName: String,
    val battingTeamName: String,
    val bowlingTeamName: String,
    val battingColor: ThemeColor,
    val bowlingColor: ThemeColor,
    // Live stats
    val score: Int = 0,
    val wickets: Int = 0,
    val legalBalls: Int = 0,
    val oversStr: String = "0.0",
    val crr: Double = 0.0,
    val rrr: Double? = null,
    val projectedScore: Int? = null,
    val runsNeeded: Int? = null,
    val ballsRemaining: Int = 0,
    // Batter figures
    val strikerId: String? = null,
    val strikerName: String = "",
    val strikerRuns: Int = 0,
    val strikerBalls: Int = 0,
    val strikerFours: Int = 0,
    val strikerSixes: Int = 0,
    val nonStrikerId: String? = null,
    val nonStrikerName: String = "",
    val nonStrikerRuns: Int = 0,
    val nonStrikerBalls: Int = 0,
    val nonStrikerFours: Int = 0,
    val nonStrikerSixes: Int = 0,
    val currentPartnershipRuns: Int = 0,
    val currentPartnershipBalls: Int = 0,
    // Bowler figures
    val bowlerId: String? = null,
    val bowlerName: String = "",
    val bowlerOvers: String = "0.0",
    val bowlerRuns: Int = 0,
    val bowlerWickets: Int = 0,
    // Over history
    val currentOverNumber: Int = 1,
    val currentOverBalls: List<Ball> = emptyList(),
    val overRuns: Int = 0,
    // Scorecard tables
    val firstInningsScorecard: List<BatterScorecard> = emptyList(),
    val firstInningsBowling: List<BowlerScorecard> = emptyList(),
    val secondInningsScorecard: List<BatterScorecard> = emptyList(),
    val secondInningsBowling: List<BowlerScorecard> = emptyList(),
    val partnerships: List<PartnershipInfo> = emptyList(),
    val completedOversList: List<CompletedOverInfo> = emptyList()
)

class MatchViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as GullyCrixApplication
    private val repository = app.repository

    // Current setup state
    private val _matchSetupId = MutableStateFlow<String?>(null)
    val matchSetupId: StateFlow<String?> = _matchSetupId.asStateFlow()

    // Coin Toss flow states
    val tossWinner = MutableStateFlow<String?>(null) // TeamSheet ID
    val tossDecision = MutableStateFlow<TossDecision?>(null)
    val tossCompleted = MutableStateFlow(false)

    // Match setup state
    val oversInput = MutableStateFlow(4)
    val maxOversPerBowlerInput = MutableStateFlow(2)
    val gullyRulesEnabled = MutableStateFlow(true)
    val lastManStanding = MutableStateFlow(true)
    val commonPlayerId = MutableStateFlow<String?>(null)
    val arenaInput = MutableStateFlow(ArenaType.GROUND)
    val ballTypeInput = MutableStateFlow(BallType.TENNIS)
    val venueInput = MutableStateFlow("")
    val cityInput = MutableStateFlow("")

    // Live state machine
    private val _liveState = MutableStateFlow<LiveMatchState?>(null)
    val liveState: StateFlow<LiveMatchState?> = _liveState.asStateFlow()

    // Openers selection temp state
    val selectedStrikerId = MutableStateFlow<String?>(null)
    val selectedNonStrikerId = MutableStateFlow<String?>(null)
    val selectedOpeningBowlerId = MutableStateFlow<String?>(null)

    // Wicket dialog step states
    val wicketDismissalType = MutableStateFlow<DismissalType?>(null)
    val wicketFielderId = MutableStateFlow<String?>(null)
    val wicketDismissedEnd = MutableStateFlow<DismissedEnd>(DismissedEnd.STRIKER)
    val nextBatterId = MutableStateFlow<String?>(null)

    // Selected run for confirmation
    val selectedRuns = MutableStateFlow<Int?>(null)
    val selectedExtraType = MutableStateFlow<ExtraType?>(null)
    val selectedExtraRuns = MutableStateFlow<Int?>(null)

    // Live alerts/modals
    val showEndOverOverlay = MutableStateFlow(false)
    val showBowlerPicker = MutableStateFlow(false)
    val showTargetDialog = MutableStateFlow(false)

    // Initialize/Load a Match
    fun loadMatch(matchId: String) {
        viewModelScope.launch {
            _matchSetupId.value = matchId
            refreshLiveState(matchId)
        }
    }

    fun setOvers(o: Int) {
        oversInput.value = o
        maxOversPerBowlerInput.value = maxOf(1, Math.ceil(o.toDouble() / 4.0).toInt())
    }

    // Start Toss Setup
    fun setupToss(teamAId: String, teamBId: String) {
        viewModelScope.launch {
            tossCompleted.value = false
            tossWinner.value = null
            tossDecision.value = null
        }
    }

    // Perform Coin Toss
    fun performToss(callerTeamId: String, callIsHeads: Boolean) {
        viewModelScope.launch {
            val randomHeads = (0..1).random() == 0
            val isCallCorrect = (callIsHeads && randomHeads) || (!callIsHeads && !randomHeads)
            val winner = if (isCallCorrect) callerTeamId else {
                val match = repository.getActiveMatch()
                // If we don't have active match, we'll assign opposite
                "opposite"
            }
            tossWinner.value = winner
            tossCompleted.value = true
        }
    }

    // Complete Match Setup and Start
    fun startMatch(teamAId: String, teamBId: String, tossWinnerId: String, decision: TossDecision) {
        viewModelScope.launch {
            val dateStr = java.text.SimpleDateFormat("MMMM d", java.util.Locale.getDefault()).format(java.util.Date())
            val existingMatches = repository.allMatches.first()
            val matchNumber = existingMatches.size + 1
            val matchName = "${ballTypeInput.value.displayName()} Ball match at ${arenaInput.value.displayName()} - Match $matchNumber, $dateStr"

            val newMatch = Match(
                name = matchName,
                teamAId = teamAId,
                teamBId = teamBId,
                tossWinnerTeamId = tossWinnerId,
                tossDecision = decision,
                totalOvers = oversInput.value,
                maxOversPerBowler = maxOversPerBowlerInput.value,
                gullyRulesEnabled = gullyRulesEnabled.value,
                lastManStanding = lastManStanding.value,
                commonPlayerId = commonPlayerId.value,
                arena = arenaInput.value,
                ballType = ballTypeInput.value,
                venue = venueInput.value.ifBlank { "Local Ground" },
                city = cityInput.value.ifBlank { "Local City" },
                status = MatchStatus.SETUP_IN_PROGRESS
            )
            repository.createMatch(newMatch)
            _matchSetupId.value = newMatch.id

            // Create Innings 1
            val teamA = repository.getTeamSheetById(teamAId)
            val teamB = repository.getTeamSheetById(teamBId)
            val isTeamABatting = (tossWinnerId == teamAId && decision == TossDecision.BAT) ||
                                 (tossWinnerId == teamBId && decision == TossDecision.BOWL)
            
            val batTeamId = if (isTeamABatting) teamAId else teamBId
            val bowlTeamId = if (isTeamABatting) teamBId else teamAId

            val innings1 = Innings(
                matchId = newMatch.id,
                inningsNumber = 1,
                battingTeamId = batTeamId,
                bowlingTeamId = bowlTeamId
            )
            repository.insertInnings(innings1)
            
            // Advance match status to INNINGS_1
            repository.updateMatch(newMatch.copy(status = MatchStatus.INNINGS_1))
            refreshLiveState(newMatch.id)
        }
    }

    // Set Openers
    fun confirmOpeners(strikerId: String, nonStrikerId: String, bowlerId: String) {
        val currentMatchId = _matchSetupId.value ?: return
        viewModelScope.launch {
            val state = _liveState.value ?: return@launch
            val innings = state.currentInnings ?: return@launch
            
            // Create first over
            val firstOver = Over(
                inningsId = innings.id,
                overNumber = 1,
                bowlerId = bowlerId
            )
            repository.insertOver(firstOver)
            
            // Initialize strike positions
            selectedStrikerId.value = strikerId
            selectedNonStrikerId.value = nonStrikerId
            selectedOpeningBowlerId.value = bowlerId

            refreshLiveState(currentMatchId)
        }
    }

    // Select Ball Outcome
    fun selectOutcome(runs: Int?, extraType: ExtraType? = null) {
        selectedRuns.value = runs
        selectedExtraType.value = extraType
        selectedExtraRuns.value = if (extraType != null) 1 else null
    }

    // Confirm Ball Delivery
    fun confirmBallDelivery() {
        val currentMatchId = _matchSetupId.value ?: return
        viewModelScope.launch {
            val state = _liveState.value ?: return@launch
            val innings = state.currentInnings ?: return@launch
            val overs = repository.getOversForInnings(innings.id)
            val currentOver = overs.lastOrNull() ?: return@launch

            val ballsInOver = repository.getBallsForOver(currentOver.id)
            val legalBallsInOver = ballsInOver.count { it.isLegalDelivery }
            if (legalBallsInOver >= 6) {
                showBowlerPicker.value = true
                showEndOverOverlay.value = true
                return@launch
            }

            val isLegal = selectedExtraType.value != ExtraType.WIDE && selectedExtraType.value != ExtraType.NO_BALL
            val extraRunsVal = selectedExtraRuns.value ?: 0
            val runsOffBatVal = selectedRuns.value ?: 0

            // Determine if Free Hit
            val lastBall = ballsInOver.lastOrNull()
            val wasFreeHit = lastBall?.extraType == ExtraType.NO_BALL || lastBall?.isFreeHit == true

            // Striker & Non-Striker
            val activeStriker = state.strikerId ?: return@launch
            val activeNonStriker = state.nonStrikerId ?: return@launch

            val newBall = Ball(
                overId = currentOver.id,
                sequenceInOver = ballsInOver.size + 1,
                strikerPlayerId = activeStriker,
                nonStrikerPlayerId = activeNonStriker,
                runsOffBat = runsOffBatVal,
                extraType = selectedExtraType.value,
                extraRuns = extraRunsVal,
                isLegalDelivery = isLegal,
                isFreeHit = wasFreeHit,
                isWicket = false
            )

            repository.recordBall(newBall, null)

            // Reset selection
            selectedRuns.value = null
            selectedExtraType.value = null
            selectedExtraRuns.value = null

            // Check if Innings ended (Runs reached for 2nd innings)
            checkMatchTriggerAndRefresh(currentMatchId)
        }
    }

    // Record Wicket Sub-Flow
    fun confirmWicket() {
        val currentMatchId = _matchSetupId.value ?: return
        viewModelScope.launch {
            val state = _liveState.value ?: return@launch
            val innings = state.currentInnings ?: return@launch
            val overs = repository.getOversForInnings(innings.id)
            val currentOver = overs.lastOrNull() ?: return@launch
            val ballsInOver = repository.getBallsForOver(currentOver.id)

            val legalBallsInOver = ballsInOver.count { it.isLegalDelivery }
            if (legalBallsInOver >= 6) {
                showBowlerPicker.value = true
                showEndOverOverlay.value = true
                return@launch
            }

            val activeStriker = state.strikerId ?: return@launch
            val activeNonStriker = state.nonStrikerId ?: return@launch

            val isLegal = selectedExtraType.value != ExtraType.WIDE && selectedExtraType.value != ExtraType.NO_BALL
            val isStrikerOut = wicketDismissedEnd.value == DismissedEnd.STRIKER
            val dismissedPlayerId = if (isStrikerOut) activeStriker else activeNonStriker

            val newBall = Ball(
                id = UUID.randomUUID().toString(),
                overId = currentOver.id,
                sequenceInOver = ballsInOver.size + 1,
                strikerPlayerId = activeStriker,
                nonStrikerPlayerId = activeNonStriker,
                runsOffBat = selectedRuns.value ?: 0,
                extraType = selectedExtraType.value,
                extraRuns = selectedExtraRuns.value ?: 0,
                isLegalDelivery = isLegal,
                isWicket = true
            )

            val dismissal = Dismissal(
                ballId = newBall.id,
                dismissedPlayerId = dismissedPlayerId,
                dismissalType = wicketDismissalType.value ?: DismissalType.BOWLED,
                fielderPlayerId = wicketFielderId.value,
                dismissedEnd = wicketDismissedEnd.value
            )

            repository.recordBall(newBall, dismissal)

            // Reset sub-flow
            wicketDismissalType.value = null
            wicketFielderId.value = null
            selectedRuns.value = null
            selectedExtraType.value = null
            selectedExtraRuns.value = null

            // If we have more players, install next batter
            val nextBatter = nextBatterId.value
            if (nextBatter != null) {
                if (isStrikerOut) {
                    selectedStrikerId.value = nextBatter
                } else {
                    selectedNonStrikerId.value = nextBatter
                }
                nextBatterId.value = null
            }

            checkMatchTriggerAndRefresh(currentMatchId)
        }
    }

    // Undo Ball
    fun undoLastBall() {
        val currentMatchId = _matchSetupId.value ?: return
        viewModelScope.launch {
            val success = repository.undoLastBall(currentMatchId)
            if (success) {
                // Re-fetch striker positions from the last active ball in db to reconstruct correct strike positions
                reconstructStrikePositions(currentMatchId)
                refreshLiveState(currentMatchId)
            }
        }
    }

    // Select Bowler for next over
    fun startNextOver(bowlerId: String) {
        val currentMatchId = _matchSetupId.value ?: return
        viewModelScope.launch {
            val state = _liveState.value ?: return@launch
            val innings = state.currentInnings ?: return@launch
            val overs = repository.getOversForInnings(innings.id)

            val nextOver = Over(
                inningsId = innings.id,
                overNumber = overs.size + 1,
                bowlerId = bowlerId
            )
            repository.insertOver(nextOver)

            // Rotate strike automatically on end of over
            val oldStriker = state.strikerId
            val oldNonStriker = state.nonStrikerId
            selectedStrikerId.value = oldNonStriker
            selectedNonStrikerId.value = oldStriker

            showBowlerPicker.value = false
            refreshLiveState(currentMatchId)
        }
    }

    // Change current over bowler manually
    fun changeCurrentBowler(newBowlerId: String) {
        val currentMatchId = _matchSetupId.value ?: return
        viewModelScope.launch {
            val state = _liveState.value ?: return@launch
            val innings = state.currentInnings ?: return@launch
            val overs = repository.getOversForInnings(innings.id)
            val currentOver = overs.lastOrNull() ?: return@launch

            val updatedOver = currentOver.copy(bowlerId = newBowlerId)
            repository.insertOver(updatedOver)

            refreshLiveState(currentMatchId)
        }
    }

    // Manual swap strike override
    fun manualSwapStrike() {
        val state = _liveState.value ?: return
        val temp = selectedStrikerId.value
        selectedStrikerId.value = selectedNonStrikerId.value
        selectedNonStrikerId.value = temp
        viewModelScope.launch {
            _matchSetupId.value?.let { refreshLiveState(it) }
        }
    }

    // Transition to Innings 2
    fun startInnings2(strikerId: String, nonStrikerId: String, bowlerId: String) {
        val currentMatchId = _matchSetupId.value ?: return
        viewModelScope.launch {
            val match = repository.getMatchById(currentMatchId) ?: return@launch
            val inningsList = repository.getInningsForMatch(currentMatchId)
            val innings1 = inningsList.find { it.inningsNumber == 1 } ?: return@launch

            // Calculate total runs scored in Innings 1
            val overs1 = repository.getOversForInnings(innings1.id)
            val overIds1 = overs1.map { it.id }
            val balls1 = repository.getBallsForOvers(overIds1).filter { !it.isUndone }
            val innings1Runs = balls1.sumOf { it.runsOffBat + it.extraRuns }

            val target = innings1Runs + 1

            // Create Innings 2
            val innings2 = Innings(
                matchId = currentMatchId,
                inningsNumber = 2,
                battingTeamId = innings1.bowlingTeamId,
                bowlingTeamId = innings1.battingTeamId,
                targetRuns = target
            )
            repository.insertInnings(innings2)

            val updatedMatch = match.copy(status = MatchStatus.INNINGS_2)
            repository.updateMatch(updatedMatch)

            // Create first over of Innings 2
            val firstOver = Over(
                inningsId = innings2.id,
                overNumber = 1,
                bowlerId = bowlerId
            )
            repository.insertOver(firstOver)

            selectedStrikerId.value = strikerId
            selectedNonStrikerId.value = nonStrikerId
            selectedOpeningBowlerId.value = bowlerId

            showTargetDialog.value = false
            refreshLiveState(currentMatchId)
        }
    }

    // End match and calculate awards
    fun endMatch(resultSummary: String, winnerTeamId: String?) {
        val currentMatchId = _matchSetupId.value ?: return
        viewModelScope.launch {
            repository.completeMatch(currentMatchId, resultSummary, winnerTeamId)
            refreshLiveState(currentMatchId)
        }
    }

    // Helper to refresh live state purely from database sequence
    private suspend fun refreshLiveState(matchId: String) {
        val match = repository.getMatchById(matchId) ?: return
        val teamA = repository.getTeamSheetById(match.teamAId) ?: return
        val teamB = repository.getTeamSheetById(match.teamBId) ?: return

        val inningsList = repository.getInningsForMatch(matchId)
        val currentInnings = if (match.status == MatchStatus.COMPLETED) {
            inningsList.lastOrNull()
        } else if (match.status == MatchStatus.INNINGS_2) {
            inningsList.find { it.inningsNumber == 2 }
        } else {
            inningsList.find { it.inningsNumber == 1 }
        }

        if (currentInnings == null) {
            _liveState.value = LiveMatchState(
                match = match,
                currentInnings = null,
                teamAName = teamA.name,
                teamBName = teamB.name,
                battingTeamName = teamA.name,
                bowlingTeamName = teamB.name,
                battingColor = teamA.themeColor,
                bowlingColor = teamB.themeColor
            )
            return
        }

        val isBattingTeamA = currentInnings.battingTeamId == match.teamAId
        val battingTeam = if (isBattingTeamA) teamA else teamB
        val bowlingTeam = if (isBattingTeamA) teamB else teamA

        val overs = repository.getOversForInnings(currentInnings.id)
        val overIds = overs.map { it.id }
        val balls = if (overIds.isNotEmpty()) repository.getBallsForOvers(overIds).filter { !it.isUndone } else emptyList()
        val ballIds = balls.map { it.id }
        val dismissals = if (ballIds.isNotEmpty()) repository.getDismissalsForBalls(ballIds) else emptyList()

        // 1. Calculate Score & Wickets
        val score = balls.sumOf { it.runsOffBat + it.extraRuns }
        val wickets = dismissals.size

        // 2. Calculate Legal Balls & Overs
        val legalBalls = balls.count { it.isLegalDelivery }
        val completedOversCount = legalBalls / 6
        val remainingBallsCount = legalBalls % 6
        val oversStr = "$completedOversCount.$remainingBallsCount"

        // 3. Current Run Rate
        val oversDecimal = completedOversCount + (remainingBallsCount / 6.0)
        val crr = if (oversDecimal > 0) score / oversDecimal else 0.0

        // 4. Required Run Rate / Target stats
        var rrr: Double? = null
        var runsNeeded: Int? = null
        var ballsRemaining = (match.totalOvers * 6) - legalBalls

        if (currentInnings.inningsNumber == 2 && currentInnings.targetRuns != null) {
            runsNeeded = currentInnings.targetRuns - score
            rrr = if (ballsRemaining > 0) (runsNeeded.toDouble() / (ballsRemaining / 6.0)) else 0.0
        }

        // 5. Projected Score
        val projectedScore = if (currentInnings.inningsNumber == 1) {
            val totalMatchBalls = match.totalOvers * 6
            if (legalBalls > 0) {
                (score + (crr * (match.totalOvers - oversDecimal))).toInt()
            } else {
                0
            }
        } else null

        // 6. Current active bowler and over balls
        val currentOver = overs.lastOrNull()
        val currentOverBalls = if (currentOver != null) balls.filter { it.overId == currentOver.id } else emptyList()
        val bowlerId = currentOver?.bowlerId
        val bowlerName = if (bowlerId != null) repository.getPlayerById(bowlerId)?.name ?: "Bowler" else ""

        // Calculate Bowler current figures
        var bowlerRuns = 0
        var bowlerWickets = 0
        var bowlerLegalBalls = 0
        if (bowlerId != null) {
            val bowlerBalls = balls.filter { b -> overs.find { o -> o.id == b.overId }?.bowlerId == bowlerId }
            bowlerRuns = bowlerBalls.sumOf { b ->
                var runsConceded = b.runsOffBat
                if (b.extraType == ExtraType.WIDE || b.extraType == ExtraType.NO_BALL) {
                    runsConceded += b.extraRuns
                }
                runsConceded
            }
            val bowlerBallIds = bowlerBalls.map { it.id }
            bowlerWickets = dismissals.count { d -> 
                d.ballId in bowlerBallIds && d.dismissalType != DismissalType.RUN_OUT && d.dismissalType != DismissalType.OBSTRUCTING 
            }
            bowlerLegalBalls = bowlerBalls.count { it.isLegalDelivery }
        }
        val bowlerOversStr = "${bowlerLegalBalls / 6}.${bowlerLegalBalls % 6}"

        // 7. Partnership runs
        // We find the index of the last wicket, and count runs since then
        var lastWicketTime = 0L
        val lastDismissal = dismissals.maxByOrNull { d -> 
            val b = balls.find { it.id == d.ballId }
            b?.recordedAt ?: 0L
        }
        if (lastDismissal != null) {
            val b = balls.find { it.id == lastDismissal.ballId }
            if (b != null) {
                lastWicketTime = b.recordedAt
            }
        }
        val ballsSinceWicket = balls.filter { it.recordedAt > lastWicketTime }
        val currentPartnershipRuns = ballsSinceWicket.sumOf { it.runsOffBat + it.extraRuns }
        val currentPartnershipBalls = ballsSinceWicket.count { it.extraType != ExtraType.WIDE }

        // 8. Striker & Non-Striker details
        val lastBallInInnings = balls.lastOrNull()
        val strikerId = selectedStrikerId.value ?: lastBallInInnings?.strikerPlayerId
        val nonStrikerId = selectedNonStrikerId.value ?: lastBallInInnings?.nonStrikerPlayerId

        val strikerName = if (strikerId != null) repository.getPlayerById(strikerId)?.name ?: "Striker" else ""
        val nonStrikerName = if (nonStrikerId != null) repository.getPlayerById(nonStrikerId)?.name ?: "Non-Striker" else ""

        val strikerBallsList = balls.filter { it.strikerPlayerId == strikerId }
        val strikerRuns = strikerBallsList.sumOf { it.runsOffBat }
        val strikerBalls = strikerBallsList.count { it.extraType != ExtraType.WIDE }
        val strikerFours = strikerBallsList.count { it.runsOffBat == 4 }
        val strikerSixes = strikerBallsList.count { it.runsOffBat == 6 }

        val nonStrikerBallsList = balls.filter { it.strikerPlayerId == nonStrikerId }
        val nonStrikerRuns = nonStrikerBallsList.sumOf { it.runsOffBat }
        val nonStrikerBalls = nonStrikerBallsList.count { it.extraType != ExtraType.WIDE }
        val nonStrikerFours = nonStrikerBallsList.count { it.runsOffBat == 4 }
        val nonStrikerSixes = nonStrikerBallsList.count { it.runsOffBat == 6 }

        // 9. Generate Scorecards for UI Scorecard View
        val firstInnings = inningsList.find { it.inningsNumber == 1 }
        val secondInnings = inningsList.find { it.inningsNumber == 2 }

        val firstInningsScorecard = if (firstInnings != null) buildBattingScorecard(firstInnings, strikerId, nonStrikerId) else emptyList()
        val firstInningsBowling = if (firstInnings != null) buildBowlingScorecard(firstInnings) else emptyList()
        val secondInningsScorecard = if (secondInnings != null) buildBattingScorecard(secondInnings, strikerId, nonStrikerId) else emptyList()
        val secondInningsBowling = if (secondInnings != null) buildBowlingScorecard(secondInnings) else emptyList()

        // 10. Generate Completed Overs List for Over-By-Over Replay
        val completedOversList = mutableListOf<CompletedOverInfo>()
        for (o in overs) {
            val oBalls = balls.filter { it.overId == o.id }
            if (oBalls.isNotEmpty()) {
                val oRuns = oBalls.sumOf { b ->
                    var runs = b.runsOffBat
                    if (b.extraType == ExtraType.WIDE || b.extraType == ExtraType.NO_BALL) {
                        runs += b.extraRuns
                    }
                    runs
                }
                val oBallIds = oBalls.map { it.id }
                val oWickets = dismissals.count { d ->
                    d.ballId in oBallIds && d.dismissalType != DismissalType.RUN_OUT && d.dismissalType != DismissalType.OBSTRUCTING
                }
                val bName = repository.getPlayerById(o.bowlerId)?.name ?: "Bowler"
                
                completedOversList.add(
                    CompletedOverInfo(
                        overNumber = o.overNumber,
                        bowlerName = bName,
                        balls = oBalls,
                        runsConceded = oRuns,
                        wickets = oWickets
                    )
                )
            }
        }

        _liveState.value = LiveMatchState(
            match = match,
            currentInnings = currentInnings,
            teamAName = teamA.name,
            teamBName = teamB.name,
            battingTeamName = battingTeam.name,
            bowlingTeamName = bowlingTeam.name,
            battingColor = battingTeam.themeColor,
            bowlingColor = bowlingTeam.themeColor,
            score = score,
            wickets = wickets,
            legalBalls = legalBalls,
            oversStr = oversStr,
            crr = crr,
            rrr = rrr,
            projectedScore = projectedScore,
            runsNeeded = runsNeeded,
            ballsRemaining = ballsRemaining,
            strikerId = strikerId,
            strikerName = strikerName,
            strikerRuns = strikerRuns,
            strikerBalls = strikerBalls,
            strikerFours = strikerFours,
            strikerSixes = strikerSixes,
            nonStrikerId = nonStrikerId,
            nonStrikerName = nonStrikerName,
            nonStrikerRuns = nonStrikerRuns,
            nonStrikerBalls = nonStrikerBalls,
            nonStrikerFours = nonStrikerFours,
            nonStrikerSixes = nonStrikerSixes,
            currentPartnershipRuns = currentPartnershipRuns,
            currentPartnershipBalls = currentPartnershipBalls,
            bowlerId = bowlerId,
            bowlerName = bowlerName,
            bowlerOvers = bowlerOversStr,
            bowlerRuns = bowlerRuns,
            bowlerWickets = bowlerWickets,
            currentOverNumber = overs.size,
            currentOverBalls = currentOverBalls,
            overRuns = currentOverBalls.sumOf { it.runsOffBat + it.extraRuns },
            firstInningsScorecard = firstInningsScorecard,
            firstInningsBowling = firstInningsBowling,
            secondInningsScorecard = secondInningsScorecard,
            secondInningsBowling = secondInningsBowling,
            completedOversList = completedOversList
        )
    }

    private suspend fun buildBattingScorecard(innings: Innings, activeStriker: String?, activeNonStriker: String?): List<BatterScorecard> {
        val overs = repository.getOversForInnings(innings.id)
        val overIds = overs.map { it.id }
        if (overIds.isEmpty()) return emptyList()

        val balls = repository.getBallsForOvers(overIds).filter { !it.isUndone }
        val ballIds = balls.map { it.id }
        val dismissals = repository.getDismissalsForBalls(ballIds)

        // Get batting roster players
        val playersInInnings = repository.getTeamSheetPlayersList(innings.battingTeamId)
        
        return playersInInnings.map { p ->
            val pBalls = balls.filter { it.strikerPlayerId == p.id }
            val runs = pBalls.sumOf { it.runsOffBat }
            val faced = pBalls.count { it.extraType != ExtraType.WIDE }
            val fours = pBalls.count { it.runsOffBat == 4 }
            val sixes = pBalls.count { it.runsOffBat == 6 }
            val sr = if (faced > 0) (runs.toDouble() / faced) * 100.0 else 0.0

            val dismissal = dismissals.find { it.dismissedPlayerId == p.id }
            val isOut = dismissal != null
            val isNotOutAtCrease = (p.id == activeStriker || p.id == activeNonStriker) && !isOut

            val dismissalText = when {
                dismissal == null -> if (isNotOutAtCrease) "not out" else "yet to bat"
                dismissal.dismissalType == DismissalType.BOWLED -> "b ${getBowlerNameForBall(dismissal.ballId, overs)}"
                dismissal.dismissalType == DismissalType.LBW -> "lbw b ${getBowlerNameForBall(dismissal.ballId, overs)}"
                dismissal.dismissalType == DismissalType.CAUGHT -> "c ${getPlayerName(dismissal.fielderPlayerId)} b ${getBowlerNameForBall(dismissal.ballId, overs)}"
                dismissal.dismissalType == DismissalType.RUN_OUT -> "run out (${getPlayerName(dismissal.fielderPlayerId)})"
                dismissal.dismissalType == DismissalType.STUMPED -> "st ${getPlayerName(dismissal.fielderPlayerId)} b ${getBowlerNameForBall(dismissal.ballId, overs)}"
                else -> dismissal.dismissalType.displayName()
            }

            BatterScorecard(
                playerId = p.id,
                name = p.name,
                runs = runs,
                balls = faced,
                fours = fours,
                sixes = sixes,
                strikeRate = sr,
                dismissalText = dismissalText,
                isOut = isOut,
                isNotOutAtCrease = isNotOutAtCrease
            )
        }
    }

    private suspend fun buildBowlingScorecard(innings: Innings): List<BowlerScorecard> {
        val overs = repository.getOversForInnings(innings.id)
        val overIds = overs.map { it.id }
        if (overIds.isEmpty()) return emptyList()

        val balls = repository.getBallsForOvers(overIds).filter { !it.isUndone }
        val ballIds = balls.map { it.id }
        val dismissals = repository.getDismissalsForBalls(ballIds)

        // Find unique bowlers in these overs
        val bowlerIds = overs.map { it.bowlerId }.distinct()

        return bowlerIds.map { bid ->
            val name = repository.getPlayerById(bid)?.name ?: "Bowler"
            val bowlerOvers = overs.filter { it.bowlerId == bid }
            val bowlerOverIds = bowlerOvers.map { it.id }
            val bowlerBalls = balls.filter { it.overId in bowlerOverIds }
            
            val runs = bowlerBalls.sumOf { b ->
                var rc = b.runsOffBat
                if (b.extraType == ExtraType.WIDE || b.extraType == ExtraType.NO_BALL) {
                    rc += b.extraRuns
                }
                rc
            }
            val bLegal = bowlerBalls.count { it.isLegalDelivery }
            val completedOv = bLegal / 6
            val remB = bLegal % 6
            val oversDouble = completedOv + (remB / 10.0)

            val wickets = dismissals.count { d ->
                val b = balls.find { it.id == d.ballId }
                b != null && b.overId in bowlerOverIds && d.dismissalType != DismissalType.RUN_OUT && d.dismissalType != DismissalType.OBSTRUCTING
            }

            val econ = if (bLegal > 0) (runs.toDouble() / (bLegal / 6.0)) else 0.0

            BowlerScorecard(
                playerId = bid,
                name = name,
                overs = oversDouble,
                runs = runs,
                wickets = wickets,
                economy = econ
            )
        }
    }

    private suspend fun getBowlerNameForBall(ballId: String, overs: List<Over>): String {
        // Query to find ball's over, then bowler
        val ball = repository.getBallsForOvers(overs.map { it.id }).find { it.id == ballId } ?: return "Bowler"
        val over = overs.find { it.id == ball.overId } ?: return "Bowler"
        return repository.getPlayerById(over.bowlerId)?.name ?: "Bowler"
    }

    private suspend fun getPlayerName(id: String?): String {
        if (id == null) return "Fielder"
        return repository.getPlayerById(id)?.name ?: "Fielder"
    }

    private suspend fun reconstructStrikePositions(matchId: String) {
        val state = _liveState.value ?: return
        val innings = state.currentInnings ?: return
        val overs = repository.getOversForInnings(innings.id)
        if (overs.isEmpty()) return
        val lastOver = overs.last()
        val balls = repository.getBallsForOver(lastOver.id)
        val lastBall = balls.findLast { !it.isUndone }

        if (lastBall != null) {
            // Apply strike rotation rules backwards or forwards to restore striker position
            var striker = lastBall.strikerPlayerId
            var nonStriker = lastBall.nonStrikerPlayerId

            // Check if rotation occurred on this last ball
            val rotationOccurred = lastBall.runsOffBat % 2 != 0 || 
                                   lastBall.extraType == ExtraType.BYE || 
                                   lastBall.extraType == ExtraType.LEG_BYE // Odd runs rot
            if (rotationOccurred) {
                // Swap back
                val t = striker
                striker = nonStriker
                nonStriker = t
            }
            selectedStrikerId.value = striker
            selectedNonStrikerId.value = nonStriker
        }
    }

    private suspend fun checkMatchTriggerAndRefresh(matchId: String) {
        val match = repository.getMatchById(matchId) ?: return
        val inningsList = repository.getInningsForMatch(matchId)
        val currentInnings = if (match.status == MatchStatus.INNINGS_2) {
            inningsList.find { it.inningsNumber == 2 }
        } else {
            inningsList.find { it.inningsNumber == 1 }
        } ?: return

        val overs = repository.getOversForInnings(currentInnings.id)
        val overIds = overs.map { it.id }
        val balls = repository.getBallsForOvers(overIds).filter { !it.isUndone }
        val ballIds = balls.map { it.id }
        val dismissals = repository.getDismissalsForBalls(ballIds)

        val score = balls.sumOf { it.runsOffBat + it.extraRuns }
        val wickets = dismissals.size
        val legalBalls = balls.count { it.isLegalDelivery }

        // Innings 2 Chase end check
        if (currentInnings.inningsNumber == 2 && currentInnings.targetRuns != null) {
            if (score >= currentInnings.targetRuns) {
                // Batting team 2 won!
                val wicketsLostStr = if (wickets == 1) "1 wicket" else "$wickets wickets"
                val marginStr = "Won by ${10 - wickets} wickets"
                endMatch(marginStr, currentInnings.battingTeamId)
                return
            }
        }

        // All Out check
        val battingRoster = repository.getTeamSheetPlayersList(currentInnings.battingTeamId)
        val maxWickets = if (match.lastManStanding) battingRoster.size else battingRoster.size - 1
        val isAllOut = wickets >= maxWickets || wickets >= 10

        // Overs completed check
        val isOversCompleted = legalBalls >= (match.totalOvers * 6)

        if (isAllOut || isOversCompleted) {
            if (currentInnings.inningsNumber == 1) {
                // Target dialog appears
                showTargetDialog.value = true
            } else {
                // Innings 2 ended without reaching target -> Defending team 1 wins or tie
                val innings1 = inningsList.find { it.inningsNumber == 1 }!!
                val overs1 = repository.getOversForInnings(innings1.id)
                val balls1 = repository.getBallsForOvers(overs1.map { it.id }).filter { !it.isUndone }
                val innings1Runs = balls1.sumOf { it.runsOffBat + it.extraRuns }

                val summary = when {
                    score > innings1Runs -> "Won by ${10 - wickets} wickets" // already handled but safeguard
                    score < innings1Runs -> "Won by ${innings1Runs - score} runs"
                    else -> "Match tied"
                }
                val winnerId = when {
                    score > innings1Runs -> currentInnings.battingTeamId
                    score < innings1Runs -> currentInnings.bowlingTeamId
                    else -> null
                }
                endMatch(summary, winnerId)
                return
            }
        } else {
            // Check if over completed (6 legal balls in over)
            val currentOverBalls = balls.filter { it.overId == overs.last().id }
            val currentOverLegal = currentOverBalls.count { it.isLegalDelivery }
            if (currentOverLegal >= 6) {
                // Show end over overlay
                showEndOverOverlay.value = true
                showBowlerPicker.value = true
            } else {
                // Rotate strike off-bat runs rules
                val lastBall = balls.lastOrNull()
                if (lastBall != null) {
                    val rot = lastBall.runsOffBat % 2 != 0 || 
                              lastBall.extraType == ExtraType.BYE || 
                              lastBall.extraType == ExtraType.LEG_BYE
                    if (rot) {
                        val oldStriker = selectedStrikerId.value
                        val oldNonStriker = selectedNonStrikerId.value
                        selectedStrikerId.value = oldNonStriker
                        selectedNonStrikerId.value = oldStriker
                    }
                }
            }
        }

        refreshLiveState(matchId)
    }
}
