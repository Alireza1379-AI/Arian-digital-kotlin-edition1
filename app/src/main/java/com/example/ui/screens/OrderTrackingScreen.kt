package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.ui.AryanViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: Int,
    viewModel: AryanViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToChat: (Int) -> Unit,
    onNavigateToWallet: () -> Unit
) {
    LaunchedEffect(orderId) {
        viewModel.selectOrderId(orderId)
    }

    val orderDetails by viewModel.currentOrderDetails.collectAsState(initial = null)
    val userProfile by viewModel.userProfile.collectAsState()
    
    var showRatingDialog by remember { mutableStateOf(false) }
    var selectedRating by remember { mutableStateOf(5) }
    var commentText by remember { mutableStateOf("") }
    
    val scrollState = rememberScrollState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("رهگیری سفارش #${orderId}", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
            if (orderDetails == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6750A4))
                }
                return@Scaffold
            }

            val order = orderDetails!!
            val priceMismatch = (userProfile?.balance ?: 0) < order.totalPrice

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Main Status Banner Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "وضعیت فعلی سفارش",
                            fontSize = 11.sp,
                            color = Color(0xFF49454F),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = translateStatus(order.currentStatus),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = getStatusColor(order.currentStatus),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Visual Stepper Tracker
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val steps = listOf("PendingPayment", "Paid", "Assigned", "InProgress", "Delivered", "Completed")
                            val currentStepIndex = steps.indexOf(order.currentStatus)
                            
                            steps.forEachIndexed { index, step ->
                                val isDone = index <= currentStepIndex
                                val isCurrent = index == currentStepIndex
                                
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isCurrent) Color(0xFF6750A4) 
                                            else if (isDone) Color(0xFF6750A4).copy(alpha = 0.4f) 
                                            else Color(0xFFCAC4D0)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone && !isCurrent) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    } else {
                                        Text((index + 1).toString(), fontSize = 11.sp, color = if (isCurrent) Color.White else Color(0xFF1D1B20), fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (index < steps.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(3.dp)
                                            .background(if (index < currentStepIndex) Color(0xFF6750A4) else Color(0xFFCAC4D0))
                                    )
                                }
                            }
                        }

                        // Status subtitle reminders
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ثبت سفارش", fontSize = 9.sp, color = Color(0xFF49454F))
                            Text("پرداخت شده", fontSize = 9.sp, color = Color(0xFF49454F))
                            Text("تخصیص یافته", fontSize = 9.sp, color = Color(0xFF49454F))
                            Text("در حال اجرا", fontSize = 9.sp, color = Color(0xFF49454F))
                            Text("تحویل کار", fontSize = 9.sp, color = Color(0xFF49454F))
                            Text("پایان یافته", fontSize = 9.sp, color = Color(0xFF49454F))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Order General Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("مشخصات کل فاکتور:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("خدمت درخواستی:", fontSize = 12.sp, color = Color(0xFF49454F))
                            Text(order.serviceTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("دسته‌بندی خدمات:", fontSize = 12.sp, color = Color(0xFF49454F))
                            Text(order.category, fontSize = 12.sp, color = Color(0xFF1D1B20))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("تعداد سفارش:", fontSize = 12.sp, color = Color(0xFF49454F))
                            Text("${order.qty}", fontSize = 12.sp, color = Color(0xFF1D1B20))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("هزینه کل فاکتور:", fontSize = 12.sp, color = Color(0xFF49454F))
                            Text("${formatAmount(order.totalPrice)} تومان", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6750A4))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("تاریخ ثبت درخواست:", fontSize = 12.sp, color = Color(0xFF49454F))
                            Text(formatTimestamp(order.createdAt), fontSize = 11.sp, color = Color(0xFF1D1B20))
                        }

                        if (order.notes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFCAC4D0))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("یادداشت شما جهت اجرا:", fontSize = 12.sp, color = Color(0xFF49454F))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(order.notes, fontSize = 12.sp, color = Color(0xFF1D1B20), lineHeight = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Assigned Operator Details Panel
                if (order.operatorName.isNotEmpty() || order.designerName.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEADDFF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF21005D))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                val labelRole = if (order.designerName.isNotEmpty()) "طراح منتخب شما" else "اپراتور مسئول فرآیند"
                                val labelName = if (order.designerName.isNotEmpty()) order.designerName else order.operatorName
                                Text(labelRole, fontSize = 11.sp, color = Color(0xFF49454F))
                                Text(labelName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                            }
                            
                            // Direct Support Messenger CTA
                            IconButton(onClick = { onNavigateToChat(order.id) }) {
                                Icon(Icons.Default.Chat, contentDescription = "گفتگو", tint = Color(0xFF6750A4))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Delivery Panel (If status is Delivered)
                if (order.currentStatus == "Delivered" || order.currentStatus == "Completed") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF3EDF7)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6750A4))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFF6750A4))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("فایل یا نتیجه تحویلی نهایی:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6750A4))
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = order.deliveryText.ifEmpty { "نتیجه انجام کار برای شما توسط اپراتور ثبت شده است." },
                                fontSize = 13.sp,
                                color = Color(0xFF1D1B20),
                                lineHeight = 22.sp
                            )
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("لینک دانلود فایل اجرایی:", fontSize = 11.sp, color = Color(0xFF49454F))
                                Text(
                                    text = order.deliveryFile,
                                    fontSize = 11.sp,
                                    color = Color(0xFF6750A4),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {  }
                                )
                            }
                            
                            if (order.currentStatus == "Delivered") {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { showRatingDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                                ) {
                                    Icon(Icons.Default.RateReview, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("تایید نهایی کار و ثبت امتیـاز کیفیت ⭐", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Rated detail panel
                if (order.currentStatus == "Completed" && order.rating > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("بازخورد ثبت شده شما:", fontSize = 12.sp, color = Color(0xFF49454F))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) { starIndex ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (starIndex < order.rating) Color(0xFFFFCC00) else Color(0xFFCAC4D0),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            if (order.ratingComment.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "« ${order.ratingComment} »",
                                    fontSize = 13.sp,
                                    color = Color(0xFF1D1B20),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Workflow CTA Actions (Clientside simulation buttons)
                if (order.currentStatus == "PendingPayment") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (priceMismatch) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFDE8E8))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFC62828))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "موجودی کیف پول شما کافی نیست. لطفا حساب خود را شارژ فرمایید.",
                                        fontSize = 11.sp,
                                        color = Color(0xFFC62828),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Button(
                                    onClick = onNavigateToWallet,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                                ) {
                                    Text("شارژ فوری کیف پول", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text(
                                    "مبلغ فاکتور پیش‌نویس از اعتبار کیف پول کسر خواهد شد.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF49454F),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Button(
                                    onClick = {
                                        viewModel.payForOrder(order.id) { success ->
                                            // Handle paid
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                                ) {
                                    Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("پرداخت سفارش با کیف پول", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            TextButton(
                                onClick = { viewModel.refundOrder(order.id) },
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFC62828))
                            ) {
                                Text("انصراف و حذف فاکتور")
                            }
                        }
                    }
                } else {
                    // Chat Support Entry button
                    Button(
                        onClick = { onNavigateToChat(order.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3EDF7))
                    ) {
                        Icon(Icons.Default.SendToMobile, contentDescription = null, tint = Color(0xFF6750A4))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ارسال پیام به اپراتور هماهنگ کننده 💬", color = Color(0xFF6750A4), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Rating Feedback Dialog Container
        if (showRatingDialog) {
            AlertDialog(
                onDismissRequest = { showRatingDialog = false },
                title = { Text("ثبت امتیاز و کیفیت خروجی", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("لطفا امتیاز خود را مشخص کنید:", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(5) { starIndex ->
                                val starRate = starIndex + 1
                                Icon(
                                    imageVector = if (starRate <= selectedRating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (starRate <= selectedRating) Color(0xFFFFCC00) else Color(0xFFCAC4D0),
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable { selectedRating = starRate }
                                        .padding(4.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("دیدگاه تان درباره عملکرد طراح یا اپراتور...", fontSize = 11.sp, color = Color(0xFF49454F)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                focusedContainerColor = Color(0xFFF3EDF7),
                                unfocusedContainerColor = Color(0xFFF3EDF7)
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = Color(0xFF1D1B20)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.rateOrder(orderId, selectedRating, commentText)
                            showRatingDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                    ) {
                        Text("ثبت نظر", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRatingDialog = false }) {
                        Text("انصراف", color = Color(0xFF6750A4))
                    }
                }
            )
        }
    }
}

fun translateStatus(status: String): String {
    return when(status) {
        "PendingPayment" -> "در انتظار پرداخت فاکتور 🧾"
        "Paid" -> "پرداخت شده، در نوبت تخصیص فریلنسر ⏳"
        "Assigned" -> "تخصیص یافته به اپراتور 🛡️"
        "InProgress" -> "در حال آماده‌سازی و اجرا 🛠️"
        "Delivered" -> "آماده تحویل (بازبینی کلاینت) 🎉"
        "Completed" -> "تکمیل و بایگانی شده ✅"
        "Cancelled" -> "لغو شده ❌"
        "Refunded" -> "مرجوع شده (استرداد وجه) 💰"
        else -> "نامشخص"
    }
}

fun getStatusColor(status: String): Color {
    return when(status) {
        "PendingPayment" -> Color(0xFFF57C00)
        "Paid" -> Color(0xFF6750A4)
        "Assigned", "InProgress" -> Color(0xFF1976D2)
        "Delivered" -> Color(0xFF2E7D32)
        "Completed" -> Color(0xFF00796B)
        "Cancelled", "Refunded" -> Color(0xFFC62828)
        else -> Color(0xFF1D1B20)
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
