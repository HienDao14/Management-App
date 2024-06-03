package com.example.managementapp.main.presentation.record

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.managementapp.main.model.record.Record
import com.example.managementapp.main.presentation.imageScreen.ImageScreen
import com.example.managementapp.util.ImageLink

@Composable
fun RecordCardItem(
    modifier: Modifier = Modifier,
    record: Record,
    context: Context,
    onDeleteClick: (String) -> Unit,
    onUpdateClick: (String) -> Unit
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
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    val images = ArrayList<String>()
    if (record.recordImages.isNotBlank()) {
        images.addAll(record.recordImages.split(",", limit = 2))
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
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Điện nước tháng ${record.recordedAt.substring(3, 5)}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
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
                                    expanded = false
                                    onUpdateClick(record.recordId)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = "Xóa", fontSize = 15.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Xóa điện nước"
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    //Open dialog to confirm delete
                                    onDeleteClick(record.recordId)
                                }
                            )
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
                        text = "Ngày ghi:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = record.recordedAt, fontSize = 18.sp, fontStyle = FontStyle.Italic)
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Số điện:", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = record.electricNumber,
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Số nước:", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = record.waterNumber,
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (images.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        var electricImg: String? = null
                        var waterImg: String? = null
                        try {
                            electricImg = ImageLink(images[0])
                            waterImg = ImageLink(images[1])
                        } catch (e: Exception) {
                            println(e)
                        }
                        if (electricImg != null) {
                            AsyncImage(
                                model = electricImg,
                                contentDescription = "Ảnh đồng hồ điện",
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .height(150.dp)
                                    .clickable {
                                        showImageScreen = true
                                        uriForImageScreen = ImageLink(images[0]).toUri()
                                        titleForImageScreen =
                                            "Đồng hồ điện ngày ${record.recordedAt}"
                                    },
                                contentScale = ContentScale.Fit
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        waterImg?.let {
                            AsyncImage(
                                model = waterImg,
                                contentDescription = "Ảnh đồng hồ nước",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clickable {
                                        showImageScreen = true
                                        uriForImageScreen = ImageLink(images[1]).toUri()
                                        titleForImageScreen =
                                            "Đồng hồ nước ngày ${record.recordedAt}"
                                    },
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }  else {
                    Text(
                        text = "Chưa có ảnh điện nước",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}