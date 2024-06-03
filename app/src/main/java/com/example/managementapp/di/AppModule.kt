package com.example.managementapp.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.managementapp.auth.data.local.UserDatabase
import com.example.managementapp.auth.data.remote.api.AuthApi
import com.example.managementapp.auth.data.remote.api.TokenApi
import com.example.managementapp.auth.data.repositoryImp.AuthRepositoryImp
import com.example.managementapp.auth.domain.repository.AuthRepository
import com.example.managementapp.main.local.apartment.ApartmentDatabase
import com.example.managementapp.main.local.bill.BillDatabase
import com.example.managementapp.main.local.record.RecordDatabase
import com.example.managementapp.main.local.room.RoomDatabase
import com.example.managementapp.main.local.tenant.TenantDatabase
import com.example.managementapp.main.remote.api.ApartmentApi
import com.example.managementapp.main.remote.api.BillApi
import com.example.managementapp.main.remote.api.RecordApi
import com.example.managementapp.main.remote.api.RoomApi
import com.example.managementapp.main.remote.api.TenantApi
import com.example.managementapp.main.remote.api.UserApi
import com.example.managementapp.main.repository.apartment.ApartmentRepository
import com.example.managementapp.main.repository.apartment.ApartmentRepositoryImp
import com.example.managementapp.main.repository.bill.BillRepository
import com.example.managementapp.main.repository.bill.BillRepositoryImp
import com.example.managementapp.main.repository.record.RecordRepository
import com.example.managementapp.main.repository.record.RecordRepositoryImp
import com.example.managementapp.main.repository.room.RoomRepository
import com.example.managementapp.main.repository.room.RoomRepositoryImp
import com.example.managementapp.main.repository.tenant.TenantRepository
import com.example.managementapp.main.repository.tenant.TenantRepositoryImp
import com.example.managementapp.main.repository.user.UserRepository
import com.example.managementapp.main.repository.user.UserRepositoryImp
import com.example.managementapp.util.MyInterceptor
import com.example.managementapp.util.constant.LocalhostConst
import com.hadiyarajesh.flower_retrofit.FlowerCallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideMyInterceptor(prefs: SharedPreferences): MyInterceptor {
        return MyInterceptor(prefs)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(myInterceptor: MyInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(myInterceptor)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient) : Retrofit{
        return Retrofit.Builder()
            .baseUrl("http://${LocalhostConst.ipAddress}:5000/api/v1/")
            .client(okHttpClient)
            .addCallAdapterFactory(FlowerCallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideLoginApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideApartmentApi(retrofit: Retrofit): ApartmentApi = retrofit.create(ApartmentApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideRoomApi(retrofit: Retrofit): RoomApi = retrofit.create(RoomApi::class.java)

    @Provides
    @Singleton
    fun provideTenantApi(retrofit: Retrofit): TenantApi = retrofit.create(TenantApi::class.java)

    @Provides
    @Singleton
    fun provideBillApi(retrofit: Retrofit): BillApi = retrofit.create(BillApi::class.java)

    @Provides
    @Singleton
    fun provideRecordApi(retrofit: Retrofit): RecordApi = retrofit.create(RecordApi::class.java)

    @Provides
    @Singleton
    fun provideSharedPref(app: Application): SharedPreferences{
        return app.getSharedPreferences("prefs", MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun bindAuthRepository(authApi: AuthApi, prefs: SharedPreferences, userDatabase: UserDatabase): AuthRepository {
        return AuthRepositoryImp(authApi, prefs, userDatabase)
    }

    @Provides
    @Singleton
    fun bindApartmentRepository(apartmentApi: ApartmentApi, apartmentDatabase: ApartmentDatabase): ApartmentRepository{
        return ApartmentRepositoryImp(apartmentApi, apartmentDatabase)
    }

    @Provides
    @Singleton
    fun bindUserRepository(userDatabase: UserDatabase, userApi: UserApi): UserRepository{
        return UserRepositoryImp(userDatabase, userApi)
    }

    @Provides
    @Singleton
    fun bindRoomRepository(roomDatabase: RoomDatabase, roomApi: RoomApi): RoomRepository{
        return RoomRepositoryImp(roomDatabase, roomApi)
    }

    @Provides
    @Singleton
    fun bindTenantRepository(tenantDatabase: TenantDatabase, tenantApi: TenantApi): TenantRepository{
        return TenantRepositoryImp(tenantDatabase, tenantApi)
    }

    @Provides
    @Singleton
    fun bindRecordRepository(recordDatabase: RecordDatabase, recordApi: RecordApi): RecordRepository{
        return RecordRepositoryImp(recordDatabase, recordApi)
    }

    @Provides
    @Singleton
    fun bindBillRepository(billDatabase: BillDatabase, billApi: BillApi): BillRepository {
        return BillRepositoryImp(billDatabase, billApi)
    }

//    private val userDatabase: UserDatabase,
//    private val userApi : UserApi

    @Provides
    @Singleton
    fun provideUserDatabase(app:Application): UserDatabase{
        return Room.databaseBuilder(
            app,
            UserDatabase::class.java,
            "user_db.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideApartmentDatabase(app: Application): ApartmentDatabase{
        return Room.databaseBuilder(
            app,
            ApartmentDatabase::class.java,
            "apartment_db.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(app: Application): RoomDatabase{
        return Room.databaseBuilder(
            app,
            RoomDatabase::class.java,
            "room_db.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTenantDatabase(app: Application): TenantDatabase{
        return Room.databaseBuilder(
            app,
            TenantDatabase::class.java,
            "tenant_db.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideBillDatabase(app: Application): BillDatabase{
        return Room.databaseBuilder(
            app,
            BillDatabase::class.java,
            "bill_db.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRecordDatabase(app: Application): RecordDatabase{
        return Room.databaseBuilder(
            app,
            RecordDatabase::class.java,
            "record_db.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}