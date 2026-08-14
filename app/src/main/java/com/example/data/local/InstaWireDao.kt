package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BurnerLine
import com.example.data.model.Channel
import com.example.data.model.Contact
import com.example.data.model.Transmission
import com.example.data.model.UserIdentity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstaWireDao {
    // Identity
    @Query("SELECT * FROM user_identity WHERE id = 1")
    fun getUserIdentity(): Flow<UserIdentity?>

    @Query("SELECT * FROM user_identity WHERE id = 1")
    suspend fun getUserIdentitySync(): UserIdentity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateIdentity(identity: UserIdentity)

    // Contacts
    @Query("SELECT * FROM contacts ORDER BY isFavorite DESC, lastTransmissionTime DESC")
    fun getAllContacts(): Flow<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact): Long

    @Update
    suspend fun updateContact(contact: Contact)

    @Query("UPDATE contacts SET isKeyVerified = :verified WHERE id = :id")
    suspend fun setKeyVerified(id: Long, verified: Boolean)

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteContact(id: Long)

    // Burner Lines
    @Query("SELECT * FROM burner_lines ORDER BY createdAt DESC")
    fun getAllBurnerLines(): Flow<List<BurnerLine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBurnerLine(burnerLine: BurnerLine)

    @Query("DELETE FROM burner_lines WHERE number = :number")
    suspend fun deleteBurnerLine(number: String)

    @Query("DELETE FROM burner_lines")
    suspend fun clearAllBurnerLines()

    // Channels
    @Query("SELECT * FROM channels")
    fun getAllChannels(): Flow<List<Channel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: Channel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<Channel>)

    @Query("DELETE FROM channels WHERE id = :id")
    suspend fun deleteChannel(id: String)

    // Transmissions
    @Query("SELECT * FROM transmissions WHERE targetId = :targetId ORDER BY timestamp DESC LIMIT 50")
    fun getTransmissionsForTarget(targetId: String): Flow<List<Transmission>>

    @Query("SELECT * FROM transmissions ORDER BY timestamp DESC LIMIT 30")
    fun getRecentTransmissions(): Flow<List<Transmission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransmission(transmission: Transmission): Long

    @Query("DELETE FROM transmissions WHERE timestamp < :cutoffTime")
    suspend fun deleteOldTransmissions(cutoffTime: Long)

    @Query("DELETE FROM transmissions")
    suspend fun clearAllTransmissions()
}
