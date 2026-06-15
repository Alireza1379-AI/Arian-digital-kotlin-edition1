package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ServiceEntity
import com.example.ui.AryanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AryanViewModel,
    onNavigateToServiceDetail: (Int) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    val services by viewModel.services.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val categories = listOf("همه", "خدمات پرداخت", "خدمات اداری", "طراحی گرافیک", "گیفت کارت")

    // Filter services based on search text and selected category
    val filteredServices = services.filter { service ->
        val matchesSearch = service.title.contains(searchQuery, ignoreCase = true) || 
                            service.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "همه" || service.category == selectedCategory
        matchesSearch && matchesCategory
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Column {
                            Text(
                                "آرین دیجیتال",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1B20),
                                fontSize = 24.sp
                            )
                            Text(
                                "سامانه جامع ارائه و پرداخت سریع خدمات برخط",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF49454F)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color(0xFFFEF7FF),
                        titleContentColor = Color(0xFF1D1B20)
                    ),
                    actions = {
                        // Profile/Role badge click
                        AssistChip(
                            onClick = onNavigateToDashboard,
                            label = { 
                                Text(
                                    text = when(userProfile?.role) {
                                        "OPERATOR" -> "پنل اپراتور 👷"
                                        "DESIGNER" -> "پنل طراح 🎨"
                                        "ADMIN" -> "مدیریت ⚙️"
                                        else -> "مشتری👤"
                                    },
                                    color = Color(0xFF6750A4),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFFF3EDF7)
                            )
                        )
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFFF3EDF7),
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { /* Stay here */ },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("خدمات", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D192B),
                            selectedTextColor = Color(0xFF1D192B),
                            indicatorColor = Color(0xFFEADDFF),
                            unselectedTextColor = Color(0xFF49454F),
                            unselectedIconColor = Color(0xFF49454F)
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToDashboard,
                        icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                        label = { Text("سفارش‌های من", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedTextColor = Color(0xFF49454F),
                            unselectedIconColor = Color(0xFF49454F)
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToWallet,
                        icon = { Icon(Icons.Default.Wallet, contentDescription = null) },
                        label = { Text("کیف پول", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedTextColor = Color(0xFF49454F),
                            unselectedIconColor = Color(0xFF49454F)
                        )
                    )
                }
            },
            containerColor = Color(0xFFFEF7FF)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFFEF7FF))
            ) {
                // Balance Banner & Search Box
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8DEF8))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "خوش‌آمدید، ${userProfile?.name ?: "کاربر گرامی"}",
                                    fontSize = 14.sp,
                                    color = Color(0xFF1D1B20),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "موجودی کیف پول شما",
                                    fontSize = 11.sp,
                                    color = Color(0xFF49454F),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = formatAmount(userProfile?.balance ?: 0),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6750A4)
                                )
                                Text(
                                    text = "تومان",
                                    fontSize = 10.sp,
                                    color = Color(0xFF49454F)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Custom elegant search field
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("جستجو در خدمات اداری، لوگو، گیفت‌کارت...", color = Color(0xFF49454F), fontSize = 13.sp) },
                            textStyle = LocalTextStyle.current.copy(color = Color(0xFF1D1B20), fontSize = 14.sp),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF6750A4)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                focusedContainerColor = Color(0xFFF3EDF7),
                                unfocusedContainerColor = Color(0xFFF3EDF7)
                            )
                        )
                    }
                }

                // Category badges horizontal list
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color(0xFF6750A4) else Color(0xFFF3EDF7))
                                .clickable { viewModel.setCategory(category) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) Color.White else Color(0xFF49454F),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                if (filteredServices.isEmpty()) {
                    // Empty state visual
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF6750A4),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "هیچ خدمتی در این دسته‌بندی یافت نشد.",
                                color = Color(0xFF49454F),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    // Service catalog items list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredServices) { service ->
                            ServiceItem(
                                service = service,
                                onClick = { onNavigateToServiceDetail(service.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceItem(
    service: ServiceEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon box - styling extracted from Professional Polish HTML markup
            val (boxBg, iconColor) = when(service.category) {
                "خدمات پرداخت" -> Color(0xFFEADDFF) to Color(0xFF21005D)
                "گیفت کارت" -> Color(0xFFFDE293) to Color(0xFF574100)
                "طراحی گرافیک" -> Color(0xFFB1F0AD) to Color(0xFF002204)
                "خدمات اداری" -> Color(0xFFFAD8FD) to Color(0xFF35003B)
                else -> Color(0xFFD3E4FF) to Color(0xFF001D36)
            }
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(boxBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(service.category) {
                        "خدمات پرداخت" -> Icons.Default.Payment
                        "خدمات اداری" -> Icons.Default.Assignment
                        "طراحی گرافیک" -> Icons.Default.Brush
                        "گیفت کارت" -> Icons.Default.ConfirmationNumber
                        else -> Icons.Default.Work
                    },
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = service.description,
                    fontSize = 12.sp,
                    color = Color(0xFF49454F),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "کارمزد شروع از: ",
                        fontSize = 10.sp,
                        color = Color(0xFF49454F)
                    )
                    Text(
                        text = formatAmount(service.basePrice),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6750A4)
                    )
                    Text(
                        text = " تومان (به ازای ${service.unitType})",
                        fontSize = 10.sp,
                        color = Color(0xFF49454F)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF6750A4)
            )
        }
    }
}

// Global amount helper format
fun formatAmount(amount: Long): String {
    return String.format("%,d", amount)
}
