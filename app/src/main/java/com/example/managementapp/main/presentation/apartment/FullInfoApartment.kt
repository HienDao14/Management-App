package com.example.managementapp.main.presentation.apartment

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.managementapp.main.model.TabItem
import com.example.managementapp.main.presentation.bill.ListBillScreen
import com.example.managementapp.main.presentation.room.RoomsScreen
import com.example.managementapp.main.presentation.tenant.TenantScreen
import com.example.managementapp.ui.theme.BackgroundColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FullInfoApartment(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    apartmentId: String,
    apartmentName: String
) {
    val pagerState = rememberPagerState(
        pageCount = { 4 }
    )
    val tabItems = listOf(
        TabItem(
            title = "Phòng trọ",
            unselectedIcon = Icons.Outlined.MeetingRoom,
            selectedIcon = Icons.Filled.MeetingRoom
        ),
        TabItem(
            title = "Hóa đơn",
            unselectedIcon = Icons.Outlined.Payment,
            selectedIcon = Icons.Filled.Payment
        ),
        TabItem(
            title = "Người thuê trọ",
            unselectedIcon = Icons.Outlined.Person,
            selectedIcon = Icons.Filled.Person
        ),
        TabItem(
            title = "Thông tin chi tiết",
            unselectedIcon = Icons.Outlined.Info,
            selectedIcon = Icons.Filled.Info
        )
    )

    var selectedTabIndex by remember {
        mutableStateOf(0)
    }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    LaunchedEffect(true, prefs.getBoolean("network_status", false)) {

    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                ),
                title = {
                    Text(text = apartmentName)
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
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { position ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(position[pagerState.currentPage]),
                        height = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                containerColor = BackgroundColor
            ) {
                tabItems.forEachIndexed { index, item ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                selectedTabIndex = index
                                pagerState.scrollToPage(index)
                            }
                        },
                        text = {
                            Text(text = item.title)
                        },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedTabIndex) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.fillMaxWidth()
            ) {page ->
                when (page) {
                    0 -> {
                        RoomsScreen(apartmentId = apartmentId, navHostController = navHostController)
                    }
                    1 -> {
                        ListBillScreen(roomId = null, modifier = Modifier.fillMaxWidth(), navHostController = navHostController, apartmentId = apartmentId)
                    }
                    2 -> {
                        TenantScreen(navHostController = navHostController, apartmentId = apartmentId, roomId = "", roomName = "")
                    }

                    else -> {
                        ApartmentDetailScreen(apartmentId = apartmentId, navHostController = navHostController)
                    }
                }
            }
        }
    }

}