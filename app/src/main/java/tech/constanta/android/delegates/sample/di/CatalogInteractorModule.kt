package tech.constanta.android.delegates.sample.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.constanta.android.delegates.sample.domain.CartInteractor
import tech.constanta.android.delegates.sample.domain.CartInteractorImpl
import tech.constanta.android.delegates.sample.domain.catalog.CatalogInteractor
import tech.constanta.android.delegates.sample.domain.catalog.CatalogInteractorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CatalogInteractorModule {

    @Binds
    @Singleton
    fun bindCatalogInteractor(
        CatalogInteractor: CatalogInteractorImpl
    ): CatalogInteractor
}