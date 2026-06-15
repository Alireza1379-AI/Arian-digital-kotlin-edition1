package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.database.ServiceEntity
import com.example.ui.AryanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: Int,
    viewModel: AryanViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToOrderTracking: (Int) -> Unit
) {
    val services by viewModel.services.collectAsState()
    val service = services.find { it.id == serviceId }

    if (service == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("سرویس مورد نظر یافت نشد.", color = Color(0xFF1D1B20))
        }
        return
    }

    var qty by remember { mutableStateOf(service.minQty) }
    var notes by remember { mutableStateOf("") }
    var giftCardValue by remember { mutableStateOf(10) } // For gift cards: 10, 20, 50, 100 dollars
    var errorText by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    // Calculate live pricing in realtime
    val isGiftCard = service.category == "گیفت کارت"
    val liveComputedPrice = if (isGiftCard) {
        val dollarExchangeRate = 65000L
        (giftCardValue * dollarExchangeRate) + (service.basePrice * (giftCardValue / 10L))
    } else {
        service.basePrice * qty
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(service.title, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Header Details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text(service.category, color = Color(0xFF6750A4), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFF3EDF7))
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "توضیحات خدمات:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = service.description,
                            fontSize = 13.sp,
                            color = Color(0xFF1D1B20),
                            lineHeight = 22.sp
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Limit Info Panel
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFF4E5))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFB76E00))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = service.limitInfo,
                                color = Color(0xFF663C00),
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User input form card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "تنظیم جزئیات سفارش:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isGiftCard) {
                            // Gift card dollar value picker
                            Text("انتخاب مقدار دلاری گیفت کارت:", fontSize = 12.sp, color = Color(0xFF49454F))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(10, 20, 50, 100).forEach { value ->
                                    val isSelected = giftCardValue == value
                                    Button(
                                        onClick = { giftCardValue = value },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) Color(0xFF6750A4) else Color(0xFFF3EDF7)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "$$value",
                                            color = if (isSelected) Color.White else Color(0xFF6750A4),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        } else {
                            // Standard Quantity Picker
                            val qtyLabel = when(service.unitType) {
                                "صفحه" -> "تعداد صفحات"
                                "نمونه" -> "تعداد نمونه/اتودها"
                                else -> "تعداد کار"
                            }
                            Text("$qtyLabel:", fontSize = 12.sp, color = Color(0xFF49454F))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = { 
                                        if (qty > 1) {
                                            qty--
                                            errorText = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3EDF7))
                                ) {
                                    Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6750A4))
                                }
                                Text(
                                    qty.toString(),
                                    color = Color(0xFF1D1B20),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Button(
                                    onClick = { 
                                        // Validate category-specific bounds
                                        var reachedMax = false
                                        if (service.title.contains("بروشور") && qty >= 8) {
                                            errorText = "حداکثر سقف سفارش برای بروشور داخلی ۸ صفحه است."
                                            reachedMax = true
                                        } else if (service.title.contains("کاتالوگ") && qty >= 16) {
                                            errorText = "حداکثر سقف سفارش برای کاتالوگ داخلی ۱۶ صفحه است."
                                            reachedMax = true
                                        } else if (service.title.contains("پست") && qty >= 5) {
                                            errorText = "حداکثر سقف سفارش برای پست و استوری ۵ نمونه است."
                                            reachedMax = true
                                        }

                                        if (!reachedMax) {
                                            qty++
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                                ) {
                                    Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        if (errorText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(errorText, color = Color(0xFFC62828), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Instructions
                        Text("توضیحات و فایل‌های ضمیمه:", fontSize = 12.sp, color = Color(0xFF49454F))
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = { Text("مثال: متن برای تایپ، آدرس ایمیل انتخابی، لینک تصویر لوگو برای ویرایش و...", color = Color(0xFF49454F)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                focusedContainerColor = Color(0xFFF3EDF7),
                                unfocusedContainerColor = Color(0xFFF3EDF7)
                            ),
                            textStyle = LocalTextStyle.current.copy(color = Color(0xFF1D1B20), fontSize = 13.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price checkout summary card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("پیش‌فاکتور سفارش:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("کارمزد پایه (${service.unitType}):", fontSize = 12.sp, color = Color(0xFF49454F))
                            Text("${formatAmount(service.basePrice)} تومان", fontSize = 12.sp, color = Color(0xFF1D1B20))
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("تعداد کل سفارش:", fontSize = 12.sp, color = Color(0xFF49454F))
                            Text(if (isGiftCard) "$$giftCardValue" else "$qty ${service.unitType}", fontSize = 12.sp, color = Color(0xFF1D1B20))
                        }

                        if (isGiftCard) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("قیمت پایه دلار ($1 = 65,000T):", fontSize = 12.sp, color = Color(0xFF49454F))
                                Text("${formatAmount(giftCardValue * 65000L)} تومان", fontSize = 12.sp, color = Color(0xFF1D1B20))
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFCAC4D0))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("هزینه قابل پرداخت نهایی:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                            Text(
                                "${formatAmount(liveComputedPrice)} تومان",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6750A4)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                val finalQty = if (isGiftCard) giftCardValue else qty
                                viewModel.placeOrder(service, finalQty, notes) { orderId ->
                                    onNavigateToOrderTracking(orderId)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("ثبت نهایی سفارش و فاکتور پیش‌نویس", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
