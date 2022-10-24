package tech.constanta.android.delegates.sample.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import tech.constanta.android.delegates.sample.ui.cart.CartVMDelegate
import tech.constanta.android.delegates.sample.ui.cart.CartVMDelegateImpl

@Module
@InstallIn(ViewModelComponent::class)
interface CartVMDelegateModule {
    @Binds
    @ViewModelScoped
    fun bindCartVMDelegate(
        cartVMDelegate: CartVMDelegateImpl
    ): CartVMDelegate
}