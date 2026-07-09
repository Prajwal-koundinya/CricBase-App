package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.AvatarSize
import com.example.ui.components.PlayerAvatar
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TeamSelectionScreen(
    teamSheets: List<ThemeColor>, // wait, teamSheets list of TeamSheet
    allTeams: List<TeamSheet>,
    selectedTeamAId: String?,
    selectedTeamBId: String?,
    onSelectTeamA: (String?) -> Unit,
    onSelectTeamB: (String?) -> Unit,
    onSwap: () -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showASelection by remember { mutableStateOf(false) }
    var showBSelection by remember { mutableStateOf(false) }

    val teamA = allTeams.find { it.id == selectedTeamAId }
    val teamB = allTeams.find { it.id == selectedTeamBId }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NearBlack)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Teams", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = NearBlack,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Choose Opponents",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Pick two different team sheets to face off",
                    color = TextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 40.dp)
                )

                // Team Slot A
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBlack)
                        .clickable { showASelection = true }
                        .border(
                            2.dp,
                            if (teamA != null) Color(android.graphics.Color.parseColor(teamA.themeColor.hex)) else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (teamA != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TEAM A", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(teamA.name, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Theme: ${teamA.themeColor.displayName()}", color = TextMuted, fontSize = 12.sp)
                        }
                    } else {
                        Text("Select Team A", color = TextMuted, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                // Swap Button
                IconButton(
                    onClick = onSwap,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen)
                ) {
                    Icon(Icons.Filled.SwapVert, "Swap teams", tint = Color.White)
                }

                // Team Slot B
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBlack)
                        .clickable { showBSelection = true }
                        .border(
                            2.dp,
                            if (teamB != null) Color(android.graphics.Color.parseColor(teamB.themeColor.hex)) else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (teamB != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TEAM B", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(teamB.name, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Theme: ${teamB.themeColor.displayName()}", color = TextMuted, fontSize = 12.sp)
                        }
                    } else {
                        Text("Select Team B", color = TextMuted, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Button(
                onClick = onContinue,
                enabled = selectedTeamAId != null && selectedTeamBId != null && selectedTeamAId != selectedTeamBId,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("team_selection_continue")
            ) {
                Text("Continue to Coin Toss", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // Dropdown Selection Bottom Sheets
        if (showASelection) {
            TeamSheetPickerSheet(
                allTeams = allTeams,
                excludedId = selectedTeamBId,
                onSelected = {
                    onSelectTeamA(it)
                    showASelection = false
                },
                onDismiss = { showASelection = false }
            )
        }

        if (showBSelection) {
            TeamSheetPickerSheet(
                allTeams = allTeams,
                excludedId = selectedTeamAId,
                onSelected = {
                    onSelectTeamB(it)
                    showBSelection = false
                },
                onDismiss = { showBSelection = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamSheetPickerSheet(
    allTeams: List<TeamSheet>,
    excludedId: String?,
    onSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardBlack,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextMuted) }
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Select Team Sheet", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            val selectable = allTeams.filter { it.id != excludedId }
            if (selectable.isEmpty()) {
                Text("No other team sheets available. Create teams in Squad Book.", color = TextMuted)
                Spacer(modifier = Modifier.height(40.dp))
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    selectable.forEach { team ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NearBlack)
                                .clickable { onSelected(team.id) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(team.themeColor.hex)))
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(team.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CoinTossScreen(
    teamA: TeamSheet,
    teamB: TeamSheet,
    tossCompleted: Boolean,
    tossWinnerId: String?,
    tossDecision: TossDecision?,
    onTossResult: (winnerId: String, decision: TossDecision) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCallerId by remember { mutableStateOf(teamA.id) }
    var selectedCallHeads by remember { mutableStateOf(true) }
    var isSpinning by remember { mutableStateOf(false) }
    var coinRotationY by remember { mutableStateOf(0f) }
    var coinFace by remember { mutableStateOf("H") }
    var showTossButtons by remember { mutableStateOf(false) }
    var localTossWinnerId by remember { mutableStateOf<String?>(null) }

    val animatedRotationY by animateFloatAsState(
        targetValue = coinRotationY,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
    )

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NearBlack)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Coin Toss", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = NearBlack,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Toss Caller Selector
                Text(
                    "WHO'S CALLING?",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf(teamA, teamB).forEach { team ->
                        val isSel = selectedCallerId == team.id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) SuccessGreen else CardBlack)
                                .clickable { if (!isSpinning && !showTossButtons) selectedCallerId = team.id }
                                .border(1.dp, if (isSel) SuccessGreen else Color.Transparent, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                team.name,
                                color = if (isSel) Color.White else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Heads/Tails Call
                Text(
                    "THEIR CALL?",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf(true, false).forEach { heads ->
                        val isSel = selectedCallHeads == heads
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) SuccessGreen else CardBlack)
                                .clickable { if (!isSpinning && !showTossButtons) selectedCallHeads = heads }
                                .border(1.dp, if (isSel) SuccessGreen else Color.Transparent, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (heads) "Heads" else "Tails",
                                color = if (isSel) Color.White else TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Beautiful Golden 3D-like Flipping Coin
                val goldGradient = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFD700), Color(0xFFDAA520))
                )

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer {
                            rotationY = animatedRotationY
                            cameraDistance = 12f * density
                        }
                        .clip(CircleShape)
                        .background(goldGradient)
                        .border(6.dp, Color(0xFF8B6508), CircleShape)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = coinFace,
                            color = Color(0xFF4A3B00),
                            fontSize = 64.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            if (!showTossButtons) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isSpinning = true
                            coinRotationY += 1800f // 5 full spins
                            
                            val flipJob = launch {
                                while (isSpinning) {
                                    coinFace = if (coinFace == "H") "T" else "H"
                                    delay(80)
                                }
                            }
                            
                            delay(1500)
                            flipJob.cancel()
                            isSpinning = false
                            
                            // Fair random draw
                            val isHeads = (0..1).random() == 0
                            coinFace = if (isHeads) "H" else "T"
                            
                            val callerWon = selectedCallHeads == isHeads
                            val winner = if (callerWon) selectedCallerId else {
                                if (selectedCallerId == teamA.id) teamB.id else teamA.id
                            }
                            
                            localTossWinnerId = winner
                            showTossButtons = true
                        }
                    },
                    enabled = !isSpinning,
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("toss_spin_btn")
                ) {
                    Text("Toss Coin", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            } else {
                // Toss outcome decision buttons
                val winnerTeam = if (localTossWinnerId == teamA.id) teamA else teamB
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Result: ${if (coinFace == "H") "HEADS" else "TAILS"}",
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${winnerTeam.name} WON THE TOSS!",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onTossResult(winnerTeam.id, TossDecision.BAT) },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Text("Bat First", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { onTossResult(winnerTeam.id, TossDecision.BOWL) },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Text("Bowl First", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FullMatchSetupScreen(
    teamA: TeamSheet,
    teamB: TeamSheet,
    tossWinnerId: String,
    tossDecision: TossDecision,
    overs: Int,
    maxOversPerBowler: Int,
    gullyRules: Boolean,
    lastManStanding: Boolean,
    commonPlayerId: String?,
    onOversChanged: (Int) -> Unit,
    onMaxOversChanged: (Int) -> Unit,
    onGullyRulesChanged: (Boolean) -> Unit,
    onLastManStandingChanged: (Boolean) -> Unit,
    onStartMatch: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NearBlack)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Match Setup", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = NearBlack,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Header summary
            Text(
                "Match Settings",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val winnerName = if (tossWinnerId == teamA.id) teamA.name else teamB.name
            Text(
                "Toss result: $winnerName won the toss & chose to ${tossDecision.name.lowercase()} first.",
                color = SuccessGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Stepper Overs
            Text(
                "OVERS PER INNINGS",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { if (overs > 1) onOversChanged(overs - 1) },
                    modifier = Modifier.background(CardBlack)
                ) {
                    Icon(Icons.Filled.Remove, "Decrease overs", tint = TextPrimary)
                }
                Text(overs.toString(), color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                IconButton(
                    onClick = { onOversChanged(overs + 1) },
                    modifier = Modifier.background(CardBlack)
                ) {
                    Icon(Icons.Filled.Add, "Increase overs", tint = TextPrimary)
                }
            }

            // Presets
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(5, 10, 20, 50).forEach { preset ->
                    val isSel = overs == preset
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) SuccessGreen else CardBlack)
                            .clickable { onOversChanged(preset) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(preset.toString(), color = if (isSel) Color.White else TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Stepper Max Overs Bowler
            Text(
                "MAX OVERS PER BOWLER",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { if (maxOversPerBowler > 1) onMaxOversChanged(maxOversPerBowler - 1) },
                    modifier = Modifier.background(CardBlack)
                ) {
                    Icon(Icons.Filled.Remove, "Decrease max", tint = TextPrimary)
                }
                Text(maxOversPerBowler.toString(), color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = { onMaxOversChanged(maxOversPerBowler + 1) },
                    modifier = Modifier.background(CardBlack)
                ) {
                    Icon(Icons.Filled.Add, "Increase max", tint = TextPrimary)
                }
            }

            // Gully rules
            Text(
                "GULLY CRICKET RULES",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBlack)
                    .clickable { onLastManStandingChanged(!lastManStanding) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = lastManStanding,
                    onCheckedChange = { onLastManStandingChanged(it) },
                    colors = CheckboxDefaults.colors(checkedColor = SuccessGreen)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Last Man Standing", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Last batter bats alone without a partner end-innings", color = TextMuted, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Start CTA
            Button(
                onClick = onStartMatch,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("full_match_setup_start")
            ) {
                Text("Start Full Match", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpeningSelectionScreen(
    battingPlayers: List<Player>,
    bowlingPlayers: List<Player>,
    onConfirm: (strikerId: String, nonStrikerId: String, bowlerId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var strikerId by remember { mutableStateOf<String?>(null) }
    var nonStrikerId by remember { mutableStateOf<String?>(null) }
    var bowlerId by remember { mutableStateOf<String?>(null) }

    var showStrikerPicker by remember { mutableStateOf(false) }
    var showNonStrikerPicker by remember { mutableStateOf(false) }
    var showBowlerPicker by remember { mutableStateOf(false) }

    val striker = battingPlayers.find { it.id == strikerId }
    val nonStriker = battingPlayers.find { it.id == nonStrikerId }
    val bowler = bowlingPlayers.find { it.id == bowlerId }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NearBlack)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Who's Opening?", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = NearBlack,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                // Striker
                Column {
                    Text("OPENING STRIKER", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBlack)
                            .clickable { showStrikerPicker = true }
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (striker != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PlayerAvatar(name = striker.name, alias = striker.alias, color = striker.themeColor, size = AvatarSize.SM)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(striker.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text("Select Striker", color = TextMuted)
                        }
                    }
                }

                // Non-striker
                Column {
                    Text("OPENING NON-STRIKER", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBlack)
                            .clickable { showNonStrikerPicker = true }
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (nonStriker != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PlayerAvatar(name = nonStriker.name, alias = nonStriker.alias, color = nonStriker.themeColor, size = AvatarSize.SM)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(nonStriker.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text("Select Non-Striker", color = TextMuted)
                        }
                    }
                }

                // Bowler
                Column {
                    Text("OPENING BOWLER", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBlack)
                            .clickable { showBowlerPicker = true }
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (bowler != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PlayerAvatar(name = bowler.name, alias = bowler.alias, color = bowler.themeColor, size = AvatarSize.SM)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(bowler.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text("Select Opening Bowler", color = TextMuted)
                        }
                    }
                }
            }

            Button(
                onClick = { onConfirm(strikerId!!, nonStrikerId!!, bowlerId!!) },
                enabled = strikerId != null && nonStrikerId != null && bowlerId != null,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("opening_selection_start")
            ) {
                Text("Start Innings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // Selection sheets
        if (showStrikerPicker) {
            PlayerSelectionSheet(
                title = "Select Striker",
                players = battingPlayers,
                excludedId = nonStrikerId,
                onSelected = {
                    strikerId = it
                    showStrikerPicker = false
                },
                onDismiss = { showStrikerPicker = false }
            )
        }

        if (showNonStrikerPicker) {
            PlayerSelectionSheet(
                title = "Select Non-Striker",
                players = battingPlayers,
                excludedId = strikerId,
                onSelected = {
                    nonStrikerId = it
                    showNonStrikerPicker = false
                },
                onDismiss = { showNonStrikerPicker = false }
            )
        }

        if (showBowlerPicker) {
            PlayerSelectionSheet(
                title = "Select Bowler",
                players = bowlingPlayers,
                excludedId = null,
                onSelected = {
                    bowlerId = it
                    showBowlerPicker = false
                },
                onDismiss = { showBowlerPicker = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSelectionSheet(
    title: String,
    players: List<Player>,
    excludedId: String?,
    onSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardBlack,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextMuted) }
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            val select = players.filter { it.id != excludedId }
            if (select.isEmpty()) {
                Text("No other players available.", color = TextMuted)
                Spacer(modifier = Modifier.height(40.dp))
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    select.forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NearBlack)
                                .clickable { onSelected(p.id) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PlayerAvatar(name = p.name, alias = p.alias, color = p.themeColor, size = AvatarSize.SM)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(p.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
