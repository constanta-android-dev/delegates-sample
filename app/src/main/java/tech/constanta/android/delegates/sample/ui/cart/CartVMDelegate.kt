package tech.constanta.android.delegates.sample.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.domain.CartInteractor
import tech.constanta.android.delegates.sample.domain.catalog.model.CatalogItem
import tech.constanta.android.delegates.sample.domain.model.CartItem
import javax.inject.Inject

interface CartVMDelegate {
    val cartItems: Flow<List<CartItem>>
    val cartItemsCount: Flow<Int>

    fun addToCart(item: CatalogItem)

    fun removeCartItem(item: CartItem)
}

class CartVMDelegateImpl @Inject constructor(
    private val cartInteractor: CartInteractor,
) : ViewModel(), CartVMDelegate {

    override val cartItems: Flow<List<CartItem>> = cartInteractor.cartItems
    override val cartItemsCount: Flow<Int> = cartInteractor.totalItemsCount

    override fun addToCart(item: CatalogItem) {
        viewModelScope.launch {
            cartInteractor.addCatalogItem(item)
        }
    }

    override fun removeCartItem(item: CartItem) {
        viewModelScope.launch {
            cartInteractor.removeCartItem(item)
        }
    }
}