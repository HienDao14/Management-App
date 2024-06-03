package com.example.managementapp.main.presentation.record

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.managementapp.main.presentation.room.RoomCardItem
import com.example.managementapp.main.presentation.showLoading.ShowProgressBar
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.UiState
import com.facebook.internal.Utility.isNullOrEmpty

@Composable
fun ListRecordsScreen(
    modifier: Modifier = Modifier,
    apartmentId: String,
    roomId: String,
    roomName: String,
    navHostController: NavHostController,
    viewModel: RecordViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    LaunchedEffect(viewModel, prefs.getBoolean("network_status", false)) {
        viewModel.getRecordsOfRoom(
            prefs.getBoolean("network_status", false),
            roomId
        )
    }
    val recordsState = viewModel.records

    var openDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var recordId by rememberSaveable {
        mutableStateOf("")
    }
    LaunchedEffect(viewModel, context) {
        viewModel.deleteRecordChannel.collect {
            when (it) {
                is UiState.Success -> {
                    Toast.makeText(context, "Xoá bản ghi thành công", Toast.LENGTH_SHORT).show()
                    navHostController.navigateUp()
                }

                is UiState.Error -> {
                    val errorMessage = it.data
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }

                else -> Unit
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navHostController.navigate("create-record/${apartmentId}/${roomId}")
                }
            ) {
                Row {
                    Text(text = "Ghi điện nước")
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Ghi thông tin điện nước"
                    )
                }
            }
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(bottom = it.calculateBottomPadding(), top = 10.dp)
        ) {
            if (openDeleteDialog) {
                AlertDialog(
                    title = {
                        Text(text = "Xóa bản ghi")
                    },
                    text = {
                        Text(text = "Bạn có chắc muốn xóa bản ghi này không\n Sau khi xóa, mọi dữ liệu sẽ mất")
                    },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.deleteRecord(recordId)
                            openDeleteDialog = false
                        }) {
                            Text(text = "Đồng ý")
                        }
                    },
                    onDismissRequest = {
                        openDeleteDialog = false
                    },
                    dismissButton = {
                        Button(onClick = { openDeleteDialog = false }) {
                            Text(text = "Hủy bỏ")
                        }
                    }
                )
            }
            if (recordsState.isLoading) {
                ShowProgressBar(modifier = Modifier.fillMaxSize())
            }
            if (!recordsState.error.isNullOrBlank()) {
                Toast.makeText(context, recordsState.error.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
            recordsState.records?.let { records ->
                if (records.isEmpty()) {
                    Text(
                        text = "Phòng này chưa có bản ghi điện nước nào",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Danh sách bản ghi điện nước: ", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            records.forEach { record ->
                                item {
                                    println(record.recordId)
                                    RecordCardItem(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        record = record,
                                        context = context,
                                        onDeleteClick = {id ->
                                            recordId = id
                                            openDeleteDialog = true
                                        },
                                        onUpdateClick = {
                                            navHostController.navigate("update-record/${roomName}/${record.recordId}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
