package com.example.managementapp.auth.presentation

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.managementapp.R
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.AuthResult
import com.example.managementapp.util.AuthUiEvent
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import java.util.Arrays


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    val state = viewModel.state

    var passwordHidden by rememberSaveable {
        mutableStateOf(true)
    }
    var openBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState()

    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current

    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    LaunchedEffect(true) {
        val token = prefs.getString("accessToken", null)
        val networkStatus = prefs.getBoolean("network_status", false)
        if (token != null && networkStatus != true) {
            navHostController.navigate("main") {
                popUpTo("login") {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(viewModel, context) {
        viewModel.authResult.collect { result ->
            when (result) {
                is AuthResult.Authorized -> {
                    navHostController.navigate("main") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }

                is AuthResult.Unauthorized -> {
                    Toast.makeText(context, "Bạn đăng nhập không thành công", Toast.LENGTH_SHORT)
                        .show()
                }

                is AuthResult.MissingInfo -> {
                    Toast.makeText(
                        context,
                        "Hãy điền đầy đủ những thông tin cần thiết",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is AuthResult.IncorrectInfo -> {
                    Toast.makeText(
                        context,
                        "Tên đăng nhập hoặc mật khẩu bị sai",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is AuthResult.UnknownError -> {
                    Toast.makeText(context, "Một lỗi nào đó đã xảy ra", Toast.LENGTH_SHORT).show()
                }

                is AuthResult.DuplicateUsername -> {
                    Unit
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(10.dp)
    ) {
        Column(modifier = Modifier.padding(top = 20.dp)) {
            Text(
                text = "Đăng nhập",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Hãy đăng nhập với tài khoản đã được đăng kí",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
            )
            Spacer(modifier = Modifier.height(60.dp))
        }

        Column {
            Text(
                text = "Tên đăng nhập",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = state.loginUsername,
                onValueChange = { viewModel.onEvent(AuthUiEvent.LoginUsernameChanged(it)) },
                placeholder = {
                    Text(text = "Email hoặc tên đăng nhập")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Mật khẩu",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = state.loginPassword,
                onValueChange = { viewModel.onEvent(AuthUiEvent.LoginPasswordChanged(it)) },
                placeholder = {
                    Text(text = "Nhập mật khẩu của bạn")
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
            TextButton(
                onClick = { openBottomSheet = true },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Quên mật khẩu?")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { viewModel.onEvent(AuthUiEvent.Login) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Đăng nhập", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Chưa có tài khoản?", fontSize = 20.sp)
            TextButton(onClick = { navHostController.navigate("signUp") }) {
                Text(text = "Đăng kí", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            windowInsets = WindowInsets(0.dp),
            sheetState = sheetState
        ) {
            BottomSheetSendCode(viewModel, context, navigate = {
                navHostController.navigate("forgot-password/$it")
            })
        }
    }
}

@Composable
fun BottomSheetSendCode(
    viewModel: AuthViewModel,
    context: Context,
    navigate: (String) -> Unit
) {

    var email by rememberSaveable {
        mutableStateOf("")
    }
    LaunchedEffect(viewModel) {
        viewModel.sendCodeState.collect {
            if(it.message != ""){
                Toast.makeText(
                    context,
                    "Mã đã được gửi. Hãy kiểm tra hộp thư email của mình để lấy mã",
                    Toast.LENGTH_SHORT
                ).show()
                navigate(email)
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(text = "Quên mật khẩu", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Nhập email của bạn để lấy mã xác thực",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Email",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {
                Text(text = "Nhập email")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                viewModel.getOTP(email)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Lấy mã", fontSize = 15.sp)
        }
    }
}
