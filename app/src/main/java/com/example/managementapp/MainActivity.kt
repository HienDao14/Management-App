package com.example.managementapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.managementapp.auth.presentation.LoginScreen
import com.example.managementapp.auth.presentation.RegisterScreen
import com.example.managementapp.auth.presentation.ResetPassword
import com.example.managementapp.internet_connectivity.ConnectivityObserver
import com.example.managementapp.internet_connectivity.NetworkConnectivityObserver
import com.example.managementapp.main.presentation.room.FullInfoRoomScreen
import com.example.managementapp.main.presentation.MainScreen
import com.example.managementapp.main.presentation.apartment.EditApartmentScreen
import com.example.managementapp.main.presentation.apartment.FullInfoApartment
import com.example.managementapp.main.presentation.apartment.CreateApartmentScreen
import com.example.managementapp.main.presentation.bill.BillDetailScreen
import com.example.managementapp.main.presentation.bill.CreateBillScreen
import com.example.managementapp.main.presentation.bill.EditBillScreen
import com.example.managementapp.main.presentation.record.EditRecordScreen
import com.example.managementapp.main.presentation.record.RecordScreen
import com.example.managementapp.main.presentation.room.CreateRoomScreen
import com.example.managementapp.main.presentation.room.EditRoomScreen
import com.example.managementapp.main.presentation.room.RoomDetailScreen
import com.example.managementapp.main.presentation.room.RoomsScreen
import com.example.managementapp.main.presentation.tenant.CreateTenantScreen
import com.example.managementapp.main.presentation.tenant.EditTenantScreen
import com.example.managementapp.main.presentation.tenant.TenantDetailScreen
import com.example.managementapp.ui.theme.ManagementAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 0)
        }

        connectivityObserver = NetworkConnectivityObserver(applicationContext)

        setContent {
            ManagementAppTheme {
                val status = connectivityObserver.observe().collectAsState(initial = ConnectivityObserver.Status.Unavailable)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding()
                    ) {
                        MainApp()
                    }
                }
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("main") {
            MainScreen(
                navController
            )
        }
        composable(
            route = "room_screen/{apartmentId}",
            arguments = listOf(
                navArgument(name = "apartmentId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            entry.arguments?.let { args ->
                val apartmentId = args.getString("apartmentId")
                requireNotNull(apartmentId)
                RoomsScreen(
                    apartmentId = apartmentId,
                    navHostController = navController
                )
            }
        }

        composable(
            route = "room_detail/{apartmentId}/{roomId}",
            arguments = listOf(
                navArgument(name = "apartmentId") {
                    type = NavType.StringType
                },
                navArgument(name = "roomId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            entry.arguments?.let { args ->
                val apartmentId = args.getString("apartmentId")
                val roomId = args.getString("roomId")
                requireNotNull(apartmentId)
                requireNotNull(roomId)
                RoomDetailScreen(
                    apartmentId = apartmentId,
                    roomId = roomId,
                    navHostController = navController
                )
            }

        }

        composable(
            route = "apartment_info/{apartmentId}/{apartmentName}",
            arguments = listOf(
                navArgument(name = "apartmentId") {
                    type = NavType.StringType
                },
                navArgument(name = "apartmentName") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            entry.arguments?.let { args ->
                val apartmentId = args.getString("apartmentId")
                val apartmentName = args.getString("apartmentName")
                requireNotNull(apartmentId)
                requireNotNull(apartmentName)
                FullInfoApartment(
                    navHostController = navController,
                    apartmentId = apartmentId,
                    apartmentName = apartmentName
                )
            }
        }

        composable(
            route = "room_info/{apartmentId}/{roomId}/{roomName}",
            arguments = listOf(
                navArgument(name = "apartmentId") {
                    type = NavType.StringType
                },
                navArgument(name = "roomId") {
                    type = NavType.StringType
                },
                navArgument(name = "roomName") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            entry.arguments?.let { args ->
                val apartmentId = args.getString("apartmentId")
                val roomId = args.getString("roomId")
                val roomName = args.getString("roomName")
                requireNotNull(roomId)
                requireNotNull(apartmentId)
                requireNotNull(roomName)
                FullInfoRoomScreen(
                    navHostController = navController,
                    roomId = roomId,
                    apartmentId = apartmentId,
                    roomName = roomName
                )
            }
        }

        composable(
            route = "tenant-detail/{roomId}/{tenantId}",
            arguments = listOf(
                navArgument(name = "roomId") {
                    type = NavType.StringType
                },
                navArgument(name = "tenantId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            entry.arguments?.let { args ->
                val roomId = args.getString("roomId")
                val tenantId = args.getString("tenantId")
                requireNotNull(roomId)
                requireNotNull(tenantId)
                TenantDetailScreen(
                    roomId = roomId,
                    tenantId = tenantId,
                    navHostController = navController
                )
            }
        }

        composable(
            route = "detail-bill/{billId}",
            arguments = listOf(
                navArgument(name = "billId"){
                    type = NavType.StringType
                }
            )
        ){entry ->
            entry.arguments?.let {args ->
                val billId = args.getString("billId")
                requireNotNull(billId)
                BillDetailScreen(billId = billId, navHostController = navController)
            }
        }

        composable(
            route = "create-record/{apartmentId}/{roomId}",
            arguments = listOf(
                navArgument(name = "apartmentId") {
                    type = NavType.StringType
                },
                navArgument(name = "roomId"){
                    type = NavType.StringType
                }
            )
        ){entry ->
            entry.arguments?.let {args ->
                val apartmentId = args.getString("apartmentId")
                val roomId = args.getString("roomId")
                requireNotNull(apartmentId)
                requireNotNull(roomId)
                RecordScreen(apartmentId = apartmentId, roomId = roomId, navHostController = navController)
            }
        }

        composable(
            route = "create_room/{apartmentId}",
            arguments = listOf(
                navArgument(name = "apartmentId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            entry.arguments?.let { args ->
                val apartmentId = args.getString("apartmentId")
                requireNotNull(apartmentId)
                CreateRoomScreen(navHostController = navController, apartmentId = apartmentId)
            }
        }

        composable(
            route = "create-tenant/{apartmentId}/{roomId}/{roomName}",
            arguments = listOf(
                navArgument(name = "apartmentId") {
                    type = NavType.StringType
                },
                navArgument(name = "roomId") {
                    type = NavType.StringType
                },
                navArgument(name = "roomName") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            entry.arguments?.let { args ->
                val apartmentId = args.getString("apartmentId")
                val roomId = args.getString("roomId")
                val roomName = args.getString("roomName")
                requireNotNull(roomId)
                requireNotNull(apartmentId)
                requireNotNull(roomName)
                CreateTenantScreen(
                    navHostController = navController,
                    apartmentId = apartmentId,
                    roomId = roomId,
                    roomName = roomName
                )
            }
        }
        composable(
            route = "create-bill/{roomId}",
            arguments = listOf(
                navArgument(name = "roomId"){
                    type = NavType.StringType
                }
            )
        ){entry ->
            entry.arguments?.let {args ->
                val roomId = args.getString("roomId")
                requireNotNull(roomId)
                CreateBillScreen(navHostController = navController, roomId = roomId)
            }
        }

        composable(
            route = "update-apartment/{apartmentId}",
            arguments = listOf(
                navArgument(name = "apartmentId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            entry.arguments?.let { args ->
                val apartmentId = args.getString("apartmentId")
                requireNotNull(apartmentId)
                EditApartmentScreen(apartmentId = apartmentId, navHostController = navController)
            }
        }

        composable(
            route = "update-room/{apartmentId}/{roomId}",
            arguments = listOf(
                navArgument(name = "apartmentId") {
                    type = NavType.StringType
                },
                navArgument(name = "roomId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            entry.arguments?.let { args ->
                val apartmentId = args.getString("apartmentId")
                val roomId = args.getString("roomId")
                requireNotNull(apartmentId)
                requireNotNull(roomId)
                EditRoomScreen(
                    apartmentId = apartmentId,
                    roomId = roomId,
                    navHostController = navController
                )
            }
        }

        composable(
            route = "update-tenant/{roomId}/{tenantId}",
            arguments = listOf(
                navArgument(name = "roomId") {
                    type = NavType.StringType
                },
                navArgument(name = "tenantId") {
                    type = NavType.StringType
                }
            )
        ){entry ->
            entry.arguments?.let { args ->
                val roomId = args.getString("roomId")
                val tenantId = args.getString("tenantId")
                requireNotNull(roomId)
                requireNotNull(tenantId)
                EditTenantScreen(roomId = roomId, tenantId = tenantId, navHostController = navController)
            }
        }

        composable(
            route = "update-record/{roomName}/{recordId}",
            arguments = listOf(
                navArgument(name = "roomName"){
                    type = NavType.StringType
                },
                navArgument(name = "recordId"){
                    type = NavType.StringType
                }
            )
        ){entry ->
            entry.arguments?.let {args ->
                val roomName = args.getString("roomName")
                val recordId = args.getString("recordId")
                requireNotNull(roomName)
                requireNotNull(recordId)
                EditRecordScreen(recordId = recordId, navHostController = navController, roomName = roomName)
            }
        }

        composable(
            route = "update-bill/{billId}",
            arguments = listOf(
                navArgument(name = "billId"){
                    type = NavType.StringType
                }
            )
        ){entry ->
            entry.arguments?.let {args ->
                val billId = args.getString("billId")
                requireNotNull(billId)
                EditBillScreen(billId = billId, navHostController = navController)
            }
        }

        composable("login") {
            LoginScreen(
                navHostController = navController
            )
        }
        composable("signUp") {
            RegisterScreen(
                mainNavigate = {
                    navController.navigate("main")
                },
                navigate = {
                    navController.navigate("login")
                }
            )
        }

        composable(
            route = "forgot-password/{email}",
            arguments = listOf(
                navArgument(name = "email") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            entry.arguments?.let { args ->
                val email = args.getString("email")
                requireNotNull(email)
                ResetPassword(
                    navigate = {
                        navController.navigate("login") {
                            popUpTo("forgot-password/$email") {
                                inclusive = true
                            }
                        }
                    },
                    email = email
                )
            }
        }

        composable(
            route = "create_apartment"
        ) {
            CreateApartmentScreen(
                navController = navController
            )
        }
    }
}