package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.*
import com.example.data.repository.AryanRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AryanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AryanRepository
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = AryanRepository(database.aryanDao())
        
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
        }
    }

    val services: StateFlow<List<ServiceEntity>> = repository.allServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<WalletTransaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current selected service for booking
    private val _selectedService = MutableStateFlow<ServiceEntity?>(null)
    val selectedService: StateFlow<ServiceEntity?> = _selectedService.asStateFlow()

    // Tracking specific order details
    private val _vSelectedOrderId = MutableStateFlow<Int?>(null)
    val vSelectedOrderId: StateFlow<Int?> = _vSelectedOrderId.asStateFlow()

    val currentOrderDetails: Flow<OrderEntity?> = _vSelectedOrderId.flatMapLatest { id ->
        if (id != null) repository.getOrderByIdFlow(id) else flow { emit(null) }
    }

    val currentOrderMessages: Flow<List<SupportMessage>> = _vSelectedOrderId.flatMapLatest { id ->
        if (id != null) repository.getMessagesForOrder(id) else flow { emit(emptyList()) }
    }

    // Interactive UI filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("همه")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSelectedService(service: ServiceEntity) {
        _selectedService.value = service
    }

    fun selectOrderId(orderId: Int) {
        _vSelectedOrderId.value = orderId
    }

    // Place an Order
    fun placeOrder(
        service: ServiceEntity,
        qty: Int,
        notes: String,
        onSuccess: (orderId: Int) -> Unit
    ) {
        viewModelScope.launch {
            val profile = userProfile.value ?: return@launch
            
            // For dollar-based gift cards, calculate matching value
            val computedPrice = if (service.unitType.contains("ارزش")) {
                // Base price is per $10. Quantity entered represents dollar values or $10 packets.
                // Let's assume user entered exact quantity of Dollars (e.g. 50$).
                // Or unit increments. We treat input value as dollar amounts (e.g., $10 increments).
                // Let's take: qty is the dollar value.
                // Standard Toman value of dollar is 65000 Tomans.
                // Total price = qty * 65000 + (service.basePrice * (qty / 10))
                val dollarValue = qty.coerceAtLeast(10)
                (dollarValue * 65000L) + (service.basePrice * (dollarValue / 10L))
            } else {
                service.basePrice * qty
            }

            val newOrder = OrderEntity(
                serviceId = service.id,
                serviceTitle = service.title,
                category = service.category,
                customerName = profile.name,
                currentStatus = "PendingPayment", // Status: Draft / PendingPayment / Paid / Completed
                totalPrice = computedPrice,
                qty = qty,
                notes = notes
            )
            
            val orderId = repository.insertOrder(newOrder).toInt()
            onSuccess(orderId)
        }
    }

    // Checkout / Pay via Wallet
    fun payForOrder(orderId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.payOrder(orderId)
            onResult(success)
        }
    }

    // Refund
    fun refundOrder(orderId: Int) {
        viewModelScope.launch {
            repository.refundOrder(orderId)
        }
    }

    // Support Chat
    fun sendSupportMessage(orderId: Int, senderRole: String, senderName: String, text: String) {
        viewModelScope.launch {
            repository.sendMessage(orderId, senderName, senderRole, text)
        }
    }

    // Wallet Recharge
    fun addWalletBalance(amount: Long) {
        viewModelScope.launch {
            repository.depositWallet(amount)
        }
    }

    // Role Switching (Simulate multiple actors in platform)
    fun switchUserRole(newRole: String) {
        viewModelScope.launch {
            repository.updateUserRole(newRole)
        }
    }

    // Operator/Designer Workflow Actions
    fun assignOrderToMe(orderId: Int, employeeName: String, employeeRole: String) {
        viewModelScope.launch {
            val order = orders.value.find { it.id == orderId } ?: return@launch
            val updatedOrder = if (employeeRole == "DESIGNER") {
                order.copy(designerName = employeeName, currentStatus = "Assigned")
            } else {
                order.copy(operatorName = employeeName, currentStatus = "Assigned")
            }
            repository.updateOrder(updatedOrder)
            // Auto add an automated notification message to support
            repository.sendMessage(
                orderId = orderId,
                senderName = employeeName,
                senderRole = employeeRole,
                text = "سلام. من جهت بررسی و هماهنگی سفارش شما منصوب شدم. کار در سریع‌ترین زمان انجام می‌شود."
            )
        }
    }

    fun updateOrderProgress(orderId: Int, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }

    fun submitDelivery(orderId: Int, deliveryText: String, deliveryFile: String = "aryan-file-delivery.zip") {
        viewModelScope.launch {
            val order = orders.value.find { it.id == orderId } ?: return@launch
            val updated = order.copy(
                currentStatus = "Delivered",
                deliveryText = deliveryText,
                deliveryFile = deliveryFile
            )
            repository.updateOrder(updated)
            repository.sendMessage(
                orderId = orderId,
                senderName = if (order.operatorName.isNotEmpty()) order.operatorName else order.designerName,
                senderRole = if (order.designerName.isNotEmpty()) "DESIGNER" else "OPERATOR",
                text = "کار سفارش شما با موفقیت تکمیل و آپلود گردید. فایل خروجی ضمیمه شد: $deliveryText"
            )
        }
    }

    fun completeOrder(orderId: Int) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "Completed")
        }
    }

    fun rateOrder(orderId: Int, rating: Int, comment: String) {
        viewModelScope.launch {
            repository.submitRating(orderId, rating, comment)
            repository.updateOrderStatus(orderId, "Completed")
        }
    }

    fun updateServicePriceByAdmin(id: Int, newPrice: Long) {
        viewModelScope.launch {
            repository.updateServicePrice(id, newPrice)
        }
    }
}
