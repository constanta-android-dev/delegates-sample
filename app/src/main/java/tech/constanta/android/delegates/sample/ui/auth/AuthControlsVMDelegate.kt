package tech.constanta.android.delegates.sample.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.domain.auth.AuthInteractor
import tech.constanta.android.delegates.sample.domain.auth.model.AuthState
import javax.inject.Inject

interface AuthControlsVMDelegate {

    val authControlsState: Flow<AuthControlsState>
    fun onSignUpClick()

    fun onSignInClick()
}

class AuthControlsVMDelegateImpl @Inject constructor(
    private val authInteractor: AuthInteractor
) : ViewModel(), AuthControlsVMDelegate {

    override val authControlsState: Flow<AuthControlsState> = authInteractor.authState.map {
        when (it) {
            AuthState.AUTHORIZED -> AuthControlsState.UNAVAILABLE
            AuthState.UNAUTHORIZED -> AuthControlsState.AVAILABLE
        }
    }

    override fun onSignUpClick() {
        viewModelScope.launch {
            authInteractor.auth()
        }
    }

    override fun onSignInClick() {
        viewModelScope.launch {
            authInteractor.auth()
        }
    }

}