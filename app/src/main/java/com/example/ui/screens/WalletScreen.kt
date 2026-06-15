package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AryanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: AryanViewModel,
    onNavigateBack: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var showDepositSuccessMsg by remember { mutableStateOf(false) }
    var depositValueText by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("کیف پول آرین دیجیتال", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
                // Glow Credit Wallet Card - Stylized brand gradient
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF6750A4), Color(0xFF21005D))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("شارژ پرداخت درون‌برنامه‌ای", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White)
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = "${formatAmount(userProfile?.balance ?: 0)} تومان",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Text(
                                text = "کلاینت فعال: ${userProfile?.name ?: "سید آرین محمدی"}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Fast credit refill card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("افزایش فوری موجودی:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Pair(50000L, "+۵۰ هزار"),
                                Pair(100000L, "+۱۰۰ هزار"),
                                Pair(250000L, "+۲۵۰ هزار")
                            ).forEach { item ->
                                Button(
                                    onClick = {
                                        viewModel.addWalletBalance(item.first)
                                        showDepositSuccessMsg = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3EDF7)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(item.second, fontSize = 11.sp, color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Custom deposit row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = depositValueText,
                                onValueChange = { if (it.all { char -> char.isDigit() }) depositValueText = it },
                                placeholder = { Text("مبلغ دلخواه (تومان)", color = Color(0xFF49454F), fontSize = 12.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                textStyle = LocalTextStyle.current.copy(color = Color(0xFF1D1B20), fontSize = 13.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6750A4),
                                    unfocusedBorderColor = Color(0xFFCAC4D0),
                                    focusedContainerColor = Color(0xFFF3EDF7),
                                    unfocusedContainerColor = Color(0xFFF3EDF7)
                                )
                            )
                            Button(
                                onClick = {
                                    val amount = depositValueText.toLongOrNull() ?: 0L
                                    if (amount > 0) {
                                        viewModel.addWalletBalance(amount)
                                        showDepositSuccessMsg = true
                                        depositValueText = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("پرداخت بانکی", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        if (showDepositSuccessMsg) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "تراکنش شبیه‌ساز با موفقیت انجام شد و موجودی به کارت واریز گردید 🎉",
                                color = Color(0xFF2E7D32),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("تاریخچه کلی تراکنش‌های کیف پول:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable transactional records list
                if (transactions.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("طی این دوره تراکنشی ثبت نگردیده است.", color = Color(0xFF49454F), fontSize = 12.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(transactions) { txn ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(txn.description, color = Color(0xFF1D1B20), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(formatTimestamp(txn.timestamp), color = Color(0xFF49454F), fontSize = 10.sp)
                                    }
                                    
                                    val isDeposit = txn.type == "DEPOSIT" || txn.type == "REFUND"
                                    val amtSign = if (isDeposit) "+" else ""
                                    val amtColor = if (isDeposit) Color(0xFF2E7D32) else Color(0xFFC62828)
                                    
                                    Text(
                                        text = "$amtSign${formatAmount(txn.amount)} T",
                                        color = amtColor,
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
    }
}
