package tech.constanta.android.delegates.sample.ui.catalog.details

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.R
import tech.constanta.android.delegates.sample.ui.auth.AuthControlsViewDelegate
import tech.constanta.android.delegates.sample.ui.cart.CartViewDelegate

@AndroidEntryPoint
class CatalogDetailsFragment : Fragment(R.layout.fragment_catalog_details) {

    val viewModel by viewModels<CatalogDetailsViewModel>()

    private var text: TextView? = null
    private var addToCart: ImageView? = null

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

}