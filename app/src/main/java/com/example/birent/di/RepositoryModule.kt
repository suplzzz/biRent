package com.example.birent.di

import com.example.birent.data.repository.AuthRepositoryImpl
import com.example.birent.data.repository.BikeRepositoryImpl
import com.example.birent.data.repository.CartRepositoryImpl
import com.example.birent.data.repository.OrderRepositoryImpl
import com.example.birent.domain.repository.AuthRepository
import com.example.birent.domain.repository.BikeRepository
import com.example.birent.domain.repository.CartRepository
import com.example.birent.domain.repository.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindBikeRepository(impl: BikeRepositoryImpl): BikeRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository
}