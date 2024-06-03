package com.example.managementapp.main.presentation.room

import com.example.managementapp.util.visualTransform.DecimalFormatter
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.managementapp.main.presentation.showLoading.ShowProgressBar
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.visualTransform.DecimalInputVisualTransformation
import com.example.managementapp.util.MyResource
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoomScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: RoomViewModel = hiltViewModel(),
    apartmentId: String
) {
    var name by rememberSaveable {
        mutableStateOf("")
    }
    var roomPrice by rememberSaveable {
        mutableStateOf("")
    }
    var electricPrice by rememberSaveable {
        mutableStateOf("")
    }
    var waterPrice by rememberSaveable {
        mutableStateOf("")
    }
    var area by rememberSaveable {
        mutableStateOf("")
    }
    var description by rememberSaveable {
        mutableStateOf("")
    }

    val context = LocalContext.current

//    val numberFormat = NumberFormat.getInstance(Locale.getDefault())
//    val decimalFormat = numberFormat as DecimalFormat
//    decimalFormat.applyPattern("#,###")
    LaunchedEffect(viewModel, context) {
        viewModel.createRoomResult.collect {
            when (it) {
                is MyResource.Loading -> {}

                is MyResource.Success -> {
                    Toast.makeText(context, "Tạo thành công", Toast.LENGTH_SHORT).show()
                    navHostController.navigateUp()
                }

                is MyResource.Error -> {
                    val errorMessage = it.message
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
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
                title = {
                    Text(text = "Thêm phòng trọ")
                },
                navigationIcon = {
                    IconButton(onClick = { navHostController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(BackgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                //Ten phong tro
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = {
                        Text(text = "VD: Phòng trọ số 1")
                    },
                    label = {
                        Text(text = "Tên phòng trọ")
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
                //Tien phong / thang
                OutlinedTextField(
                    value = roomPrice,
                    onValueChange = {
                        roomPrice = DecimalFormatter().cleanup(it)
                    },
                    placeholder = {
                        Text(text = "VD: 1,000,000")
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

                //Tien dien / so
                OutlinedTextField(
                    value = electricPrice,
                    onValueChange = {
                        electricPrice = DecimalFormatter().cleanup(it)
                    },
                    placeholder = {
                        Text(text = "vd: 3000")
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

                //Tien nuoc / thang
                OutlinedTextField(
                    value = waterPrice,
                    onValueChange = {
                        waterPrice = DecimalFormatter().cleanup(it)
                    },
                    placeholder = {
                        Text(text = "VD: 80000")
                    },
                    label = {
                        Text(text = "Tiền nước / số")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    supportingText = {
                        Text(
                            text = "đơn vị: VND/số",
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

                //Dien tich (optional)
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    placeholder = {
                        Text(text = "16")
                    },
                    label = {
                        Text(text = "Diện tích phòng (Có thể bỏ qua)")
                    },
                    supportingText = {
                        Text(
                            text = "đơn vị: m\u00B2",
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
                        .padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Ghi chu (optional)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = {
                        Text(text = "16")
                    },
                    label = {
                        Text(text = "Ghi chú (Có thể bỏ qua)")
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
                        if (name.isEmpty() || roomPrice.isEmpty() || electricPrice.isEmpty() || waterPrice.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Vui lòng nhập toàn bộ ô thông tin",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.createRoom(
                                apartmentId,
                                name,
                                roomPrice.toInt(),
                                area.toDoubleOrNull(),
                                electricPrice.toInt(),
                                waterPrice.toInt(),
                                description
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(text = "Tạo phòng trọ")
                }
            }
        }
    }
}

