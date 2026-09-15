package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BurnerLine
import com.example.data.model.BurnerNumberMetadata
import com.example.data.model.Channel
import com.example.data.model.Contact
import com.example.data.model.EncryptedMessageRecord
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

    @Query("SELECT * FROM channels")
    suspend fun getAllChannelsSync(): List<Channel>

    @Query("SELECT * FROM channels WHERE frequencyCode = :code OR id = :code LIMIT 1")
    suspend fun getChannelByCode(code: String): Channel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: Channel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<Channel>)

    @Query("DELETE FROM channels WHERE id = :id")
    suspend fun deleteChannel(id: String)

    @Query("DELETE FROM channels WHERE isSystemChannel = 1 OR id LIKE 'chan_%'")
    suspend fun deleteSystemChannels()

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

    // User Profile
    @Query("SELECT * FROM user_profiles WHERE id = 1")
    fun getUserProfile(): Flow<com.example.data.model.UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = 1")
    suspend fun getUserProfileSync(): com.example.data.model.UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: com.example.data.model.UserProfile)

    @Query("UPDATE user_profiles SET coinsBalance = coinsBalance + :coinsToAdd WHERE id = 1")
    suspend fun addCoins(coinsToAdd: Int)

    @Query("UPDATE user_profiles SET coinsBalance = coinsBalance - :coinsToDeduct, totalGiftsSent = totalGiftsSent + 1 WHERE id = 1")
    suspend fun deductCoinsForGift(coinsToDeduct: Int)

    // Worldwide Rooms
    @Query("SELECT * FROM worldwide_rooms ORDER BY activeListeners DESC")
    fun getAllWorldwideRooms(): Flow<List<com.example.data.model.WorldwideRoom>>

    @Query("SELECT * FROM worldwide_rooms WHERE id = :roomId")
    fun getWorldwideRoom(roomId: String): Flow<com.example.data.model.WorldwideRoom?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorldwideRoom(room: com.example.data.model.WorldwideRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorldwideRooms(rooms: List<com.example.data.model.WorldwideRoom>)

    @Query("DELETE FROM worldwide_rooms WHERE id = :roomId")
    suspend fun deleteWorldwideRoom(roomId: String)

    // Friends
    @Query("SELECT * FROM friends ORDER BY isOnline DESC, username ASC")
    fun getAllFriends(): Flow<List<com.example.data.model.FriendUser>>

    @Query("SELECT * FROM friends WHERE id = :userId")
    suspend fun getFriendById(userId: String): com.example.data.model.FriendUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: com.example.data.model.FriendUser)

    @Query("DELETE FROM friends WHERE id = :userId")
    suspend fun deleteFriend(userId: String)

    // Blocked Users
    @Query("SELECT * FROM blocked_users ORDER BY blockedAt DESC")
    fun getAllBlockedUsers(): Flow<List<com.example.data.model.BlockedUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun blockUser(blockedUser: com.example.data.model.BlockedUser)

    @Query("DELETE FROM blocked_users WHERE id = :userId")
    suspend fun unblockUser(userId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM blocked_users WHERE id = :userId)")
    suspend fun isUserBlocked(userId: String): Boolean

    // Gift Transactions
    @Query("SELECT * FROM gift_transactions ORDER BY timestamp DESC LIMIT 50")
    fun getAllGiftTransactions(): Flow<List<com.example.data.model.GiftTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGiftTransaction(transaction: com.example.data.model.GiftTransaction)

    // Live Room Messages
    @Query("SELECT * FROM room_messages WHERE roomId = :roomId ORDER BY timestamp ASC LIMIT 100")
    fun getRoomMessages(roomId: String): Flow<List<com.example.data.model.LiveRoomMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoomMessage(message: com.example.data.model.LiveRoomMessage)

    // Coin Cashout Transactions & In-App Coin Purchases
    @Query("SELECT * FROM coin_cashouts ORDER BY timestamp DESC")
    fun getAllCashoutTransactions(): Flow<List<com.example.data.model.CoinCashoutTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashoutTransaction(cashout: com.example.data.model.CoinCashoutTransaction): Long

    @Query("UPDATE user_profiles SET coinsBalance = coinsBalance - :coinsToDeduct WHERE id = 1 AND coinsBalance >= :coinsToDeduct")
    suspend fun deductCoins(coinsToDeduct: Int): Int

    // ==========================================
    // Local Encrypted Message History (Offline Access)
    // ==========================================
    @Query("SELECT * FROM encrypted_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC LIMIT 100")
    fun getEncryptedMessagesForConversation(conversationId: String): Flow<List<EncryptedMessageRecord>>

    @Query("SELECT * FROM encrypted_messages ORDER BY timestamp DESC LIMIT 60")
    fun getAllEncryptedMessages(): Flow<List<EncryptedMessageRecord>>

    @Query("SELECT * FROM encrypted_messages WHERE id = :id")
    suspend fun getEncryptedMessageById(id: Long): EncryptedMessageRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncryptedMessage(message: EncryptedMessageRecord): Long

    @Query("UPDATE encrypted_messages SET deliveryStatus = :status WHERE id = :id")
    suspend fun updateMessageDeliveryStatus(id: Long, status: String)

    @Query("DELETE FROM encrypted_messages WHERE id = :id")
    suspend fun deleteEncryptedMessage(id: Long)

    @Query("DELETE FROM encrypted_messages WHERE conversationId = :conversationId")
    suspend fun clearEncryptedMessagesForConversation(conversationId: String)

    @Query("DELETE FROM encrypted_messages")
    suspend fun clearAllEncryptedMessages()

    // ==========================================
    // Burner Number Metadata (Encrypted Offline Vault)
    // ==========================================
    @Query("SELECT * FROM burner_number_metadata ORDER BY allocatedAt DESC")
    fun getAllBurnerMetadata(): Flow<List<BurnerNumberMetadata>>

    @Query("SELECT * FROM burner_number_metadata")
    suspend fun getAllBurnerMetadataSync(): List<BurnerNumberMetadata>

    @Query("SELECT * FROM burner_number_metadata WHERE verificationStatus = 'ACTIVE'")
    suspend fun getActiveBurnerMetadataSync(): List<BurnerNumberMetadata>

    @Query("SELECT * FROM burner_number_metadata WHERE phoneNumber = :phoneNumber")
    fun getBurnerMetadata(phoneNumber: String): Flow<BurnerNumberMetadata?>

    @Query("SELECT * FROM burner_number_metadata WHERE phoneNumber = :phoneNumber")
    suspend fun getBurnerMetadataSync(phoneNumber: String): BurnerNumberMetadata?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBurnerMetadata(metadata: BurnerNumberMetadata)

    @Query("UPDATE burner_number_metadata SET verificationStatus = :status WHERE phoneNumber = :phoneNumber")
    suspend fun updateBurnerVerificationStatus(phoneNumber: String, status: String)

    @Query("UPDATE burner_number_metadata SET verificationStatus = :status, firebaseUid = :firebaseUid WHERE phoneNumber = :phoneNumber")
    suspend fun updateBurnerVerificationAndUid(phoneNumber: String, status: String, firebaseUid: String?)

    @Query("UPDATE burner_number_metadata SET transmissionsUsed = transmissionsUsed + 1 WHERE phoneNumber = :phoneNumber")
    suspend fun incrementBurnerTransmissionCount(phoneNumber: String)

    @Query("DELETE FROM burner_number_metadata WHERE phoneNumber = :phoneNumber")
    suspend fun deleteBurnerMetadata(phoneNumber: String)

    @Query("DELETE FROM burner_number_metadata")
    suspend fun clearAllBurnerMetadata()

    // Callsign Uniqueness Validation
    @Query("SELECT EXISTS(SELECT 1 FROM contacts WHERE UPPER(callsign) = UPPER(:callsign) AND id != :excludeContactId)")
    suspend fun isContactCallsignTaken(callsign: String, excludeContactId: Long = -1L): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM friends WHERE UPPER(callsign) = UPPER(:callsign))")
    suspend fun isFriendCallsignTaken(callsign: String): Boolean

    @Query("SELECT callsign FROM contacts")
    suspend fun getAllContactCallsignsSync(): List<String>

    @Query("SELECT callsign FROM friends")
    suspend fun getAllFriendCallsignsSync(): List<String>
}


