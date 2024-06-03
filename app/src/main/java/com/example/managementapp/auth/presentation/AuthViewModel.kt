package com.example.managementapp.auth.presentation

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.managementapp.auth.data.remote.model.Response
import com.example.managementapp.auth.domain.repository.AuthRepository
import com.example.managementapp.util.AuthResult
import com.example.managementapp.util.AuthState
import com.example.managementapp.util.AuthUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val prefs: SharedPreferences
): ViewModel() {
    var state by mutableStateOf(AuthState())

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResult = resultChannel.receiveAsFlow()

    private val _resetPassState = MutableStateFlow<Boolean>(true)
    val resetPassState = _resetPassState.asStateFlow()

    private val _updatePassResponse = MutableStateFlow<Response>(Response(false, "Một lỗi nào đó đã xảy ra"))
    val updatePassResponse = _updatePassResponse.asStateFlow()

    private val _sendCodeState = MutableStateFlow(Response(false, ""))
    val sendCodeState = _sendCodeState.asStateFlow()

    init {
        if(!prefs.getString("accessToken", null).isNullOrBlank()){
            authenticate()
        }
    }

    fun onEvent(event: AuthUiEvent){
        when(event){
            is AuthUiEvent.RegisterUsernameChanged -> {
                state = state.copy(registerUsername = event.value)
            }
            is AuthUiEvent.RegisterPasswordChanged -> {
                state = state.copy(registerPassword = event.value)
            }
            is AuthUiEvent.RegisterEmailChanged -> {
                state = state.copy(registerEmail = event.value)
            }
            is AuthUiEvent.Register -> {
                register()
            }
            is AuthUiEvent.LoginUsernameChanged -> {
                state = state.copy(loginUsername = event.value)
            }
            is AuthUiEvent.LoginPasswordChanged -> {
                state = state.copy(loginPassword = event.value)

            }
            is AuthUiEvent.Login -> {
                login()
            }
            else -> Unit
        }
    }

    fun getOTP(email: String){
        viewModelScope.launch {
            _sendCodeState.emit(authRepository.getOTP(email.trim()))
        }
    }

    fun verifyOTP(email: String, otp: String){
        viewModelScope.launch {
            _resetPassState.emit(authRepository.verifyOTP(email, otp).success)
        }
    }

    fun updatePassword(email: String, password: String, confirmPassword: String){
        if(checkPasswordValid(password, confirmPassword)){
            viewModelScope.launch {
                _updatePassResponse.emit(authRepository.updatePassword(email.trim(), password.trim()))
            }
        } else {
            viewModelScope.launch {
                _updatePassResponse.emit(Response(false, "Mật khẩu không trùng khớp"))
            }
        }
    }

    private fun checkPasswordValid(password: String, confirmPassword: String): Boolean{
        return password.trim() == confirmPassword.trim()
    }

    private fun register(){
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = authRepository.register(
                username = state.registerUsername.trim(),
                password = state.registerPassword.trim(),
                email = state.registerEmail
            )
            resultChannel.send(result)
            state = state.copy(isLoading = false)
        }
    }

    private fun login(){
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = authRepository.login(
                username = state.loginUsername.trim(),
                password = state.loginPassword.trim()
            )
            resultChannel.send(result)
            state = state.copy(isLoading = false)
        }
    }

    private fun authenticate(){
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = authRepository.authenticate()
            resultChannel.send(result)
            state = state.copy(isLoading = false)
        }
    }
}