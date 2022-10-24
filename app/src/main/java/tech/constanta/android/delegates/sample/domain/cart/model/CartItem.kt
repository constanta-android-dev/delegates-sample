package tech.constanta.android.delegates.sample.domain.model

import tech.constanta.android.delegates.sample.domain.catalog.model.CatalogItem

data class CartItem(
    val id: String,
    val catalogItem: CatalogItem,
)