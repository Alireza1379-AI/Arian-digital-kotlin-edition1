package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.OrderEntity
import com.example.data.database.ServiceEntity
import com.example.ui.AryanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AryanViewModel,
    onNavigateToOrderTracking: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val services by viewModel.services.collectAsState()

    var showAdminDialog by remember { mutableStateOf(false) }
    var adminSelectedService by remember { mutableStateOf<ServiceEntity?>(null) }
    var adminPriceInput by remember { mutableStateOf("") }

    var deliverNotesInput by remember { mutableStateOf("") }
    var activeDeliveryOrder by remember { mutableStateOf<OrderEntity?>(null) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val currentRole = userProfile?.role ?: "CUSTOMER"

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = when (currentRole) {
                                    "OPERATOR" -> "میز کار اپراتور آرین"
                                    "DESIGNER" -> "آتلیه طراحی آرین دیجیتال"
                                    "ADMIN" -> "پنل ادمین کل سیستم"
                                    else -> "سفارش‌های من"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text("نقش فعلی: ${translateRole(currentRole)}", fontSize = 11.sp, color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "برگشت")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFEF7FF),
                        titleContentColor = Color(0xFF1D1B20),
                        navigationIconContentColor = Color(0xFF1D1B20)
                    )
                )
            },
            containerColor = Color(0xFFFEF7FF)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Role Simulator Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                    border = BorderStroke(1.dp, Color(0xFFE8DEF8))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("شبیه‌ساز تغییر نقش پلتفرم (جهت تست فرآیندها):", fontSize = 11.sp, color = Color(0xFF49454F), fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val allRoles = listOf("CUSTOMER" to "مشتری", "OPERATOR" to "اپراتور", "DESIGNER" to "طراح", "ADMIN" to "ادمین")
                            allRoles.forEach { item ->
                                val isSelected = currentRole == item.first
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF6750A4) else Color.White)
                                        .clickable { viewModel.switchUserRole(item.first) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.second,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF6750A4)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Workstation Panels based on Switch
                when (currentRole) {
                    "CUSTOMER" -> CustomerWorkspace(orders, onNavigateToOrderTracking)
                    "OPERATOR" -> OperatorWorkspace(
                        orders = orders,
                        employeeName = userProfile?.name ?: "تیم اپراتوری",
                        employeeRole = "OPERATOR",
                        onAccept = { orderId -> viewModel.assignOrderToMe(orderId, "زهرا رضایی (اپراتور ارشد)", "OPERATOR") },
                        onDeliverClick = { activeDeliveryOrder = it },
                        onNavigateToTracking = onNavigateToOrderTracking
                    )
                    "DESIGNER" -> OperatorWorkspace(
                        orders = orders,
                        employeeName = userProfile?.name ?: "طراح ارشد",
                        employeeRole = "DESIGNER",
                        onAccept = { orderId -> viewModel.assignOrderToMe(orderId, "رضا احمدی (طراح لوگو و UI)", "DESIGNER") },
                        onDeliverClick = { activeDeliveryOrder = it },
                        onNavigateToTracking = onNavigateToOrderTracking
                    )
                    "ADMIN" -> AdminWorkspace(
                        services = services,
                        orders = orders,
                        onUpdatePriceClick = { service ->
                            adminSelectedService = service
                            adminPriceInput = service.basePrice.toString()
                            showAdminDialog = true
                        }
                    )
                }
            }
        }

        // Dialog for Delivery Upload (Operator/Designer)
        if (activeDeliveryOrder != null) {
            AlertDialog(
                onDismissRequest = { activeDeliveryOrder = null },
                title = { Text("ارسال فایل نهایی سفارش", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20)) },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("نام کارفرمای سفارش: ${activeDeliveryOrder?.customerName}", fontSize = 12.sp, color = Color(0xFF49454F))
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = deliverNotesInput,
                            onValueChange = { deliverNotesInput = it },
                            placeholder = { Text("متن فایل تحویلی، کدهای گیفت کارت ارسالی، یا لینک نتیجه نهایی...", fontSize = 12.sp, color = Color(0xFF49454F)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                focusedContainerColor = Color(0xFFF3EDF7),
                                unfocusedContainerColor = Color(0xFFF3EDF7)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.submitDelivery(activeDeliveryOrder!!.id, deliverNotesInput, "aryan-delivery-file-${activeDeliveryOrder!!.id}.zip")
                            activeDeliveryOrder = null
                            deliverNotesInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                    ) {
                        Text("ارسال و فرستادن کار", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { activeDeliveryOrder = null }) {
                        Text("انصراف", color = Color(0xFF6750A4))
                    }
                }
            )
        }

        // Dialog for Admin Pricing rules Update
        if (showAdminDialog && adminSelectedService != null) {
            AlertDialog(
                onDismissRequest = { showAdminDialog = false },
                title = { Text("تغییر قانون تعرفه خدمات", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20)) },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(adminSelectedService!!.title, fontSize = 13.sp, color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = adminPriceInput,
                            onValueChange = { if (it.all { char -> char.isDigit() }) adminPriceInput = it },
                            label = { Text("تعرفه جدید حاصله (تومان)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                focusedContainerColor = Color(0xFFF3EDF7),
                                unfocusedContainerColor = Color(0xFFF3EDF7)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("واحد مبنا: به ازای هر ${adminSelectedService!!.unitType}", fontSize = 11.sp, color = Color(0xFF49454F))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val value = adminPriceInput.toLongOrNull() ?: 0L
                            viewModel.updateServicePriceByAdmin(adminSelectedService!!.id, value)
                            showAdminDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                    ) {
                        Text("بروزرسانی تعرفه", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAdminDialog = false }) {
                        Text("انصراف", color = Color(0xFF6750A4))
                    }
                }
            )
        }
    }
}

