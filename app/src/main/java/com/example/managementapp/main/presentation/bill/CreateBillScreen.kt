package com.example.managementapp.main.presentation.bill

import android.content.Context
import android.net.Uri
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.example.managementapp.main.model.bill.CreateBillModel
import com.example.managementapp.main.model.room.RoomForBill
import com.example.managementapp.main.presentation.imageScreen.ImageScreen
import com.example.managementapp.main.presentation.showDialog.AddServiceDialog
import com.example.managementapp.main.presentation.showLoading.ShowProgressBar
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.UiState
import com.example.managementapp.util.convert.toDateString
import com.example.managementapp.util.visualTransform.DecimalFormatter
import com.example.managementapp.util.visualTransform.DecimalInputVisualTransformation

data class TableData(
    val name: String,
    val number: Int?,
    val pricePerNum: Int?,
    val total: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBillScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: BillViewModel = hiltViewModel(),
    roomId: String
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    LaunchedEffect(true, prefs.getBoolean("network_status", false)) {
        viewModel.getRoomForBill(roomId)
    }

    val initState = viewModel.roomForBill

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                ),
                title = {
                    Text(text = "Thêm hóa đơn")
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
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .background(BackgroundColor)
        ) {
            if (initState.isLoading) {
                ShowProgressBar(modifier = Modifier.fillMaxSize())
            }
            if (initState.error != null) {
                Text(text = initState.error.toString())
            }
            if (initState.records != null) {
                CreateBillWhenSuccess(
                    modifier = Modifier.fillMaxSize(),
                    roomForBill = initState.records,
                    context,
                    viewModel,
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
fun CreateBillWhenSuccess(
    modifier: Modifier = Modifier,
    roomForBill: RoomForBill,
    context: Context,
    viewModel: BillViewModel,
    onSuccess: () -> Unit
) {
    LaunchedEffect(viewModel, context) {
        viewModel.createBillChannel.collect {
            when (it) {
                is UiState.Success -> {
                    Toast.makeText(context, "Tạo thành công", Toast.LENGTH_SHORT).show()
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
    val room = roomForBill.room
    val listRecords = roomForBill.records
    val listRecordsName = ArrayList<String>()

    // Records
    if (listRecords.isNotEmpty()) {
        listRecords.forEachIndexed { index, record ->
            listRecordsName.add("${index + 1}. Ghi ngày ${record.recordedAt}")
        }
    }

    var oldRecordSelected by rememberSaveable {
        mutableStateOf("")
    }
    var newRecordSelected by rememberSaveable {
        mutableStateOf("")
    }

    //Dropdown records
    var oldDropDownShown by rememberSaveable {
        mutableStateOf(false)
    }
    var newDropDownShow by rememberSaveable {
        mutableStateOf(false)
    }


    //Date Picker
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    var openDatePicker = remember {
        mutableStateOf(false)
    }
    var openServiceDialog = remember {
        mutableStateOf(false)
    }

    var recordedDate by rememberSaveable {
        mutableStateOf(System.currentTimeMillis().toDateString())
    }

    //Image Screen Dialog
    var showImageScreen by rememberSaveable {
        mutableStateOf(false)
    }
    var uriForImageScreen by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }
    var titleForImageScreen by rememberSaveable {
        mutableStateOf("")
    }

    if (showImageScreen && uriForImageScreen != null) {
        ImageScreen(
            uri = uriForImageScreen!!,
            title = titleForImageScreen,
            setShowImage = {
                showImageScreen = it
            },
            context = context,
            modifier = Modifier.fillMaxWidth()
        )
    }

    //water and electric Number
    var oldWaterNumber by rememberSaveable {
        mutableStateOf("")
    }
    var oldElectricNumber by rememberSaveable {
        mutableStateOf("")
    }

    var newWaterNumber by rememberSaveable {
        mutableStateOf("")
    }
    var newElectricNumber by rememberSaveable {
        mutableStateOf("")
    }
    var waterNumber by rememberSaveable {
        mutableIntStateOf(0)
    }
    var electricNumber by rememberSaveable {
        mutableIntStateOf(0)
    }
    var waterPrice by rememberSaveable {
        mutableIntStateOf(0)
    }
    var electricPrice by rememberSaveable {
        mutableIntStateOf(0)
    }
    var roomPrice by rememberSaveable {
        mutableStateOf(room.livingPricePerMonth.toString())
    }
    var note by rememberSaveable {
        mutableStateOf("")
    }
    var total by rememberSaveable {
        mutableIntStateOf(0)
    }


    LaunchedEffect(true) {
        try {
            oldRecordSelected = listRecordsName[1]
            oldElectricNumber = listRecords[1].electricNumber
            oldWaterNumber = listRecords[1].waterNumber
        } catch (e: Exception) {
            oldRecordSelected = ""
        }
        try {
            newRecordSelected = listRecordsName[0]
            newElectricNumber = listRecords[0].electricNumber
            newWaterNumber = listRecords[0].waterNumber
        } catch (e: Exception) {
            newRecordSelected = ""
        }
        if (newElectricNumber != "" && oldElectricNumber != "") {
            electricNumber = newElectricNumber.toInt() - oldElectricNumber.toInt()
            electricPrice = electricNumber * room.elecPricePerMonth
        }
        if (newWaterNumber != "" && oldWaterNumber != "") {
            waterNumber = newWaterNumber.toInt() - oldWaterNumber.toInt()
            waterPrice = waterNumber * room.waterPricePerMonth
        }
    }

    val listService = remember {
        mutableStateListOf<AdditionService>()
    }

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
                        recordedDate = datePickerState.selectedDateMillis.toDateString()
                        //datePickerState.setSelection(System.currentTimeMillis())
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
            text = room.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = recordedDate,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(text = "Ngày ghi")
            },
            trailingIcon = {
                IconButton(onClick = { openDatePicker.value = true }) {
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

        if (listRecordsName.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = oldDropDownShown,
                onExpandedChange = { oldDropDownShown = !oldDropDownShown }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .background(BackgroundColor),
                    value = oldRecordSelected, onValueChange = {}, readOnly = true, trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = oldDropDownShown)
                    })
                ExposedDropdownMenu(
                    modifier = Modifier.background(BackgroundColor),
                    expanded = oldDropDownShown,
                    onDismissRequest = { oldDropDownShown = false }) {
                    listRecordsName.forEachIndexed { index, name ->
                        DropdownMenuItem(
                            text = { Text(text = name) },
                            onClick = {
                                oldRecordSelected = name
                                oldElectricNumber = listRecords[index].electricNumber
                                oldWaterNumber = listRecords[index].waterNumber
                                oldDropDownShown = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

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

        if (listRecordsName.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = newDropDownShow,
                onExpandedChange = { newDropDownShow = !newDropDownShow }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .background(BackgroundColor),
                    value = newRecordSelected, onValueChange = {}, readOnly = true, trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = newDropDownShow)
                    })
                ExposedDropdownMenu(
                    modifier = Modifier.background(BackgroundColor),
                    expanded = newDropDownShow,
                    onDismissRequest = { newDropDownShow = false }) {
                    listRecordsName.forEachIndexed { index, name ->
                        DropdownMenuItem(
                            text = { Text(text = name) },
                            onClick = {
                                newRecordSelected = name
                                newElectricNumber = listRecords[index].electricNumber
                                newWaterNumber = listRecords[index].waterNumber
                                newDropDownShow = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = newElectricNumber,
                onValueChange = {
                    if (it.length <= 6) {
                        newElectricNumber = it
                        electricNumber = newElectricNumber.toInt() - oldElectricNumber.toInt()
                        electricPrice = electricNumber * room.elecPricePerMonth
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
                        waterPrice = waterNumber * room.waterPricePerMonth
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
                room.elecPricePerMonth,
                electricPrice
            )
        )
        listTableData.add(
            TableData(
                "Nước",
                waterNumber,
                room.waterPricePerMonth,
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
                val bill = CreateBillModel(
                    "Hóa đơn ${room.name} tháng ${recordedDate.substring(3, 5)}",
                    oldElectricNumber, newElectricNumber, electricNumber, electricPrice,
                    roomPrice.toInt(),
                    oldWaterNumber, newWaterNumber, waterNumber, waterPrice,
                    total,
                    recordedDate, false, null, room.name, note, listService
                )
                viewModel.createBill(
                    room.apartmentId, room.roomId, bill
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(text = "Tạo hóa đơn")
        }

    }

}