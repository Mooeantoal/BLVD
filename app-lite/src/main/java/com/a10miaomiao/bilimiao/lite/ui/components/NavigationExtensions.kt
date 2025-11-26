package com.a10miaomiao.bilimiao.lite.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.a10miaomiao.bilimiao.lite.ui.pages.*

// 导航路由定义
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Search : Screen("search")
    object VideoDetail : Screen("video_detail/{videoId}") {
        fun createRoute(videoId: String) = "video_detail/$videoId"
    }
    object DownloadList : Screen("download_list")
    object LocalVideoPlayer : Screen("local_video_player/{videoPath}?title={videoTitle}") {
        fun createRoute(videoPath: String, title: String) = 
            "local_video_player/${java.net.URLEncoder.encode(videoPath, "UTF-8")}?title=${java.net.URLEncoder.encode(title, "UTF-8")}"
    }
}

// 当前页面状态管理
class NavigationState {
    private val _currentScreen = mutableStateOf<Screen>(Screen.Main)
    val currentScreen: MutableState<Screen> = _currentScreen

    fun navigate(screen: Screen) {
        _currentScreen.value = screen
    }
}

// 导航扩展函数
fun NavController.navigateToVideoDetail(videoId: String) {
    navigate(Screen.VideoDetail.createRoute(videoId))
}

fun NavController.navigateToLocalVideoPlayer(videoPath: String, title: String) {
    navigate(Screen.LocalVideoPlayer.createRoute(videoPath, title))
}

// Compose 导航设置
@Composable
fun rememberNavigationState(): NavigationState {
    return remember { NavigationState() }
}

@Composable
fun BilimiaoNavigation(
    navController: NavHostController = rememberNavController(),
    navigationState: NavigationState = rememberNavigationState(),
    startDestination: String = Screen.Main.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        addMainGraph(navController)
    }
}

private fun NavGraphBuilder.addMainGraph(navController: NavHostController) {
    composable(Screen.Main.route) {
        MainFragment()
    }
    
    composable(Screen.Search.route) {
        SearchPage { videoId ->
            navController.navigateToVideoDetail(videoId)
        }
    }
    
    composable(Screen.VideoDetail.route) { backStackEntry ->
        val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
        VideoDetailPage(
            videoId = videoId,
            onBackClick = { navController.popBackStack() }
        )
    }
    
    composable(Screen.DownloadList.route) {
        DownloadListPage { videoPath, title ->
            navController.navigateToLocalVideoPlayer(videoPath, title)
        }
    }
    
    composable(Screen.LocalVideoPlayer.route) { backStackEntry ->
        val videoPath = backStackEntry.arguments?.getString("videoPath") ?: ""
        val videoTitle = backStackEntry.arguments?.getString("videoTitle") ?: "视频"
        LocalVideoPlayerPage(
            videoPath = java.net.URLDecoder.decode(videoPath, "UTF-8"),
            videoTitle = java.net.URLDecoder.decode(videoTitle, "UTF-8"),
            onBackClick = { navController.popBackStack() }
        )
    }
}