package tech.constanta.android.delegates.sample.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.domain.CartInteractor
import tech.constanta.android.delegates.sample.domain.catalog.model.CatalogItem
import tech.constanta.android.delegates.sample.domain.model.CartItem

abstract class BaseCartViewModel(
    private val cartInteractor: CartInteractor,
) : ViewModel() {

    val cartItems: Flow<List<CartItem>> = cartInteractor.cartItems
    val cartItemsCount: Flow<Int> = cartInteractor.totalItemsCount

    fun addToCart(item: CatalogItem) {
        viewModelScope.launch {
            cartInteractor.addCatalogItem(item)
        }
    }

    fun removeCartItem(item: CartItem) {
        viewModelScope.launch {
            cartInteractor.removeCartItem(item)
        }
    }
}