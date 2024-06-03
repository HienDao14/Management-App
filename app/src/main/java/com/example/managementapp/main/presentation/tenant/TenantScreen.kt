package com.example.managementapp.main.presentation.tenant

import android.content.Context
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.managementapp.main.model.tenant.Tenant
import com.example.managementapp.main.presentation.showLoading.ShowProgressBar
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.DeleteAndCreateState
import com.example.managementapp.util.tenant.TenantState

@Composable
fun TenantScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    apartmentId: String,
    roomId: String,
    roomName: String,
    viewModel: TenantViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    LaunchedEffect(true, prefs.getBoolean("network_status", false)) {
        viewModel.getTenants(
            prefs.getBoolean("network_status", false),
            roomId, apartmentId
        )
    }

    val deleteState = viewModel.state.collectAsState(DeleteAndCreateState())

    if (deleteState.value.success == true) {
        Toast.makeText(context, "Xóa người thuê thành công", Toast.LENGTH_SHORT).show()
        viewModel.getTenants(
            prefs.getBoolean("network_status", false),
            roomId, apartmentId
        )
    }

    if (deleteState.value.success != null) {
        Toast.makeText(context, viewModel.deleteTenantState.error.toString(), Toast.LENGTH_SHORT)
            .show()
    }

    Scaffold(
        floatingActionButton = {
            if(roomId != ""){
                ExtendedFloatingActionButton(
                    onClick = {
                        navHostController.navigate("create-tenant/${apartmentId}/${roomId}/${roomName}")
                    }
                ) {
                    Row {
                        Text(text = "Thêm người thuê trọ")
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Thêm người thuê trọ")
                    }
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
            val tenantsState = viewModel.tenantState

            tenantsState.tenants?.let { listTenant ->
                if(listTenant.isEmpty()){
                    Text(
                        text = if(roomId == "") "Bạn chưa thêm người thuê nào, nhấn phím + để thêm" else "Hiện tại chưa có người thuê nào",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                } else {
                    TenantScreenWhenSuccess(
                        tenants = listTenant,
                        modifier = Modifier.fillMaxSize(),
                        navHostController = navHostController,
                        roomId = roomId,
                        context = context
                    )
                }
            }

            tenantsState.error?.let { error ->
                Text(text = "Error: ${error}", textAlign = TextAlign.Center, fontSize = 20.sp)
            }

            if(tenantsState.isLoading){
                ShowProgressBar(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun TenantScreenWhenSuccess(
    tenants: List<Tenant>,
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    roomId: String,
    context: Context
) {
    var finalTenants = ArrayList<Tenant>()
    if(roomId == ""){
            tenants.forEach {
                if(it.endDate.isNullOrBlank()){
                    finalTenants.add(it)
                }
            }
    } else {
        finalTenants.addAll(tenants)
    }
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = if(roomId != "") "Danh sách người thuê trọ:" else "Danh sách người thuê trọ hiện tại:",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            finalTenants.forEach { tenant ->
                item {
                    TenantCardItem(
                        tenant = tenant,
                        navHostController = navHostController,
                        roomId = roomId,
                        context = context,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    )
                }
            }
        }

    }
}

@Composable
fun TenantCardItem(
    tenant: Tenant,
    modifier: Modifier = Modifier,
    viewModel: TenantViewModel = hiltViewModel(),
    navHostController: NavHostController,
    roomId: String,
    context: Context
) {

    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val openDeleteDialog = remember {
        mutableStateOf(false)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp, pressedElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            if (openDeleteDialog.value) {
                AlertDialog(
                    title = {
                        Text(text = "Xóa phòng trọ")
                    },
                    text = {
                        Text(text = "Bạn có chắc muốn xóa người thuê này không\n Sau khi xóa, mọi dữ liệu sẽ mất")
                    },
                    confirmButton = {
                        Button(onClick = {
                            openDeleteDialog.value = false
                            expanded = false
                            viewModel.deleteTenant(roomId, tenant.tenantId)

                        }) {
                            Text(text = "Đồng ý")
                        }
                    },
                    onDismissRequest = {
                        openDeleteDialog.value = false
                        expanded = false
                    },
                    dismissButton = {
                        Button(onClick = { openDeleteDialog.value = false }) {
                            Text(text = "Hủy bỏ")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tenant.fullName,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Xem tùy chọn",
                            tint = Color.Black
                        )
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text(text = "Chỉnh sửa", fontSize = 15.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Chỉnh sửa"
                                )
                            },
                            onClick = {
                                navHostController.navigate("update-tenant/${roomId}/${tenant.tenantId}")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Xóa nguời thuê", fontSize = 15.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Xóa người thuê trọ"
                                )
                            },
                            onClick = {
                                //Open dialog to confirm delete
                                openDeleteDialog.value = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Số điện thoại: ",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                )
                Text(text = "${tenant.phoneNumber}", fontSize = 15.sp, fontStyle = FontStyle.Italic)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quê quán: ",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${tenant.placeOfOrigin}",
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Phòng thuê: ",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                )
                Text(text = "${tenant.roomName}", fontSize = 15.sp, fontStyle = FontStyle.Italic)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ngày thuê: ",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                )
                Text(text = "${tenant.startDate}", fontSize = 15.sp, fontStyle = FontStyle.Italic)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tình trạng: ",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if(tenant.endDate != "") "Đã rời đi" else "Đang thuê",
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            if(!tenant.endDate.isNullOrBlank()){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ngày rời đi: ",
                        fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = tenant.endDate!!,
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Button(
                onClick = {
                    navHostController.navigate("tenant-detail/${tenant.roomId}/${tenant.tenantId}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Xem chi tiết")
            }
        }
    }
}