package com.a10miaomiao.bilimiao.lite.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import cn.a10miaomiao.bilimiao.lite.ui.pages.SearchPage
import cn.a10miaomiao.bilimiao.lite.ui.pages.DownloadListPage
import cn.a10miaomiao.bilimiao.lite.ui.components.BilimiaoNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFragment() {
    var currentPage by remember { mutableStateOf(0) }
    val navController = rememberNavController()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bilibili Lite") },
                actions = {
                    IconButton(onClick = { 
                        navController.navigate("search")
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { androidx.compose.material.icons.Icons.Default.Search },
                    label = { Text("搜索") },
                    selected = currentPage == 0,
                    onClick = { 
                        currentPage = 0
                        navController.navigate("search")
                    }
                )
                NavigationBarItem(
                    icon = { androidx.compose.material.icons.Icons.Default.Download },
                    label = { Text("下载") },
                    selected = currentPage == 1,
                    onClick = { 
                        currentPage = 1
                        navController.navigate("download_list")
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BilimiaoNavigation(
                navController = navController,
                startDestination = "search"
            )
        }
    }
}