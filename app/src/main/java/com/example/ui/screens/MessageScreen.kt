package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.SupportMessage
import com.example.ui.AryanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    orderId: Int,
    viewModel: AryanViewModel,
    onNavigateBack: () -> Unit
) {
    val orderDetails by viewModel.currentOrderDetails.collectAsState(initial = null)
    val messages by viewModel.currentOrderMessages.collectAsState(initial = emptyList())
    val userProfile by viewModel.userProfile.collectAsState()

    var textInput by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("مکالمه پیگیری سفارش #${orderId}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(orderDetails?.serviceTitle ?: "در حال بارگذاری...", fontSize = 11.sp, color = Color(0xFF6750A4), fontWeight = FontWeight.SemiBold)
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
            ) {
                // Info banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3EDF7))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "ارتباط مستقیم برای هماهنگی و تسریع امور اداری و پرداخت نهایی با پشتیبان اختصاصی آرین دیجیتال.",
                        fontSize = 11.sp,
                        color = Color(0xFF49454F),
                        lineHeight = 16.sp
                    )
                }

                // Messages Chat area
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { message ->
                        ChatBubble(message = message, currentUserRole = userProfile?.role ?: "CUSTOMER")
                    }
                }

                // Input bar container with Top Border
                Column {
                    HorizontalDivider(color = Color(0xFFCAC4D0))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text("پیغام خود را بنویسید...", color = Color(0xFF49454F), fontSize = 13.sp) },
                            textStyle = LocalTextStyle.current.copy(color = Color(0xFF1D1B20), fontSize = 13.sp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                focusedContainerColor = Color(0xFFF3EDF7),
                                unfocusedContainerColor = Color(0xFFF3EDF7)
                            )
                        )

                        FloatingActionButton(
                            onClick = {
                                if (textInput.trim().isNotEmpty()) {
                                    val currentRole = userProfile?.role ?: "CUSTOMER"
                                    val currentName = userProfile?.name ?: "کاربر سیستم"
                                    
                                    viewModel.sendSupportMessage(
                                        orderId = orderId,
                                        senderRole = currentRole,
                                        senderName = currentName,
                                        text = textInput.trim()
                                    )
                                    textInput = ""
                                }
                            },
                            containerColor = Color(0xFF6750A4),
                            contentColor = Color.White,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "ارسال")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: SupportMessage,
    currentUserRole: String
) {
    val isMyMessage = message.senderRole == currentUserRole
    
    val alignment = if (isMyMessage) Alignment.End else Alignment.Start
    val bubbleColor = when(message.senderRole) {
        "CUSTOMER" -> Color(0xFFEADDFF)
        "OPERATOR" -> Color(0xFFFFECD1)
        "DESIGNER" -> Color(0xFFE8DEF8)
        "ADMIN" -> Color(0xFFFFDAD9)
        else -> Color(0xFFF3EDF7)
    }

    val roleTitle = when(message.senderRole) {
        "CUSTOMER" -> "کلاینت 👤"
        "OPERATOR" -> "اپراتور 👷"
        "DESIGNER" -> "طراح 🎨"
        "ADMIN" -> "مدیریت ⚙️"
        else -> "پشتیبان"
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(
            modifier = Modifier.padding(bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${message.senderName} ($roleTitle)",
                fontSize = 11.sp,
                color = Color(0xFF49454F),
                fontWeight = FontWeight.Bold
            )
        }
        
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isMyMessage) 12.dp else 0.dp,
                        bottomEnd = if (isMyMessage) 0.dp else 12.dp
                    )
                )
                .background(bubbleColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.text,
                color = Color(0xFF1D1B20),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}
