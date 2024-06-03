package com.example.managementapp.main.presentation.showDialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.managementapp.util.visualTransform.DecimalFormatter
import com.example.managementapp.util.visualTransform.DecimalInputVisualTransformation

@Composable
fun AddServiceDialog(
    modifier: Modifier = Modifier,
    setShowDialog: (Boolean) -> Unit,
    onSubmit: (String, Int) -> Unit
) {
    var name by remember {
        mutableStateOf("")
    }
    var price by remember {
        mutableStateOf("")
    }
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Thêm dịch vụ",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    OutlinedTextField(
                        value = name, 
                        onValueChange = {name = it},
                        label = { Text(text = "Tên dịch vụ") },
                        placeholder = { Text(text = "VD: Tiền rác")},
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = DecimalFormatter().cleanup(it)},
                        label = { Text(text = "Số tiền") },
                        placeholder = { Text(text = "VD: 20000")},
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        supportingText = {
                            Text(
                                text = "đơn vị: VND",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        },
                        visualTransformation = DecimalInputVisualTransformation(DecimalFormatter())
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(onClick = {
                        setShowDialog(false)
                        onSubmit(name, price.toInt())
                    }) {
                        Text(text = "Thêm dịch vụ")
                    }
                }
            }
        }
    }

}