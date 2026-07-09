package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.AvatarSize
import com.example.ui.components.PlayerAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.BatterScorecard
import com.example.ui.viewmodel.BowlerScorecard
import com.example.ui.viewmodel.LiveMatchState

@Composable
fun ScorecardScreen(
    state: LiveMatchState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Team A, 1 = Team B, 2 = Stats, 3 = Over by Over

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepGreen)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SCORECARD",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
        },
        containerColor = NearBlack,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Underline Tabs (Team A / Team B / Stats / Over by Over)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DeepGreen,
                contentColor = SuccessGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = SuccessGreen
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(state.teamAName, fontWeight = FontWeight.Bold, maxLines = 1) },
                    selectedContentColor = SuccessGreen,
                    unselectedContentColor = Color.White.copy(alpha = 0.6f)
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(state.teamBName, fontWeight = FontWeight.Bold, maxLines = 1) },
                    selectedContentColor = SuccessGreen,
                    unselectedContentColor = Color.White.copy(alpha = 0.6f)
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Stats", fontWeight = FontWeight.Bold) },
                    selectedContentColor = SuccessGreen,
                    unselectedContentColor = Color.White.copy(alpha = 0.6f)
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Overs", fontWeight = FontWeight.Bold) },
                    selectedContentColor = SuccessGreen,
                    unselectedContentColor = Color.White.copy(alpha = 0.6f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> TeamScorecardTab(
                        teamName = state.teamAName,
                        batters = state.firstInningsScorecard,
                        bowlers = state.firstInningsBowling,
                        color = BlueOcean
                    )
                    1 -> TeamScorecardTab(
                        teamName = state.teamBName,
                        batters = state.secondInningsScorecard,
                        bowlers = state.secondInningsBowling,
                        color = CrimsonRed
                    )
                    2 -> ScorecardStatsTab(state)
                    3 -> OverByOverTab(state)
                }
            }
        }
    }
}

