package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.AvatarSize
import com.example.ui.components.CommonEmptyState
import com.example.ui.components.PlayerAvatar
import com.example.ui.theme.*

@Composable
fun MatchHistoryScreen(
    matches: List<Match>,
    teamSheets: List<TeamSheet>,
    onSelectMatch: (String) -> Unit,
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
                Text("Match History", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = NearBlack,
        modifier = modifier
    ) { paddingValues ->
        if (matches.isEmpty()) {
            CommonEmptyState(
                title = "No Matches Yet",
                description = "Create your first match from the home dashboard to track details.",
                icon = { Icon(Icons.Filled.History, "No matches", tint = TextMuted, modifier = Modifier.size(36.dp)) }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                matches.forEach { match ->
                    val teamA = teamSheets.find { it.id == match.teamAId }
                    val teamB = teamSheets.find { it.id == match.teamBId }

                    val borderCol = if (teamA != null) Color(android.graphics.Color.parseColor(teamA.themeColor.hex)) else SuccessGreen

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBlack),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, borderCol.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable { onSelectMatch(match.id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = match.name,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1
                                )
                                Icon(Icons.Filled.EmojiEvents, "Winner", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Vs: ${teamA?.name ?: "Team A"} & ${teamB?.name ?: "Team B"}",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = match.resultSummary ?: "Match completed",
                                color = SuccessGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerProfilesListScreen(
    players: List<Player>,
    onSelectPlayer: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

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
                Text("Player Profiles", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = NearBlack,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Instant Search TextField
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Players...") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, "Search") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SuccessGreen,
                    focusedLabelColor = SuccessGreen,
                    unfocusedBorderColor = CardBlack,
                    unfocusedContainerColor = CardBlack,
                    focusedContainerColor = CardBlack
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            val filteredPlayers = players.filter {
                it.name.contains(searchQuery, ignoreCase = true) || 
                (it.alias?.contains(searchQuery, ignoreCase = true) == true)
            }

            if (filteredPlayers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No results for \"$searchQuery\"", color = TextMuted)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filteredPlayers.forEach { player ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardBlack),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectPlayer(player.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PlayerAvatar(name = player.name, alias = player.alias, color = player.themeColor, size = AvatarSize.MD)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(player.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    val styleStr = listOfNotNull(
                                        player.battingHand?.let { if (it == BattingHand.RIGHT) "RHB" else "LHB" },
                                        player.bowlingStyle?.displayName()
                                    ).joinToString(" · ")
                                    Text(styleStr, color = TextMuted, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Filled.ChevronRight, "View profile", tint = TextMuted)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerProfileDetailScreen(
    player: Player,
    careerStats: PlayerCareerStats,
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
                Text("Career Profile", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Centered Large initials avatar
            PlayerAvatar(name = player.name, alias = player.alias, color = player.themeColor, size = AvatarSize.XL)
            Spacer(modifier = Modifier.height(16.dp))
            Text(player.name, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            if (!player.alias.isNullOrBlank()) {
                Text("\"${player.alias}\"", color = SuccessGreen, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                player.battingHand?.let {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(if (it == BattingHand.RIGHT) "Right-hand bat" else "Left-hand bat") },
                        colors = SuggestionChipDefaults.suggestionChipColors(labelColor = TextMuted)
                    )
                }
                player.bowlingStyle?.let {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(it.displayName()) },
                        colors = SuggestionChipDefaults.suggestionChipColors(labelColor = TextMuted)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Real stats tile grid
            Text(
                "CAREER SUMMARY",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile("Matches", careerStats.matchesPlayed.toString(), modifier = Modifier.weight(1f))
                    StatTile("Runs", careerStats.runsScored.toString(), modifier = Modifier.weight(1f))
                    StatTile("Average", if (careerStats.matchesPlayed > 0) String.format("%.1f", careerStats.average) else "0.0", modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile("Wickets", careerStats.wicketsTaken.toString(), modifier = Modifier.weight(1f))
                    StatTile("Econ", if (careerStats.economyRate > 0) String.format("%.2f", careerStats.economyRate) else "0.00", modifier = Modifier.weight(1f))
                    StatTile("Strike Rate", if (careerStats.strikeRate > 0) String.format("%.1f", careerStats.strikeRate) else "0.0", modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Recent games list
            Text(
                "RECENT PERFORMANCES",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBlack),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (careerStats.recentPerformances.isEmpty()) {
                    Box(modifier = Modifier.padding(16.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No performances recorded yet", color = TextMuted, fontSize = 14.sp)
                    }
                } else {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        careerStats.recentPerformances.forEach { perf ->
                            RecentPerfRow(perf, "Completed")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBlack),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, color = TextMuted, fontSize = 11.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RecentPerfRow(summary: String, date: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(summary, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(date, color = TextMuted, fontSize = 12.sp)
    }
}

@Composable
fun GlobalStatsDashboard(
    matches: List<Match>,
    players: List<Player>,
    leaderboard: List<LeaderboardEntry>,
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
                Text("Global Analytics", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = NearBlack,
        modifier = modifier
    ) { paddingValues ->
        if (matches.isEmpty() || leaderboard.isEmpty()) {
            CommonEmptyState(
                title = "No Statistics",
                description = "Play your first match to unlock comprehensive career analytics.",
                icon = { Icon(Icons.Filled.BarChart, "No Stats", tint = TextMuted, modifier = Modifier.size(36.dp)) }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    "SQUAD LEADERBOARD",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Render top stats leaders
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBlack),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        leaderboard.take(10).forEachIndexed { index, entry ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    (index + 1).toString(),
                                    color = if (index == 0) SuccessGreen else TextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = if (index == 0) 18.sp else 16.sp,
                                    modifier = Modifier.width(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(entry.player.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(1f))
                                Text("${entry.runs} runs · ${entry.wickets} wkts", color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    soundEnabled: Boolean,
    onSoundToggled: (Boolean) -> Unit,
    hapticEnabled: Boolean,
    onHapticToggled: (Boolean) -> Unit,
    defaultOvers: Int,
    onDefaultOversChanged: (Int) -> Unit,
    onClearAllData: () -> Unit,
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
                Text("Settings", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
            // Group 1: General Audio / Haptic controls
            Text(
                "PREFERENCES",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Sound Effects", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Celebratory ascents for boundaries and wickets", color = TextMuted, fontSize = 12.sp)
                }
                Switch(checked = soundEnabled, onCheckedChange = onSoundToggled, colors = SwitchDefaults.colors(checkedThumbColor = SuccessGreen))
            }

            Divider(color = Color.White.copy(alpha = 0.05f))

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Haptic Feedback", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Sensory vibrations for confirmed deliveries", color = TextMuted, fontSize = 12.sp)
                }
                Switch(checked = hapticEnabled, onCheckedChange = onHapticToggled, colors = SwitchDefaults.colors(checkedThumbColor = SuccessGreen))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Default format preset selection
            Text(
                "DEFAULT MATCH FORMAT",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(4, 5, 10, 20).forEach { o ->
                    val isSel = defaultOvers == o
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) SuccessGreen else CardBlack)
                            .clickable { onDefaultOversChanged(o) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$o Overs",
                            color = if (isSel) Color.White else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Data settings (destructive action)
            Text(
                "DATA & PRIVACY",
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
                    Text("Reset Everything", color = DismissalRed, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Completely clear all saved players, team sheets, match histories, and analytics. This action is offline and irreversible.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onClearAllData,
                        colors = ButtonDefaults.buttonColors(containerColor = DismissalRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp).testTag("clear_all_data_btn")
                    ) {
                        Text("Clear All Offline Data", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                "DEVELOPER & SUPPORT",
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Code,
                            contentDescription = "Developer",
                            tint = SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Application Developer",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = "Prajwal Koundinya (let's gooo)",
                        color = SuccessGreen,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 28.dp, top = 4.dp, bottom = 12.dp)
                    )

                    Divider(color = Color.White.copy(alpha = 0.05f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = "Contact",
                            tint = SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Contact & Feedback",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = "prajwalkowndinya@gmail.com for future improvements and queries.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(start = 28.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}
