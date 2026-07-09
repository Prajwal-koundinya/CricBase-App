package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ThemeColor
import com.example.ui.components.AvatarSize
import com.example.ui.components.PlayerAvatar
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NearBlack)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SportsCricket,
                contentDescription = "CricBase Icon",
                tint = SuccessGreen,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Welcome to CRICBASE",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Score fast. Play more.",
                color = SuccessGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            FeatureRow(
                imageVector = Icons.Filled.FlashOn,
                title = "Lightning Fast Scoring",
                description = "Score your gully matches in seconds with single-tap input grids."
            )
            FeatureRow(
                imageVector = Icons.Filled.CloudUpload,
                title = "Auto Backup",
                description = "Never lose your match statistics and player profiles with fully local persistence."
            )
            FeatureRow(
                imageVector = Icons.Filled.Share,
                title = "Share Live Scores",
                description = "Let friends follow your local games live via custom shareable links."
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Text(
                text = "Before you start, let's be clear about how we handle your data.",
                color = TextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("welcome_continue_btn")
            ) {
                Text(
                    text = "Continue",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun PrivacyScreen(
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NearBlack)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(top = 24.dp)) {
            Text(
                text = "Only what's needed to improve your experience.",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // We Collect Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBlack),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SuccessGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, "We Collect", tint = SuccessGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("We Collect", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    PrivacyBullet("Match statistics", "Scores, overs, wickets — to render history cards and awards.")
                    PrivacyBullet("App info", "Version & build — to debug and fix app crashes quickly.")
                    PrivacyBullet("Device type", "Model & platform — to optimize performance and layouts.")
                    PrivacyBullet("General location", "Country/city from IP — to understand where we're used.")
                }
            }

            // We NEVER Collect Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBlack),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DismissalRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Cancel, "We Never Collect", tint = DismissalRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("We NEVER Collect", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    PrivacyBullet("Your name or identity", "You're fully anonymous to us. We collect zero profile data.")
                    PrivacyBullet("Exact GPS location", "We do not track where you are playing or going.")
                    PrivacyBullet("Contacts or photos", "We never request access to private media or local files.")
                    PrivacyBullet("App history", "What you do outside GULLY CRIX stays entirely on your phone.")
                }
            }
        }

        Button(
            onClick = onAccept,
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = 24.dp, bottom = 12.dp)
                .testTag("privacy_accept_btn")
        ) {
            Text(
                text = "Accept & Continue",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun HowItWorksScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NearBlack)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Text(
                text = "Built for the middle of a match.",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No typing mid-over. Fully offline. Multi-step confirmations preserve score integrity instantly.",
                color = TextMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(CardBlack)
                .border(2.dp, SuccessGreen.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.OfflineBolt,
                    contentDescription = "Offline capability",
                    tint = SuccessGreen,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "100% Offline Scoring",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "No connectivity needed",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 24.dp)
                .testTag("howitworks_continue_btn")
        ) {
            Text(
                text = "Let's Play",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SquadBookIntroScreen(
    onSkip: () -> Unit,
    onAddPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NearBlack)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onSkip) {
                Text("Skip", color = TextMuted, fontWeight = FontWeight.Bold)
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> IntroSlide(
                    avatar = {
                        PlayerAvatar(name = "Chikoo", color = ThemeColor.BLUE_OCEAN, size = AvatarSize.MD)
                    },
                    title = "Add your players",
                    description = "Create each player once — give them a profile colour, batting hand and bowling style. No photographs needed!"
                )
                1 -> IntroSlide(
                    avatar = {
                        Icon(Icons.Filled.Group, "Team sheets", tint = SuccessGreen, modifier = Modifier.size(48.dp))
                    },
                    title = "Group them into Team Sheets",
                    description = "A team sheet is a reusable team (name + colour + players). You'll pick two when you start a match."
                )
                2 -> IntroSlide(
                    avatar = {
                        Icon(Icons.Filled.Sports, "Captain and keeper", tint = SuccessGreen, modifier = Modifier.size(48.dp))
                    },
                    title = "Captain, keeper & shared players",
                    description = "Tag the wicket-keeper and captain. The same player can feature for more than one team — no need to duplicate them."
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Page Indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                repeat(3) { index ->
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

            // CTA Button
            val isLastPage = pagerState.currentPage == 2
            Button(
                onClick = {
                    if (isLastPage) {
                        onAddPlayer()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 12.dp)
                    .testTag("squad_book_intro_next_btn")
            ) {
                Text(
                    text = if (isLastPage) "Add My First Player" else "Next",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// Small helper Composables
@Composable
private fun FeatureRow(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SuccessGreen.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector, contentDescription = title, tint = SuccessGreen)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, color = TextMuted, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun PrivacyBullet(title: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(TextMuted)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Text(
            description,
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 14.dp, top = 2.dp)
        )
    }
}

@Composable
private fun IntroSlide(
    avatar: @Composable () -> Unit,
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(CardBlack),
            contentAlignment = Alignment.Center
        ) {
            avatar()
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = description,
            color = TextMuted,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}
