package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.screens.*
import com.example.ui.theme.GullyCrixTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.MatchViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var matchViewModel: MatchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as GullyCrixApplication
        mainViewModel = MainViewModel(app)
        matchViewModel = MatchViewModel(app)

        // Preload default players & teams once to make the app ready-to-test
        lifecycleScopeLaunch {
            val db = app.database
            val pDao = db.playerDao()
            val tsDao = db.teamSheetDao()
            val hasPlayers = app.repository.allPlayers.firstOrNull()?.isNotEmpty() == true
            if (!hasPlayers) {
                // Preload players
                val p1 = Player(id = "p1", name = "Chikoo", alias = "CH", battingHand = BattingHand.RIGHT, bowlingStyle = BowlingStyle.RA_OFFSPIN, themeColor = ThemeColor.BLUE_OCEAN)
                val p2 = Player(id = "p2", name = "Mahi", alias = "MSD", battingHand = BattingHand.RIGHT, bowlingStyle = BowlingStyle.RA_MEDIUM, themeColor = ThemeColor.EMERALD)
                val p3 = Player(id = "p3", name = "Rishabh", alias = "RP", battingHand = BattingHand.LEFT, bowlingStyle = BowlingStyle.LA_FAST, themeColor = ThemeColor.SUNSET)
                val p4 = Player(id = "p4", name = "Prajwal Koundinyaa", alias = "PK", battingHand = BattingHand.RIGHT, bowlingStyle = BowlingStyle.RA_FAST, themeColor = ThemeColor.ROYAL_PURPLE)
                val p5 = Player(id = "p5", name = "Junaad", alias = "JU", battingHand = BattingHand.RIGHT, bowlingStyle = BowlingStyle.LEGSPIN, themeColor = ThemeColor.CRIMSON)
                val p6 = Player(id = "p6", name = "Samrat", alias = "SM", battingHand = BattingHand.LEFT, bowlingStyle = BowlingStyle.LA_SPIN, themeColor = ThemeColor.AMBER)
                val p7 = Player(id = "p7", name = "Bhuvan", alias = "BH", battingHand = BattingHand.RIGHT, bowlingStyle = BowlingStyle.RA_MEDIUM, themeColor = ThemeColor.SLATE)
                val p8 = Player(id = "p8", name = "Sam", alias = "SS", battingHand = BattingHand.RIGHT, bowlingStyle = BowlingStyle.RA_FAST, themeColor = ThemeColor.MIDNIGHT)

                pDao.insertPlayer(p1)
                pDao.insertPlayer(p2)
                pDao.insertPlayer(p3)
                pDao.insertPlayer(p4)
                pDao.insertPlayer(p5)
                pDao.insertPlayer(p6)
                pDao.insertPlayer(p7)
                pDao.insertPlayer(p8)

                // Preload team sheets
                val ts1 = TeamSheet(id = "t1", name = "Strikers XI", themeColor = ThemeColor.BLUE_OCEAN)
                val ts2 = TeamSheet(id = "t2", name = "Rangers XI", themeColor = ThemeColor.CRIMSON)

                app.repository.insertTeamSheet(ts1, listOf("p1", "p2", "p3", "p4"), listOf("p2"), listOf("p3"))
                app.repository.insertTeamSheet(ts2, listOf("p5", "p6", "p7", "p8"), listOf("p5"), listOf("p6"))
            }
        }

        setContent {
            GullyCrixTheme {
                val prefs = app.preferences
                val initialScreen = if (prefs.hasCompletedOnboarding) "home" else "onboarding_welcome"
                var currentRoute by remember { mutableStateOf(initialScreen) }
                val backStack = remember { mutableStateListOf<String>() }
                var lastRoute by remember { mutableStateOf(initialScreen) }

                if (currentRoute != lastRoute) {
                    if (currentRoute == "home" || currentRoute == "onboarding_welcome") {
                        backStack.clear()
                    } else if (backStack.isNotEmpty() && backStack.last() == currentRoute) {
                        backStack.removeLast()
                    } else {
                        backStack.add(lastRoute)
                    }
                    lastRoute = currentRoute
                }

                BackHandler(enabled = backStack.isNotEmpty()) {
                    if (backStack.isNotEmpty()) {
                        currentRoute = backStack.removeLast()
                        lastRoute = currentRoute
                    }
                }

                // Safe parameters
                var editingPlayerId by remember { mutableStateOf<String?>(null) }
                var editingTeamSheetId by remember { mutableStateOf<String?>(null) }
                var activeMatchId by remember { mutableStateOf<String?>(null) }
                var selectedProfileId by remember { mutableStateOf<String?>(null) }

                // State mappings
                val players by mainViewModel.players.collectAsStateWithLifecycle()
                val teamSheets by mainViewModel.teamSheets.collectAsStateWithLifecycle()
                val matches by mainViewModel.matches.collectAsStateWithLifecycle()
                val activeMatch by mainViewModel.activeMatch.collectAsStateWithLifecycle()

                val liveState by matchViewModel.liveState.collectAsStateWithLifecycle()

                val selectedTeamAId by mainViewModel.selectedTeamAId.collectAsStateWithLifecycle()
                val selectedTeamBId by mainViewModel.selectedTeamBId.collectAsStateWithLifecycle()

                // Toss state mapping
                val tossWinnerId by matchViewModel.tossWinner.collectAsStateWithLifecycle()
                val tossDecision by matchViewModel.tossDecision.collectAsStateWithLifecycle()
                val tossCompleted by matchViewModel.tossCompleted.collectAsStateWithLifecycle()

                // Selected ball entry state mapping
                val selectedRuns by matchViewModel.selectedRuns.collectAsStateWithLifecycle()
                val selectedExtraType by matchViewModel.selectedExtraType.collectAsStateWithLifecycle()
                val selectedExtraRuns by matchViewModel.selectedExtraRuns.collectAsStateWithLifecycle()

                // Alert and triggers mapping
                val showEndOverOverlay by matchViewModel.showEndOverOverlay.collectAsStateWithLifecycle()
                val showBowlerPicker by matchViewModel.showBowlerPicker.collectAsStateWithLifecycle()
                val showTargetDialog by matchViewModel.showTargetDialog.collectAsStateWithLifecycle()

                // State machine parameters for Wicket dialogs
                val wicketDismissal by matchViewModel.wicketDismissalType.collectAsStateWithLifecycle()
                val wicketFielderId by matchViewModel.wicketFielderId.collectAsStateWithLifecycle()
                val wicketDismissedEnd by matchViewModel.wicketDismissedEnd.collectAsStateWithLifecycle()
                val nextBatterId by matchViewModel.nextBatterId.collectAsStateWithLifecycle()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentRoute) {
                        "onboarding_welcome" -> {
                            WelcomeScreen(onContinue = { currentRoute = "onboarding_privacy" })
                        }
                        "onboarding_privacy" -> {
                            PrivacyScreen(onAccept = { currentRoute = "onboarding_howitworks" })
                        }
                        "onboarding_howitworks" -> {
                            HowItWorksScreen(onContinue = {
                                prefs.hasCompletedOnboarding = true
                                currentRoute = "home"
                            })
                        }
                        "home" -> {
                            HomeScreen(
                                hasPlayers = players.isNotEmpty(),
                                activeMatch = activeMatch,
                                onStartMatch = {
                                    mainViewModel.selectTeamA(null)
                                    mainViewModel.selectTeamB(null)
                                    currentRoute = "team_selection"
                                },
                                onResumeMatch = { id ->
                                    matchViewModel.loadMatch(id)
                                    activeMatchId = id
                                    currentRoute = "live_scoring"
                                },
                                onPastMatches = { currentRoute = "match_history" },
                                onSquadBook = { currentRoute = "squad_book" },
                                onPlayerProfiles = { currentRoute = "player_profiles" },
                                onStatistics = { currentRoute = "global_stats" },
                                onSettings = { currentRoute = "settings" },
                                onQuickBallClick = {
                                    currentRoute = "squad_book_intro"
                                }
                            )
                        }
                        "squad_book_intro" -> {
                            SquadBookIntroScreen(
                                onSkip = { currentRoute = "squad_book" },
                                onAddPlayer = {
                                    editingPlayerId = null
                                    currentRoute = "add_player"
                                }
                            )
                        }
                        "squad_book" -> {
                            SquadBookDashboard(
                                players = players,
                                teamSheets = teamSheets,
                                onAddPlayer = {
                                    editingPlayerId = null
                                    currentRoute = "add_player"
                                },
                                onEditPlayer = { id ->
                                    editingPlayerId = id
                                    currentRoute = "add_player"
                                },
                                onAddTeamSheet = {
                                    editingTeamSheetId = null
                                    currentRoute = "new_team_sheet"
                                },
                                onEditTeamSheet = { id ->
                                    editingTeamSheetId = id
                                    currentRoute = "new_team_sheet"
                                },
                                onBack = { currentRoute = "home" }
                            )
                        }
                        "add_player" -> {
                            val player = players.find { it.id == editingPlayerId }
                            AddEditPlayerForm(
                                player = player,
                                onSave = { name, alias, hand, style, color ->
                                    if (player != null) {
                                        mainViewModel.updatePlayer(player.id, name, alias, hand, style, color)
                                    } else {
                                        mainViewModel.addPlayer(name, alias, hand, style, color)
                                    }
                                    currentRoute = "squad_book"
                                },
                                onBack = { currentRoute = "squad_book" }
                            )
                        }
                        "new_team_sheet" -> {
                            val sheet = teamSheets.find { it.id == editingTeamSheetId }
                            val allPlayers = players
                            NewEditTeamSheetForm(
                                sheet = sheet,
                                allPlayers = allPlayers,
                                onSave = { name, color, playerIds, captains, keepers ->
                                    if (sheet != null) {
                                        mainViewModel.updateTeamSheet(sheet.id, name, color, playerIds, captains, keepers)
                                    } else {
                                        mainViewModel.createTeamSheet(name, color, playerIds, captains, keepers)
                                    }
                                    currentRoute = "squad_book"
                                },
                                onBack = { currentRoute = "squad_book" }
                            )
                        }
                        "team_selection" -> {
                            TeamSelectionScreen(
                                teamSheets = emptyList(), // using real allTeams
                                allTeams = teamSheets,
                                selectedTeamAId = selectedTeamAId,
                                selectedTeamBId = selectedTeamBId,
                                onSelectTeamA = { mainViewModel.selectTeamA(it) },
                                onSelectTeamB = { mainViewModel.selectTeamB(it) },
                                onSwap = { mainViewModel.swapTeams() },
                                onContinue = {
                                    val tA = teamSheets.find { it.id == selectedTeamAId }!!
                                    val tB = teamSheets.find { it.id == selectedTeamBId }!!
                                    matchViewModel.setupToss(tA.id, tB.id)
                                    currentRoute = "coin_toss"
                                },
                                onBack = { currentRoute = "home" }
                            )
                        }
                        "coin_toss" -> {
                            val tA = teamSheets.find { it.id == selectedTeamAId }!!
                            val tB = teamSheets.find { it.id == selectedTeamBId }!!
                            CoinTossScreen(
                                teamA = tA,
                                teamB = tB,
                                tossCompleted = tossCompleted,
                                tossWinnerId = tossWinnerId,
                                tossDecision = tossDecision,
                                onTossResult = { winnerId, decision ->
                                    matchViewModel.tossWinner.value = winnerId
                                    matchViewModel.tossDecision.value = decision
                                    matchViewModel.tossCompleted.value = true
                                    
                                    // Go to full match setup
                                    currentRoute = "full_match_setup"
                                },
                                onBack = { currentRoute = "team_selection" }
                            )
                        }
                        "full_match_setup" -> {
                            val tA = teamSheets.find { it.id == selectedTeamAId }!!
                            val tB = teamSheets.find { it.id == selectedTeamBId }!!
                            val tWinner = tossWinnerId ?: tA.id
                            val tDecision = tossDecision ?: TossDecision.BAT

                            FullMatchSetupScreen(
                                teamA = tA,
                                teamB = tB,
                                tossWinnerId = tWinner,
                                tossDecision = tDecision,
                                overs = matchViewModel.oversInput.collectAsStateWithLifecycle().value,
                                maxOversPerBowler = matchViewModel.maxOversPerBowlerInput.collectAsStateWithLifecycle().value,
                                gullyRules = matchViewModel.gullyRulesEnabled.collectAsStateWithLifecycle().value,
                                lastManStanding = matchViewModel.lastManStanding.collectAsStateWithLifecycle().value,
                                commonPlayerId = matchViewModel.commonPlayerId.collectAsStateWithLifecycle().value,
                                onOversChanged = { matchViewModel.setOvers(it) },
                                onMaxOversChanged = { matchViewModel.maxOversPerBowlerInput.value = it },
                                onGullyRulesChanged = { matchViewModel.gullyRulesEnabled.value = it },
                                onLastManStandingChanged = { matchViewModel.lastManStanding.value = it },
                                onStartMatch = {
                                    matchViewModel.startMatch(tA.id, tB.id, tWinner, tDecision)
                                    currentRoute = "opening_selection"
                                },
                                onBack = { currentRoute = "coin_toss" }
                            )
                        }
                        "opening_selection" -> {
                            val tA = teamSheets.find { it.id == selectedTeamAId }!!
                            val tB = teamSheets.find { it.id == selectedTeamBId }!!
                            // Determine which team bats first
                            val isTeamABatting = (tossWinnerId == tA.id && tossDecision == TossDecision.BAT) ||
                                                 (tossWinnerId == tB.id && tossDecision == TossDecision.BOWL)
                            val battingTeamId = if (isTeamABatting) tA.id else tB.id
                            val bowlingTeamId = if (isTeamABatting) tB.id else tA.id

                            var battingTeamPlayers by remember { mutableStateOf<List<Player>>(emptyList()) }
                            var bowlingTeamPlayers by remember { mutableStateOf<List<Player>>(emptyList()) }

                            LaunchedEffect(battingTeamId, bowlingTeamId) {
                                battingTeamPlayers = app.repository.getTeamSheetPlayersList(battingTeamId)
                                bowlingTeamPlayers = app.repository.getTeamSheetPlayersList(bowlingTeamId)
                            }

                            OpeningSelectionScreen(
                                battingPlayers = battingTeamPlayers,
                                bowlingPlayers = bowlingTeamPlayers,
                                onConfirm = { striker, nonStriker, bowler ->
                                    matchViewModel.confirmOpeners(striker, nonStriker, bowler)
                                    val currentActive = activeMatch ?: mainViewModel.activeMatch.value
                                    if (currentActive != null) {
                                        activeMatchId = currentActive.id
                                    }
                                    currentRoute = "live_scoring"
                                }
                            )
                        }
                        "live_scoring" -> {
                            val activeState = liveState
                            if (activeState != null) {
                                if (activeState.match.status == MatchStatus.COMPLETED) {
                                    currentRoute = "match_result"
                                } else {
                                    var battingTeamPlayers by remember { mutableStateOf<List<Player>>(emptyList()) }
                                    var bowlingTeamPlayers by remember { mutableStateOf<List<Player>>(emptyList()) }

                                    LaunchedEffect(activeState.currentInnings?.battingTeamId, activeState.currentInnings?.bowlingTeamId) {
                                        activeState.currentInnings?.let {
                                            battingTeamPlayers = app.repository.getTeamSheetPlayersList(it.battingTeamId)
                                            bowlingTeamPlayers = app.repository.getTeamSheetPlayersList(it.bowlingTeamId)
                                        }
                                    }

                                    LiveScoringScreen(
                                        state = activeState,
                                        selectedRuns = selectedRuns,
                                        selectedExtraType = selectedExtraType,
                                        selectedExtraRuns = selectedExtraRuns,
                                        onSelectOutcome = { r, e -> matchViewModel.selectOutcome(r, e) },
                                        onConfirmDelivery = { matchViewModel.confirmBallDelivery() },
                                        onUndo = { matchViewModel.undoLastBall() },
                                        onManualSwap = { matchViewModel.manualSwapStrike() },
                                        onOpenScorecard = { currentRoute = "scorecard" },
                                        onBack = { currentRoute = "home" },
                                        wicketDismissal = wicketDismissal,
                                        onWicketDismissalChanged = { matchViewModel.wicketDismissalType.value = it },
                                        wicketFielderId = wicketFielderId,
                                        onWicketFielderChanged = { matchViewModel.wicketFielderId.value = it },
                                        wicketDismissedEnd = wicketDismissedEnd,
                                        onWicketEndChanged = { matchViewModel.wicketDismissedEnd.value = it },
                                        nextBatterId = nextBatterId,
                                        onNextBatterChanged = { matchViewModel.nextBatterId.value = it },
                                        onConfirmWicket = { matchViewModel.confirmWicket() },
                                        showEndOverOverlay = showEndOverOverlay,
                                        onDismissEndOver = { matchViewModel.showEndOverOverlay.value = false },
                                        showBowlerPicker = showBowlerPicker,
                                        onBowlerSelected = { matchViewModel.startNextOver(it) },
                                        onChangeBowler = { matchViewModel.changeCurrentBowler(it) },
                                        showTargetDialog = showTargetDialog,
                                        onStartNextInnings = { striker, nonStriker, bowler ->
                                            matchViewModel.startInnings2(striker, nonStriker, bowler)
                                        },
                                        battingPlayers = battingTeamPlayers,
                                        bowlingPlayers = bowlingTeamPlayers
                                    )
                                }
                            }
                        }
                        "match_result" -> {
                            val activeState = liveState
                            if (activeState != null) {
                                var matchAwards by remember { mutableStateOf<List<Award>>(emptyList()) }
                                LaunchedEffect(activeState.match.id) {
                                    matchAwards = app.repository.getAwardsForMatch(activeState.match.id)
                                }

                                val tA = teamSheets.find { it.id == activeState.match.teamAId }!!
                                val tB = teamSheets.find { it.id == activeState.match.teamBId }!!

                                MatchResultScreens(
                                    match = activeState.match,
                                    teamA = tA,
                                    teamB = tB,
                                    awards = matchAwards,
                                    players = players,
                                    onViewScorecard = { currentRoute = "scorecard" },
                                    onStartNewMatch = {
                                        mainViewModel.selectTeamA(null)
                                        mainViewModel.selectTeamB(null)
                                        currentRoute = "team_selection"
                                    }
                                )
                            }
                        }
                        "scorecard" -> {
                            val activeState = liveState
                            if (activeState != null) {
                                ScorecardScreen(
                                    state = activeState,
                                    onBack = {
                                        if (activeState.match.status == MatchStatus.COMPLETED) {
                                            currentRoute = "match_result"
                                        } else {
                                            currentRoute = "live_scoring"
                                        }
                                    }
                                )
                            }
                        }
                        "match_history" -> {
                            MatchHistoryScreen(
                                matches = matches.filter { it.status == MatchStatus.COMPLETED },
                                teamSheets = teamSheets,
                                onSelectMatch = { id ->
                                    matchViewModel.loadMatch(id)
                                    activeMatchId = id
                                    currentRoute = "scorecard"
                                },
                                onBack = { currentRoute = "home" }
                            )
                        }
                        "player_profiles" -> {
                            PlayerProfilesListScreen(
                                players = players,
                                onSelectPlayer = { id ->
                                    selectedProfileId = id
                                    currentRoute = "player_profile_detail"
                                },
                                onBack = { currentRoute = "home" }
                            )
                        }
                        "player_profile_detail" -> {
                            val profile = players.find { it.id == selectedProfileId }
                            if (profile != null) {
                                var stats by remember(profile.id) { mutableStateOf<PlayerCareerStats?>(null) }
                                LaunchedEffect(profile.id) {
                                    stats = app.repository.getPlayerCareerStats(profile.id)
                                }
                                if (stats != null) {
                                    PlayerProfileDetailScreen(
                                        player = profile,
                                        careerStats = stats!!,
                                        onBack = { currentRoute = "player_profiles" }
                                    )
                                } else {
                                    androidx.compose.foundation.layout.Box(
                                        modifier = Modifier.fillMaxSize().background(com.example.ui.theme.NearBlack),
                                        contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        androidx.compose.material3.CircularProgressIndicator(color = com.example.ui.theme.SuccessGreen)
                                    }
                                }
                            }
                        }
                        "global_stats" -> {
                            var leaderboard by remember { mutableStateOf<List<LeaderboardEntry>>(emptyList()) }
                            LaunchedEffect(matches) {
                                leaderboard = app.repository.getSquadLeaderboard()
                            }
                            GlobalStatsDashboard(
                                matches = matches,
                                players = players,
                                leaderboard = leaderboard,
                                onBack = { currentRoute = "home" }
                            )
                        }
                        "settings" -> {
                            SettingsScreen(
                                soundEnabled = prefs.soundEnabled,
                                onSoundToggled = { prefs.soundEnabled = it },
                                hapticEnabled = prefs.hapticEnabled,
                                onHapticToggled = { prefs.hapticEnabled = it },
                                defaultOvers = prefs.defaultOvers,
                                onDefaultOversChanged = { prefs.defaultOvers = it },
                                onClearAllData = {
                                    mainViewModel.clearAllData()
                                    currentRoute = "onboarding_welcome"
                                },
                                onBack = { currentRoute = "home" }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun lifecycleScopeLaunch(block: suspend () -> Unit) {
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Main) {
            block()
        }
    }
}

// Map screen forms directly to make editing fully inline
@Composable
fun AddEditPlayerForm(
    player: Player?,
    onSave: (String, String?, BattingHand?, BowlingStyle?, ThemeColor) -> Unit,
    onBack: () -> Unit
) {
    AddEditPlayerScreen(
        playerId = player?.id,
        existingPlayer = player,
        onSave = onSave,
        onDelete = null,
        onBack = onBack
    )
}

@Composable
fun NewEditTeamSheetForm(
    sheet: TeamSheet?,
    allPlayers: List<Player>,
    onSave: (String, ThemeColor, List<String>, List<String>, List<String>) -> Unit,
    onBack: () -> Unit
) {
    NewEditTeamSheetScreen(
        teamSheetId = sheet?.id,
        existingTeam = sheet,
        existingPlayers = allPlayers,
        selectedPlayerIds = emptyList(),
        onSave = onSave,
        onBack = onBack
    )
}
