package com.example.managementapp.main.presentation.bill

import android.content.Context
import android.widget.Space
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.managementapp.main.model.bill.AdditionService
import com.example.managementapp.main.model.bill.Bill
import com.example.managementapp.main.presentation.showLoading.ShowProgressBar
import com.example.managementapp.ui.theme.BackgroundColor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailScreen(
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
                    Text(text = "Hóa đơn")
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navHostController.navigate("update-bill/${billId}")
                }
            ) {
                Row {
                    Text(text = "Cập nhật hóa đơn")
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Cập nhật hóa đơn")
                }
            }
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
                DetailBillWhenSuccess(
                    modifier = Modifier.fillMaxSize(),
                    bill = billState.bill
                )
            }
        }
    }
}

@Composable
fun DetailBillWhenSuccess(
    modifier: Modifier = Modifier,
    bill: Bill,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        Text(text = bill.name, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Được tạo ngày: ", fontSize = 16.sp
            )
            Text(
                text = bill.createdAt, fontSize = 16.sp, fontStyle = FontStyle.Italic,
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
                text = "Phòng: ", fontSize = 16.sp
            )
            Text(
                text = bill.roomName, fontSize = 16.sp, fontStyle = FontStyle.Italic,
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Thông tin hoá đơn", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Nước", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Số nước cũ: ", fontSize = 16.sp
            )
            Text(
                text = bill.oldWaterNumber, fontSize = 16.sp, fontStyle = FontStyle.Italic,
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
                text = "Số nước mới: ", fontSize = 16.sp
            )
            Text(
                text = bill.newWaterNumber, fontSize = 16.sp, fontStyle = FontStyle.Italic,
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
                text = "Số nước sử dụng: ", fontSize = 16.sp
            )
            Text(
                text = bill.waterNumber.toString(), fontSize = 16.sp, fontStyle = FontStyle.Italic,
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
                text = "Tiền nước: ", fontSize = 16.sp
            )
            Text(
                text ="${bill.waterPrice} VND", fontSize = 18.sp, fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(20.dp))


        Text(text = "Điện", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Số điện cũ: ", fontSize = 16.sp
            )
            Text(
                text = bill.oldElectricNumber, fontSize = 16.sp, fontStyle = FontStyle.Italic,
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
                text = "Số điện mới: ", fontSize = 16.sp
            )
            Text(
                text = bill.newElectricNumber, fontSize = 16.sp, fontStyle = FontStyle.Italic,
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
                text = "Số điện sử dụng: ", fontSize = 16.sp
            )
            Text(
                text = bill.electricNumber.toString(), fontSize = 16.sp, fontStyle = FontStyle.Italic,
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
                text = "Tiền điện: ", fontSize = 16.sp
            )
            Text(
                text = "${bill.electricPrice} VND", fontSize = 18.sp, fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        val listService = ArrayList<AdditionService>()

        LaunchedEffect(true) {
            val typeToken = object : TypeToken<List<AdditionService>>() {}.type
            val services = Gson().fromJson<List<AdditionService>>(bill.additionServices, typeToken)
            listService.addAll(services)
        }
        if(listService.isNotEmpty()){
            Text(text = "Dịch vụ thêm", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(10.dp))

            listService.forEach {service ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${service.name}: ", fontSize = 16.sp
                    )
                    Text(
                        text = "${service.price} VND", fontSize = 16.sp, fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tiền phòng: ", fontSize = 16.sp
            )
            Text(
                text = "${bill.roomPrice} VND", fontSize = 16.sp, fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tổng tiền: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${bill.total} VND", fontSize = 20.sp, fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
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
            Text(
                text = "Tình trạng: ", fontSize = 16.sp
            )
            Text(
                text = if(bill.status) "Đã thanh toán" else "Chưa thanh toán",
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                color = if(bill.status) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        if(bill.note != null && bill.note != ""){
            Text(text = "Ghi chú", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = bill.note, fontSize = 16.sp)
        }
    }
}