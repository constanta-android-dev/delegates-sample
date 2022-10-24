package tech.constanta.android.delegates.sample.ui.auth

import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.R

class AuthControlsViewDelegate {

    private var authControlsContainer: LinearLayout? = null
    private var signUp: Button? = null
    private var signIn: Button? = null

    fun setUp(
        viewLifecycleOwner: LifecycleOwner,
        authControlsContainer: LinearLayout,
        viewModel: AuthControlsVMDelegate
    ) {
        this.authControlsContainer = authControlsContainer
        signUp = authControlsContainer.findViewById<Button?>(R.id.sign_up)?.apply {
            setOnClickListener {
                viewModel.onSignUpClick()
            }
        }
        signIn = authControlsContainer.findViewById<Button?>(R.id.sign_in)?.apply {
            setOnClickListener {
                viewModel.onSignInClick()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authControlsState.collect {
                when (it) {
                    AuthControlsState.AVAILABLE -> authControlsContainer.visibility = View.VISIBLE
                    AuthControlsState.UNAVAILABLE -> authControlsContainer.visibility = View.GONE
                }
            }
        }
    }

}