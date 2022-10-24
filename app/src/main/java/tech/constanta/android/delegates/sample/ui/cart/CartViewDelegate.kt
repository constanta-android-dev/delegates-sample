package tech.constanta.android.delegates.sample.ui.cart

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.R

class CartViewDelegate(
    private val context: Context,
    private val viewLifecycleOwner: LifecycleOwner,
) {

    fun setUp(
        cartItemsCount: TextView?,
        cartFab: FloatingActionButton?,
        viewModel: CartVMDelegate
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cartItemsCount.collect {
                cartItemsCount?.text = it.toString()
            }
        }

        cartFab?.setOnClickListener {
            showCartDialog(viewModel)
        }
    }

    private fun showCartDialog(viewModel: CartVMDelegate) {
        val cartDialogView = LayoutInflater.from(context).inflate(R.layout.d_cart, null)

        val cartItemsContainer = cartDialogView.findViewById<LinearLayout>(R.id.cart_items_container)
        val close = cartDialogView.findViewById<Button>(R.id.close)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cartItems.collect { cartItems ->
                cartItemsContainer.removeAllViews()
                cartItems.forEach { item ->
                    cartItemsContainer.addView(
                        CartItemView(context).apply {
                            setCartItem(item)
                            setOnRemoveFromCartClickListener {
                                viewModel.removeCartItem(it)
                            }
                        }
                    )
                }
            }
        }

        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(cartDialogView)
        close.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

}