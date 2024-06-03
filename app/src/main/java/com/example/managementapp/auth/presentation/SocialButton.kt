package com.example.managementapp.auth.presentation


import android.util.Log
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

@Composable
fun SocialLogin(
    btnText: String,
    iconRes: Int,
    modifier: Modifier = Modifier
) {
    val callbackManager = CallbackManager.Factory.create()
    val loginManager = LoginManager.getInstance()
    val context = LocalContext.current

    loginManager.registerCallback(
        callbackManager,
        object: FacebookCallback<LoginResult> {
            override fun onCancel() {}

            override fun onError(error: FacebookException) {
                Log.d("test", error.message.toString())
            }

            override fun onSuccess(result: LoginResult) {
                Log.d("test", result.accessToken.token)
            }
        })

    Card(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .height(50.dp)
            .fillMaxWidth()
            .clickable {
                loginManager.logIn(
                    context as ActivityResultRegistryOwner,
                    callbackManager,
                    listOf("email")
                )
            },
        shape = RoundedCornerShape(100.dp),
        border = BorderStroke(width = 1.dp, color = Color.Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(24.dp)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = iconRes),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = btnText, fontSize = 18.sp)
        }
    }
}
