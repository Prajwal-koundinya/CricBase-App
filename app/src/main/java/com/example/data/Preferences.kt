package com.example.data

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("gully_crix_prefs", Context.MODE_PRIVATE)

    var hasCompletedOnboarding: Boolean
        get() = prefs.getBoolean("hasCompletedOnboarding", false)
        set(value) = prefs.edit().putBoolean("hasCompletedOnboarding", value).apply()

    var hasSeenSquadBookIntro: Boolean
        get() = prefs.getBoolean("hasSeenSquadBookIntro", false)
        set(value) = prefs.edit().putBoolean("hasSeenSquadBookIntro", value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean("soundEnabled", true)
        set(value) = prefs.edit().putBoolean("soundEnabled", value).apply()

    var hapticEnabled: Boolean
        get() = prefs.getBoolean("hapticEnabled", true)
        set(value) = prefs.edit().putBoolean("hapticEnabled", value).apply()

    var defaultOvers: Int
        get() = prefs.getInt("defaultOvers", 4)
        set(value) = prefs.edit().putInt("defaultOvers", value).apply()

    var defaultMaxOversPerBowler: Int
        get() = prefs.getInt("defaultMaxOversPerBowler", 2)
        set(value) = prefs.edit().putInt("defaultMaxOversPerBowler", value).apply()

    var defaultArena: ArenaType
        get() {
            val name = prefs.getString("defaultArena", ArenaType.GROUND.name) ?: ArenaType.GROUND.name
            return try { ArenaType.valueOf(name) } catch (e: Exception) { ArenaType.GROUND }
        }
        set(value) = prefs.edit().putString("defaultArena", value.name).apply()

    var defaultBallType: BallType
        get() {
            val name = prefs.getString("defaultBallType", BallType.TENNIS.name) ?: BallType.TENNIS.name
            return try { BallType.valueOf(name) } catch (e: Exception) { BallType.TENNIS }
        }
        set(value) = prefs.edit().putString("defaultBallType", value.name).apply()

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
