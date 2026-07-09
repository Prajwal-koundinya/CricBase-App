package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Match
import com.example.data.ThemeColor
import com.example.ui.components.AvatarSize
import com.example.ui.components.PlayerAvatar
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    hasPlayers: Boolean,
    activeMatch: Match?,
    onStartMatch: () -> Unit,
    onResumeMatch: (String) -> Unit,
    onPastMatches: () -> Unit,
    onSquadBook: () -> Unit,
    onPlayerProfiles: () -> Unit,
    onStatistics: () -> Unit,
    onSettings: () -> Unit,
    onQuickBallClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!hasPlayers) {
        HomeFirstRunEmptyState(onQuickBallClick, onSettings, modifier)
    } else {
        HomeReturningDashboard(
            activeMatch = activeMatch,
            onStartMatch = onStartMatch,
            onResumeMatch = onResumeMatch,
            onPastMatches = onPastMatches,
            onSquadBook = onSquadBook,
            onPlayerProfiles = onPlayerProfiles,
            onStatistics = onStatistics,
            onSettings = onSettings,
            modifier = modifier
        )
    }
}

@Composable
fun HomeFirstRunEmptyState(
    onQuickBallClick: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val frostedBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFD0BCFF), Color(0xFFEADDFF))
    )
    val textPurple = Color(0xFF21005D)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(frostedBrush)
            .padding(24.dp)
    ) {
        // Settings & Audio Icons
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSettings) {
                Icon(Icons.Filled.Settings, "Settings", tint = textPurple)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Filled.VolumeUp, "Sound", tint = textPurple)
            }
        }

        // Center Ball CTA
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.SportsCricket,
                contentDescription = "CricBase",
                tint = textPurple.copy(alpha = 0.3f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            // Large circular cricket ball button
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onQuickBallClick() }
                    .testTag("home_quick_ball_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Start Match",
                    tint = SuccessGreen,
                    modifier = Modifier.size(72.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Tap 👆 to start your First Match",
                color = textPurple,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Set up your players and teams to track live scores",
                color = textPurple.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
fun HomeReturningDashboard(
    activeMatch: Match?,
    onStartMatch: () -> Unit,
    onResumeMatch: (String) -> Unit,
    onPastMatches: () -> Unit,
    onSquadBook: () -> Unit,
    onPlayerProfiles: () -> Unit,
    onStatistics: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NearBlack)
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.SportsCricket, "CricBase", tint = SuccessGreen, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CRICBASE",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            IconButton(onClick = onSettings, modifier = Modifier.testTag("home_settings_btn")) {
                Icon(Icons.Filled.Settings, "Settings", tint = TextPrimary)
            }
        }

        // Quick Resume Card (if match active)
        if (activeMatch != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, SuccessGreen, RoundedCornerShape(16.dp))
                    .clickable { onResumeMatch(activeMatch.id) }
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LIVE MATCH IN PROGRESS",
                            color = SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = activeMatch.name,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Overs: ${activeMatch.totalOvers} · format: ${activeMatch.ballType.displayName()} ball",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.PlayCircle,
                        contentDescription = "Resume",
                        tint = SuccessGreen,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        // Primary Grid Options
        Text(
            text = "ACTIONS",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        DashboardActionRow(
            icon = Icons.Filled.PlayArrow,
            iconBg = SuccessGreen,
            title = "Start Match",
            subtitle = "Launch a new scored innings wizard",
            onClick = onStartMatch,
            tag = "home_start_match_btn"
        )
        Spacer(modifier = Modifier.height(12.dp))
        DashboardActionRow(
            icon = Icons.Filled.Group,
            iconBg = BlueOcean,
            title = "Squad Book",
            subtitle = "Manage global players and team sheets",
            onClick = onSquadBook,
            tag = "home_squad_book_btn"
        )
        Spacer(modifier = Modifier.height(12.dp))
        DashboardActionRow(
            icon = Icons.Filled.Person,
            iconBg = SunsetOrange,
            title = "Player Profiles",
            subtitle = "See individual career statistics",
            onClick = onPlayerProfiles,
            tag = "home_player_profiles_btn"
        )
        Spacer(modifier = Modifier.height(12.dp))
        DashboardActionRow(
            icon = Icons.Filled.History,
            iconBg = RoyalPurple,
            title = "Past Matches",
            subtitle = "Browse completed match scorecards",
            onClick = onPastMatches,
            tag = "home_past_matches_btn"
        )
        Spacer(modifier = Modifier.height(12.dp))
        DashboardActionRow(
            icon = Icons.Filled.BarChart,
            iconBg = CrimsonRed,
            title = "Statistics",
            subtitle = "Global trends and analytics dashboard",
            onClick = onStatistics,
            tag = "home_statistics_btn"
        )
    }
}

@Composable
fun DashboardActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tag: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBlack),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconBg)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = TextMuted, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Filled.ChevronRight, "Open", tint = TextMuted)
        }
    }
}
