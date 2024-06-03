package com.example.managementapp.main.presentation.record

import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.managementapp.main.model.record.Record
import com.example.managementapp.main.repository.record.RecordRepository
import com.example.managementapp.main.repository.room.RoomRepository
import com.example.managementapp.util.MyResource
import com.example.managementapp.util.UiState
import com.example.managementapp.util.record.RecordsState
import com.example.managementapp.util.record.RoomWithRecordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val recordRepository: RecordRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    var roomWithRecordsState by mutableStateOf(RoomWithRecordState())
        private set

    var records by mutableStateOf(RecordsState())
        private set

    private val _record = MutableStateFlow<UiState<Record>>(UiState.Empty)
    val record = _record.asStateFlow()

    private val createChannel = Channel<UiState<Unit>>()
    val createRecordChannel = createChannel.receiveAsFlow()

    private val updateChannel = Channel<UiState<Unit>>()
    val updateRecordChannel = updateChannel.receiveAsFlow()

    private val deleteChannel = Channel<UiState<Unit>>()
    val deleteRecordChannel = deleteChannel.receiveAsFlow()

    fun getRoomsWithRecord(apartmentId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                roomWithRecordsState = roomWithRecordsState.copy(
                    records = null,
                    isLoading = true,
                    error = null
                )
                when (val result = roomRepository.getRoomsWithRecord(apartmentId)) {
                    is MyResource.Success -> {
                        roomWithRecordsState = roomWithRecordsState.copy(
                            records = result.data,
                            isLoading = false,
                            error = null
                        )
                    }

                    is MyResource.Error -> {
                        roomWithRecordsState = roomWithRecordsState.copy(
                            records = null,
                            isLoading = false,
                            error = result.message.toString()
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    fun createRecord(
        roomId: String,
        electricNumber: String,
        waterNumber: String,
        recordedAt: String,
        isTheLast: Boolean,
        recordImages: List<File>?
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                createChannel.send(UiState.Loading)
                when (val result = recordRepository.createRecord(
                    roomId,
                    electricNumber,
                    waterNumber,
                    recordedAt,
                    isTheLast,
                    recordImages
                )) {
                    is MyResource.Success -> {
                        createChannel.send(UiState.Success(Unit))
                    }

                    is MyResource.Error -> {
                        createChannel.send(UiState.Error(result.message.toString()))
                    }

                    else -> Unit
                }
            }
        }
    }

    fun updateRecord(
        roomId: String,
        recordId: String,
        electricNumber: String,
        waterNumber: String,
        recordedAt: String,
        recordImages: List<File>?
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = recordRepository.updateRecord(
                    roomId,
                    recordId,
                    electricNumber,
                    waterNumber,
                    recordedAt,
                    recordImages
                )
                when (result) {
                    is MyResource.Success -> {
                        updateChannel.send(UiState.Success(Unit))
                        println("success")
                    }

                    is MyResource.Error -> {
                        updateChannel.send(UiState.Error(result.message.toString()))
                    }

                    else -> Unit
                }
            }
        }
    }

    fun getRecordsOfRoom(
        shouldMakeNetworkRequest: Boolean?,
        roomId: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                records = records.copy(
                    records = null,
                    isLoading = true,
                    error = null
                )
                when (val result = recordRepository.getRecords(
                    shouldMakeNetworkRequest = shouldMakeNetworkRequest,
                    roomId = roomId
                )) {
                    is MyResource.Success -> {
                        records = records.copy(
                            records = result.data,
                            isLoading = false,
                            error = null
                        )
                    }

                    is MyResource.Error -> {
                        records = records.copy(
                            records = null,
                            isLoading = false,
                            error = result.message.toString()
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    fun getRecordDetail(
        shouldMakeNetworkRequest: Boolean?,
        recordId: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _record.emit(UiState.Loading)

                when (val result = recordRepository.getRecord(
                    shouldMakeNetworkRequest = shouldMakeNetworkRequest,
                    recordId = recordId
                )) {
                    is MyResource.Success -> {
                        val record = result.data
                        if (record != null) {
                            _record.emit(UiState.Success(result.data))
                        } else {
                            _record.emit(UiState.Error("Record not found"))
                        }
                    }

                    is MyResource.Error -> {
                        _record.emit(UiState.Error(result.message.toString()))
                    }

                    else -> Unit
                }
            }
        }
    }

    fun deleteRecord(
        recordId: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (val result = recordRepository.deleteRecord(recordId)) {
                    is MyResource.Success -> {
                        deleteChannel.send(UiState.Success(Unit))
                    }

                    is MyResource.Error -> {
                        deleteChannel.send(UiState.Error(result.message.toString()))
                    }

                    else -> Unit
                }
            }
        }

    }
}