package tech.constanta.android.delegates.sample.ui.catalog.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.domain.catalog.CatalogInteractor
import tech.constanta.android.delegates.sample.domain.catalog.model.CatalogItem
import tech.constanta.android.delegates.sample.ui.auth.AuthControlsVMDelegate
import tech.constanta.android.delegates.sample.ui.cart.CartVMDelegate
import javax.inject.Inject

@HiltViewModel
class CatalogDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val catalogInteractor: CatalogInteractor,
    private val cartVMDelegate: CartVMDelegate,
    private val authControlsVMDelegate: AuthControlsVMDelegate,
) : ViewModel(),
    AuthControlsVMDelegate by authControlsVMDelegate,
    CartVMDelegate by cartVMDelegate {

    private var catalogItem: CatalogItem? = null
    private val _itemInfo: MutableStateFlow<String> = MutableStateFlow("")
    val itemInfo: Flow<String> = _itemInfo

    init {
        viewModelScope.launch {
            catalogItem = savedStateHandle.get<String>("catalog_item_id")
                ?.let {
                    catalogInteractor.getCatalogItem(it)
                }?.also {
                    _itemInfo.emit(it.title)
                }
        }
    }

    fun addToCart() {
        catalogItem?.also {
            addToCart(it)
        }
    }

}