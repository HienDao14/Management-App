package com.example.managementapp.main.presentation

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.managementapp.main.model.apartment.Apartment
import com.example.managementapp.main.presentation.showLoading.ShowProgressBar
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.apartment.ApartmentsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    LaunchedEffect(true, prefs.getBoolean("network_status", false)) {
        viewModel.getApartments()
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                ),
                title = {
                    Text(text = "Quản lý nhà trọ")
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = {
                        viewModel.logOut {
                            navHostController.navigate("login")
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Logout, contentDescription = "Đăng xuất")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {

                    navHostController.navigate("create_apartment")
                }
            ) {
                Row {
                    Text(text = "Thêm nhà trọ")
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Thêm nhà trọ")
                }
            }
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(it)
        ) {
            MainScreenWhenSuccess(state = viewModel.state, modifier = Modifier.fillMaxSize(), navHostController)


            viewModel.state.error?.let { error ->
                Text(text = "Error: $error", textAlign = TextAlign.Center, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun MainScreenWhenSuccess(
    state: ApartmentsState,
    modifier: Modifier = Modifier,
    navHostController: NavHostController
) {

    val apartments = state.apartments
    apartments?.let {
        if (apartments.isEmpty()) {
            Text(
                text = "Bạn chưa thêm nhà trọ nào, hãy ấn vào + bên dưới để thêm nhà trọ",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        } else {
            Column(
                modifier = modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    apartments.forEach { apartment ->
                        item {
                            ApartmentCardItem(
                                apartment = apartment,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                navHostController = navHostController
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApartmentCardItem(
    modifier: Modifier = Modifier,
    apartment: Apartment,
    viewModel: MainViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val openDeleteDialog = remember {
        mutableStateOf(false)
    }

    Card(
        onClick = {},
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
//            if (viewModel.deleteState.isLoading) {
//                openDeleteDialog.value = false
//                CircularProgressIndicator(
//                    modifier = Modifier
//                        .size(40.dp)
//                        .align(Alignment.CenterHorizontally),
//                    color = MaterialTheme.colorScheme.onBackground,
//                    strokeWidth = 4.dp
//                )
//            }
            if (viewModel.deleteState.error != null) {
                openDeleteDialog.value = false
            }
            if (viewModel.deleteState.success == true) {
                viewModel.getApartments()
                openDeleteDialog.value = false
            }

            if (openDeleteDialog.value) {
                AlertDialog(
                    title = {
                        Text(text = "Xóa nhà trọ")
                    },
                    text = {
                        Text(text = "Bạn có chắc muốn xóa nhà trọ này không\n Sau khi xóa, mọi dữ liệu sẽ mất")
                    },
                    confirmButton = {
                        Button(onClick = {
                            openDeleteDialog.value = false
                            expanded = false
                            viewModel.deleteApartment(apartment.apartmentId)
                        }) {
                            Text(text = "Đồng ý")
                        }
                    },
                    onDismissRequest = {
                        openDeleteDialog.value = false
                        expanded = false
                    },
                    dismissButton = {
                        Button(onClick = {
                            openDeleteDialog.value = false
                            expanded = false
                        }) {
                            Text(text = "Hủy bỏ")
                        }
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = apartment.name,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "more",
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
                                navHostController.navigate("update-apartment/${apartment.apartmentId}")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Xóa nhà", fontSize = 15.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Xóa nhà"
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

            Row {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Địa chỉ nhà trọ của bạn",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = apartment.address,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, top = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Số phòng: ${apartment.roomCount}",
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Số phòng trống: ${apartment.roomEmpty}",
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Button(
                onClick = { navHostController.navigate("apartment_info/${apartment.apartmentId}/${apartment.name}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(text = "Xem chi tiết")
            }
        }
    }
}