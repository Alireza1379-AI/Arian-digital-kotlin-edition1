package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.AryanViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: AryanViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable("login") {
                            LoginScreen(
                                viewModel = viewModel,
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToServiceDetail = { serviceId ->
                                    navController.navigate("service_detail/$serviceId")
                                },
                                onNavigateToWallet = {
                                    navController.navigate("wallet")
                                },
                                onNavigateToDashboard = {
                                    navController.navigate("dashboard")
                                }
                            )
                        }

                        composable(
                            route = "service_detail/{serviceId}",
                            arguments = listOf(navArgument("serviceId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val serviceId = backStackEntry.arguments?.getInt("serviceId") ?: 1
                            ServiceDetailScreen(
                                serviceId = serviceId,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToOrderTracking = { orderId ->
                                    navController.navigate("order_tracking/$orderId") {
                                        popUpTo("home") { saveState = true }
                                    }
                                }
                            )
                        }

                        composable(
                            route = "order_tracking/{orderId}",
                            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 1
                            OrderTrackingScreen(
                                orderId = orderId,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToChat = { id ->
                                    navController.navigate("chat/$id")
                                },
                                onNavigateToWallet = {
                                    navController.navigate("wallet")
                                }
                            )
                        }

                        composable(
                            route = "chat/{orderId}",
                            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
                            MessageScreen(
                                orderId = orderId,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("wallet") {
                            WalletScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("dashboard") {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToOrderTracking = { id ->
                                    navController.navigate("order_tracking/$id")
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
