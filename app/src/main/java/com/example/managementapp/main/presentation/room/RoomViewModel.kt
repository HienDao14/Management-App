package com.example.managementapp.main.presentation.room

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.managementapp.main.model.room.CreateRoomModel
import com.example.managementapp.main.model.room.Room
import com.example.managementapp.main.repository.room.RoomRepository
import com.example.managementapp.util.DeleteAndCreateState
import com.example.managementapp.util.MyResource
import com.example.managementapp.util.UiState
import com.example.managementapp.util.room.RoomState
import com.example.managementapp.util.room.RoomsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    var roomState by mutableStateOf(RoomsState())
        private set

    private var _state = MutableSharedFlow<DeleteAndCreateState>()
    val state = _state.asSharedFlow()

    private var _roomDetail = MutableStateFlow<UiState<Room>>(UiState.Empty)
    val roomDetail = _roomDetail.asStateFlow()

    var roomDetailState by mutableStateOf(RoomState())
        private set

    private val roomChannel = Channel<MyResource<Unit>>()
    val createRoomResult = roomChannel.receiveAsFlow()

    private val _updateRoomChannel = MutableSharedFlow<UiState<Unit>>()
    val updateRoomResult = _updateRoomChannel.asSharedFlow()

    fun getRooms(
        shouldMakeNetworkRequest: Boolean? = prefs.getBoolean("network_status", false),
        apartmentId: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                roomState = roomState.copy(
                    isLoading = true,
                    error = null
                )
                when (val response = roomRepository.getRooms(
                    shouldMakeNetworkRequest, apartmentId
                )) {
                    is MyResource.Success -> {
                        val rooms = response.data
                        if (rooms.isNullOrEmpty()) {
                            roomState = roomState.copy(
                                rooms = emptyList(),
                                isLoading = false,
                                error = null
                            )
                        } else {
                            roomState = roomState.copy(
                                rooms = rooms,
                                isLoading = false,
                                error = null
                            )
                        }
                    }

                    is MyResource.Error -> {
                        roomState = roomState.copy(
                            isLoading = false,
                            error = response.message.toString()
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    fun getRoomDetail(
        apartmentId: String,
        roomId: String,
        shouldMakeNetworkRequest: Boolean?
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _roomDetail.emit(UiState.Loading)
                roomDetailState = roomDetailState.copy(
                    isLoading = true,
                    error = null
                )
                when (val result = roomRepository.getRoom(
                    shouldMakeNetworkRequest, apartmentId, roomId
                )) {
                    is MyResource.Success -> {
                        val room = result.data
                        room?.let {
                            _roomDetail.emit(UiState.Success(room))
                            roomDetailState = roomDetailState.copy(
                                room = it,
                                isLoading = false,
                                error = null
                            )
                        }
                    }

                    is MyResource.Error -> {
                        _roomDetail.emit(UiState.Error(result.message.toString()))
                        roomDetailState = roomDetailState.copy(
                            room = null,
                            isLoading = false,
                            error = result.message.toString()
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    fun deleteRoom(
        apartmentId: String,
        roomId: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _state.emit(
                    DeleteAndCreateState(
                        isLoading = true,
                        success = null,
                        error = null
                    )
                )

                when (val result = roomRepository.deleteRoom(apartmentId, roomId)) {
                    is MyResource.Success -> {
                        _state.emit(
                            DeleteAndCreateState(
                                success = true,
                                isLoading = false,
                                error = null
                            )
                        )
                    }

                    is MyResource.Error -> {
                        _state.emit(
                            DeleteAndCreateState(
                                success = false,
                                isLoading = false,
                                error = result.message.toString()
                            )
                        )
                        Log.d("api_error", result.message.toString())
                    }

                    is MyResource.Loading -> {}
                }
            }
        }
    }

    fun createRoom(
        apartmentId: String,
        name: String,
        roomPrice: Int,
        area: Double?,
        electricPrice: Int,
        waterPrice: Int,
        description: String?
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = roomRepository.createRoom(
                    apartmentId,
                    name,
                    roomPrice,
                    area,
                    electricPrice,
                    waterPrice,
                    description
                )
                roomChannel.send(result)
            }
        }
    }

    fun updateRoom(
        apartmentId: String,
        roomId: String,
        name: String,
        roomPrice: Int,
        area: Double?,
        electricPrice: Int,
        waterPrice: Int,
        description: String?
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                _updateRoomChannel.emit(UiState.Loading)
                val room =
                    CreateRoomModel(name, electricPrice, roomPrice, waterPrice, area, description)

                when (val result = roomRepository.updateRoom(apartmentId, roomId, room)) {
                    is MyResource.Success -> {
                        _updateRoomChannel.emit(UiState.Success(Unit))
                    }

                    is MyResource.Error -> {
                        _updateRoomChannel.emit(UiState.Error(result.message.toString()))
                    }

                    is MyResource.Loading -> {}
                }
            }
        }
    }
}