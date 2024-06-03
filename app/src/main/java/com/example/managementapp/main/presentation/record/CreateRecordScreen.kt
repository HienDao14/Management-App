package com.example.managementapp.main.presentation.record

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.managementapp.main.model.room.RoomWithRecord
import com.example.managementapp.main.presentation.imageScreen.ImageScreen
import com.example.managementapp.main.presentation.showDialog.ShowImagePickerDialog
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.CompareDateString
import com.example.managementapp.util.ComposeFileProvider
import com.example.managementapp.util.MyResource
import com.example.managementapp.util.UiState
import com.example.managementapp.util.convert.toDateString
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    modifier: Modifier = Modifier,
    apartmentId: String,
    roomId: String,
    navHostController: NavHostController,
    viewModel: RecordViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.getRoomsWithRecord(apartmentId)
    }
    var roomName by rememberSaveable {
        mutableStateOf("")
    }
    var selectedRoom: RoomWithRecord? = null
    val listName = ArrayList<String>()
    val listRecords = viewModel.roomWithRecordsState.records
    listRecords?.forEach { room ->
        if (room.roomId == roomId) {
            selectedRoom = room
            roomName = room.roomName
        }
        listName.add(room.roomName)
    }
    println(selectedRoom)
    var dropDownShown by rememberSaveable {
        mutableStateOf(false)
    }

    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    var openDatePicker = remember {
        mutableStateOf(false)
    }

    var recordedDate by rememberSaveable {
        mutableStateOf(System.currentTimeMillis().toDateString())
    }

    //Pick images
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

    var selectedElectricURi by remember {
        mutableStateOf<Uri?>(null)
    }
    var selectedWaterUri by remember {
        mutableStateOf<Uri?>(null)
    }
    var hasElectricImage by remember {
        mutableStateOf(false)
    }
    var hasWaterImage by remember {
        mutableStateOf(false)
    }

    var showImagePickerDialog by remember {
        mutableStateOf(false)
    }

    var imageDialogFor by remember {
        mutableStateOf("")
    }

    val electricCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        hasElectricImage = success
    }
    val waterCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        hasWaterImage = success
    }

    val electricPhotoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            hasElectricImage = uri != null
            selectedElectricURi = uri
        }
    val waterPhotoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            hasWaterImage = uri != null
            selectedWaterUri = uri
        }

    var electricValue by rememberSaveable {
        mutableStateOf("")
    }
    var waterValue by rememberSaveable {
        mutableStateOf("")
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                ),
                title = {
                    Text(text = "Ghi điện/nước")
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
                .background(BackgroundColor)
                .padding(it)
        ) {
            LaunchedEffect(viewModel, context) {
                viewModel.createRecordChannel.collect {
                    when (it) {
                        is UiState.Success -> {
                            Toast.makeText(context, "Tạo thành công", Toast.LENGTH_SHORT).show()
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
            if (showImagePickerDialog) {
                ShowImagePickerDialog(
                    setShowDialog = {
                        showImagePickerDialog = it
                    },
                    pickImage = {
                        if (imageDialogFor == "electric") {
                            electricPhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        } else {
                            waterPhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    },
                    takeImage = {
                        if (imageDialogFor == "electric") {
                            hasElectricImage = false
                            val uri = ComposeFileProvider.getImageUri(context)
                            electricCameraLauncher.launch(uri)
                            selectedElectricURi = uri
                        } else {
                            hasWaterImage = false
                            val uri = ComposeFileProvider.getImageUri(context)
                            selectedWaterUri = uri
                            waterCameraLauncher.launch(uri)
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Chọn phòng", fontSize = 16.sp)

                    ExposedDropdownMenuBox(
                        expanded = dropDownShown,
                        onExpandedChange = { dropDownShown = !dropDownShown }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor()
                                .background(BackgroundColor),
                            value = roomName, onValueChange = {}, readOnly = true, trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropDownShown)
                            })
                        ExposedDropdownMenu(
                            modifier = Modifier.background(BackgroundColor),
                            expanded = dropDownShown,
                            onDismissRequest = { dropDownShown = false }) {
                            listName.forEachIndexed { index, name ->
                                DropdownMenuItem(
                                    text = { Text(text = name) },
                                    onClick = {
                                        selectedRoom = listRecords?.get(index)
                                        roomName = name
                                        dropDownShown = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Ngày ghi", fontSize = 16.sp)

                    OutlinedTextField(
                        value = recordedDate,
                        onValueChange = {},
                        trailingIcon = {
                            IconButton(onClick = { openDatePicker.value = true }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Chọn ngày ghi"
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = roomName,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))

                selectedRoom?.let { room ->
                    if (room.waterNumber != "" && room.electricNumber != "" && room.recordedAt != "") {
                        Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp)) {
                            Text(
                                text = "Thông tin ghi điện nước gần nhất:",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(text = "Số điện: ${room.electricNumber}", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(text = "Số nước: ${room.waterNumber}", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(text = "Ngày ghi: ${room.recordedAt}", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
                Text(
                    text = "Ghi thông tin mới",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        OutlinedTextField(
                            value = electricValue,
                            onValueChange = {
                                if(it.length <= 6) electricValue = it},
                            label = {
                                Text(text = "Số điện")
                            },
                            supportingText = {
                                Text(text = "${electricValue.length}/6")
                            },
                            placeholder = {
                                Text(text = "068995")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            )
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Đồng hồ điện")
                            IconButton(onClick = {
                                imageDialogFor = "electric"
                                showImagePickerDialog = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null
                                )
                            }
                        }
                        //Image Async
                        if (hasElectricImage && selectedElectricURi != null) {
                            AsyncImage(
                                model = selectedElectricURi,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(150.dp)
                                    .clickable {
                                        showImageScreen = true
                                        uriForImageScreen = selectedElectricURi
                                        titleForImageScreen = "Ảnh đồng hồ điện"
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

//                    Spacer(modifier = Modifier.width(20.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = waterValue,
                            onValueChange = {
                                if(it.length <= 6) waterValue = it },
                            label = {
                                Text(text = "Số nước")
                            },
                            supportingText = {
                                Text(text = "${waterValue.length}/6")
                            },
                            placeholder = {
                                Text(text = "068995")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            )
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Đồng hồ nước")
                            IconButton(onClick = {
                                imageDialogFor = "water"
                                showImagePickerDialog = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null
                                )
                            }
                        }
                        //Image Async
                        if (hasWaterImage && selectedWaterUri != null) {
                            AsyncImage(
                                model = selectedWaterUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(150.dp)
                                    .clickable {
                                        showImageScreen = true
                                        uriForImageScreen = selectedWaterUri
                                        titleForImageScreen = "Ảnh đồng hồ nước"
                                    },

                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        var isTheLast = true
                        selectedRoom?.let {
                            if(it.recordedAt != ""){
                                isTheLast = CompareDateString(selectedRoom!!.recordedAt, recordedDate)
                            }
                            if(it.electricNumber != ""){
                                if(isTheLast){
                                    if(it.electricNumber > electricValue){
                                        Toast.makeText(context, "Số điện tháng sau phải cao hơn tháng trước", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                } else {
                                    if(it.electricNumber < electricValue){
                                        Toast.makeText(context, "Số điện tháng sau phải cao hơn tháng trước", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                }
                            }
                            if(it.waterNumber != ""){
                                if(isTheLast){
                                    if(it.waterNumber > waterValue){
                                        Toast.makeText(context, "Số nước tháng sau phải cao hơn tháng trước", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                } else {
                                    if(it.waterNumber < waterValue){
                                        Toast.makeText(context, "Số nước tháng sau phải cao hơn tháng trước", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                }
                            }
                        }

                        val listImg = ArrayList<File>()
                        selectedElectricURi?.let { electric ->
                            val frontBytes =
                                context.contentResolver.openInputStream(electric)?.use {
                                    it.readBytes()
                                }
                            val frontFile =
                                File(
                                    context.cacheDir,
                                    "${roomName.trim()}_${recordedDate.toDateString()}_electric.jpg"
                                )
                            frontFile.createNewFile()
                            frontFile.outputStream().use {
                                it.write(frontBytes)
                            }
                            listImg.add(frontFile)
                        }
                        selectedWaterUri?.let { water ->
                            val behindBytes =
                                context.contentResolver.openInputStream(water)?.use {
                                    it.readBytes()
                                }

                            val behindFile =
                                File(
                                    context.cacheDir,
                                    "${roomName.trim()}_${recordedDate.toDateString()}_water.jpg"
                                )
                            behindFile.createNewFile()
                            behindFile.outputStream().use {
                                it.write(behindBytes)
                            }
                            listImg.add(behindFile)
                        }
                        viewModel.createRecord(
                            roomId = selectedRoom?.roomId ?: "",
                            electricNumber = electricValue,
                            waterNumber = waterValue,
                            recordedAt = recordedDate,
                            isTheLast = isTheLast,
                            recordImages = if (listImg.isEmpty()) null else listImg
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Ghi số liệu")
                }
            }
        }
    }
}