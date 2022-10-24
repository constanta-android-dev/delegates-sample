package tech.constanta.android.delegates.sample.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.constanta.android.delegates.sample.domain.CartInteractor
import tech.constanta.android.delegates.sample.domain.CartInteractorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CartInteractorModule {

    @Binds
    @Singleton
    fun bindCartInteractor(
        cartInteractor: CartInteractorImpl
    ): CartInteractor
}