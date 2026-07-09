package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.AvatarSize
import com.example.ui.components.PlayerAvatar
import com.example.ui.components.StatChip
import com.example.ui.theme.*
import com.example.ui.viewmodel.LiveMatchState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScoringScreen(
    state: LiveMatchState,
    selectedRuns: Int?,
    selectedExtraType: ExtraType?,
    selectedExtraRuns: Int?,
    onSelectOutcome: (Int?, ExtraType?) -> Unit,
    onConfirmDelivery: () -> Unit,
    onUndo: () -> Unit,
    onManualSwap: () -> Unit,
    onOpenScorecard: () -> Unit,
    onBack: () -> Unit,
    // Wicket state triggers
    wicketDismissal: DismissalType?,
    onWicketDismissalChanged: (DismissalType?) -> Unit,
    wicketFielderId: String?,
    onWicketFielderChanged: (String?) -> Unit,
    wicketDismissedEnd: DismissedEnd,
    onWicketEndChanged: (DismissedEnd) -> Unit,
    nextBatterId: String?,
    onNextBatterChanged: (String?) -> Unit,
    onConfirmWicket: () -> Unit,
    // Next Over Bowler triggers
    showEndOverOverlay: Boolean,
    onDismissEndOver: () -> Unit,
    showBowlerPicker: Boolean,
    onBowlerSelected: (String) -> Unit,
    onChangeBowler: (String) -> Unit,
    // Target / Next Innings triggers
    showTargetDialog: Boolean,
    onStartNextInnings: (strikerId: String, nonStrikerId: String, bowlerId: String) -> Unit,
    // Players lists for sheets
    battingPlayers: List<Player>,
    bowlingPlayers: List<Player>,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var showWicketHowOutSheet by remember { mutableStateOf(false) }
    var showWicketCaughtBySheet by remember { mutableStateOf(false) }
    var showNextBatterSheet by remember { mutableStateOf(false) }
    var showCustomRunsSheet by remember { mutableStateOf(false) }

    var showOpeningSelectionForInnings2 by remember { mutableStateOf(false) }
    var showManualBowlerPicker by remember { mutableStateOf(false) }

    val battingColorHex = Color(android.graphics.Color.parseColor(state.battingColor.hex))
    val bowlingColorHex = Color(android.graphics.Color.parseColor(state.bowlingColor.hex))

    // Automatically trigger end of over overlay timer (1.6s)
    LaunchedEffect(showEndOverOverlay) {
        if (showEndOverOverlay) {
            delay(1600)
            onDismissEndOver()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(NearBlack)) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Zone A - Match Context (top ~55% height)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f)
                    .background(battingColorHex)
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                    Text(
                        text = "LIVE SCORING",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = onOpenScorecard, modifier = Modifier.testTag("live_scorecard_btn")) {
                        Icon(Icons.Filled.BarChart, "Scorecard", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Score Display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(state.battingTeamName.uppercase(), color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = state.score.toString(),
                                color = Color.White,
                                fontSize = 64.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.alignByBaseline()
                            )
                            Text(
                                text = " / ${state.wickets}",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                    }

                    // Stat chips
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatChip(label = "Overs", value = state.oversStr)
                        StatChip(label = "CRR", value = String.format("%.2f", state.crr))
                        StatChip(
                            label = "P'ship", 
                            value = "${state.currentPartnershipRuns} (${state.currentPartnershipBalls})"
                        )
                    }
                }

                // Projected / Required text
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (state.currentInnings?.inningsNumber == 1) {
                        Text(
                            text = "Projected Score: ${state.projectedScore ?: 0}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else if (state.currentInnings?.inningsNumber == 2 && state.runsNeeded != null) {
                        Text(
                            text = "Need ${state.runsNeeded} off ${state.ballsRemaining} balls (RRR: ${String.format("%.2f", state.rrr ?: 0.0)})",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Batsman Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Striker Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.5.dp, Color.White, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(state.strikerName, color = Color.White, fontWeight = FontWeight.ExtraBold, maxLines = 1)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${state.strikerRuns} runs off ${state.strikerBalls} balls", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                    }

                    // Swap button
                    IconButton(
                        onClick = onManualSwap,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Filled.SwapHoriz, "Swap strike", tint = Color.White)
                    }

                    // Non Striker Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(state.nonStrikerName, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold, maxLines = 1)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${state.nonStrikerRuns} runs off ${state.nonStrikerBalls} balls", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bowling strip (in bowling team color)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(bowlingColorHex)
                        .clickable { showManualBowlerPicker = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("BOWLER", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Select bowler",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(state.bowlerName, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("${state.bowlerWickets} - ${state.bowlerRuns} (${state.bowlerOvers} ov)", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Over Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.2f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("OVER ${state.currentOverNumber}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        state.currentOverBalls.forEach { ball ->
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (ball.isWicket) DismissalRed
                                        else if (ball.runsOffBat == 6) SixPurple
                                        else if (ball.runsOffBat == 4) BoundaryGreen
                                        else if (ball.extraType != null) WarningExtras
                                        else DotGray
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (ball.isWicket) "W" 
                                           else if (ball.extraType == ExtraType.WIDE) "Wd"
                                           else if (ball.extraType == ExtraType.NO_BALL) "Nb"
                                           else ball.runsOffBat.toString(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Hollow placeholders for remaining legal balls
                        val completedLegal = state.currentOverBalls.count { it.isLegalDelivery }
                        val remaining = maxOf(0, 6 - completedLegal)
                        repeat(remaining) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            )
                        }
                    }
                }
            }

            // Zone B - Ball Input (bottom ~45% height)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .background(NearBlack)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Extras Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(ExtraType.WIDE, ExtraType.NO_BALL, ExtraType.BYE, ExtraType.LEG_BYE).forEach { extra ->
                            val isSel = selectedExtraType == extra
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) WarningExtras else CardBlack)
                                    .clickable {
                                        val newType = if (isSel) null else extra
                                        onSelectOutcome(selectedRuns ?: 0, newType)
                                    }
                                    .border(1.dp, if (isSel) WarningExtras else Color.Transparent, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = extra.name.replace("_", " ").lowercase(),
                                    color = if (isSel) Color.White else WarningExtras,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // Input Grid (Wicket on Left, runs on right)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Wicket Button (Spans vertically)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(108.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.5.dp, DismissalRed, RoundedCornerShape(12.dp))
                                .clickable {
                                    // Trigger wicket selection flow
                                    showWicketHowOutSheet = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Cancel, "Wicket", tint = DismissalRed, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("WICKET", color = DismissalRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        // Runs Numbers Grid (4 cols x 2 rows: 0,1,2,3 / 4,5,6,...)
                        Column(
                            modifier = Modifier.weight(3f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(0, 1, 2, 3).forEach { r ->
                                    val isSel = selectedRuns == r && selectedExtraType == null
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSel) SuccessGreen else CardBlack)
                                            .clickable { onSelectOutcome(r, selectedExtraType) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (r == 0) "DOT" else r.toString(),
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(4, 5, 6).forEach { r ->
                                    val isSel = selectedRuns == r && selectedExtraType == null
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSel) SuccessGreen
                                                else if (r == 4) BoundaryGreen.copy(alpha = 0.2f)
                                                else if (r == 6) SixPurple.copy(alpha = 0.2f)
                                                else CardBlack
                                            )
                                            .clickable { onSelectOutcome(r, selectedExtraType) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = r.toString(),
                                            color = if (r == 4) BoundaryGreen else if (r == 6) SixPurple else Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }

                                // More option "···"
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CardBlack)
                                        .clickable { showCustomRunsSheet = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("···", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }

                // Action Buttons (Undo and Next Ball)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onUndo,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(CardBlack)
                    ) {
                        Icon(Icons.Filled.Undo, "Undo", tint = TextPrimary)
                    }

                    Button(
                        onClick = onConfirmDelivery,
                        enabled = selectedRuns != null || selectedExtraType != null,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("live_confirm_ball_btn")
                    ) {
                        Text("Confirm Delivery", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        // Overlay 1: End of over Recaps
        if (showEndOverOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepGreen),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("END OF OVER", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("RUNS", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                                Text(state.overRuns.toString(), color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("WICKETS", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                                Text(state.currentOverBalls.count { it.isWicket }.toString(), color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Overlay 2: Bowler Picker Bottom Sheet (at End of Over)
        if (showBowlerPicker && !showEndOverOverlay) {
            NextBowlerPickerSheet(
                bowlers = bowlingPlayers,
                currentBowlerId = state.bowlerId,
                maxOvers = state.match.maxOversPerBowler,
                onSelected = onBowlerSelected,
                onDismiss = {}
            )
        }

        // Overlay 2b: Manual Bowler Picker Bottom Sheet (at any time)
        if (showManualBowlerPicker) {
            NextBowlerPickerSheet(
                bowlers = bowlingPlayers,
                currentBowlerId = state.bowlerId,
                maxOvers = state.match.maxOversPerBowler,
                onSelected = {
                    onChangeBowler(it)
                    showManualBowlerPicker = false
                },
                onDismiss = { showManualBowlerPicker = false }
            )
        }

        // Overlay 3: Target Dialog Sheet (End of Innings 1)
        if (showTargetDialog && !showOpeningSelectionForInnings2) {
            TargetDialogSheet(
                target = state.score + 1,
                onStartInnings2 = { showOpeningSelectionForInnings2 = true },
                onDismiss = {}
            )
        }

        // Wicket Step 1 Sheet: How Out?
        if (showWicketHowOutSheet) {
            WicketHowOutSheet(
                onHowOutChosen = {
                    onWicketDismissalChanged(it)
                    showWicketHowOutSheet = false
                    if (it == DismissalType.CAUGHT) {
                        showWicketCaughtBySheet = true
                    } else {
                        showNextBatterSheet = true
                    }
                },
                onDismiss = { showWicketHowOutSheet = false }
            )
        }

        // Wicket Step 2 Sheet: Caught By?
        if (showWicketCaughtBySheet) {
            WicketCaughtBySheet(
                fielders = bowlingPlayers,
                onFielderChosen = {
                    onWicketFielderChanged(it)
                    showWicketCaughtBySheet = false
                    showNextBatterSheet = true
                },
                onDismiss = { showWicketCaughtBySheet = false }
            )
        }

        // Wicket Step 3 Sheet: Choose Next Batter
        if (showNextBatterSheet) {
            val alreadyOutIds = state.firstInningsScorecard.filter { it.isOut }.map { it.playerId }
            val currentBatsmen = listOfNotNull(state.strikerId, state.nonStrikerId)
            val selectableBatter = battingPlayers.filter { it.id !in alreadyOutIds && it.id !in currentBatsmen }

            if (selectableBatter.isEmpty()) {
                // Roster fully used, LMS or allout
                LaunchedEffect(Unit) {
                    onConfirmWicket()
                    showNextBatterSheet = false
                }
            } else {
                WicketNextBatterSheet(
                    players = selectableBatter,
                    onSelected = {
                        onNextBatterChanged(it)
                        onConfirmWicket()
                        showNextBatterSheet = false
                    },
                    onDismiss = { showNextBatterSheet = false }
                )
            }
        }

        // Custom runs off this ball sheet (7-12)
        if (showCustomRunsSheet) {
            CustomRunsSheet(
                onSelected = {
                    onSelectOutcome(it, selectedExtraType)
                    showCustomRunsSheet = false
                },
                onDismiss = { showCustomRunsSheet = false }
            )
        }

        // Opening Selection for Innings 2 overlay
        if (showOpeningSelectionForInnings2) {
            OpeningSelectionScreen(
                battingPlayers = bowlingPlayers, // roles swap
                bowlingPlayers = battingPlayers,
                onConfirm = { s, ns, b ->
                    onStartNextInnings(s, ns, b)
                    showOpeningSelectionForInnings2 = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WicketHowOutSheet(
    onHowOutChosen: (DismissalType) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = CardBlack) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("How Out?", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DismissalType.values().forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NearBlack)
                            .clickable { onHowOutChosen(type) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(type.displayName(), color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WicketCaughtBySheet(
    fielders: List<Player>,
    onFielderChosen: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = CardBlack) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Caught By?", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                fielders.forEach { p ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NearBlack)
                            .clickable { onFielderChosen(p.id) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlayerAvatar(name = p.name, alias = p.alias, color = p.themeColor, size = AvatarSize.SM)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(p.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WicketNextBatterSheet(
    players: List<Player>,
    onSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = CardBlack) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Next Batter?", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                players.forEach { p ->
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
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NextBowlerPickerSheet(
    bowlers: List<Player>,
    currentBowlerId: String?,
    maxOvers: Int,
    onSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = CardBlack, dragHandle = null) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Next Over — Pick Bowler", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Enforce Back-to-Back bowlers selection rules
                val selectable = bowlers.filter { it.id != currentBowlerId }
                selectable.forEach { b ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NearBlack)
                            .clickable { onSelected(b.id) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlayerAvatar(name = b.name, alias = b.alias, color = b.themeColor, size = AvatarSize.SM)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(b.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetDialogSheet(
    target: Int,
    onStartInnings2: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = CardBlack, dragHandle = null) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("First Innings Completed!", color = TextMuted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("TARGET IS $target RUNS", color = SuccessGreen, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStartInnings2,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Start Next Innings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRunsSheet(
    onSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = CardBlack) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Runs off this ball", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(7, 8, 9, 10, 11, 12).forEach { r ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NearBlack)
                            .clickable { onSelected(r) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(r.toString(), color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
