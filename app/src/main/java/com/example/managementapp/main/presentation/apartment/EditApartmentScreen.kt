package com.example.managementapp.main.presentation.apartment

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.managementapp.main.model.apartment.Apartment
import com.example.managementapp.main.presentation.MainViewModel
import com.example.managementapp.main.presentation.showLoading.ShowProgressBar
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.MyResource
import com.example.managementapp.util.UiState
import com.example.managementapp.util.convert.toEmptyString
import com.example.managementapp.util.visualTransform.DecimalFormatter
import com.example.managementapp.util.visualTransform.DecimalInputVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditApartmentScreen(
    modifier: Modifier = Modifier,
    apartmentId: String,
    navHostController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    LaunchedEffect(viewModel, prefs.getBoolean("network_status", false)) {
        viewModel.getApartment(
            shouldMakeNetworkRequest = prefs.getBoolean("network_status", false),
            userId = prefs.getString("user_id", "")!!,
            apartmentId = apartmentId
        )
    }


    val apartmentState = viewModel.apartment.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.updateResult.collect {
            when (it) {
                is UiState.Success -> {
                    Toast.makeText(context, "Tạo thành công", Toast.LENGTH_SHORT).show()
                    println("Quay lai day")
                    navHostController.navigateUp()
                }

                is UiState.Error -> {
                    val errorMessage = it.data.toString()
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                ),
                navigationIcon = {
                    IconButton(onClick = { navHostController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                title = {
                    Text(
                        text = "Sửa thông tin nhà trọ",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(it)
        ) {
            when (apartmentState.value) {
                is UiState.Success -> {
                    val apartment = (apartmentState.value as UiState.Success).data
                    EditApartmentScreenSuccess(
                        apartment = apartment,
                        context = context,
                        navHostController = navHostController,
                        viewModel = viewModel
                    )
                }

                is UiState.Error -> {
                    val error = (apartmentState.value as UiState.Error).data.toString()
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

                else -> {

                }
            }
        }
    }
}


@Composable
fun EditApartmentScreenSuccess(
    modifier: Modifier = Modifier,
    apartment: Apartment,
    context: Context,
    navHostController: NavHostController,
    viewModel: MainViewModel
) {


    var name by rememberSaveable {
        mutableStateOf(apartment.name)
    }

    var roomCount by rememberSaveable {
        mutableStateOf(apartment.roomCount.toString())
    }

    var address by rememberSaveable {
        mutableStateOf(apartment.address)
    }

    var waterPrice by rememberSaveable {
        mutableStateOf(apartment.defaultWaterPrice.toEmptyString())
    }

    var electricPrice by rememberSaveable {
        mutableStateOf(apartment.defaultElecPrice.toEmptyString())
    }

    var roomPrice by rememberSaveable {
        mutableStateOf(apartment.defaultRoomPrice.toEmptyString())
    }

    var description by rememberSaveable {
        mutableStateOf(apartment.description)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = {
                Text(text = "Nhà trọ số 1")
            },
            label = {
                Text(text = "Tên nhà trọ")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = roomCount,
            onValueChange = { roomCount = it },
            placeholder = {
                Text(text = "12")
            },
            label = {
                Text(text = "Số phòng")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            placeholder = {
                Text(text = "Ngõ X, Tả Thanh Oai, Thanh Trì, Hà Nội")
            },
            label = {
                Text(text = "Địa chỉ nhà trọ")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = roomPrice,
            onValueChange = {
                roomPrice = DecimalFormatter().cleanup(it)
            },
            placeholder = {
                Text(text = "1000000")
            },
            label = {
                Text(text = "Tiền phòng / tháng")
            },
            supportingText = {
                Text(
                    text = "đơn vị: VND",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            visualTransformation = DecimalInputVisualTransformation(DecimalFormatter())
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = electricPrice,
            onValueChange = {
                electricPrice = DecimalFormatter().cleanup(it)
            },
            placeholder = {
                Text(text = "3000")
            },
            supportingText = {
                Text(
                    text = "đơn vị: VND/số",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            },
            label = {
                Text(text = "Tiền điện / số")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            visualTransformation = DecimalInputVisualTransformation(DecimalFormatter())
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = waterPrice,
            onValueChange = {
                waterPrice = DecimalFormatter().cleanup(it)
            },
            placeholder = {
                Text(text = "80000")
            },
            label = {
                Text(text = "Tiền nước / khối")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            supportingText = {
                Text(
                    text = "đơn vị: VND/khối",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            visualTransformation = DecimalInputVisualTransformation(DecimalFormatter())
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
            },
            placeholder = {
                Text(text = "Nhà trọ này mới xây")
            },
            label = {
                Text(text = "Ghi chú")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                if (name.isEmpty() || address.isEmpty() || roomCount.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Vui lòng nhập toàn bộ ô thông tin",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.updateApartment(
                        apartment.apartmentId,
                        name,
                        address,
                        roomCount.toInt(),
                        waterPrice.toInt(),
                        electricPrice.toInt(),
                        roomPrice.toInt(),
                        description
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(text = "Cập nhật nhà trọ")
        }
    }
}