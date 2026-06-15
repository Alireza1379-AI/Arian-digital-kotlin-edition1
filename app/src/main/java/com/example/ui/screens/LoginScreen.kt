package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AryanViewModel

@Composable
fun LoginScreen(
    viewModel: AryanViewModel,
    onNavigateToHome: () -> Unit
) {
    var phoneNumber by remember { mutableStateFlowOf("09123456789") }
    var otpCode by remember { mutableStateFlowOf("") }
    var isOtpSent by remember { mutableStateFlowOf(false) }
    var selectedRole by remember { mutableStateFlowOf("CUSTOMER") }
    var errorMessage by remember { mutableStateFlowOf("") }

    val userProfile by viewModel.userProfile.collectAsState()

    // Extract roles for switching easily
    val roles = listOf(
        Pair("CUSTOMER", "مشتری"),
        Pair("OPERATOR", "اپراتور"),
        Pair("DESIGNER", "طراح گرافیک"),
        Pair("ADMIN", "مدیر سیستم")
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFEF7FF),
                            Color(0xFFF3EDF7)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ورود به آرین دیجیتال",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "پلتفرم چندنقشی هوشمند ارائه خدمات آنلاین",
                        fontSize = 13.sp,
                        color = Color(0xFF6750A4),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (!isOtpSent) {
                        // Phone Number Step
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) {
                                    phoneNumber = it
                                }
                            },
                            label = { Text("شماره موبایل", color = Color(0xFF49454F)) },
                            textStyle = LocalTextStyle.current.copy(color = Color(0xFF1D1B20)),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                focusedContainerColor = Color(0xFFF3EDF7),
                                unfocusedContainerColor = Color(0xFFF3EDF7)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "انتخاب نقش جهت ورود (شبیه‌ساز تستی):",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1D1B20),
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Roles switcher chips
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            roles.chunked(2).forEach { rowRoles ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowRoles.forEach { role ->
                                        FilterChip(
                                            selected = selectedRole == role.first,
                                            onClick = { selectedRole = role.first },
                                            label = {
                                                Text(
                                                    role.second,
                                                    fontSize = 12.sp,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textAlign = TextAlign.Center
                                                )
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF6750A4),
                                                selectedLabelColor = Color.White,
                                                containerColor = Color(0xFFF3EDF7),
                                                labelColor = Color(0xFF49454F)
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (phoneNumber.length < 11 || !phoneNumber.startsWith("09")) {
                                    errorMessage = "شماره همراه باید ۱۱ رقم و با ۰۹ آغاز شود."
                                } else {
                                    errorMessage = ""
                                    isOtpSent = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("دریافت کد تایید", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else {
                        // OTP Code Step
                        Text(
                            text = "کد تایید ۴ رقمی به شماره $phoneNumber ارسال شد. (هر کدی وارد کنید تایید می‌شود)",
                            fontSize = 13.sp,
                            color = Color(0xFF49454F),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = {
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    otpCode = it
                                }
                            },
                            placeholder = { Text("کد تایید", color = Color(0xFF49454F)) },
                            textStyle = LocalTextStyle.current.copy(
                                color = Color(0xFF1D1B20),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            ),
                            modifier = Modifier.fillMaxWidth(0.6f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                focusedContainerColor = Color(0xFFF3EDF7),
                                unfocusedContainerColor = Color(0xFFF3EDF7)
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                viewModel.switchUserRole(selectedRole)
                                onNavigateToHome()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("ورود به سیستم", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = { isOtpSent = false },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF6750A4))
                        ) {
                            Text("ویرایش شماره تماس")
                        }
                    }

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// Fixed mutableStateOf typo
fun <T> mutableStateFlowOf(value: T): MutableState<T> = mutableStateOf(value)
