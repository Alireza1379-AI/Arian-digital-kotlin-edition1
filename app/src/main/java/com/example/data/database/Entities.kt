package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val category: String,
    val basePrice: Long, // in Tomans
    val unitType: String, // "واحد", "صفحه", "نمونه", "ارزش دلاری گیفت‌کارت"
    val description: String,
    val limitInfo: String,
    val minQty: Int = 1,
    val maxQty: Int = 100
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serviceId: Int,
    val serviceTitle: String,
    val category: String,
    val customerName: String,
    val operatorName: String = "",
    val designerName: String = "",
    val currentStatus: String, // "PendingPayment", "Paid", "Assigned", "InProgress", "Delivered", "Completed", "Cancelled", "Refunded"
    val totalPrice: Long,
    val qty: Int,
    val notes: String,
    val createdAt: Long = System.currentTimeMillis(),
    val deliveryText: String = "",
    val deliveryFile: String = "",
    val rating: Int = 0,
    val ratingComment: String = ""
)

@Entity(tableName = "wallet_transactions")
data class WalletTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Long,
    val type: String, // "DEPOSIT", "PAYMENT", "REFUND"
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_messages")
data class SupportMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Int,
    val senderName: String,
    val senderRole: String, // "CUSTOMER", "OPERATOR", "DESIGNER", "ADMIN"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val role: String, // "CUSTOMER", "OPERATOR", "DESIGNER", "ADMIN"
    val phoneNumber: String,
    val balance: Long // in Tomans
)
