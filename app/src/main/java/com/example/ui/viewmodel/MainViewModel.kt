package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.GullyCrixApplication
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as GullyCrixApplication
    val repository = app.repository
    val preferences = app.preferences

    // Global flows
    val players: StateFlow<List<Player>> = repository.allPlayers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val teamSheets: StateFlow<List<TeamSheet>> = repository.allTeamSheets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val matches: StateFlow<List<Match>> = repository.allMatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMatch: StateFlow<Match?> = repository.activeMatchFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current selection / Temp state for creation
    private val _selectedTeamAId = MutableStateFlow<String?>(null)
    val selectedTeamAId: StateFlow<String?> = _selectedTeamAId.asStateFlow()

    private val _selectedTeamBId = MutableStateFlow<String?>(null)
    val selectedTeamBId: StateFlow<String?> = _selectedTeamBId.asStateFlow()

    fun selectTeamA(id: String?) {
        _selectedTeamAId.value = id
    }

    fun selectTeamB(id: String?) {
        _selectedTeamBId.value = id
    }

    fun swapTeams() {
        val temp = _selectedTeamAId.value
        _selectedTeamAId.value = _selectedTeamBId.value
        _selectedTeamBId.value = temp
    }

    // Player CRUD
    fun addPlayer(name: String, alias: String?, battingHand: BattingHand?, bowlingStyle: BowlingStyle?, color: ThemeColor) {
        viewModelScope.launch {
            val player = Player(
                name = name,
                alias = alias,
                battingHand = battingHand,
                bowlingStyle = bowlingStyle,
                themeColor = color
            )
            repository.insertPlayer(player)
        }
    }

    fun updatePlayer(id: String, name: String, alias: String?, battingHand: BattingHand?, bowlingStyle: BowlingStyle?, color: ThemeColor) {
        viewModelScope.launch {
            val player = Player(
                id = id,
                name = name,
                alias = alias,
                battingHand = battingHand,
                bowlingStyle = bowlingStyle,
                themeColor = color
            )
            repository.updatePlayer(player)
        }
    }

    fun deletePlayer(player: Player) {
        viewModelScope.launch {
            repository.deletePlayer(player)
        }
    }

    // TeamSheet CRUD
    fun createTeamSheet(name: String, color: ThemeColor, playerIds: List<String>, captains: List<String>, keepers: List<String>) {
        viewModelScope.launch {
            val ts = TeamSheet(name = name, themeColor = color)
            repository.insertTeamSheet(ts, playerIds, captains, keepers)
        }
    }

    fun updateTeamSheet(id: String, name: String, color: ThemeColor, playerIds: List<String>, captains: List<String>, keepers: List<String>) {
        viewModelScope.launch {
            val ts = TeamSheet(id = id, name = name, themeColor = color)
            repository.insertTeamSheet(ts, playerIds, captains, keepers)
        }
    }

    fun deleteTeamSheet(teamSheet: TeamSheet) {
        viewModelScope.launch {
            repository.deleteTeamSheet(teamSheet)
        }
    }

    // Full reset
    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            preferences.clearAll()
        }
    }
}
