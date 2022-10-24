package tech.constanta.android.delegates.sample.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.constanta.android.delegates.sample.domain.auth.AuthInteractor
import tech.constanta.android.delegates.sample.domain.auth.AuthInteractorImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface AuthInteractorModule {

    @Binds
    @Singleton
    fun bindAuthInteractor(
        AuthInteractor: AuthInteractorImpl
    ): AuthInteractor

}