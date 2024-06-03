package com.example.managementapp.main.presentation.apartment

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.managementapp.main.model.apartment.Apartment
import com.example.managementapp.main.presentation.MainViewModel
import com.example.managementapp.main.presentation.showLoading.ShowProgressBar
import com.example.managementapp.main.presentation.tenant.TenantScreenWhenSuccess
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ApartmentDetailScreen(
    modifier: Modifier = Modifier,
    apartmentId: String,
    viewModel: MainViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    LaunchedEffect(true, prefs.getBoolean("network_status", false)) {
        viewModel.getApartment(
            prefs.getBoolean("network_status", false),
            prefs.getString("user_id", "")!!,
            apartmentId
        )
    }

    val apartment = viewModel.apartment.collectAsState()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navHostController.navigate("update-apartment/${apartmentId}")
                }
            ) {
                Row {
                    Text(text = "Sửa thông tin")
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Thêm người thuê trọ"
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
            when (apartment.value) {
                is UiState.Success -> {
                    ApartmentDetailScreenWhenSuccess(
                        modifier = Modifier.fillMaxSize(),
                        apartment = (apartment.value as UiState.Success<Apartment>).data
                    )
                }

                is UiState.Error -> {
                    val error = (apartment.value as UiState.Error).data.toString()
                    Text(
                        text = error,
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
fun ApartmentDetailScreenWhenSuccess(
    modifier: Modifier = Modifier,
    apartment: Apartment
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
            Text(text = "Tên nhà trọ: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(text = apartment.name, fontSize = 18.sp, fontStyle = FontStyle.Italic)
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Địa chỉ ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = apartment.address,
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.End
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
            Text(text = "Số phòng: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = apartment.roomCount.toString(),
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
            Text(text = "Số phòng trống: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = apartment.roomEmpty.toString(),
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
            Text(text = "Giá điện mặc định: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = if (apartment.defaultElecPrice != null) apartment.defaultElecPrice.toString()
                else "Chưa xét",
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
            Text(text = "Giá nước mặc định: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = if (apartment.defaultWaterPrice != null) apartment.defaultWaterPrice.toString()
                else "Chưa xét",
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
            Text(text = "Giá phòng mặc định: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = if (apartment.defaultRoomPrice != null) apartment.defaultRoomPrice.toString()
                else "Chưa xét",
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}