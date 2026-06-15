package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AryanDao {
    // Services
    @Query("SELECT * FROM services ORDER BY category ASC, id ASC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getServiceById(id: Int): ServiceEntity?

    @Query("UPDATE services SET basePrice = :newPrice WHERE id = :id")
    suspend fun updateServicePrice(id: Int, newPrice: Long)

    // Orders
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderByIdFlow(orderId: Int): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: Int): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET currentStatus = :newStatus WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Int, newStatus: String)

    // Transactions
    @Query("SELECT * FROM wallet_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<WalletTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransaction)

    // Messages
    @Query("SELECT * FROM support_messages WHERE orderId = :orderId ORDER BY timestamp ASC")
    fun getMessagesForOrder(orderId: Int): Flow<List<SupportMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: SupportMessage)

    // User Profile
    @Query("SELECT * FROM user_profiles WHERE id = 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = 1")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    @Query("UPDATE user_profiles SET balance = :newBalance WHERE id = 1")
    suspend fun updateWalletBalance(newBalance: Long)

    @Query("UPDATE user_profiles SET role = :newRole WHERE id = 1")
    suspend fun updateUserRole(newRole: String)
}
