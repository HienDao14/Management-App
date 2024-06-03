package com.example.managementapp.main.presentation.bill

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.managementapp.main.model.bill.AdditionService
import com.example.managementapp.main.model.bill.Bill
import com.example.managementapp.main.model.bill.CreateBillModel
import com.example.managementapp.main.presentation.showDialog.AddServiceDialog
import com.example.managementapp.main.presentation.showLoading.ShowProgressBar
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.UiState
import com.example.managementapp.util.convert.toDateString
import com.example.managementapp.util.visualTransform.DecimalFormatter
import com.example.managementapp.util.visualTransform.DecimalInputVisualTransformation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBillScreen(
    modifier: Modifier = Modifier,
    billId: String,
    viewModel: BillViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    LaunchedEffect(true) {
        viewModel.getBill(
            prefs.getBoolean("network_status", false),
            billId
        )
    }
    val billState = viewModel.bill

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                ),
                title = {
                    Text(text = "Sửa hóa đơn")
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
                .background(BackgroundColor)
                .padding(it)
        ) {
            if (billState.isLoading) {
                ShowProgressBar(modifier = Modifier.fillMaxSize())
            }
            if (billState.error != null) {
                Toast.makeText(context, billState.error, Toast.LENGTH_SHORT).show()
            }
            if (billState.bill != null) {
                EditBillWhenSuccess(
                    modifier = Modifier.fillMaxSize(),
                    bill = billState.bill,
                    viewModel = viewModel,
                    context = context,
                    onSuccess = {
                        navHostController.navigateUp()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBillWhenSuccess(
    modifier: Modifier = Modifier,
    bill: Bill,
    viewModel: BillViewModel,
    context: Context,
    onSuccess: () -> Unit
) {
    LaunchedEffect(viewModel, context) {
        viewModel.updateBillResult.collect {
            when (it) {
                is UiState.Success -> {
                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    onSuccess()
                }

                is UiState.Error -> {
                    val errorMessage = it.data
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }

                else -> Unit
            }
        }
    }
    //Date Picker
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    var openDatePicker = remember {
        mutableStateOf(false)
    }
    var datePickerFor by rememberSaveable {
        mutableStateOf("")
    }

    var openServiceDialog = remember {
        mutableStateOf(false)
    }

    var createdAt by rememberSaveable {
        mutableStateOf(bill.createdAt)
    }

    var paidAt by rememberSaveable {
        mutableStateOf(System.currentTimeMillis().toDateString())
    }

    var oldWaterNumber by rememberSaveable {
        mutableStateOf(bill.oldWaterNumber)
    }
    var oldElectricNumber by rememberSaveable {
        mutableStateOf(bill.oldElectricNumber)
    }

    var newWaterNumber by rememberSaveable {
        mutableStateOf(bill.newWaterNumber)
    }
    var newElectricNumber by rememberSaveable {
        mutableStateOf(bill.newElectricNumber)
    }
    var waterNumber by rememberSaveable {
        mutableIntStateOf(bill.waterNumber)
    }
    var electricNumber by rememberSaveable {
        mutableIntStateOf(bill.electricNumber)
    }
    var waterPrice by rememberSaveable {
        mutableIntStateOf(bill.waterPrice)
    }
    var electricPrice by rememberSaveable {
        mutableIntStateOf(bill.electricPrice)
    }
    var roomPrice by rememberSaveable {
        mutableStateOf(bill.roomPrice.toString())
    }
    var note by rememberSaveable {
        mutableStateOf("")
    }
    var total by rememberSaveable {
        mutableIntStateOf(bill.total)
    }

    val listService = remember {
        mutableStateListOf<AdditionService>()
    }

    var paidSwitch by rememberSaveable {
        mutableStateOf(bill.status)
    }

    LaunchedEffect(true) {
        val typeToken = object : TypeToken<List<AdditionService>>() {}.type
        val services = Gson().fromJson<List<AdditionService>>(bill.additionServices, typeToken)
        listService.addAll(services)
    }

    val electricPricePerNUmber = bill.electricPrice / bill.electricNumber
    val waterPricePerNumber = bill.waterPrice / bill.waterNumber
    if (openServiceDialog.value) {
        AddServiceDialog(
            setShowDialog = {
                openServiceDialog.value = it
            },
            onSubmit = { name, price ->
                listService.add(AdditionService(name, price))
            }
        )
    }

    if (openDatePicker.value) {
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = {
                openDatePicker.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDatePicker.value = false
                        when (datePickerFor) {
                            "create" -> {
                                createdAt = datePickerState.selectedDateMillis.toDateString()
                                datePickerState.setSelection(System.currentTimeMillis())
                            }

                            "paid" -> {
                                paidAt = datePickerState.selectedDateMillis.toDateString()
                                datePickerState.setSelection(System.currentTimeMillis())
                            }
                        }
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDatePicker.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = bill.roomName,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = createdAt,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(text = "Ngày ghi")
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        openDatePicker.value = true
                        datePickerFor = "create"
                    }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Chọn ngày ghi"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Thông tin điện nước cũ", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = oldElectricNumber,
                onValueChange = {
                    if (it.length <= 6) {
                        oldElectricNumber = it
                    }
                },
                label = {
                    Text(text = "Số điện cũ")
                },
                supportingText = {
                    Text(text = "${oldElectricNumber.length}/6")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(20.dp))
            OutlinedTextField(
                value = oldWaterNumber,
                onValueChange = {
                    if (it.length <= 6) {
                        oldWaterNumber = it
                    }
                },
                label = {
                    Text(text = "Số nước cũ")
                },
                supportingText = {
                    Text(text = "${oldWaterNumber.length}/6")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        //Thông tin mới
        Text(text = "Thông tin điện nước mới", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = newElectricNumber,
                onValueChange = {
                    if (it.length <= 6) {
                        newElectricNumber = it
                        electricNumber = newElectricNumber.toInt() - oldElectricNumber.toInt()
                        electricPrice = electricNumber * electricPricePerNUmber
                    }
                },
                label = {
                    Text(text = "Số điện mới")
                },
                supportingText = {
                    Text(text = "${newElectricNumber.length}/6")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(20.dp))
            OutlinedTextField(
                value = newWaterNumber,
                onValueChange = {
                    if (it.length <= 6) {
                        newWaterNumber = it
                        waterNumber = newWaterNumber.toInt() - oldWaterNumber.toInt()
                        waterPrice = waterNumber * waterPricePerNumber
                    }
                },
                label = {
                    Text(text = "Số nước mới")
                },
                supportingText = {
                    Text(text = "${newWaterNumber.length}/6")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = roomPrice,
            onValueChange = {
                if (it != "")
                    roomPrice = DecimalFormatter().cleanup(it)
                else roomPrice = ""
            },
            label = {
                Text(text = "Giá phòng")
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            supportingText = {
                Text(
                    text = "đơn vị: VND",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = DecimalInputVisualTransformation(DecimalFormatter())
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Phí dịch vụ thêm", fontSize = 18.sp)
            IconButton(onClick = {
                openServiceDialog.value = true
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm dịch vụ")
            }
        }
        listService.forEachIndexed { index, additionService ->
            OutlinedTextField(
                value = additionService.price.toString(),
                onValueChange = {
                    if (it != "") listService[index] =
                        AdditionService(additionService.name, it.toInt())
                    else listService[index] = AdditionService(additionService.name, 0)
                },
                label = {
                    Text(text = additionService.name)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                supportingText = {
                    Text(
                        text = "đơn vị: VND",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = DecimalInputVisualTransformation(DecimalFormatter())
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = {
                Text(text = "Ghi chú")
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        val listTableData = ArrayList<TableData>()
        var totalPriceTmp = 0
        listTableData.add(
            TableData(
                "Điện",
                electricNumber,
                electricPricePerNUmber,
                electricPrice
            )
        )
        listTableData.add(
            TableData(
                "Nước",
                waterNumber,
                waterPricePerNumber,
                waterPrice
            )
        )
        listService.forEach { service ->
            listTableData.add(
                TableData(
                    service.name,
                    null,
                    null,
                    service.price
                )
            )
            totalPriceTmp += service.price
        }

        listTableData.add(
            TableData(
                "Phòng",
                1,
                roomPrice.toInt(),
                roomPrice.toInt()
            )
        )
        totalPriceTmp += electricPrice + waterPrice + roomPrice.toInt()
        listTableData.add(
            TableData(
                "Tổng tiền",
                null, null, totalPriceTmp
            )
        )

        val nameColumnWeight = .3f
        val numberColumnWeight = .2f
        val priceColumnWeight = .3f
        val totalColumnWeight = .3f

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                TableCell(text = "Tên", weight = nameColumnWeight)
                TableCell(text = "Số", weight = numberColumnWeight)
                TableCell(text = "Giá", weight = priceColumnWeight)
                TableCell(text = "Tổng tiền", weight = totalColumnWeight)
            }
            listTableData.forEach { data ->
                Row {
                    TableCell(text = data.name, weight = nameColumnWeight)
                    TableCell(
                        text = if (data.number != null) data.number.toString() else "",
                        weight = numberColumnWeight
                    )
                    TableCell(
                        text = if (data.pricePerNum != null) data.pricePerNum.toString() else "",
                        weight = priceColumnWeight
                    )
                    TableCell(text = data.total.toString(), weight = totalColumnWeight)
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Đã thanh toán?",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Switch(
                checked = paidSwitch,
                onCheckedChange = {
                    paidSwitch = !paidSwitch
                }
            )
        }
        if (paidSwitch) {
            OutlinedTextField(
                value = paidAt,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(text = "Ngày thanh toán")
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            openDatePicker.value = true
                            datePickerFor = "paid"
                        }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Chọn ngày ghi"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        Button(
            onClick = {
                if (newElectricNumber.toInt() < oldElectricNumber.toInt()) {
                    Toast.makeText(
                        context,
                        "Số điện mới không được nhỏ hơn số điện cũ",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }
                if (newWaterNumber.toInt() < oldWaterNumber.toInt()) {
                    Toast.makeText(
                        context,
                        "Số nước mới không được nhỏ hơn số nước cũ",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }
                total = totalPriceTmp
                val updatedBill = CreateBillModel(
                    bill.name,
                    oldElectricNumber, newElectricNumber, electricNumber, electricPrice,
                    roomPrice.toInt(),
                    oldWaterNumber, newWaterNumber, waterNumber, waterPrice,
                    total,
                    createdAt, paidSwitch,
                    if (!paidSwitch) null else paidAt,
                    bill.roomName, note, listService
                )
                viewModel.updateBill(
                    bill.billId, updatedBill
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Xác nhận")

        }
    }
}