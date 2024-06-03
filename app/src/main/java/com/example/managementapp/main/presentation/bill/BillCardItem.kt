package com.example.managementapp.main.presentation.bill

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.managementapp.main.model.bill.Bill
import com.example.managementapp.util.UiState


@Composable
fun BilLCardItem(
    modifier: Modifier = Modifier,
    bill: Bill,
    navHostController: NavHostController,
    viewModel: BillViewModel,
    context: Context,
    onItemClick: (String) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var openDeleteDialog = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = viewModel) {
        viewModel.deleteBillResult.collect {
            when (it) {
                is UiState.Success -> {
                    Toast.makeText(context, "Xoá thành công", Toast.LENGTH_SHORT).show()
                    navHostController.navigateUp()
                }

                is UiState.Error -> {
                    val errorMsg = it.data.toString()
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    if (openDeleteDialog.value) {
        AlertDialog(
            title = {
                Text(text = "Xóa hóa đơn")
            },
            text = {
                Text(text = "Bạn có chắc muốn xóa hóa đơn này không\n Sau khi xóa, mọi dữ liệu sẽ mất")
            },
            confirmButton = {
                Button(onClick = {
                    openDeleteDialog.value = false
                    expanded = false
                    viewModel.deleteBill(bill.billId)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = bill.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    overflow = TextOverflow.Ellipsis
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
                                navHostController.navigate("update-bill/${bill.billId}")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Xóa hóa đơn", fontSize = 15.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = "Xóa hóa đơn"
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Được tạo ngày: ",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = bill.createdAt, fontSize = 18.sp, fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
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
                    text = "Phòng: ",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = bill.roomName,
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
                    text = "Tiền phòng ",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                )
                Text(text = "${bill.roomPrice}", fontSize = 15.sp, fontStyle = FontStyle.Italic)
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
                    text = "Tiền điện ",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                )
                Text(text = "${bill.electricPrice}", fontSize = 15.sp, fontStyle = FontStyle.Italic)
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
                    text = "Tền nước ",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                )
                Text(text = "${bill.waterPrice}", fontSize = 15.sp, fontStyle = FontStyle.Italic)
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
                    text = "Tổng tiền ",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${bill.total}",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (bill.status) TextDecoration.LineThrough else TextDecoration.None
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
                    text = "Tình trạng: ",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (bill.status) "Đã thanh toán" else "Chưa thanh toán",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 20.dp),
                    color = if (bill.status) MaterialTheme.colorScheme.primary else Color.Red,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onItemClick(bill.billId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(text = "Xem chi tiết")
            }
        }
    }
}