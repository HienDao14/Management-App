package com.example.managementapp.auth.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.AuthUiEvent

@Composable
fun ResetPassword(
    modifier: Modifier = Modifier,
    navigate: () -> Unit,
    email: String,
    viewModel: AuthViewModel = hiltViewModel()
) {

    var state by rememberSaveable {
        mutableStateOf(true)
    }

    LaunchedEffect(viewModel) {
        viewModel.resetPassState.collect {
            state = it
        }
    }

    if (state) {
        CheckOTP(
            onButtonClick = {otp ->
                viewModel.verifyOTP(email, otp)
            },
            modifier = Modifier.background(BackgroundColor)
        )
    } else {
        ChangePassword(
            viewModel = viewModel, navigateToLogin = { navigate() }, email = email,
            modifier = Modifier.background(BackgroundColor)
        )
    }
}

@Composable
fun CheckOTP(
    modifier: Modifier = Modifier,
    onButtonClick: (String) -> Unit
) {
    var otp by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(
            text = "Xác thực OTP",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Hãy nhập mã OTP mà bạn nhận được từ email",
            fontSize = 15.sp,
            fontWeight = FontWeight.Light,
        )
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Mã xác thực",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = otp,
            onValueChange = { otp = it },
            placeholder = {
                Text(text = "Nhập mã xác thực")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Password,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { onButtonClick(otp) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Xác thực", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChangePassword(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel,
    navigateToLogin: () -> Unit,
    email: String
) {
    var password by rememberSaveable {
        mutableStateOf("")
    }

    var confirmPassword by rememberSaveable {
        mutableStateOf("")
    }

    var passwordHidden by rememberSaveable {
        mutableStateOf(true)
    }

    var confirmPasswordHidden by rememberSaveable {
        mutableStateOf(true)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.updatePassResponse.collect {
            if (it.success) {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                navigateToLogin()
            } else {
//                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
            .padding(top = 20.dp)
    ) {
        Text(
            text = "Tạo mật khẩu mới",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Hãy tạo mật khẩu mới cho tài khoản của bạn",
            fontSize = 15.sp,
            fontWeight = FontWeight.Light,
        )
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Mật khẩu",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text(text = "Tạo mật khẩu của bạn")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordHidden = !passwordHidden }) {
                    val visibilityIcon = if (passwordHidden)
                        Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                    Icon(imageVector = visibilityIcon, contentDescription = null)
                }
            },
            visualTransformation =
            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Xác nận mật khẩu",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = {
                Text(text = "Xác nhận mật khẩu của bạn")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                IconButton(onClick = { confirmPasswordHidden = !confirmPasswordHidden }) {
                    val visibilityIcon = if (confirmPasswordHidden)
                        Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                    Icon(imageVector = visibilityIcon, contentDescription = null)
                }
            },
            visualTransformation =
            if (confirmPasswordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.updatePassword(email, password, confirmPassword)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Tạo mật khẩu", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))

    }
}