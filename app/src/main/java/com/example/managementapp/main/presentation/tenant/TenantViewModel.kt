package com.example.managementapp.main.presentation.tenant

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.managementapp.main.model.tenant.Tenant
import com.example.managementapp.main.repository.tenant.TenantRepository
import com.example.managementapp.util.DeleteAndCreateState
import com.example.managementapp.util.MyResource
import com.example.managementapp.util.UiState
import com.example.managementapp.util.tenant.TenantState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TenantViewModel @Inject constructor(
    private val tenantRepository: TenantRepository,
    private val prefs: SharedPreferences
): ViewModel() {

    var tenantState by mutableStateOf(TenantState())
        private set

    var deleteTenantState by mutableStateOf(DeleteAndCreateState())
        private set

    private var _state = MutableSharedFlow<DeleteAndCreateState>()
    val state = _state.asSharedFlow()

    private val _tenant = MutableStateFlow<UiState<Tenant>>(UiState.Empty)
    val tenant = _tenant.asStateFlow()

    private val channel = Channel<MyResource<Unit>>()
    val createTenantChannel = channel.receiveAsFlow()

    private val updateChannel = Channel<UiState<Unit>>()
    val updateTenantChannel = updateChannel.receiveAsFlow()

    fun getTenants(
        shouldMakeNetworkRequest: Boolean ?= prefs.getBoolean("network_state", false),
        roomId: String,
        apartmentId: String
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                tenantState = tenantState.copy(
                    tenants = null,
                    isLoading = true,
                    error = null
                )

                when( val response = if(roomId == "") tenantRepository.getTenantsInApartment(shouldMakeNetworkRequest, apartmentId)
                    else tenantRepository.getTenants(shouldMakeNetworkRequest, roomId)
                ){
                    is MyResource.Success -> {
                        val tenants = response.data
                        if(tenants.isNullOrEmpty()){
                            tenantState = tenantState.copy(
                                tenants = emptyList(),
                                isLoading = false,
                                error = null
                            )
                        } else {
                            tenantState = tenantState.copy(
                                tenants = tenants,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is MyResource.Error -> {
                        tenantState = tenantState.copy(
                            isLoading = false,
                            error = response.message.toString()
                        )
                    }
                    else -> Unit
                }
            }
        }
    }

    fun getTenant(
        shouldMakeNetworkRequest: Boolean?,
        roomId: String,
        tenantId: String
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _tenant.emit(UiState.Loading)
                when(val response = tenantRepository.getTenant(shouldMakeNetworkRequest, roomId, tenantId)){
                    is MyResource.Success -> {
                        val tenant = response.data
                        tenant?.let {
                            _tenant.emit(UiState.Success(it))
                        }
                    }
                    is MyResource.Error -> {
                        val errorMsg = response.message.toString()
                        _tenant.emit(UiState.Error(errorMsg))
                    }
                    else -> {}
                }
            }
        }
    }

    fun deleteTenant(
        roomId: String,
        tenantId: String
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _state.emit(
                    DeleteAndCreateState(
                        isLoading = true,
                        success = null,
                        error = null
                    )
                )
                deleteTenantState = deleteTenantState.copy(
                    isLoading = true,
                    success = null,
                    error = null
                )
                when(val response = tenantRepository.deleteTenant(roomId, tenantId)){
                    is MyResource.Success -> {
                        _state.emit(
                            DeleteAndCreateState(
                                success = true,
                                isLoading = false,
                                error = null
                            )
                        )
                        deleteTenantState = deleteTenantState.copy(
                            success = true,
                            isLoading = false,
                            error = null
                        )
                    }
                    is MyResource.Error -> {
                        _state.emit(
                            DeleteAndCreateState(
                                success = false,
                                isLoading = false,
                                error = response.message.toString()
                            )
                        )

                        deleteTenantState = deleteTenantState.copy(
                            success = false,
                            isLoading = false,
                            error = response.message.toString()
                        )
                        Log.d("api_error", response.message.toString())
                    }
                    is MyResource.Loading -> {}
                }
            }
        }
    }

    fun createTenant(
        apartmentId: String,
        roomId: String,
        fullName: String,
        gender: String,
        phoneNum: String,
        dob: String,
        placeOfOrigin: String,
        identityCardNumber: String,
        deposit: Int,
        startDate: String,
        endDate: String?,
        note: String?,
        roomName: String,
        identityCardImg: List<File>?
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val response = tenantRepository.createTenant(
                    apartmentId, roomId,
                    fullName, gender, phoneNum, dob, placeOfOrigin, identityCardNumber, deposit, startDate, endDate, note, roomName,
                    identityCardImg
                )
                channel.send(response)
            }
        }
    }

    fun updateTenant(
        tenantId: String,
        fullName: String,
        gender: String,
        phoneNum: String,
        dob: String,
        placeOfOrigin: String,
        identityCardNumber: String,
        deposit: Int,
        startDate: String,
        endDate: String?,
        note: String?,
        roomName: String,
        identityCardImg: List<File>?
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val result = tenantRepository.updateTenant(tenantId,
                    fullName, gender, phoneNum, dob, placeOfOrigin, identityCardNumber, deposit, startDate, endDate, note, roomName,
                    identityCardImg)
                when(result){
                    is MyResource.Success -> {
                        updateChannel.send(UiState.Success(Unit))
                    }
                    is MyResource.Error -> {
                        updateChannel.send(UiState.Error(result.message.toString()))
                    }
                    is MyResource.Loading -> {
                        updateChannel.send(UiState.Loading)
                    }
                }
            }
        }
    }
}