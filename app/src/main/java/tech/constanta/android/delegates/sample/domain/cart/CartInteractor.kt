package tech.constanta.android.delegates.sample.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import tech.constanta.android.delegates.sample.domain.catalog.model.CatalogItem
import tech.constanta.android.delegates.sample.domain.model.CartItem
import javax.inject.Inject

interface CartInteractor {

    val cartItems: Flow<List<CartItem>>

    val totalItemsCount: Flow<Int>

    suspend fun addCatalogItem(item: CatalogItem)

    suspend fun removeCartItem(item: CartItem)
}

class CartInteractorImpl @Inject constructor(

) : CartInteractor {
    override val cartItems: MutableStateFlow<List<CartItem>> = MutableStateFlow(emptyList())
    override val totalItemsCount: Flow<Int> = cartItems.map { items ->
        items.size
    }

    override suspend fun addCatalogItem(item: CatalogItem) {
        val currentItems = cartItems.value.toMutableList()
        currentItems.add(CartItem(
            id = "${System.currentTimeMillis()}",
            catalogItem = item,
        ))
        cartItems.emit(currentItems)
    }

    override suspend fun removeCartItem(item: CartItem) {
        val currentItems = cartItems.value.toMutableList()
        currentItems.remove(item)
        cartItems.emit(currentItems)
    }

}