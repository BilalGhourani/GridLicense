package com.grid.gridlicense.di

import com.grid.gridlicense.data.client.ClientRepository
import com.grid.gridlicense.data.client.ClientRepositoryImpl
import com.grid.gridlicense.data.license.LicenseRepository
import com.grid.gridlicense.data.license.LicenseRepositoryImpl
import com.grid.gridlicense.data.user.UserRepository
import com.grid.gridlicense.data.user.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideClientRepository(): ClientRepository {
        return ClientRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideLicenseRepository(): LicenseRepository {
        return LicenseRepositoryImpl()
    }
}