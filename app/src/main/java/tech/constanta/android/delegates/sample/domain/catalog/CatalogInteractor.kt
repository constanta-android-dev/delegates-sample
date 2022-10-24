package tech.constanta.android.delegates.sample.domain.catalog

import tech.constanta.android.delegates.sample.domain.catalog.model.CatalogItem
import javax.inject.Inject

interface CatalogInteractor {

    suspend fun getCatalogItems(): List<CatalogItem>

    suspend fun getCatalogItem(id: String): CatalogItem?
}

class CatalogInteractorImpl @Inject constructor(

) : CatalogInteractor {
    private val items: List<CatalogItem> by lazy {
        Array(20) {
            CatalogItem(
                id = "${it + 1}",
                title = "Item ${it + 1}"
            )
        }.toList()
    }

    override suspend fun getCatalogItems(): List<CatalogItem> {
        return items
    }

    override suspend fun getCatalogItem(id: String): CatalogItem? {
        return items.find { it.id == id }
    }

}