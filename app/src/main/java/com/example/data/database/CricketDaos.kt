package com.example.data.database

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY name ASC")
    fun getAllPlayers(): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE isActive = 1 ORDER BY name ASC")
    fun getActivePlayers(): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getPlayerById(id: Long): Player?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player): Long

    @Update
    suspend fun updatePlayer(player: Player)

    @Delete
    suspend fun deletePlayer(player: Player)

    @Query("SELECT COUNT(*) FROM players")
    suspend fun getPlayerCount(): Int
}

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches ORDER BY id DESC")
    fun getAllMatches(): Flow<List<Match>>

    @Query("SELECT * FROM matches WHERE status = :status ORDER BY id DESC")
    fun getMatchesByStatus(status: String): Flow<List<Match>>

    @Query("SELECT * FROM matches WHERE id = :id")
    suspend fun getMatchById(id: Long): Match?

    @Query("SELECT * FROM matches WHERE id = :id")
    fun getMatchByIdFlow(id: Long): Flow<Match?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: Match): Long

    @Update
    suspend fun updateMatch(match: Match)

    @Delete
    suspend fun deleteMatch(match: Match)
}

@Dao
interface BallEventDao {
    @Query("SELECT * FROM ball_events WHERE matchId = :matchId AND inningsIndex = :inningsIndex ORDER BY id ASC")
    fun getBallEventsForInnings(matchId: Long, inningsIndex: Int): Flow<List<BallEvent>>

    @Query("SELECT * FROM ball_events WHERE matchId = :matchId ORDER BY id ASC")
    fun getAllBallEventsForMatch(matchId: Long): Flow<List<BallEvent>>

    @Query("SELECT * FROM ball_events WHERE matchId = :matchId AND inningsIndex = :inningsIndex ORDER BY id ASC")
    suspend fun getBallEventsList(matchId: Long, inningsIndex: Int): List<BallEvent>

    @Query("SELECT * FROM ball_events")
    suspend fun getAllBallEvents(): List<BallEvent>

    @Query("SELECT * FROM ball_events WHERE matchId = :matchId ORDER BY id ASC")
    suspend fun getBallEventsListForMatch(matchId: Long): List<BallEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBallEvent(ballEvent: BallEvent): Long

    @Update
    suspend fun updateBallEvent(ballEvent: BallEvent)

    @Delete
    suspend fun deleteBallEvent(ballEvent: BallEvent)

    @Query("DELETE FROM ball_events WHERE id = (SELECT MAX(id) FROM ball_events WHERE matchId = :matchId AND inningsIndex = :inningsIndex)")
    suspend fun deleteLastBallEvent(matchId: Long, inningsIndex: Int)

    @Query("DELETE FROM ball_events WHERE matchId = :matchId")
    suspend fun deleteBallEventsForMatch(matchId: Long)
}

@Dao
interface TournamentDao {
    @Query("SELECT * FROM tournaments ORDER BY id DESC")
    fun getAllTournaments(): Flow<List<Tournament>>

    @Query("SELECT * FROM tournaments WHERE id = :id")
    suspend fun getTournamentById(id: Long): Tournament?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: Tournament): Long

    @Update
    suspend fun updateTournament(tournament: Tournament)

    @Delete
    suspend fun deleteTournament(tournament: Tournament)
}

@Dao
interface FixtureDao {
    @Query("SELECT * FROM fixtures ORDER BY date ASC")
    fun getAllFixtures(): Flow<List<Fixture>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFixture(fixture: Fixture): Long

    @Update
    suspend fun updateFixture(fixture: Fixture)

    @Delete
    suspend fun deleteFixture(fixture: Fixture)
}

@Dao
interface NewsDao {
    @Query("SELECT * FROM news ORDER BY id DESC")
    fun getAllNews(): Flow<List<NewsItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(newsItem: NewsItem): Long

    @Update
    suspend fun updateNews(newsItem: NewsItem)

    @Delete
    suspend fun deleteNews(newsItem: NewsItem)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY id DESC")
    fun getAllExpenses(): Flow<List<ExpenseItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expenseItem: ExpenseItem): Long

    @Update
    suspend fun updateExpense(expenseItem: ExpenseItem)

    @Delete
    suspend fun deleteExpense(expenseItem: ExpenseItem)
}
