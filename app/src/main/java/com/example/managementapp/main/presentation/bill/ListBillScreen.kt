package com.example.managementapp.main.presentation.bill

import android.content.Context
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.managementapp.main.model.bill.Bill
import com.example.managementapp.main.presentation.showLoading.ShowProgressBar
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.UiState

@Composable
fun ListBillScreen(
    roomId: String?,
    modifier: Modifier = Modifier,
    viewModel: BillViewModel = hiltViewModel(),
    apartmentId: String?,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    LaunchedEffect(true) {
        if(apartmentId != null){
            viewModel.getUnpaidBills(
                prefs.getBoolean("network_status", false),
                apartmentId
            )
        } else {
            viewModel.getBillsInRoom(
                prefs.getBoolean("network_status", false),
                roomId!!
            )
        }
    }

    val billsState = viewModel.bills

    Scaffold(
        floatingActionButton = {
            if(apartmentId == null){
                ExtendedFloatingActionButton(
                    onClick = {
                        navHostController.navigate("create-bill/${roomId}")
                    }
                ) {
                    Row {
                        Text(text = "Tạo hóa đơn")
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
            if (billsState.isLoading) {
                ShowProgressBar(modifier = Modifier.fillMaxSize())
            }
            if (billsState.error != null) {
                Text(text = "Lỗi: ${billsState.error}")
            }
            billsState.bills?.let {
                if (billsState.bills.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = if (apartmentId == null) "Danh sách hoá đơn" else "Danh sách hoá đơn chưa thanh toán",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            billsState.bills.forEach { bill ->
                                item {
                                    BilLCardItem(
                                        bill = bill,
                                        navHostController = navHostController,
                                        viewModel = viewModel,
                                        context = context,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        onItemClick = { billId ->
                                            navHostController.navigate("detail-bill/${billId}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = if(apartmentId == null) "Bạn chưa thêm hóa đơn nào, nhấn phím + để thêm!!!" else "Không có hoá đơn nào chưa được thanh toán!!!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
