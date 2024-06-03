package com.example.managementapp.main.presentation.tenant

import android.content.Context
import android.net.Uri
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.managementapp.main.model.tenant.Tenant
import com.example.managementapp.main.presentation.imageScreen.ImageScreen
import com.example.managementapp.main.presentation.showLoading.ShowProgressBar
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.ImageLink
import com.example.managementapp.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantDetailScreen(
    modifier: Modifier = Modifier,
    roomId: String,
    tenantId: String,
    navHostController: NavHostController,
    viewModel: TenantViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    LaunchedEffect(true, prefs.getBoolean("network_status", false)) {
        viewModel.getTenant(prefs.getBoolean("network_status", false), roomId, tenantId)
    }
    val tenant = viewModel.tenant.collectAsState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                ),
                title = {
                    Text(text = "Thông tin chi tiết")
                },
                navigationIcon = {
                    IconButton(onClick = { navHostController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navHostController.navigate("update-tenant/${roomId}/${tenantId}")
                }
            ) {
                Row {
                    Text(text = "Sửa thông tin")
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Sửa thông tin")
                }
            }
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .background(BackgroundColor)
        ) {

            when (tenant.value) {
                is UiState.Success -> {
                    val tenantData = (tenant.value as UiState.Success<Tenant>).data
                    TenantDetailScreenSuccess(
                        tenant = tenantData,
                        navHostController = navHostController,
                        context = context
                    )
                }

                is UiState.Error -> {
                    val errorMsg = (tenant.value as UiState.Error).data.toString()
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
fun TenantDetailScreenSuccess(
    modifier: Modifier = Modifier,
    tenant: Tenant,
    navHostController: NavHostController,
    context: Context
) {
    var showImageScreen by rememberSaveable {
        mutableStateOf(false)
    }
    var uriForImageScreen by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }
    var titleForImageScreen by rememberSaveable {
        mutableStateOf("")
    }
    var showImage by rememberSaveable {
        mutableStateOf(false)
    }
    println(tenant.identityCardImages.toString())

    if(showImageScreen && uriForImageScreen != null){
        ImageScreen(
            uri = uriForImageScreen!!,
            title = titleForImageScreen,
            setShowImage = {
                showImageScreen = it
            },
            context =  context,
            modifier = Modifier.fillMaxWidth()
        )
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(10.dp)

    ) {
        Text(
            text = "Thông tin chi tiết:",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Họ và tên: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(text = tenant.fullName, fontSize = 18.sp, fontStyle = FontStyle.Italic)
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Giới tính: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(text = tenant.gender, fontSize = 18.sp, fontStyle = FontStyle.Italic)
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Ngày sinh: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = tenant.dob,
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
            Text(text = "Quê quán: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = tenant.placeOfOrigin,
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
            Text(text = "Căn cước công dân: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = tenant.identityCardNumber,
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
        }
        if (!tenant.identityCardImages.isNullOrBlank()) {
            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 20.dp)
                    .clickable {
                        showImage = !showImage
                    }
            ) {
                Text(
                    text = if (!showImage) "Xem ảnh" else "Thu gọn",
                    fontSize = 15.sp, fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (showImage) Icons.Default.KeyboardArrowUp else Icons.TwoTone.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            if (showImage) {
                val images = ArrayList<String>()
                tenant.identityCardImages.let {
                    images.addAll(it.split(",", limit = 2))
                }
                AsyncImage(
                    model = ImageLink(images[0]),
                    contentDescription = "CCCD mặt trước",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 10.dp)
                        .clickable {
                            showImageScreen = true
                            uriForImageScreen = ImageLink(images[0]).toUri()
                            titleForImageScreen = "CCCD mặt trước"
                        },
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = ImageLink(images[1]),
                    contentDescription = "CCCD mặt sau",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 10.dp)
                        .clickable {
                            showImageScreen = true
                            uriForImageScreen = ImageLink(images[1]).toUri()
                            titleForImageScreen = "CCCD mặt sau"
                        },
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        } else {
            Text(
                text = "Chưa thêm ảnh",
                fontSize = 15.sp, fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End)
                    .padding(end = 20.dp),
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        Text(
            text = "Thông tin thuê trọ:",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Phòng trọ: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = tenant.roomName,
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
            Text(text = "Ngày thuê: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = tenant.startDate,
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
            Text(text = "Tiền cọc: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = "${tenant.deposit} VND",
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
            Text(text = "Tình trạng: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = if (tenant.endDate == "") "Đang thuê" else "Đã rời đi",
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        if (!tenant.endDate.isNullOrBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Ngày rời đi: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = tenant.endDate!!,
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Text(
            text = "Thông tin liên hệ:",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Số điện thoại: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = tenant.phoneNumber,
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        if(!tenant.note.isNullOrBlank()){
            Text(
                text = "Ghi chú:",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = tenant.note,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp)
            )
        }
    }
}
