package com.example.managementapp.main.presentation.room

import android.content.Context
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.managementapp.util.UiState

@Composable
fun RoomDetailScreen(
    modifier: Modifier = Modifier,
    apartmentId: String,
    roomId: String,
    navHostController: NavHostController,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    LaunchedEffect(true, prefs.getBoolean("network_status", false)) {
        viewModel.getRoomDetail(apartmentId, roomId, prefs.getBoolean("network_status", false))
    }
    val room = viewModel.roomDetail.collectAsState()
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navHostController.navigate( "update-room/${apartmentId}/${roomId}")
                }
            ) {
                Row {
                    Text(text = "Sửa thông tin")
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Thêm phòng trọ")
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

            when (room.value) {
                is UiState.Success -> {
                    val roomData = (room.value as UiState.Success<Room>).data
                    RoomDetailScreenSuccess(room = roomData, navHostController = navHostController)
                }

                is UiState.Error -> {
                    val errorMsg = (room.value as UiState.Error).data.toString()
                    Text(
                        text = errorMsg,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                }
                is UiState.Loading -> {
                    ShowProgressBar(modifier = Modifier.fillMaxSize())
                }
                else -> {}
            }
        }
    }
}

@Composable
fun RoomDetailScreenSuccess(
    modifier: Modifier = Modifier,
    room: Room,
    navHostController: NavHostController
) {
    Column(
        modifier = modifier

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Tên phòng trọ: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(text = room.name, fontSize = 18.sp, fontStyle = FontStyle.Italic)
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Số người ở: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(text = if(room.tenantNumber != 0) "${room.tenantNumber} người" else "Trống", fontSize = 18.sp, fontStyle = FontStyle.Italic)
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Tình trạng phòng: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = if(room.available) "Còn trống" else "Đã được thuê",
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        room.area?.let {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Diện tích phòng: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${room.area} m²",
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Giá điện: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = "${room.elecPricePerMonth} VND / số",
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Giá nước: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = "${room.waterPricePerMonth} VND / khối",
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Giá phòng: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = "${room.livingPricePerMonth} VND",
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
        }
        Spacer(modifier = Modifier.height(20.dp))


    }
}