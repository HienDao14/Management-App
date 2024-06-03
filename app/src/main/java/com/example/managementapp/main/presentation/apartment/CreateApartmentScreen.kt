package com.example.managementapp.main.presentation.apartment

import com.example.managementapp.util.visualTransform.DecimalFormatter
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.managementapp.main.presentation.MainViewModel
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.visualTransform.DecimalInputVisualTransformation
import com.example.managementapp.util.MyResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateApartmentScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController
) {
    var name by rememberSaveable {
        mutableStateOf("")
    }

    var roomCount by rememberSaveable {
        mutableStateOf("")
    }

    var address by rememberSaveable {
        mutableStateOf("")
    }

    var switchChecked by rememberSaveable {
        mutableStateOf(true)
    }

    var waterPrice by rememberSaveable {
        mutableStateOf("")
    }

    var electricPrice by rememberSaveable {
        mutableStateOf("")
    }

    var roomPrice by rememberSaveable {
        mutableStateOf("")
    }
    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(viewModel, context) {
        viewModel.createResult.collect {
            when (it) {
                is MyResource.Loading -> {}

                is MyResource.Success -> {
                    Toast.makeText(context, "Tạo thành công", Toast.LENGTH_SHORT).show()
                    navController.navigateUp()
                }

                is MyResource.Error -> {
                    val errorMessage = it.message
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                title = {
                    Text(
                        text = "Tạo nhà trọ của bạn",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(paddingValue)
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
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tạo toàn bộ phòng", fontSize = 15.sp)
                Switch(
                    checked = switchChecked,
                    onCheckedChange = { switchChecked = !switchChecked }
                )
            }

            if (switchChecked) {
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
                        Text(text = "Tiền nước / số")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
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
            }

            Button(
                onClick = {
                    if (name.isEmpty() || address.isEmpty() || roomCount.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Vui lòng nhập toàn bộ ô thông tin",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (switchChecked && (electricPrice.isEmpty() || waterPrice.isEmpty() || roomPrice.isEmpty())) {
                        Toast.makeText(
                            context,
                            "Cần nhập toàn bộ thông tin khi chọn tạo phòng",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.createApartment(
                            name,
                            address,
                            roomCount,
                            switchChecked,
                            electricPrice.toIntOrNull(),
                            waterPrice.toIntOrNull(),
                            roomPrice.toIntOrNull()
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(text = "Tạo nhà trọ")
            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 10.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Divider(
//                    modifier = Modifier.width(40.dp),
//                    thickness = 1.dp
//                )
//                Text(text = "Hoặc nhập kết quả tự động")
//                Divider(
//                    modifier = Modifier.width(40.dp),
//                    thickness = 1.dp
//                )
//            }
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 20.dp)
//                    .size(50.dp),
//                shape = RoundedCornerShape(10.dp),
//                onClick = {
//
//                },
//                border = BorderStroke(width = 1.dp, color = Color.Black)
//            ) {
//                Column(
//                    modifier = Modifier.fillMaxSize(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.KeyboardVoice,
//                        contentDescription = "Nhập bằng giọng nói"
//                    )
//                    Text(text = "Giọng nói", fontSize = 15.sp)
//                }
//            }
        }
    }
}