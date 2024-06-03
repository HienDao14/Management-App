package com.example.managementapp.main.presentation.bill

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.managementapp.main.model.bill.CreateBillModel
import com.example.managementapp.main.model.room.RoomForBill
import com.example.managementapp.main.repository.bill.BillRepository
import com.example.managementapp.main.repository.room.RoomRepository
import com.example.managementapp.util.MyResource
import com.example.managementapp.util.UiState
import com.example.managementapp.util.bill.BillState
import com.example.managementapp.util.bill.BillsState
import com.example.managementapp.util.bill.RoomForBillState
import com.example.managementapp.util.record.RoomWithRecordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BillViewModel @Inject constructor(
    private val billRepository: BillRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {
    var bills by mutableStateOf(BillsState())
        private set

    var unpaidBills by mutableStateOf(BillsState())
        private set

    var bill by mutableStateOf(BillState())
        private set

    private val createChannel = Channel<UiState<Unit>>()
    val createBillChannel = createChannel.receiveAsFlow()

    var roomForBill by mutableStateOf(RoomForBillState())
        private set

    private val _updateBill = MutableSharedFlow<UiState<Unit>>()
    val updateBillResult = _updateBill.asSharedFlow()

    private val _deleteBill = MutableSharedFlow<UiState<Unit>>()
    val deleteBillResult = _deleteBill.asSharedFlow()


    fun getRoomForBill(
        roomId: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                roomForBill = roomForBill.copy(
                    records = null,
                    isLoading = true,
                    error = null
                )
                when (val result = roomRepository.getRoomForBill(roomId)) {
                    is MyResource.Success -> {
                        roomForBill = roomForBill.copy(
                            records = result.data,
                            isLoading = false,
                            error = null
                        )
                    }

                    is MyResource.Error -> {
                        roomForBill = roomForBill.copy(
                            records = null,
                            isLoading = false,
                            error = result.message
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    fun createBill(
        apartmentId: String,
        roomId: String,
        bill: CreateBillModel
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                createChannel.send(UiState.Loading)
                val response = billRepository.createBill(
                    apartmentId, roomId, bill
                )
                when (response) {
                    is MyResource.Success -> {
                        createChannel.send(UiState.Success(Unit))
                    }

                    is MyResource.Error -> {
                        createChannel.send(UiState.Error(response.message))
                    }

                    else -> Unit
                }

            }
        }
    }

    fun getBillsInRoom(
        shouldMakeNetworkRequest: Boolean?,
        roomId: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                bills = bills.copy(
                    bills = null,
                    isLoading = true,
                    error = null
                )
                when (val result = billRepository.getBillsRoom(
                    shouldMakeNetworkRequest,
                    roomId
                )) {
                    is MyResource.Success -> {
                        bills = bills.copy(
                            bills = result.data,
                            isLoading = false,
                            error = null
                        )
                    }

                    is MyResource.Error -> {
                        bills = bills.copy(
                            bills = null,
                            isLoading = false,
                            error = result.message
                        )
                    }

                    else -> Unit
                }
            }
        }
    }


    fun deleteBill(
        billId: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _deleteBill.emit(UiState.Loading)
                val result = billRepository.deleteBill(billId)
                when (result) {
                    is MyResource.Success -> {
                        _deleteBill.emit(UiState.Success(Unit))
                    }

                    is MyResource.Error -> {
                        _deleteBill.emit(UiState.Error(result.message.toString()))
                    }

                    else -> Unit
                }
            }
        }
    }

    fun getBill(
        shouldMakeNetworkRequest: Boolean?,
        billId: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                bill = bill.copy(
                    bill = null,
                    isLoading = true,
                    error = null
                )
                val result = billRepository.getBill(
                    shouldMakeNetworkRequest, billId
                )
                when(result){
                    is MyResource.Success -> {
                        bill = bill.copy(
                            bill = result.data,
                            isLoading = false,
                            error = null
                        )
                    }

                    is MyResource.Error -> {
                        bill = bill.copy(
                            bill = null,
                            isLoading = false,
                            error = result.message
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    fun getUnpaidBills(
        shouldMakeNetworkRequest: Boolean?,
        apartmentId: String
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                bills = bills.copy(
                    bills = null,
                    isLoading = true,
                    error = null
                )
                val result = billRepository.getUnpaidBillsApartment(
                    shouldMakeNetworkRequest,
                    apartmentId
                )
                when(result) {
                    is MyResource.Success -> {
                        bills = bills.copy(
                            bills = result.data,
                            isLoading = false,
                            error = null
                        )
                    }

                    is MyResource.Error -> {
                        bills = bills.copy(
                            bills = null,
                            isLoading = false,
                            error = result.message
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    fun updateBill(
        billId: String,
        bill: CreateBillModel
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _updateBill.emit(UiState.Loading)
                val result = billRepository.updateBill(
                    billId, bill
                )
                when (result) {
                    is MyResource.Success -> {
                        _updateBill.emit(UiState.Success(Unit))
                    }

                    is MyResource.Error -> {
                        _updateBill.emit(UiState.Error(result.message.toString()))
                    }

                    else -> Unit
                }
            }
        }
    }
}