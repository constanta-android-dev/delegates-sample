package tech.constanta.android.delegates.sample.ui.cart

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.R

abstract class BaseCartFragment(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId) {

    abstract val vm: BaseCartViewModel

    private var cartItemsCount: TextView? = null
    private var cartFab: FloatingActionButton? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartItemsCount = view.findViewById(R.id.cart_items_count)
        viewLifecycleOwner.lifecycleScope.launch {
            vm.cartItemsCount.collect {
                cartItemsCount?.text = it.toString()
            }
        }

        cartFab = view.findViewById<FloatingActionButton?>(R.id.cart_fab)?.apply {
            setOnClickListener {
                showCartDialog()
            }
        }
    }


    private fun showCartDialog() {
        val cartDialogView = layoutInflater.inflate(R.layout.d_cart, null)

        val cartItemsContainer = cartDialogView.findViewById<LinearLayout>(R.id.cart_items_container)
        val close = cartDialogView.findViewById<Button>(R.id.close)

        viewLifecycleOwner.lifecycleScope.launch {
            vm.cartItems.collect { cartItems ->
                cartItemsContainer.removeAllViews()
                cartItems.forEach { item ->
                    cartItemsContainer.addView(
                        CartItemView(requireContext()).apply {
                            setCartItem(item)
                            setOnRemoveFromCartClickListener {
                                vm.removeCartItem(it)
                            }
                        }
                    )
                }
            }
        }

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(cartDialogView)
        close.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

}