package tech.constanta.android.delegates.sample.domain.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import tech.constanta.android.delegates.sample.domain.auth.model.AuthState
import javax.inject.Inject

interface AuthInteractor {

    val authState: Flow<AuthState>

    suspend fun auth()
    suspend fun unauth()
}

class AuthInteractorImpl @Inject constructor(

) : AuthInteractor {
    override val authState: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.UNAUTHORIZED)

    override suspend fun auth() {
        authState.emit(AuthState.AUTHORIZED)
    }

    override suspend fun unauth() {
        authState.emit(AuthState.UNAUTHORIZED)
    }

}