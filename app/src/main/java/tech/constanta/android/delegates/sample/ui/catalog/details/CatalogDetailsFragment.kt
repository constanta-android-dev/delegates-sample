package tech.constanta.android.delegates.sample.ui.catalog.details

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.R
import tech.constanta.android.delegates.sample.ui.cart.CartItemView

@AndroidEntryPoint
class CatalogDetailsFragment : Fragment(R.layout.fragment_catalog_details) {

    val viewModel by viewModels<CatalogDetailsViewModel>()

    private var text: TextView? = null
    private var addToCart: ImageView? = null
    private var cartItemsCount: TextView? = null
    private var cartFab: FloatingActionButton? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text = view.findViewById(R.id.text)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.itemInfo.collect {
                text?.text = it
            }
        }

        addToCart = view.findViewById<ImageView?>(R.id.add_to_cart)?.apply {
            setOnClickListener {
                viewModel.addToCart()
            }
        }

        cartItemsCount = view.findViewById(R.id.cart_items_count)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cartItemsCount.collect {
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
            viewModel.cartItems.collect { cartItems ->
                cartItemsContainer.removeAllViews()
                cartItems.forEach { item ->
                    cartItemsContainer.addView(
                        CartItemView(requireContext()).apply {
                            setCartItem(item)
                            setOnRemoveFromCartClickListener {
                                viewModel.removeCartItem(it)
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