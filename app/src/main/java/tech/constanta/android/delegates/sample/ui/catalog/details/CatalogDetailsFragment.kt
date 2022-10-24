package tech.constanta.android.delegates.sample.ui.catalog.details

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.R
import tech.constanta.android.delegates.sample.ui.cart.BaseCartFragment
import tech.constanta.android.delegates.sample.ui.cart.BaseCartViewModel

@AndroidEntryPoint
class CatalogDetailsFragment : BaseCartFragment(R.layout.fragment_catalog_details) {

    val viewModel by viewModels<CatalogDetailsViewModel>()

    override val vm: BaseCartViewModel
        get() {
            return viewModel
        }

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
    }

}