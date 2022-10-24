package tech.constanta.android.delegates.sample.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import tech.constanta.android.delegates.sample.domain.auth.AuthInteractor
import tech.constanta.android.delegates.sample.domain.auth.model.AuthState

abstract class BaseAuthControlsViewModel(
    private val authInteractor: AuthInteractor
) : ViewModel() {

    val authControlsState: Flow<AuthControlsState> = authInteractor.authState.map {
        when (it) {
            AuthState.AUTHORIZED -> AuthControlsState.UNAVAILABLE
            AuthState.UNAUTHORIZED -> AuthControlsState.AVAILABLE
        }
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            authInteractor.auth()
        }
    }

    fun onSignInClick() {
        viewModelScope.launch {
            authInteractor.auth()
        }
    }

}