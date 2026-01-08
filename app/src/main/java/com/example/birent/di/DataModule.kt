package com.example.birent.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.birent.data.local.AppDatabase
import com.example.birent.data.local.BikeType
import com.example.birent.data.local.dao.BikeDao
import com.example.birent.data.local.dao.CartDao
import com.example.birent.data.local.dao.OrderDao
import com.example.birent.data.local.dao.UserDao
import com.example.birent.data.local.entity.BikeEntity
import com.example.birent.data.local.entity.PickupPointEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        bikeDaoProvider: Provider<BikeDao>,
        orderDaoProvider: Provider<OrderDao>
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "birent.db"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDb(bikeDaoProvider.get(), orderDaoProvider.get())
                    }
                }
            })
            .build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideBikeDao(db: AppDatabase): BikeDao = db.bikeDao()

    @Provides
    fun provideCartDao(db: AppDatabase): CartDao = db.cartDao()

    @Provides
    fun provideOrderDao(db: AppDatabase): OrderDao = db.orderDao()

    private suspend fun populateDb(bikeDao: BikeDao, orderDao: OrderDao) {
        val bikes = listOf(
            BikeEntity(
                model = "Stels Navigator 500",
                type = BikeType.MOUNTAIN,
                color = "Красный",
                frameSize = "19 inch",
                speeds = 21,
                priceHour = 200.0,
                priceDay = 1000.0,
                totalQuantity = 10,
                description = "Надежный горный велосипед для пересеченной местности.",
                imageUrl = "stels_nav"
            ),
            BikeEntity(
                model = "Giant Escape 3",
                type = BikeType.CITY,
                color = "Черный",
                frameSize = "L",
                speeds = 7,
                priceHour = 250.0,
                priceDay = 1200.0,
                totalQuantity = 5,
                description = "Легкий городской велосипед для комфортной езды.",
                imageUrl = "giant_esc"
            ),
            BikeEntity(
                model = "Merida Scultura",
                type = BikeType.ROAD,
                color = "Синий",
                frameSize = "M",
                speeds = 18,
                priceHour = 400.0,
                priceDay = 2000.0,
                totalQuantity = 3,
                description = "Шоссейный велосипед для скоростной езды по асфальту.",
                imageUrl = "merida_sc"
            ),
            BikeEntity(
                model = "Xiaomi Himo C20",
                type = BikeType.ELECTRIC,
                color = "Белый",
                frameSize = "Universal",
                speeds = 6,
                priceHour = 500.0,
                priceDay = 2500.0,
                totalQuantity = 4,
                description = "Электровелосипед с запасом хода до 80 км.",
                imageUrl = "xiaomi_himo"
            )
        )
        bikeDao.insertBikes(bikes)

        val points = listOf(
            PickupPointEntity(name = "Центральный парк", address = "ул. Ленина, 1"),
            PickupPointEntity(name = "Набережная", address = "ул. Речная, 15"),
            PickupPointEntity(name = "ТЦ Плаза", address = "пр. Мира, 100")
        )
        orderDao.insertPickupPoints(points)
    }
}