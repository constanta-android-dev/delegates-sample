package tech.constanta.android.delegates.sample.ui.catalog.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.domain.CartInteractor
import tech.constanta.android.delegates.sample.domain.catalog.CatalogInteractor
import tech.constanta.android.delegates.sample.domain.catalog.model.CatalogItem
import tech.constanta.android.delegates.sample.domain.model.CartItem
import javax.inject.Inject

@HiltViewModel
class CatalogListViewModel @Inject constructor(
    private val catalogInteractor: CatalogInteractor,
    private val cartInteractor: CartInteractor,
) : ViewModel() {

    val _catalogItems: MutableStateFlow<List<CatalogItem>> = MutableStateFlow(emptyList())
    val catalogItems: Flow<List<CatalogItem>> = _catalogItems
    val cartItems: Flow<List<CartItem>> = cartInteractor.cartItems
    val cartItemsCount: Flow<Int> = cartInteractor.totalItemsCount

    init {
        viewModelScope.launch {
            _catalogItems.emit(
                catalogInteractor.getCatalogItems()
            )
        }
    }

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