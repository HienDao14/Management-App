package com.example.managementapp.main.presentation

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.managementapp.auth.data.local.User
import com.example.managementapp.main.model.apartment.Apartment
import com.example.managementapp.main.model.apartment.UpdateApartmentModel
import com.example.managementapp.main.repository.apartment.ApartmentRepository
import com.example.managementapp.main.repository.user.UserRepository
import com.example.managementapp.util.apartment.ApartmentsState
import com.example.managementapp.util.DeleteAndCreateState
import com.example.managementapp.util.MyResource
import com.example.managementapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefs: SharedPreferences,
    private val userRepository: UserRepository,
    private val apartmentRepository: ApartmentRepository
) : ViewModel() {

    var state by mutableStateOf(ApartmentsState())
        private set

    var deleteState by mutableStateOf(DeleteAndCreateState())
        private set

    private val updateChannel = MutableSharedFlow<UiState<Unit>>()
    val updateResult = updateChannel.asSharedFlow()

    private val channel = Channel<MyResource<Unit>>()
    val createResult = channel.receiveAsFlow()

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _apartments = MutableStateFlow<UiState<List<Apartment>>>(UiState.Empty)
    val apartments = _apartments.asStateFlow()

    private val _apartment = MutableStateFlow<UiState<Apartment>>(UiState.Empty)
    val apartment = _apartment.asStateFlow()

    fun getApartments(
        shouldMakeNetworkRequest: Boolean? = prefs.getBoolean(
            "network_status",
            false
        )
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                state = state.copy(
                    isLoading = true,
                    error = null
                )
                val userId = prefs.getString("user_id", "")

                when (val response = apartmentRepository.getApartments(
                    shouldMakeNetworkRequest = shouldMakeNetworkRequest,
                    userId!!
                )) {
                    is MyResource.Loading -> {
                    }

                    is MyResource.Success -> {
                        val apartments = response.data
                        if (apartments.isNullOrEmpty()) {
                            state = state.copy(
                                apartments = emptyList(),
                                isLoading = false,
                                error = null
                            )
                        } else {
                            state = state.copy(
                                apartments = apartments,
                                isLoading = false,
                                error = null
                            )
                        }
                    }

                    is MyResource.Error -> {
                        state = state.copy(
                            isLoading = false,
                            error = response.message
                        )
                    }
                }
            }
        }
    }

    fun getApartment(
        shouldMakeNetworkRequest: Boolean? = prefs.getBoolean("network_status", false),
        userId: String,
        apartmentId: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _apartment.emit(UiState.Loading)

                when(val result = apartmentRepository.getApartmentById(
                    shouldMakeNetworkRequest, userId, apartmentId
                )){
                    is MyResource.Success -> {
                        result.data?.let {
                            _apartment.emit(
                                UiState.Success(it)
                            )
                        }
                    }
                    is MyResource.Error -> {
                        _apartment.emit(UiState.Error(result.message.toString()))
                    }
                    else -> {}
                }
            }
        }
    }

    fun deleteApartment(apartmentId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deleteState = deleteState.copy(
                    isLoading = true,
                    success = null,
                    error = null
                )
                when (val result = apartmentRepository.deleteApartment(apartmentId)) {
                    is MyResource.Success -> {
                        deleteState = deleteState.copy(
                            success = true,
                            isLoading = false,
                            error = null
                        )
                    }

                    is MyResource.Error -> {
                        deleteState = deleteState.copy(
                            success = false,
                            isLoading = false,
                            error = result.message.toString()
                        )
                    }

                    is MyResource.Loading -> {}
                }
            }
        }
    }

    fun createApartment(
        name: String,
        address: String,
        roomCount: String,
        isCreate: Boolean,
        electricPrice: Int?,
        waterPrice: Int?,
        roomPrice: Int?
    ){
        viewModelScope.launch {
            val result = apartmentRepository
                .createApartment(name, address, roomCount.toInt(), isCreate, electricPrice, waterPrice, roomPrice)
            channel.send(result)
        }
    }

    fun updateApartment(
        apartmentId: String,
        name: String,
        address: String,
        roomCount: Int,
        electricPrice: Int?,
        waterPrice: Int?,
        roomPrice: Int?,
        description: String?,
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                updateChannel.emit(UiState.Loading)
                val apartment = UpdateApartmentModel(name, roomCount, address, description, electricPrice, waterPrice, roomPrice)
                when(val result = apartmentRepository.updateApartment(apartmentId, apartment)){
                    is MyResource.Success -> {
                        updateChannel.emit(UiState.Success(Unit))
                    }
                    is MyResource.Error -> {
                        updateChannel.emit(UiState.Error(result.message.toString()))
                    }
                    is MyResource.Loading -> {}
                }
            }
        }
    }

    fun logOut(
        navigateToLogin: () -> Unit
    ) {
        prefs.edit().remove("accessToken").apply()
        prefs.edit().remove("user_id").apply()

        navigateToLogin()
    }
}
