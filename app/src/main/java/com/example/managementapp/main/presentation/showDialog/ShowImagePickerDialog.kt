package com.example.managementapp.main.presentation.showDialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Preview
@Composable
fun ShowImagePickerDialogPreview() {
    ShowImagePickerDialog(setShowDialog = {}, pickImage = {}, takeImage = {})
}

@Composable
fun ShowImagePickerDialog(
    modifier: Modifier = Modifier,
    setShowDialog: (Boolean) -> Unit,
    pickImage : () -> Unit,
    takeImage: () -> Unit
) {
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Box(contentAlignment = Alignment.Center){
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                ) {
                    Text(text = "Chọn ảnh",modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                takeImage()
                                setShowDialog(false)
                            }
                            .border(2.dp, Color.LightGray, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Chụp ảnh")
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(text = "Chụp ảnh từ Camera", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                pickImage()
                                setShowDialog(false)
                            }
                            .border(2.dp, Color.LightGray, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Photo, contentDescription = "Chọn ảnh")
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(text = "Chọn ảnh từ Galery", fontSize = 18.sp)
                    }
                }
            }
        }
        
    }

}