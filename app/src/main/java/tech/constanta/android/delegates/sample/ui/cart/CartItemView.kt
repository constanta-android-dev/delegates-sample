package tech.constanta.android.delegates.sample.ui.cart

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import tech.constanta.android.delegates.sample.R
import tech.constanta.android.delegates.sample.domain.model.CartItem

class CartItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var onRemoveFromCartClickListener: ((CartItem) -> Unit)? = null
    private val title: TextView
    private val removeFromCart: ImageView
    init {
        inflate(context, R.layout.v_cart_item, this)
        title = findViewById(R.id.title)
        removeFromCart = findViewById(R.id.remove_from_cart)
    }

    fun setCartItem(cartItem: CartItem) {
        title.text = cartItem.catalogItem.title
        removeFromCart.setOnClickListener {
            onRemoveFromCartClickListener?.invoke(cartItem)
        }
    }

    fun setOnRemoveFromCartClickListener(action: (CartItem) -> Unit) {
        this.onRemoveFromCartClickListener = action
    }
}