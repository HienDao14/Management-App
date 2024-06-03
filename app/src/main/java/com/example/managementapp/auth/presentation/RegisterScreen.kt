package com.example.managementapp.auth.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.managementapp.R
import com.example.managementapp.auth.presentation.SocialLogin
import com.example.managementapp.ui.theme.BackgroundColor
import com.example.managementapp.util.AuthResult
import com.example.managementapp.util.AuthUiEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegisterScreen(
    mainNavigate: () -> Unit,
    navigate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state = viewModel.state

    var passwordHidden by rememberSaveable {
        mutableStateOf(true)
    }

    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        viewModel.authResult.collect {result ->
            when(result){
                is AuthResult.Authorized -> {
                    Toast.makeText(context, "Đăng kí thành công", Toast.LENGTH_SHORT).show()
                    mainNavigate()
                }
                is AuthResult.Unauthorized -> {
                    Toast.makeText(context, "Bạn đăng nhập không thành công", Toast.LENGTH_SHORT).show()
                }
                is AuthResult.MissingInfo -> {
                    Toast.makeText(context, "Hãy điền đầy đủ những thông tin cần thiết", Toast.LENGTH_SHORT).show()
                }
                is AuthResult.DuplicateUsername -> {
                    Toast.makeText(context, "Tên đăng nhập hoặc email đã được sử dụng", Toast.LENGTH_SHORT).show()
                }
                is AuthResult.UnknownError -> {
                    Toast.makeText(context, "Một lỗi nào đó đã xảy ra", Toast.LENGTH_SHORT).show()
                }
                is AuthResult.IncorrectInfo -> {
                    Unit
                }
            }
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(10.dp)
    ) {
        Column(modifier = Modifier.padding(top = 20.dp)) {
            Text(
                text = "Tạo tài khoản",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Tạo tài khoản mới của bạn",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
            )
            Spacer(modifier = Modifier.height(30.dp))
        }

        Column {

            Text(
                text = "Tên đăng nhập",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = state.registerUsername,
                onValueChange = { viewModel.onEvent(AuthUiEvent.RegisterUsernameChanged(it)) },
                placeholder = {
                    Text(text = "Tạo tên đăng nhập mới")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Email",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = state.registerEmail,
                onValueChange = { viewModel.onEvent(AuthUiEvent.RegisterEmailChanged(it)) },
                placeholder = {
                    Text(text = "Điền email của bạn")
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
                value = state.registerPassword,
                onValueChange = { viewModel.onEvent(AuthUiEvent.RegisterPasswordChanged(it)) },
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
            Button(
                onClick = { viewModel.onEvent(AuthUiEvent.Register) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Đăng kí", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Đã có tài khoản?", fontSize = 20.sp)
            TextButton(onClick = { navigate() }) {
                Text(text = "Đăng nhập", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}