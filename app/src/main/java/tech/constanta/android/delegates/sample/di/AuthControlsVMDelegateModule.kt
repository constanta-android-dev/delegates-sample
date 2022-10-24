package tech.constanta.android.delegates.sample.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import tech.constanta.android.delegates.sample.ui.auth.AuthControlsVMDelegate
import tech.constanta.android.delegates.sample.ui.auth.AuthControlsVMDelegateImpl

@Module
@InstallIn(ViewModelComponent::class)
interface AuthControlsVMDelegateModule {
    @Binds
    @ViewModelScoped
    fun bindAuthControlsVMDelegate(
        authControlsVMDelegate: AuthControlsVMDelegateImpl
    ): AuthControlsVMDelegate
}