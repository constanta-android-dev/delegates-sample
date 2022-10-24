package tech.constanta.android.delegates.sample.ui.catalog.list

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.R
import tech.constanta.android.delegates.sample.domain.catalog.model.CatalogItem
import tech.constanta.android.delegates.sample.ui.auth.AuthControlsViewDelegate
import tech.constanta.android.delegates.sample.ui.cart.CartViewDelegate

@AndroidEntryPoint
class CatalogListFragment : Fragment(R.layout.fragment_catalog_list) {

    val viewModel by viewModels<CatalogListViewModel>()

    private var catalogContainer: LinearLayout? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        catalogContainer = view.findViewById(R.id.catalog_container)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.catalogItems.collect { items ->
                showCatalogItems(items)
            }
        }
        CartViewDelegate(
            context = requireContext(),
            viewLifecycleOwner = viewLifecycleOwner
        ).setUp(
            cartItemsCount = view.findViewById(R.id.cart_items_count),
            cartFab = view.findViewById(R.id.cart_fab),
            viewModel = viewModel
        )
        AuthControlsViewDelegate().setUp(
            viewLifecycleOwner = viewLifecycleOwner,
            authControlsContainer = view.findViewById(R.id.auth_controls_container),
            viewModel = viewModel
        )
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

}