// Translate Role titles helper
fun translateRole(role: String): String {
    return when(role) {
        "CUSTOMER" -> "مشتری گرامی 👤"
        "OPERATOR" -> "زهرا رضایی (اپراتور ارشد) 👷"
        "DESIGNER" -> "رضا احمدی (طراح لوگو و UI) 🎨"
        "ADMIN" -> "مدیریت کل سیستم آرین ⚙️"
        else -> "پشتیبان سیستم"
    }
}

// WORKSTATION: CUSTOMER
@Composable
fun CustomerWorkspace(
    orders: List<OrderEntity>,
    onNavigateToOrderTracking: (Int) -> Unit
) {
    Text(
        text = "تاریخچه کلی سفارش‌های ثبت شده:",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = Color(0xFF1D1B20),
        modifier = Modifier.padding(vertical = 8.dp)
    )

    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("هنوز هیچ سفارشی در این سیستم ثبت نکرده‌اید.", color = Color(0xFF49454F))
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(orders) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToOrderTracking(order.id) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(order.serviceTitle, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                            Text(
                                text = translateStatus(order.currentStatus),
                                color = getStatusColor(order.currentStatus),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text("شناسه سفارش: #${order.id}", fontSize = 11.sp, color = Color(0xFF49454F))
                                Text("تعداد: ${order.qty}", fontSize = 11.sp, color = Color(0xFF49454F))
                            }
                            Text(
                                text = "${formatAmount(order.totalPrice)} T",
                                color = Color(0xFF6750A4),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// WORKSTATION: OPERATOR & DESIGNER
@Composable
fun OperatorWorkspace(
    orders: List<OrderEntity>,
    employeeName: String,
    employeeRole: String,
    onAccept: (Int) -> Unit,
    onDeliverClick: (OrderEntity) -> Unit,
    onNavigateToTracking: (Int) -> Unit
) {
    val scrollState = rememberScrollState()

    // Queues
    val isDesigner = employeeRole == "DESIGNER"
    val targetCategory = if (isDesigner) "طراحی گرافیک" else ""
    
    // Unassigned Pool - Orders that are "Paid"
    val poolOrders = orders.filter { order ->
        val rightCategory = if (isDesigner) order.category == "طراحی گرافیک" else order.category != "طراحی گرافیک"
        order.currentStatus == "Paid" && rightCategory
    }

    // Active assigned to me (Either Assigned or InProgress)
    val myActiveOrders = orders.filter { order ->
        val isAssignedToMe = if (isDesigner) order.designerName.isNotEmpty() else order.operatorName.isNotEmpty()
        isAssignedToMe && listOf("Assigned", "InProgress").contains(order.currentStatus)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        // Pool section
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFC62828))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("صف سفارش‌های پرداخت شده (در انتظار پذیرش):", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
        }
        
        Spacer(modifier = Modifier.height(10.dp))

        if (poolOrders.isEmpty()) {
            Text("هیچ سفارش آماده پذیری در سیستم وجود ندارد.", color = Color(0xFF49454F), fontSize = 12.sp, modifier = Modifier.padding(8.dp))
        } else {
            poolOrders.forEach { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(order.serviceTitle, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                            Text("پرداخت شده", color = Color(0xFF6750A4), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("کاربر ثبت‌کننده: ${order.customerName}", fontSize = 11.sp, color = Color(0xFF49454F))
                        Text("تعداد: ${order.qty}", fontSize = 11.sp, color = Color(0xFF49454F))
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onAccept(order.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("قبول سفارش و شروع فرآیند 🤝", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // My assigned list
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("سفارش‌های در دست اقدام شما:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (myActiveOrders.isEmpty()) {
            Text("سفارشی در دست اقدام ندارید. از لیست فوق پذیرش کنید.", color = Color(0xFF49454F), fontSize = 12.sp, modifier = Modifier.padding(8.dp))
        } else {
            myActiveOrders.forEach { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(order.serviceTitle, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                            Text(translateStatus(order.currentStatus), color = Color(0xFFF57C00), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("توضیحات خریدار: ${order.notes}", fontSize = 11.sp, color = Color(0xFF49454F))
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onDeliverClick(order) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ارسال فایل نهایی 📤", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            
                            OutlinedButton(
                                onClick = { onNavigateToTracking(order.id) },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, Color(0xFF6750A4)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6750A4))
                            ) {
                                Text("پنل رهگیری و گفتگو 💬", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// WORKSTATION: ADMIN
@Composable
fun AdminWorkspace(
    services: List<ServiceEntity>,
    orders: List<OrderEntity>,
    onUpdatePriceClick: (ServiceEntity) -> Unit
) {
    val scrollState = rememberScrollState()

    // Basic system stats
    val totalIncome = orders.filter { it.currentStatus == "Completed" || it.currentStatus == "Paid" }.sumOf { it.totalPrice }
    val totalUsersCount = 54 // pre-estimated mock count
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Text("گزارش وضعیت مالی و آمارهای کل:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("درآمد ناخالص سیستم", fontSize = 10.sp, color = Color(0xFF49454F))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${formatAmount(totalIncome)} T", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6750A4))
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("کل سفارشات فعال", fontSize = 10.sp, color = Color(0xFF49454F))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${orders.size} فقره", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("مدیریت و ویرایش فوری تعرفه قوانین خدمت:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
        Spacer(modifier = Modifier.height(10.dp))

        services.forEach { service ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(service.title, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20), fontSize = 13.sp)
                        Text("تعرفه فعلی: ${formatAmount(service.basePrice)} تومان به ازای ${service.unitType}", fontSize = 11.sp, color = Color(0xFF49454F))
                    }
                    Button(
                        onClick = { onUpdatePriceClick(service) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3EDF7)),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFCAC4D0))
                    ) {
                        Text("ویرایش قیمت", fontSize = 11.sp, color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
