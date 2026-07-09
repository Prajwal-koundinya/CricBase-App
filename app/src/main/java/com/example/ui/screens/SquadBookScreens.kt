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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
fun SquadBookDashboard(
    players: List<Player>,
    teamSheets: List<TeamSheet>,
    onAddPlayer: () -> Unit,
    onEditPlayer: (String) -> Unit,
    onAddTeamSheet: () -> Unit,
    onEditTeamSheet: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }

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
                Text("Squad Book", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (selectedTab == 0) onAddPlayer() else onAddTeamSheet() },
                containerColor = SuccessGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("squad_book_fab")
            ) {
                Icon(Icons.Filled.Add, if (selectedTab == 0) "Add Player" else "New Team Sheet")
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
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = NearBlack,
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
                    text = { Text("Players (${players.size})", fontWeight = FontWeight.Bold) },
                    selectedContentColor = SuccessGreen,
                    unselectedContentColor = TextMuted
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Team Sheets (${teamSheets.size})", fontWeight = FontWeight.Bold) },
                    selectedContentColor = SuccessGreen,
                    unselectedContentColor = TextMuted
                )
            }

            if (selectedTab == 0) {
                if (players.isEmpty()) {
                    CommonEmptyState(
                        title = "No players yet",
                        description = "Add players to reuse them across team sheets and matches.",
                        icon = { Icon(Icons.Filled.Person, "No players", tint = TextMuted, modifier = Modifier.size(36.dp)) },
                        actionLabel = "+ Add Player",
                        onAction = onAddPlayer
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        players.forEach { player ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardBlack),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onEditPlayer(player.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PlayerAvatar(name = player.name, alias = player.alias, color = player.themeColor, size = AvatarSize.SM)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(player.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        val sub = listOfNotNull(
                                            player.battingHand?.let { if (it == BattingHand.RIGHT) "Right-hand bat" else "Left-hand bat" },
                                            player.bowlingStyle?.displayName()
                                        ).joinToString(" · ")
                                        Text(sub.ifEmpty { "Stats unset" }, color = TextMuted, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.Filled.Edit, "Edit", tint = TextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(80.dp)) // Avoid Fab overlap
                    }
                }
            } else {
                if (teamSheets.isEmpty()) {
                    CommonEmptyState(
                        title = "No team sheets yet",
                        description = "Group your players into reusable team sheets for matches.",
                        icon = { Icon(Icons.Filled.Group, "No teams", tint = TextMuted, modifier = Modifier.size(36.dp)) },
                        actionLabel = "+ New Team Sheet",
                        onAction = onAddTeamSheet
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        teamSheets.forEach { team ->
                            val colorHex = Color(android.graphics.Color.parseColor(team.themeColor.hex))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardBlack),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, colorHex.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .clickable { onEditTeamSheet(team.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(colorHex)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(team.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text("Colour: ${team.themeColor.displayName()}", color = TextMuted, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.Filled.Edit, "Edit", tint = TextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditPlayerScreen(
    playerId: String?,
    existingPlayer: Player?,
    onSave: (name: String, alias: String?, batting: BattingHand?, bowling: BowlingStyle?, color: ThemeColor) -> Unit,
    onDelete: (() -> Unit)?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(existingPlayer?.name ?: "") }
    var alias by remember { mutableStateOf(existingPlayer?.alias ?: "") }
    var battingHand by remember { mutableStateOf<BattingHand?>(existingPlayer?.battingHand) }
    var bowlingStyle by remember { mutableStateOf<BowlingStyle?>(existingPlayer?.bowlingStyle) }
    var selectedColor by remember { mutableStateOf(existingPlayer?.themeColor ?: ThemeColor.BLUE_OCEAN) }

    var showColorPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NearBlack)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (playerId != null) "Edit Player" else "Add Player",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (playerId != null && onDelete != null) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, "Delete", tint = DismissalRed)
                    }
                }
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
            // Live Avatar Preview - No camera, no photo picker! Just gorgeous colored initials.
            Box(
                modifier = Modifier.padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                PlayerAvatar(name = name.ifBlank { "N" }, alias = alias.ifBlank { null }, color = selectedColor, size = AvatarSize.XL)
            }

            // Input Fields
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Player Name") },
                singleLine = true,
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
                    .testTag("player_name_input")
            )

            OutlinedTextField(
                value = alias,
                onValueChange = { alias = it },
                label = { Text("Alias / Nickname (Optional)") },
                singleLine = true,
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
                    .padding(bottom = 24.dp)
            )

            // Batting Hand selector
            Text(
                "BATTING HAND",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(BattingHand.RIGHT, BattingHand.LEFT).forEach { hand ->
                    val isSel = battingHand == hand
                    val handStr = if (hand == BattingHand.RIGHT) "Right Handed" else "Left Handed"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) SuccessGreen else CardBlack)
                            .clickable { battingHand = hand }
                            .border(1.dp, if (isSel) SuccessGreen else Color.Transparent, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = handStr,
                            color = if (isSel) Color.White else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Bowling Style selector
            Text(
                "BOWLING STYLE (OPTIONAL)",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BowlingStyle.values().forEach { style ->
                    val isSel = bowlingStyle == style
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSel) SuccessGreen else CardBlack)
                            .clickable { bowlingStyle = if (isSel) null else style }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = style.displayName(),
                            color = if (isSel) Color.White else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Profile Color Picker Trigger
            Text(
                "PROFILE COLOUR",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBlack)
                    .clickable { showColorPicker = true }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(selectedColor.hex)))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(selectedColor.displayName(), color = TextPrimary, fontWeight = FontWeight.Medium)
                }
                Icon(Icons.Filled.Edit, "Edit colour", tint = SuccessGreen)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Save Button
            Button(
                onClick = { onSave(name, alias.ifBlank { null }, battingHand, bowlingStyle, selectedColor) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("player_save_btn")
            ) {
                Text("Save Player", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // Color Picker Modal Bottom Sheet
        if (showColorPicker) {
            ColorPickerSheet(
                onColorSelected = {
                    selectedColor = it
                    showColorPicker = false
                },
                onDismiss = { showColorPicker = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerSheet(
    onColorSelected: (ThemeColor) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardBlack,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextMuted) }
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Select Profile Colour", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            // Grid of 8 colors
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf(
                    ThemeColor.values().take(4),
                    ThemeColor.values().drop(4)
                ).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        row.forEach { color ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onColorSelected(color) },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(color.hex))),
                                    contentAlignment = Alignment.Center
                                ) {}
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = color.displayName(),
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun NewEditTeamSheetScreen(
    teamSheetId: String?,
    existingTeam: TeamSheet?,
    existingPlayers: List<Player>,
    selectedPlayerIds: List<String>,
    onSave: (name: String, color: ThemeColor, players: List<String>, captains: List<String>, keepers: List<String>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(existingTeam?.name ?: "") }
    var selectedColor by remember { mutableStateOf(existingTeam?.themeColor ?: ThemeColor.CRIMSON) }
    val playersChecklist = remember { mutableStateListOf<String>().apply { addAll(selectedPlayerIds) } }

    var captainId by remember { mutableStateOf<String?>(null) }
    var keeperId by remember { mutableStateOf<String?>(null) }

    var showColorPicker by remember { mutableStateOf(false) }

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
                Text(
                    text = if (teamSheetId != null) "Edit Team Sheet" else "New Team Sheet",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Team Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Team Name") },
                singleLine = true,
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
                    .padding(bottom = 24.dp)
                    .testTag("team_name_input")
            )

            // Team Theme Color
            Text(
                "TEAM COLOUR",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBlack)
                    .clickable { showColorPicker = true }
                    .padding(16.dp)
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(selectedColor.hex)))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(selectedColor.displayName(), color = TextPrimary, fontWeight = FontWeight.Medium)
                }
                Icon(Icons.Filled.Edit, "Edit colour", tint = SuccessGreen)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Labeled Tip
            Card(
                colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Lightbulb, "Tip", tint = SuccessGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Tap the (C) and (WK) tags on selected players to mark the Captain and Wicket-keeper.",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Players checklist
            Text(
                "SELECT PLAYERS (${playersChecklist.size})",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (existingPlayers.isEmpty()) {
                Text("No players available. Add players first.", color = TextMuted, fontSize = 14.sp)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    existingPlayers.forEach { player ->
                        val isChecked = playersChecklist.contains(player.id)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardBlack),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isChecked) {
                                        playersChecklist.remove(player.id)
                                        if (captainId == player.id) captainId = null
                                        if (keeperId == player.id) keeperId = null
                                    } else {
                                        playersChecklist.add(player.id)
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            if (isChecked) {
                                                playersChecklist.remove(player.id)
                                                if (captainId == player.id) captainId = null
                                                if (keeperId == player.id) keeperId = null
                                            } else {
                                                playersChecklist.add(player.id)
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = SuccessGreen)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    PlayerAvatar(name = player.name, alias = player.alias, color = player.themeColor, size = AvatarSize.SM)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(player.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                                }

                                if (isChecked) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        // Captain toggle
                                        AssistChip(
                                            onClick = { captainId = if (captainId == player.id) null else player.id },
                                            label = { Text("C") },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = if (captainId == player.id) SuccessGreen else Color.Transparent,
                                                labelColor = if (captainId == player.id) Color.White else TextPrimary
                                            )
                                        )
                                        // Keeper toggle
                                        AssistChip(
                                            onClick = { keeperId = if (keeperId == player.id) null else player.id },
                                            label = { Text("WK") },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = if (keeperId == player.id) SuccessGreen else Color.Transparent,
                                                labelColor = if (keeperId == player.id) Color.White else TextPrimary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Save Button
            Button(
                onClick = {
                    onSave(
                        name,
                        selectedColor,
                        playersChecklist,
                        listOfNotNull(captainId),
                        listOfNotNull(keeperId)
                    )
                },
                enabled = name.isNotBlank() && playersChecklist.size >= 2,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("team_save_btn")
            ) {
                Text("Save Team Sheet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // Color Picker Sheet
        if (showColorPicker) {
            ColorPickerSheet(
                onColorSelected = {
                    selectedColor = it
                    showColorPicker = false
                },
                onDismiss = { showColorPicker = false }
            )
        }
    }
}
