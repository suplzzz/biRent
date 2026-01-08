package com.example.birent.presentation.navigation

sealed class Screen(val route: String) {
    data object Catalog : Screen("catalog")
    data object Cart : Screen("cart")
    data object Profile : Screen("profile")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object CreateOrder : Screen("create_order")
}