package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MatchResultScreens(
    match: Match,
    teamA: TeamSheet,
    teamB: TeamSheet,
    awards: List<Award>,
    players: List<Player>,
    onViewScorecard: () -> Unit,
    onStartNewMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Total pages = 1 (result summary card) + size of awards list
    val pagerState = rememberPagerState(pageCount = { 1 + awards.size })

    val winnerTeam = if (match.winnerTeamId == teamA.id) teamA else if (match.winnerTeamId == teamB.id) teamB else null
    val resultColor = if (winnerTeam != null) Color(android.graphics.Color.parseColor(winnerTeam.themeColor.hex)) else SlateGray

    Scaffold(
        containerColor = NearBlack,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                if (page == 0) {
                    // Page 1: Main match result celebration
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(resultColor)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = "Trophy Celebration",
                                tint = Color.White,
                                modifier = Modifier.size(96.dp)
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                "CONGRATULATIONS",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = winnerTeam?.name ?: "IT'S A TIE!",
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = match.resultSummary ?: "Match Tied",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Pages 2 to N: Award Cards
                    val award = awards[page - 1]
                    val awardPlayer = players.find { it.id == award.playerId }
                    val playerTeamColor = if (winnerTeam != null && winnerTeam.themeColor != null) winnerTeam.themeColor else ThemeColor.BLUE_OCEAN
                    val awardColor = Color(android.graphics.Color.parseColor(playerTeamColor.hex))

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(awardColor)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getAwardIcon(award.awardType),
                                    contentDescription = award.awardType.displayName(),
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = award.awardType.displayName().uppercase(),
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(32.dp))

                            if (awardPlayer != null) {
                                PlayerAvatar(
                                    name = awardPlayer.name,
                                    alias = awardPlayer.alias,
                                    color = awardPlayer.themeColor,
                                    size = AvatarSize.XL
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = awardPlayer.name,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = award.headlineStat,
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Carousel Bottom Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NearBlack)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indicator dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(1 + awards.size) { index ->
                        val isActive = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(width = if (isActive) 16.dp else 8.dp, height = 8.dp)
                                .clip(CircleShape)
                                .background(if (isActive) SuccessGreen else TextMuted.copy(alpha = 0.3f))
                        )
                    }
                }

                // CTA Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewScorecard,
                        border = BorderStroke(1.dp, SuccessGreen),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Match Stats", color = SuccessGreen, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onStartNewMatch,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("postmatch_next_match_btn")
                    ) {
                        Text("Next Match", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun getAwardIcon(type: AwardType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        AwardType.POTM -> Icons.Filled.EmojiEvents
        AwardType.SUPER_HITTER -> Icons.Filled.Whatshot
        AwardType.WICKET_TAKER -> Icons.Filled.OfflineBolt
        AwardType.CONTAINER -> Icons.Filled.Shield
        AwardType.MOST_BOUNDARIES -> Icons.Filled.TrackChanges
        AwardType.ECONOMY_KING -> Icons.Filled.ShieldMoon
        AwardType.GAME_CHANGER -> Icons.Filled.FlashOn
        AwardType.BEST_PARTNERSHIP -> Icons.Filled.Hub
        AwardType.HIGHEST_SR -> Icons.Filled.Speed
        AwardType.MOST_DOT_BALLS -> Icons.Filled.FiberManualRecord
        AwardType.BEST_FIELDER -> Icons.Filled.FrontHand
    }
}
