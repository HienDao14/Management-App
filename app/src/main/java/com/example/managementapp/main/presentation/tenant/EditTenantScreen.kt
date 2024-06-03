package com.example.managementapp.main.presentation.tenant

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.managementapp.main.model.tenant.Tenant
import com.example.managementapp.main.presentation.showDialog.ShowImagePickerDialog
import com.example.managementapp.main.presentation.showLoading.ShowProgressBar
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.ComposeFileProvider
import com.example.managementapp.util.DateParse
import com.example.managementapp.util.ImageLink
import com.example.managementapp.util.MyResource
import com.example.managementapp.util.UiState
import com.example.managementapp.util.convert.toDateString
import com.example.managementapp.util.convert.toLinkImage
import com.example.managementapp.util.visualTransform.DateVisualTransformation
import com.example.managementapp.util.visualTransform.DecimalFormatter
import com.example.managementapp.util.visualTransform.DecimalInputVisualTransformation
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTenantScreen(
    modifier: Modifier = Modifier,
    roomId: String,
    tenantId: String,
    navHostController: NavHostController,
    viewModel: TenantViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    LaunchedEffect(viewModel, prefs.getBoolean("network_status", false)) {
        viewModel.getTenant(
            shouldMakeNetworkRequest = prefs.getBoolean("network_status", false),
            roomId = roomId,
            tenantId = tenantId
        )
    }
    val tenantState = viewModel.tenant.collectAsState()
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
                        text = "Sửa thông tin nguời thuê trọ",
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
            when (tenantState.value) {
                is UiState.Success -> {
                    val tenant = (tenantState.value as UiState.Success).data
                    EditTenantScreenSuccess(
                        tenant = tenant,
                        navHostController = navHostController,
                        viewModel = viewModel,
                        context = context
                    )
                }

                is UiState.Error -> {
                    val error = (tenantState.value as UiState.Error).data.toString()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTenantScreenSuccess(
    modifier: Modifier = Modifier,
    tenant: Tenant,
    navHostController: NavHostController,
    viewModel: TenantViewModel,
    context: Context,
) {
    LaunchedEffect(viewModel, context) {
        viewModel.updateTenantChannel.collect {
            when (it) {
                is UiState.Success -> {
                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    navHostController.navigateUp()
                }

                is UiState.Error -> {
                    val errorMessage = it.data.toString()
                    println(errorMessage)
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }


    var fullName by remember {
        mutableStateOf(tenant.fullName)
    }
    var phoneNumber by remember {
        mutableStateOf(tenant.phoneNumber)
    }
    var dob by remember {
        mutableStateOf(tenant.dob)
    }
    var placeOfOrigin by remember {
        mutableStateOf(tenant.placeOfOrigin)
    }
    var identityCard by remember {
        mutableStateOf(tenant.identityCardNumber)
    }
    var deposit by remember {
        mutableStateOf(tenant.deposit.toString())
    }
    var startDate by remember {
        mutableStateOf(tenant.startDate)
    }
    var endDate by remember {
        mutableStateOf(tenant.endDate ?: "")
    }
    var note by remember {
        mutableStateOf(tenant.note ?: "")
    }

    var switchChecked by remember {
        mutableStateOf(!tenant.endDate.isNullOrBlank())
    }

    var genderExpanded by remember {
        mutableStateOf(false)
    }
    var gender by remember {
        mutableStateOf(tenant.gender)
    }
    val listGender = listOf("Nam", "Nu", "Không biết")

    var textFieldSize by remember {
        mutableStateOf(Size.Zero)
    }

    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    var openDatePicker = remember {
        mutableStateOf(false)
    }
    var datePickerFor = remember {
        mutableStateOf("")
    }

    val numberFormat = NumberFormat.getInstance(Locale.getDefault())
    val decimalFormat = numberFormat as DecimalFormat
    decimalFormat.applyPattern("#,###")
    val listImg = ArrayList<String>()
    if(!tenant.identityCardImages.isNullOrBlank()){
        tenant.identityCardImages.split(",", limit = 2).forEach {
            listImg.add(it)
        }
    }

    var selectedFrontUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var selectedBehindUri by remember {
        mutableStateOf<Uri?>(null)
    }
    var hasFrontImage by remember {
        mutableStateOf(false)
    }
    var hasBehindImage by remember{
        mutableStateOf(false)
    }
    LaunchedEffect(true) {
        if (listImg.isNotEmpty()) {
            hasFrontImage = true
            hasBehindImage = true
            selectedFrontUri = ImageLink(listImg[0]).toUri()
            selectedBehindUri = ImageLink(listImg[1]).toUri()
        }
    }

    var showImagePickerDialog by remember {
        mutableStateOf(false)
    }

    var imageDialogFor by remember {
        mutableStateOf("")
    }

    val frontCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()){ success ->
        hasFrontImage = success
    }
    val behindCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()){ success ->
        hasBehindImage = success
    }

    val frontPhotoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            hasFrontImage = uri != null
            selectedFrontUri = uri
        }
    val behindPhotoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            hasBehindImage = uri != null
            selectedBehindUri = uri
        }

    if(showImagePickerDialog){
        ShowImagePickerDialog(
            setShowDialog = {
                showImagePickerDialog = it
            },
            pickImage = {
                if(imageDialogFor == "front"){
                    frontPhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else {
                    behindPhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            },
            takeImage = {
                if(imageDialogFor == "front"){
                    hasFrontImage = false
                    val uri = ComposeFileProvider.getImageUri(context)
                    frontCameraLauncher.launch(uri)
                    selectedFrontUri = uri
                } else {
                    hasBehindImage = false
                    val uri = ComposeFileProvider.getImageUri(context)
                    selectedBehindUri = uri
                    behindCameraLauncher.launch(uri)
                }
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
                        when (datePickerFor.value) {
                            "dob" -> {
                                dob = datePickerState.selectedDateMillis.toDateString()
                                datePickerState.setSelection(System.currentTimeMillis())
                            }

                            "startDate" -> {
                                startDate = datePickerState.selectedDateMillis.toDateString()
                                datePickerState.setSelection(System.currentTimeMillis())
                            }

                            "endDate" -> {
                                endDate = datePickerState.selectedDateMillis.toDateString()
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
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = {
                Text(text = "VD: Nguyễn Văn A")
            },
            label = {
                Text(text = "Tên người thuê")
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

        ExposedDropdownMenuBox(
            expanded = genderExpanded,
            onExpandedChange = { genderExpanded = !genderExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            OutlinedTextField(
                value = gender,
                onValueChange = { },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .onGloballyPositioned { layout ->
                        textFieldSize = layout.size.toSize()
                    },
                label = {
                    Text(text = "Giới tính")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                },
                readOnly = true,
                placeholder = {
                    Text(text = "Giới tính")
                }
            )
            ExposedDropdownMenu(
                expanded = genderExpanded, onDismissRequest = { genderExpanded = false },
                modifier = Modifier
                    .background(BackgroundColor)
                    .width(with(LocalDensity.current) {
                        textFieldSize.width.toDp()
                    })
            ) {
                listGender.forEach { genderItem ->
                    DropdownMenuItem(
                        text = { Text(text = genderItem) },
                        onClick = {
                            gender = genderItem
                            genderExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        modifier = Modifier.background(BackgroundColor)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        //SDT
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
            },
            placeholder = {
                Text(text = "VD: 0963437xxx")
            },
            label = {
                Text(text = "Số điện thoại")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        //Date of birth
        OutlinedTextField(
            value = dob,
            onValueChange = {
                if (it.length <= 8) {
                    dob = it
                }
            },
            placeholder = {
                Text(text = "vd: 01/01/2000")
            },
            label = {
                Text(text = "Ngày sinh")
            },
            trailingIcon = {
                IconButton(onClick = {
                    datePickerFor.value = "dob"
                    openDatePicker.value = true
                }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Chọn ngày sinh"
                    )
                }
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

        //Place of origin
        OutlinedTextField(
            value = placeOfOrigin,
            onValueChange = {
                placeOfOrigin = it
            },
            placeholder = {
                Text(text = "VD: Xã xxx, Huyện xxx, Thành phố xxx")
            },
            label = {
                Text(text = "Quê quán")
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

        //ID
        OutlinedTextField(
            value = identityCard,
            onValueChange = { identityCard = it },
            placeholder = {
                Text(text = "00120301xxxx")
            },
            label = {
                Text(text = "Căn cước công dân")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "CCCD mặt trước")
                    IconButton(onClick = {
                        imageDialogFor = "front"
                        showImagePickerDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null
                        )
                    }
                }
                //Image Async
                if (hasFrontImage && (selectedFrontUri != null || listImg.isNotEmpty()))
                    AsyncImage(
                        model = selectedFrontUri,
                        contentDescription = null,
                        modifier = Modifier.size(if (selectedFrontUri == null) 0.dp else 150.dp),
                        contentScale = ContentScale.Crop
                    )
            }
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "CCCD mặt sau")
                    IconButton(onClick = {
                        imageDialogFor = "behind"
                        showImagePickerDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null
                        )
                    }
                }
                //Image Async
                if (hasBehindImage && (selectedBehindUri != null || listImg.isNotEmpty())){
                    AsyncImage(
                        model = selectedBehindUri,
                        contentDescription = null,
                        modifier = Modifier.size(if (selectedBehindUri == null) 0.dp else 150.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        //Deposit
        OutlinedTextField(
            value = deposit,
            onValueChange = {
                deposit = DecimalFormatter().cleanup(it)
            },
            placeholder = {
                Text(text = "VD: 1,000,000")
            },
            label = {
                Text(text = "Tiền cọc")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            supportingText = {
                Text(
                    text = "đơn vị: VND",
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

        // Ngay thue
        OutlinedTextField(
            value = startDate,
            onValueChange = {
                if (it.length <= 8) {
                    startDate = it
                }
            },
            placeholder = {
                Text(text = "vd: 01/01/2024")
            },
            label = {
                Text(text = "Ngày thuê")
            },
            trailingIcon = {
                IconButton(onClick = {
                    datePickerFor.value = "startDate"
                    openDatePicker.value = true
                }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Chọn ngày bắt đầu thuê"
                    )
                }
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Đã rời đi?", fontSize = 15.sp)
            Switch(
                checked = switchChecked,
                onCheckedChange = { switchChecked = !switchChecked }
            )
        }

        if (switchChecked) {
            OutlinedTextField(
                value = endDate,
                onValueChange = {
                    if (it.length <= 8) {
                        endDate = it
                    }
                },
                placeholder = {
                    Text(text = "vd: 01/05/2024")
                },
                label = {
                    Text(text = "Ngày rời đi")
                },
                trailingIcon = {
                    IconButton(onClick = {
                        datePickerFor.value = "endDate"
                        openDatePicker.value = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Chọn ngày rời đi"
                        )
                    }
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
        }

        OutlinedTextField(
            value = note,
            onValueChange = {
                note = it
            },
            placeholder = {
                Text(text = "VD: Người thuê là sinh viên trường xxx")
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
                .height(120.dp)
                .padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (fullName.isEmpty() || phoneNumber.isEmpty() || dob.isEmpty() || placeOfOrigin.isEmpty()
                    || gender.isEmpty() || identityCard.isEmpty() || deposit.isEmpty() || startDate.isEmpty()
                ) {
                    Toast.makeText(
                        context,
                        "Vui lòng nhập toàn bộ ô thông tin",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (switchChecked && endDate.length < 8) {
                    Toast.makeText(
                        context,
                        "Cần nhập ngày rời đi nếu khách không còn thuê nữa",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val listImages = ArrayList<File>()
                    if ((listImg.isNotEmpty() && selectedFrontUri != ImageLink(listImg[0]).toUri())
                        || (listImg.isEmpty() && selectedFrontUri != null)) {
                        selectedFrontUri?.let { front ->
                            val frontBytes =
                                context.contentResolver.openInputStream(front)?.use {
                                    it.readBytes()
                                }
                            val frontFile =
                                File(context.cacheDir, "${fullName.trim()}_front.jpg")
                            frontFile.createNewFile()
                            frontFile.outputStream().use {
                                it.write(frontBytes)
                            }
                            listImages.add(frontFile)
                        }
                    }

                    if ((listImg.isNotEmpty() && selectedBehindUri != ImageLink(listImg[1]).toUri())
                        || (listImg.isEmpty() && selectedBehindUri != null)) {
                        selectedBehindUri?.let { behind ->
                            val behindBytes =
                                context.contentResolver.openInputStream(behind)?.use {
                                    it.readBytes()
                                }

                            val behindFile =
                                File(context.cacheDir, "${fullName.trim()}_behind.jpg")
                            behindFile.createNewFile()
                            behindFile.outputStream().use {
                                it.write(behindBytes)
                            }
                            listImages.add(behindFile)
                        }
                    }
                    //Update tenant
                    viewModel.updateTenant(
                        tenant.tenantId,
                        fullName,
                        gender,
                        phoneNumber,
                        dob,
                        placeOfOrigin,
                        identityCard,
                        deposit.toInt(),
                        startDate,
                        if (switchChecked) endDate else null,
                        note,
                        tenant.roomName,
                        if (listImages.isEmpty()) null else listImages
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(text = "Lưu thông tin")
        }
    }
}