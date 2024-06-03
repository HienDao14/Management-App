package com.example.managementapp.main.presentation.room

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.managementapp.main.model.room.Room
import com.example.managementapp.main.presentation.showLoading.ShowProgressBar
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.DeleteAndCreateState
import com.example.managementapp.util.room.RoomsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsScreen(
    modifier: Modifier = Modifier,
    apartmentId: String,
    navHostController: NavHostController,
    viewModel: RoomViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    LaunchedEffect(true, prefs.getBoolean("network_status", false)) {
        viewModel.getRooms(
            prefs.getBoolean("network_status", false),
            apartmentId
        )
    }

    val deleteState = viewModel.state.collectAsState(initial = DeleteAndCreateState())

    if(deleteState.value.success == true){
        viewModel.getRooms(
            prefs.getBoolean("network_status", false),
            apartmentId
        )
        Toast.makeText(context, "Xóa phòng trọ thành công", Toast.LENGTH_SHORT).show()
    }

    if(deleteState.value.error != null){
        Toast.makeText(context, deleteState.value.error.toString(), Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navHostController.navigate("create_room/${apartmentId}") }
            ) {
                Row {
                    Text(text = "Thêm phòng trọ")
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Thêm phòng trọ")
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
            RoomScreenWhenSuccess(
                state = viewModel.roomState,
                navHostController = navHostController,
                apartmentId = apartmentId,
                modifier = Modifier.fillMaxSize(),
                context= context
            )
            if (viewModel.roomState.isLoading) {
                ShowProgressBar(modifier = Modifier.fillMaxSize())
            }

            viewModel.roomState.error?.let { error ->
                Text(text = "Error: $error", textAlign = TextAlign.Center, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun RoomScreenWhenSuccess(
    state: RoomsState,
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    apartmentId: String,
    context: Context
) {
    val rooms = state.rooms
    rooms?.let { listRoom ->
        if (listRoom.isEmpty()) {
            Text(
                text = "Nhà trọ của bạn chưa có phòng nào, nhấn phím + để thêm phòng",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        } else {
            Column(
                modifier = modifier.fillMaxSize()
            ) {
                Text(text = "Danh sách phòng trọ: ", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    listRoom.forEach { room ->
                        item {
                            RoomCardItem(
                                room = room,
                                navHostController = navHostController,
                                apartmentId = apartmentId,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                context = context
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomCardItem(
    modifier: Modifier = Modifier,
    room: Room,
    viewModel: RoomViewModel = hiltViewModel(),
    navHostController: NavHostController,
    apartmentId: String,
    context: Context
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    val openDeleteDialog = remember {
        mutableStateOf(false)
    }

    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    Card(
        onClick = {},
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            if (openDeleteDialog.value) {
                AlertDialog(
                    title = {
                        Text(text = "Xóa phòng trọ")
                    },
                    text = {
                        Text(text = "Bạn có chắc muốn xóa phòng trọ này không\n Sau khi xóa, mọi dữ liệu sẽ mất")
                    },
                    confirmButton = {
                        Button(onClick = {
                            openDeleteDialog.value = false
                            expanded = false
                            viewModel.deleteRoom(apartmentId, room.roomId)
                        }) {
                            Text(text = "Đồng ý")
                        }
                    },
                    onDismissRequest = {
                        openDeleteDialog.value = false
                        expanded = false
                    },
                    dismissButton = {
                        Button(onClick = { openDeleteDialog.value = false }) {
                            Text(text = "Hủy bỏ")
                        }
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = room.name,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "more",
                            tint = Color.Black
                        )
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text(text = "Chỉnh sửa", fontSize = 15.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Chỉnh sửa"
                                )
                            },
                            onClick = {
                                navHostController.navigate("update-room/${apartmentId}/${room.roomId}")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Xóa phòng", fontSize = 15.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Xóa phòng"
                                )
                            },
                            onClick = {
                                //Open dialog to confirm delete
                                openDeleteDialog.value = true
                            }
                        )
                    }
                }
            }
            //So ng o
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Số người ở:", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = if (room.available) "Trống" else room.tenantNumber.toString(),
                    fontSize = 15.sp, fontStyle = FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            //Tien phong / thang
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tền phòng/tháng:", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${room.livingPricePerMonth} ₫",
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Tien dien / thang
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tiền điện/số:", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${room.elecPricePerMonth} ₫",
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Tien nuoc / thang
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tiền nước/số:", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${room.waterPricePerMonth} ₫",
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Tinh trang phong
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tình trạng phòng:", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = if (room.available) "Còn trống" else "Đã được thuê",
                    fontSize = 15.sp, fontStyle = FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            if(room.unpaidBill != null && room.unpaidBill != 0){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Số hóa đơn chưa thanh toán:", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = room.unpaidBill.toString(),
                        fontSize = 15.sp, fontStyle = FontStyle.Italic
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            Button(
                onClick = {
                    navHostController.navigate("room_info/${apartmentId}/${room.roomId}/${room.name}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(text = "Xem chi tiết")
            }
        }
    }
}