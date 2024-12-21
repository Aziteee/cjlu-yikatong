package cn.azite.cjlu_yikatong.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cn.azite.cjlu_yikatong.component.About
import cn.azite.cjlu_yikatong.component.Base64Image
import cn.azite.cjlu_yikatong.model.HomeViewModel
import cn.azite.cjlu_yikatong.ui.MainDestinations

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "量大一卡通") },
                actions = {
                    var expandedMenu by remember { mutableStateOf(false) }
                    var openAbout by remember { mutableStateOf(false) }

                    IconButton(onClick = { expandedMenu = true }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "更多")
                    }
                    DropdownMenu(expanded = expandedMenu, onDismissRequest = { expandedMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("登录") },
                            onClick = { expandedMenu = false; navController.navigate(MainDestinations.LOGIN_ROUTE) },
                            leadingIcon = { Icon(Icons.Outlined.ExitToApp, contentDescription = "登录") }
                        )
                        DropdownMenuItem(
                            text = { Text("关于") },
                            onClick = { expandedMenu = false; openAbout = true },
                            leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = "关于") }
                        )
                    }

                    if (openAbout) {
                        About(onDismissRequest = { openAbout = false })
                    }
                }
            )
        }
        ) { innerPadding ->

        val homeViewModel = viewModel<HomeViewModel>()

        val context = LocalContext.current
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val cookie = sharedPreferences.getString("cookie", null)

        LaunchedEffect(Unit) {
            if (cookie != null) {
                homeViewModel.getData(cookie)
            } else {
                Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
                navController.navigate(MainDestinations.LOGIN_ROUTE)
            }
        }

        if (homeViewModel.loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.width(45.dp)
                )
            }
            return@Scaffold
        }

        if (homeViewModel.qrCodeString.isEmpty()) {
            Text(text = homeViewModel.html, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.bodySmall)
            return@Scaffold
        }

        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            OutlinedCard(modifier = Modifier
                .fillMaxWidth()
                .clickable { }) {
                Box(modifier = Modifier.fillMaxWidth().clickable { }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)) {
                            Text(
                                text = homeViewModel.name,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                text = homeViewModel.className,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }

                        Text(
                            text = homeViewModel.balance,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)) {
                OutlinedCard(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(dynamicLightColorScheme(context).background).clickable {
                        homeViewModel.getData(cookie.toString())
                    }) {
                        Base64Image(modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp), base64Data = homeViewModel.qrCodeString)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedCard(onClick = { navController.navigate(MainDestinations.TRANSACTION_ROUTE) }, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f, fill = true),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "交易记录",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}
