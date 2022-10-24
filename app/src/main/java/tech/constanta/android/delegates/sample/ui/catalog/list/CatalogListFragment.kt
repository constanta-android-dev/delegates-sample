package tech.constanta.android.delegates.sample.ui.catalog.list

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.R
import tech.constanta.android.delegates.sample.domain.catalog.model.CatalogItem
import tech.constanta.android.delegates.sample.ui.cart.CartItemView

@AndroidEntryPoint
class CatalogListFragment : Fragment(R.layout.fragment_catalog_list) {

    val viewModel by viewModels<CatalogListViewModel>()

    private var catalogContainer: LinearLayout? = null
    private var cartItemsCount: TextView? = null
    private var cartFab: FloatingActionButton? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        catalogContainer = view.findViewById(R.id.catalog_container)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.catalogItems.collect { items ->
                showCatalogItems(items)
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

    private fun showCatalogItems(items: List<CatalogItem>) {
        catalogContainer?.removeAllViews()
        items.forEach { item ->
            catalogContainer?.addView(
                CatalogListItemView(requireContext()).apply {
                    setOnItemClickListener {
                        findNavController().navigate(
                            R.id.action_CatalogListFragment_to_CatalogDetailsFragment,
                            bundleOf(
                                "catalog_item_id" to item.id
                            )
                        )
                    }
                    setOnAddToCartClickListener {
                        viewModel.addToCart(it)
                    }
                    setCatalogItem(item)
                }
            )
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