package com.example.birent.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.birent.presentation.MainViewModel
import com.example.birent.presentation.screen.auth.login.LoginScreen
import com.example.birent.presentation.screen.auth.register.RegisterScreen
import com.example.birent.presentation.screen.cart.CartScreen
import com.example.birent.presentation.screen.catalog.CatalogScreen
import com.example.birent.presentation.screen.order.CreateOrderScreen
import com.example.birent.presentation.screen.profile.ProfileScreen

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute !in listOf(Screen.Login.route, Screen.Register.route, Screen.CreateOrder.route)) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, "Catalog") },
                        label = { Text("Каталог") },
                        selected = currentRoute == Screen.Catalog.route,
                        onClick = {
                            navController.navigate(Screen.Catalog.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.ShoppingCart, "Cart") },
                        label = { Text("Корзина") },
                        selected = currentRoute == Screen.Cart.route,
                        onClick = {
                            navController.navigate(Screen.Cart.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, "Profile") },
                        label = { Text("Профиль") },
                        selected = currentRoute == Screen.Profile.route,
                        onClick = {
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Catalog.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Catalog.route) {
                CatalogScreen()
            }

            composable(Screen.Cart.route) {
                CartScreen(
                    onNavigateToCreateOrder = { navController.navigate(Screen.CreateOrder.route) },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) }
                )
            }

            composable(Screen.CreateOrder.route) {
                CreateOrderScreen(
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.Catalog.route)
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = { navController.popBackStack() }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Catalog.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}