@Composable
fun TeamScorecardTab(
    teamName: String,
    batters: List<BatterScorecard>,
    bowlers: List<BowlerScorecard>,
    color: Color
) {
    // Header summary card
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(teamName.uppercase(), color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            val runs = batters.sumOf { it.runs }
            val wickets = batters.count { it.isOut }
            val overs = bowlers.sumOf { (it.overs * 10).toInt() } / 10 // approximate
            Text(
                text = "$runs / $wickets (${overs} overs)",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }

    // Batting Header row
    Text(
        text = "BATTING",
        color = TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    // Batters table
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBlack),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Table columns header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Batter", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                Text("R", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                Text("B", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                Text("4s", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                Text("6s", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                Text("SR", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
            }

            Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(bottom = 8.dp))

            if (batters.isEmpty()) {
                Text("No batting statistics logged yet.", color = TextMuted, fontSize = 13.sp, modifier = Modifier.padding(vertical = 12.dp))
            } else {
                batters.forEach { b ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(2f)) {
                            Text(b.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(b.dismissalText, color = TextMuted, fontSize = 11.sp)
                        }
                        Text(b.runs.toString(), color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                        Text(b.balls.toString(), color = TextMuted, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                        Text(b.fours.toString(), color = TextMuted, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                        Text(b.sixes.toString(), color = TextMuted, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                        Text(String.format("%.1f", b.strikeRate), color = TextPrimary, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                    }
                }
            }
        }
    }

    // Bowling Header Row
    Text(
        text = "BOWLING",
        color = TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    // Bowlers table
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBlack),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Bowler", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                Text("O", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                Text("R", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                Text("W", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                Text("Econ", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            }

            Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(bottom = 8.dp))

            if (bowlers.isEmpty()) {
                Text("No bowling statistics logged yet.", color = TextMuted, fontSize = 13.sp, modifier = Modifier.padding(vertical = 12.dp))
            } else {
                bowlers.forEach { bl ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(bl.name, color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f), maxLines = 1)
                        Text(String.format("%.1f", bl.overs), color = TextPrimary, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                        Text(bl.runs.toString(), color = TextPrimary, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                        Text(bl.wickets.toString(), color = SuccessGreen, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                        Text(String.format("%.2f", bl.economy), color = TextPrimary, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                }
            }
        }
    }
}

@Composable
fun ScorecardStatsTab(state: LiveMatchState) {
    // Worm Progression Chart drawing
    Text(
        text = "SCORE PROGRESSION (WORM)",
        color = TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBlack),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Interactive simulated or drawn graph
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(NearBlack)
                    .border(1.dp, Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    val width = size.width
                    val height = size.height

                    // Background gridlines
                    for (i in 1..4) {
                        val y = height * (i / 4f)
                        drawLine(
                            color = Color.White.copy(alpha = 0.1f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Drawn progression lines
                    val path1 = Path().apply {
                        moveTo(0f, height)
                        lineTo(width * 0.25f, height * 0.8f)
                        lineTo(width * 0.5f, height * 0.6f)
                        lineTo(width * 0.75f, height * 0.3f)
                        lineTo(width, height * 0.1f)
                    }
                    drawPath(
                        path = path1,
                        color = BlueOcean,
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Wicket markers for team 1 (blue)
                    drawCircle(color = DismissalRed, radius = 5.dp.toPx(), center = Offset(width * 0.5f, height * 0.6f))

                    // Second innings path (if started)
                    if (state.currentInnings?.inningsNumber == 2 || state.match.status == MatchStatus.COMPLETED) {
                        val path2 = Path().apply {
                            moveTo(0f, height)
                            lineTo(width * 0.25f, height * 0.85f)
                            lineTo(width * 0.5f, height * 0.7f)
                            lineTo(width * 0.75f, height * 0.45f)
                            lineTo(width * 0.9f, height * 0.2f)
                        }
                        drawPath(
                            path = path2,
                            color = CrimsonRed,
                            style = Stroke(width = 3.dp.toPx())
                        )
                        // wicket for team 2
                        drawCircle(color = DismissalRed, radius = 5.dp.toPx(), center = Offset(width * 0.75f, height * 0.45f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(BlueOcean))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(state.teamAName, color = TextPrimary, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(CrimsonRed))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(state.teamBName, color = TextPrimary, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(DismissalRed))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Wicket", color = TextPrimary, fontSize = 12.sp)
                }
            }
        }
    }

    // Over by over comparison bar chart
    Text(
        text = "OVER RUNS COMPARISON",
        color = TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBlack),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(NearBlack)
                    .border(1.dp, Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    val width = size.width
                    val height = size.height

                    // Draw 4 sample overs
                    val overWidth = width / 4f
                    val barWidth = overWidth * 0.3f

                    for (o in 0..3) {
                        val x = o * overWidth + overWidth * 0.15f
                        
                        // Team 1 bar (Blue)
                        val t1Runs = listOf(8, 12, 18, 15).getOrElse(o) { 5 }
                        val t1Height = height * (t1Runs / 24f)
                        drawRect(
                            color = BlueOcean,
                            topLeft = Offset(x, height - t1Height),
                            size = androidx.compose.ui.geometry.Size(barWidth, t1Height)
                        )

                        // Team 2 bar (Crimson)
                        val t2Runs = listOf(6, 14, 10, 11).getOrElse(o) { 5 }
                        val t2Height = height * (t2Runs / 24f)
                        drawRect(
                            color = CrimsonRed,
                            topLeft = Offset(x + barWidth + 4.dp.toPx(), height - t2Height),
                            size = androidx.compose.ui.geometry.Size(barWidth, t2Height)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OverByOverTab(state: LiveMatchState) {
    Text(
        text = "OVER BY OVER REPLAY",
        color = TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    if (state.completedOversList.isEmpty()) {
        Text("No overs completed yet in this match.", color = TextMuted, fontSize = 14.sp)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Display completed overs in reverse order (newest at top)
            state.completedOversList.asReversed().forEach { over ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBlack),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Over ${over.overNumber}", color = TextPrimary, fontWeight = FontWeight.ExtraBold)
                            Text("Bowler: ${over.bowlerName}", color = TextMuted, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val wicketStr = if (over.wickets == 1) "1 wicket" else "${over.wickets} wickets"
                            Text(
                                text = "${over.runsConceded} runs · $wicketStr",
                                color = if (over.wickets > 0) DismissalRed else SuccessGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            over.balls.forEach { b ->
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (b.isWicket) DismissalRed
                                            else if (b.runsOffBat == 6) SixPurple
                                            else if (b.runsOffBat == 4) BoundaryGreen
                                            else if (b.extraType != null) WarningExtras
                                            else DotGray
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (b.isWicket) "W" 
                                               else if (b.extraType == ExtraType.WIDE) "Wd"
                                               else if (b.extraType == ExtraType.NO_BALL) "Nb"
                                               else b.runsOffBat.toString(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
