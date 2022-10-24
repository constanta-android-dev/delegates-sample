package tech.constanta.android.delegates.sample.ui.catalog.list

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
class CatalogListViewModel @Inject constructor(
    private val catalogInteractor: CatalogInteractor,
    private val cartVMDelegate: CartVMDelegate,
    private val authControlsVMDelegate: AuthControlsVMDelegate,
) : ViewModel(),
    AuthControlsVMDelegate by authControlsVMDelegate,
    CartVMDelegate by cartVMDelegate {

    val _catalogItems: MutableStateFlow<List<CatalogItem>> = MutableStateFlow(emptyList())
    val catalogItems: Flow<List<CatalogItem>> = _catalogItems

    init {
        viewModelScope.launch {
            _catalogItems.emit(
                catalogInteractor.getCatalogItems()
            )
        }
    }

}