package tech.constanta.android.delegates.sample.ui.catalog.list

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import tech.constanta.android.delegates.sample.R
import tech.constanta.android.delegates.sample.domain.catalog.model.CatalogItem

class CatalogListItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val container: FrameLayout
    private val title: TextView
    private val addToCart: ImageView

    private var onAddToCartClickListener: ((CatalogItem) -> Unit)? = null
    private var onItemClickListener: ((CatalogItem) -> Unit)? = null

    init {
        inflate(context, R.layout.v_catalog_list_item, this)
        container = findViewById(R.id.container)
        title = findViewById(R.id.title)
        addToCart = findViewById(R.id.add_to_cart)
    }

    fun setCatalogItem(catalogItem: CatalogItem) {
        title.text = catalogItem.title
        container.setOnClickListener {
            onItemClickListener?.invoke(catalogItem)
        }
        addToCart.setOnClickListener {
            onAddToCartClickListener?.invoke(catalogItem)
        }
    }

    fun setOnAddToCartClickListener(action: (CatalogItem) -> Unit) {
        this.onAddToCartClickListener = action
    }

    fun setOnItemClickListener(action: (CatalogItem) -> Unit) {
        this.onItemClickListener = action
    }
}