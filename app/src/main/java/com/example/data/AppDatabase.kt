package com.example.data

import android.content.Context
import androidx.room.*

class AppConverters {
    @TypeConverter
    fun fromBattingHand(value: BattingHand?): String? = value?.name

    @TypeConverter
    fun toBattingHand(value: String?): BattingHand? = value?.let { BattingHand.valueOf(it) }

    @TypeConverter
    fun fromBowlingStyle(value: BowlingStyle?): String? = value?.name

    @TypeConverter
    fun toBowlingStyle(value: String?): BowlingStyle? = value?.let { BowlingStyle.valueOf(it) }

    @TypeConverter
    fun fromThemeColor(value: ThemeColor?): String? = value?.name

    @TypeConverter
    fun toThemeColor(value: String?): ThemeColor? = value?.let { ThemeColor.valueOf(it) }

    @TypeConverter
    fun fromTossDecision(value: TossDecision?): String? = value?.name

    @TypeConverter
    fun toTossDecision(value: String?): TossDecision? = value?.let { TossDecision.valueOf(it) }

    @TypeConverter
    fun fromMatchStatus(value: MatchStatus?): String? = value?.name

    @TypeConverter
    fun toMatchStatus(value: String?): MatchStatus? = value?.let { MatchStatus.valueOf(it) }

    @TypeConverter
    fun fromArenaType(value: ArenaType?): String? = value?.name

    @TypeConverter
    fun toArenaType(value: String?): ArenaType? = value?.let { ArenaType.valueOf(it) }

    @TypeConverter
    fun fromBallType(value: BallType?): String? = value?.name

    @TypeConverter
    fun toBallType(value: String?): BallType? = value?.let { BallType.valueOf(it) }

    @TypeConverter
    fun fromExtraType(value: ExtraType?): String? = value?.name

    @TypeConverter
    fun toExtraType(value: String?): ExtraType? = value?.let { ExtraType.valueOf(it) }

    @TypeConverter
    fun fromDismissalType(value: DismissalType?): String? = value?.name

    @TypeConverter
    fun toDismissalType(value: String?): DismissalType? = value?.let { DismissalType.valueOf(it) }

    @TypeConverter
    fun fromDismissedEnd(value: DismissedEnd?): String? = value?.name

    @TypeConverter
    fun toDismissedEnd(value: String?): DismissedEnd? = value?.let { DismissedEnd.valueOf(it) }

    @TypeConverter
    fun fromAwardType(value: AwardType?): String? = value?.name

    @TypeConverter
    fun toAwardType(value: String?): AwardType? = value?.let { AwardType.valueOf(it) }
}

@Database(
    entities = [
        Player::class,
        TeamSheet::class,
        TeamSheetPlayer::class,
        Match::class,
        Innings::class,
        Over::class,
        Ball::class,
        Dismissal::class,
        Award::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(AppConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun teamSheetDao(): TeamSheetDao
    abstract fun matchDao(): MatchDao
    abstract fun inningsDao(): InningsDao
    abstract fun overDao(): OverDao
    abstract fun ballDao(): BallDao
    abstract fun dismissalDao(): DismissalDao
    abstract fun awardDao(): AwardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gully_crix_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
