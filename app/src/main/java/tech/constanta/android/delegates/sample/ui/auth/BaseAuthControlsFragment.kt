package tech.constanta.android.delegates.sample.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.R

abstract class BaseAuthControlsFragment(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId) {

    abstract val vm: BaseAuthControlsViewModel

    private var authControlsContainer: LinearLayout? = null
    private var signUp: Button? = null
    private var signIn: Button? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authControlsContainer = view.findViewById(R.id.auth_controls_container)
        signUp = view.findViewById<Button?>(R.id.sign_up)?.apply {
            setOnClickListener {
                vm.onSignUpClick()
            }
        }
        signIn = view.findViewById<Button?>(R.id.sign_in)?.apply {
            setOnClickListener {
                vm.onSignInClick()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            vm.authControlsState.collect {
                when (it) {
                    AuthControlsState.AVAILABLE -> authControlsContainer?.visibility == View.VISIBLE
                    AuthControlsState.UNAVAILABLE -> authControlsContainer?.visibility == View.GONE
                }
            }
        }
    }
}